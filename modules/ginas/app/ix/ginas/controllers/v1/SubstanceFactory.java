package ix.ginas.controllers.v1;

import static ix.ncats.controllers.auth.Authentication.getUserProfile;
import ix.core.NamedResource;
import ix.core.controllers.EntityFactory;
import ix.core.models.Group;
import ix.core.models.Principal;
import ix.core.models.Role;
import ix.core.models.Structure;
import ix.core.models.UserProfile;
import ix.core.models.Value;
import ix.core.plugins.SequenceIndexerPlugin;
import ix.ginas.controllers.GinasApp;
import ix.ginas.models.v1.ChemicalSubstance;
import ix.ginas.models.v1.Code;
import ix.ginas.models.v1.MixtureSubstance;
import ix.ginas.models.v1.NucleicAcidSubstance;
import ix.ginas.models.v1.PolymerSubstance;
import ix.ginas.models.v1.ProteinSubstance;
import ix.ginas.models.v1.SpecifiedSubstanceGroup1Substance;
import ix.ginas.models.v1.StructurallyDiverseSubstance;
import ix.ginas.models.v1.Substance;
import ix.ginas.models.v1.SubstanceReference;
import ix.ginas.models.v1.Subunit;
import ix.ginas.utils.GinasProcessingStrategy;
import ix.ginas.utils.GinasV1ProblemHandler;
import ix.ginas.utils.validation.DefaultSubstanceValidator;
import ix.ncats.controllers.security.IxDeadboltHandler;
import ix.ncats.controllers.security.IxDynamicResourceHandler;
import ix.seqaln.SequenceIndexer;
import ix.seqaln.SequenceIndexer.ResultEnumeration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import play.Logger;
import play.Play;
import play.db.ebean.Model;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;

@NamedResource(name="substances",
               type=Substance.class,
               description="Resource for handling of GInAS substances")
public class SubstanceFactory extends EntityFactory {
    private static final double SEQUENCE_IDENTITY_CUTOFF = 0.5;
	static public final Model.Finder<UUID, Substance> finder =
        new Model.Finder(UUID.class, Substance.class);

	//Do we still need these?
    static public final Model.Finder<UUID, ChemicalSubstance> chemfinder =
            new Model.Finder(UUID.class, ChemicalSubstance.class);
    static public final Model.Finder<UUID, ProteinSubstance> protfinder =
            new Model.Finder(UUID.class, ProteinSubstance.class);
    
    
    public static SequenceIndexer _seqIndexer =
            Play.application().plugin(SequenceIndexerPlugin.class).getIndexer();

    public static Substance getSubstance (String id) {
        if(id==null)return null;
        return getSubstance (UUID.fromString(id));
    }

    public static Substance getSubstance (UUID uuid) {
        return getEntity (uuid, finder);
    }

    public static Result get (UUID id, String select) {
        return get (id, select, finder);
    }

    public static Substance getFullSubstance(SubstanceReference subRef){
        return getSubstanceByApprovalIDOrUUID(subRef.approvalID, subRef.refuuid);
    }
    public static List<Substance> getSubstanceWithAlternativeDefinition(Substance altSub){
    	List<Substance> sublist=finder.where().and(com.avaje.ebean.Expr.eq("relationships.relatedSubstance.refUUID",altSub.uuid.toString()), 
    			           com.avaje.ebean.Expr.eq("relationships.relatedSubstance.type",Substance.ALTERNATE_SUBSTANCE_REL)).findList();
    	List<Substance> realList = new ArrayList<Substance>();
    	for(Substance sub:sublist){
    		for(SubstanceReference sref:sub.getAlternativeDefinitionReferences()){
    			if(sref.refuuid.equals(altSub.uuid.toString())){
    				realList.add(sub);
    				break;
    			}
    		}
    	}
    	return realList;
    }
    
    
    private static Substance getSubstanceByApprovalIDOrUUID (String approvalID, String uuid) {
        Substance s=getSubstance(uuid);
        if(s!=null)return s;
        
        List<Substance> list=GinasApp.resolve(finder,approvalID);
        if(list!=null && list.size()>0){
                return list.get(0);
        }
        return null;
        //return finder.where().eq("approvalID", approvalID).findUnique();
    }
    public static Substance getSubstanceByApprovalID(String approvalID){
    	List<Substance> list=GinasApp.resolve(finder,approvalID);
        if(list!=null && list.size()>0){
                return list.get(0);
        }
        return null;
    }
    public static String getMostRecentCode(String codeSystem, String like){
    	List<Substance> subs=finder.where().and(com.avaje.ebean.Expr.like("codes.code",like), com.avaje.ebean.Expr.eq("codes.codeSystem",codeSystem)).orderBy("codes.code").setMaxRows(1).findList();
    	List<String> retCodes = new ArrayList<String>();
    	if(subs!=null){
    		if(subs.size()>=1){
    		Substance sub=subs.get(0);
    		for(Code c: sub.codes){
    			if(c.codeSystem.equals(codeSystem)){
    				retCodes.add(c.code);
    			}
    		}
    		}
    	}
    	if(retCodes.size()==0)return null;
    	Collections.sort(retCodes);
    	return retCodes.get(0);
    }

    public static List<Substance> getSubstances
        (int top, int skip, String filter) {
        SubstanceFilter subFilter = new SubstanceFilter();
        List<Substance> substances = filter (new FetchOptions (top, skip, filter), finder);
        return subFilter.filterByAccess(substances);
    }

    //TODO: Doesn't support top/skip
    public static List<Substance> getSubstancesWithExactName
    (int top, int skip, String name) {
                return finder.where().eq("names.name", name).findList();
        }
    
    //TODO: Doesn't support top/skip
    public static List<Substance> getSubstancesWithExactCode
    (int top, int skip, String code, String codeSystem) {
                return finder.where().and(com.avaje.ebean.Expr.eq("codes.code",code), com.avaje.ebean.Expr.eq("codes.codeSystem",codeSystem)).findList();
    }
    
        
    public static Integer getCount () {
        try {
            return getCount (finder);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    public static Result count () {
        return count (finder);
    }

    public static Result page (int top, int skip) {
        return page (top, skip, null);
    }

    public static Result page (int top, int skip, String filter) {
        return page (top, skip, filter, finder);
    }

    public static Result edits (UUID uuid) {
    	return edits (uuid, Substance.getAllClasses());
    }

    public static Result getUUID (UUID uuid, String expand) {
        return get (uuid, expand, finder);
    }

    public static Result field (UUID uuid, String path) {
        return field (uuid, path, finder);
    }

    public static Result create () {
    	JsonNode value = request().body().asJson();
        Class subClass = getClassFromJson(value);
        DefaultSubstanceValidator sv= new DefaultSubstanceValidator(GinasProcessingStrategy.ACCEPT_APPLY_ALL_WARNINGS());
        return create (subClass, finder,sv);
    }

    public static Result validate () {
    	JsonNode value = request().body().asJson();
        Class subClass = getClassFromJson(value);
    	DefaultSubstanceValidator sv= new DefaultSubstanceValidator(GinasProcessingStrategy.ACCEPT_APPLY_ALL_WARNINGS_MARK_FAILED());
    	return validate (subClass, finder, sv);
    }

    public static Result delete (UUID uuid) {
        return delete (uuid, finder);
    }
    
    public static Class<? extends Substance> getClassFromJson(JsonNode json){
    	Class<? extends Substance> subClass = Substance.class;
        
        String cls = null;
              
        try {
        	cls= json.get("substanceClass").asText();
            Substance.SubstanceClass type =
                Substance.SubstanceClass.valueOf(cls);
            switch (type) {
            case chemical:
                subClass = ChemicalSubstance.class;
                break;
            case protein:
                subClass = ProteinSubstance.class;
                break;
            case mixture:
                subClass = MixtureSubstance.class;
                break;
            case polymer:
                subClass = PolymerSubstance.class;
                break;
            case nucleicAcid:
                subClass = NucleicAcidSubstance.class;
                break;
            case structurallyDiverse:
                subClass = StructurallyDiverseSubstance.class;
                break;
            case specifiedSubstanceG1:
                subClass = SpecifiedSubstanceGroup1Substance.class;
                break;
            case concept:               
            default:
                subClass = Substance.class;
                break;
            }
        }
        catch (Exception ex) {
            Logger.warn("Unknown substance class: "+cls
                        +"; treating as generic substance!");
            //throw ex;
        }
        return subClass;
    }

    public static Result updateEntity () {
    	DefaultSubstanceValidator sv= new DefaultSubstanceValidator(GinasProcessingStrategy.ACCEPT_APPLY_ALL_WARNINGS());
    	
        if (!request().method().equalsIgnoreCase("PUT")) {
            return badRequest ("Only PUT is accepted!");
        }

        String content = request().getHeader("Content-Type");
        if (content == null || (content.indexOf("application/json") < 0
                                && content.indexOf("text/json") < 0)) {
            return badRequest ("Mime type \""+content+"\" not supported!");
        }
        JsonNode json = request().body().asJson();
        
        Class<? extends Substance> subClass = getClassFromJson(json);
        return updateEntity (json, subClass, sv);
    }
    
    public static Result update (UUID uuid, String field) {
    	DefaultSubstanceValidator sv= new DefaultSubstanceValidator(GinasProcessingStrategy.ACCEPT_APPLY_ALL_WARNINGS());
    	
        //if(true)return ok("###");
        try {
            JsonNode value = request().body().asJson();
            Class subClass = getClassFromJson(value);
            return update(uuid, field, subClass, finder, new GinasV1ProblemHandler(), sv);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static List<Substance> getCollsionChemicalSubstances(int i, int j, ChemicalSubstance cs) {
        String hash=cs.structure.getLychiv4Hash();
        List<Substance> dupeList= finder.where().like("structure.properties.term",hash).findList();
        return dupeList;
    }


	public static class SubstanceFilter implements EntityFilter {

		UserProfile profile = getUserProfile();
		Principal user = profile != null ? profile.user : null;
        boolean hasAdmin = false;

		public boolean hasAccess(Object grp, Object sub) {
			Group group = (Group) grp;
			Substance substance = (Substance) sub;
			return substance.getAccess().contains(group);
		}

		public List<Substance> filterByAccess(List<Substance> results) {
			List<Substance> filteredSubstances = new ArrayList<Substance>();
	    	

			if(IxDeadboltHandler.activeSessionHasPermission("isAdmin")){
				return results;
			}
			

			for (Substance sub : results) {
				Set<Group> accessG = sub.getAccess();
				if (accessG == null || accessG.isEmpty() || accessG.size() == 0) {
					filteredSubstances.add(sub);
				}else{
					if (user != null) {
						for (Group grp : profile.getGroups()) {
							if (hasAccess(grp, sub)) {
								filteredSubstances.add(sub);
							}
						}
					}
				}
			}
			return filteredSubstances;
		}
	}


	public static List<Substance> getNearCollsionProteinSubstances(int top,
			int skip, ProteinSubstance cs) {
		Set<Substance> dupes = new LinkedHashSet<Substance>();
		if(_seqIndexer==null){
			_seqIndexer =
		            Play.application().plugin(SequenceIndexerPlugin.class).getIndexer();
		}
		for(Subunit subunit : cs.protein.subunits){
			try{
				ResultEnumeration re = _seqIndexer.search(subunit.sequence, SubstanceFactory.SEQUENCE_IDENTITY_CUTOFF);
				int i=0;
				while(re.hasMoreElements()){
					SequenceIndexer.Result r = re.nextElement();
					List<ProteinSubstance> proteins = SubstanceFactory.protfinder
			                .where().eq("protein.subunits.uuid", r.id).findList();
					if(proteins!=null && proteins.size()>=0){
						for(Substance s: proteins){
							if(i>=skip)dupes.add(s);
							i++;
							if(dupes.size()>=top)break;
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return new ArrayList<Substance>(dupes);
	}
	public static List<Substance> getNearCollsionProteinSubstancesToSubunit(int top,
			int skip, Subunit subunit) {
		Set<Substance> dupes = new LinkedHashSet<Substance>();
		if(_seqIndexer==null){
			_seqIndexer =
		            Play.application().plugin(SequenceIndexerPlugin.class).getIndexer();
		}
			try{
				ResultEnumeration re = _seqIndexer.search(subunit.sequence, SubstanceFactory.SEQUENCE_IDENTITY_CUTOFF);
				int i=0;
				while(re.hasMoreElements()){
					SequenceIndexer.Result r = re.nextElement();
					List<ProteinSubstance> proteins = SubstanceFactory.protfinder
			                .where().eq("protein.subunits.uuid", r.id).findList();
					if(proteins!=null && proteins.size()>=0){
						for(Substance s: proteins){
							if(i>=skip)dupes.add(s);
							i++;
							if(dupes.size()>=top)break;
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		return new ArrayList<Substance>(dupes);
	}
}
