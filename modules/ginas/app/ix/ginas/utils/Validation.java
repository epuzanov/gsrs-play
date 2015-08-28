package ix.ginas.utils;

import ix.core.chem.StructureProcessor;
import ix.core.models.Structure;
import ix.core.models.Value;
import ix.ginas.models.v1.ChemicalSubstance;
import ix.ginas.models.v1.Moiety;
import ix.ginas.models.v1.Name;
import ix.ginas.models.v1.Substance;

import java.util.ArrayList;
import java.util.List;

public class Validation {
	
	public static List<GinasProcessingMessage> validateAndPrepare(Substance s, GinasProcessingStrategy strat){
		List<GinasProcessingMessage> gpm=new ArrayList<GinasProcessingMessage>();
		if(s.names.size()<=0){
			GinasProcessingMessage mes=GinasProcessingMessage.ERROR_MESSAGE("Substances must have names");
			gpm.add(mes);
			strat.processMessage(mes);
		}
		boolean preferred=false;
		for(Name n : s.names){
			if(n.preferred){
				preferred=true;
			}
		}
		if(!preferred){
			GinasProcessingMessage mes=GinasProcessingMessage.WARNING_MESSAGE("Substances should have at least one (1) preferred name").appliableChange(true);
			gpm.add(mes);
			strat.processMessage(mes);
			if(mes.actionType==GinasProcessingMessage.ACTION_TYPE.APPLY_CHANGE){
				if(s.names.size()>0){
					Name.sortNames(s.names);
					s.names.get(0).preferred=true;
					mes.appliedChange=true;
				}
			}
		}
		
		switch(s.substanceClass){
			case chemical:
				gpm.addAll(validateAndPrepareChemical((ChemicalSubstance) s,strat));
				break;
			case concept:
				break;
			case mixture:
				break;
			case nucleicAcid:
				break;
			case polymer:
				break;
			case protein:
				break;
			case reference:
				break;
			case specifiedSubstanceG1:
				break;
			case specifiedSubstanceG2:
				break;
			case specifiedSubstanceG3:
				break;
			case specifiedSubstanceG4:
				break;
			case structurallyDiverse:
				break;
			case unspecifiedSubstance:
				break;
			default:
				break;
		
		}
		return gpm;
	}
	public static List<GinasProcessingMessage> validateAndPrepareChemical(ChemicalSubstance cs, GinasProcessingStrategy strat){
    	List<GinasProcessingMessage> gpm=new ArrayList<GinasProcessingMessage>();
    	
    	String payload = cs.structure.molfile;
        if (payload != null) {
        	List<Moiety> moietiesForSub = new ArrayList<Moiety>();
            List<Structure> moieties = new ArrayList<Structure>();
            Structure struc = StructureProcessor.instrument
                (payload, moieties);
            cs.structure=struc;
            //struc.count
            for(Structure m: moieties){
            	Moiety m2= new Moiety();
            	m2.structure=m;
            	m2.count=m.count;
            	moietiesForSub.add(m2);
            }
            
            if(cs.moieties.size()<moietiesForSub.size()){
            	GinasProcessingMessage mes=GinasProcessingMessage.WARNING_MESSAGE("Incorrect number of moeities").appliableChange(true);
            	gpm.add(mes);
            	strat.processMessage(mes);
            	switch(mes.actionType){
				case APPLY_CHANGE:
					cs.moieties=moietiesForSub;
					mes.appliedChange=true;
					break;
				case FAIL:
					break;
				case DO_NOTHING:
				case IGNORE:
				default:
					break;
            	}            	
            }
            if(!struc.digest.equals(cs.structure.digest)){
            	GinasProcessingMessage mes=GinasProcessingMessage.WARNING_MESSAGE("Given structure digest disagrees with computed").appliableChange(true);
            	gpm.add(mes);
            	strat.processMessage(mes);
            	switch(mes.actionType){
				case APPLY_CHANGE:
					cs.structure=struc;
					mes.appliedChange=true;
					break;
				case FAIL:
					break;
				case DO_NOTHING:
				case IGNORE:
				default:
					break;
            	}
            }
            String hash=null;
            for (Value val : struc.properties) {
                if (Structure.H_LyChI_L4.equals(val.label)) {
                	hash=val.getValue()+"";
                }
            }
            
            try {
            	List<Substance> sr=ix.ginas.controllers.v1.SubstanceFactory.getSubstances(100, 0, "structure.properties.term='"+ hash+"'");
				
				if(sr.size()>0){
					
					GinasProcessingMessage mes=GinasProcessingMessage.WARNING_MESSAGE("Structure has " + sr.size() +" possible duplicate(s)").appliableChange(true);
	            	gpm.add(mes);
	            	strat.processMessage(mes);
	            	switch(mes.actionType){
					case APPLY_CHANGE:
						cs.status="FAILED";
						cs.addPropertyNote(mes.message, "FAIL_REASON");
						cs.addRestrictGroup("admin");
						mes.appliedChange=true;
						break;
					case DO_NOTHING:
						break;
					case FAIL:
						break;
					case IGNORE:
						break;
					default:
						break;
					
	            	}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
            
        }
        return gpm;
    }
}
