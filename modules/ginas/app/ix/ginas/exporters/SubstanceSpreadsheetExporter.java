package ix.ginas.exporters;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import gov.nih.ncgc.chemical.Chemical;
import ix.core.models.Group;
import ix.core.models.Structure;
import ix.ginas.controllers.v1.SubstanceFactory;
import ix.ginas.models.v1.ChemicalSubstance;
import ix.ginas.models.v1.Code;
import ix.ginas.models.v1.NucleicAcidSubstance;
import ix.ginas.models.v1.PolymerSubstance;
import ix.ginas.models.v1.ProteinSubstance;
import ix.ginas.models.v1.StructurallyDiverseSubstance;
import ix.ginas.models.v1.Substance;
import ix.ginas.models.v1.SubstanceReference;
import ix.ginas.models.v1.Subunit;
import ix.ginas.utils.GinasUtils;

/**
 * Substance Exporter that writes out data to a Spreadsheet.
 * Created by katzelda on 8/19/16.
 */
public class SubstanceSpreadsheetExporter implements Exporter<Substance> {

    private final Spreadsheet spreadsheet;

    private int row=1;

    private final Map<String, ColumnValueRecipe<Substance>> recipeMap;


    private SubstanceSpreadsheetExporter(Builder builder){
        this.spreadsheet = builder.spreadsheet;
        this.recipeMap = builder.columns;
        int j=0;
        Spreadsheet.Row header = spreadsheet.getRow(0);
        for(String col : recipeMap.keySet()){
            header.getCell(j++).writeString(col);
        }
    }
    @Override
    public void export(Substance s) throws IOException {
        Spreadsheet.Row header = spreadsheet.getRow( row++);

        int j=0;
        for(ColumnValueRecipe<Substance> recipe : recipeMap.values()){
            SpreadsheetCell cell = header.getCell(j++);
            recipe.writeValue(s, cell);
        }
    }

    @Override
    public void close() throws IOException {
        spreadsheet.close();
    }

    private static Map<Column, ColumnValueRecipe<Substance>> DEFAULT_RECIPE_MAP;

    static{
        DEFAULT_RECIPE_MAP = new LinkedHashMap<>();

        DEFAULT_RECIPE_MAP.put(DefaultColumns.UUID, (s, cell) -> cell.write(s.getOrGenerateUUID()));
        //TODO preferred TERM ?
        DEFAULT_RECIPE_MAP.put(DefaultColumns.NAME, (s, cell) -> cell.writeString(s.getName()));
        DEFAULT_RECIPE_MAP.put(DefaultColumns.APPROVAL_ID, (s, cell) -> cell.writeString(s.getApprovalID()));

        DEFAULT_RECIPE_MAP.put(DefaultColumns.SMILES, (s, cell) -> {
            if(s instanceof ChemicalSubstance){
                cell.writeString(((ChemicalSubstance)s).structure.smiles);
            }
        });

        DEFAULT_RECIPE_MAP.put(DefaultColumns.FORMULA, (s, cell) -> {
            if(s instanceof ChemicalSubstance){
                cell.writeString(((ChemicalSubstance)s).structure.formula);
            }else if(s instanceof PolymerSubstance){
                cell.writeString("Polymer substance not supported");
            }
        });
        DEFAULT_RECIPE_MAP.put(DefaultColumns.SUBSTANCE_TYPE, (s, cell) -> cell.writeString(s.substanceClass.name()));

        //DEFAULT_RECIPE_MAP.put(DefaultColumns.STD_INCHIKEY, new  ChemicalExportRecipe(Chemical.FORMAT_STDINCHIKEY));

        DEFAULT_RECIPE_MAP.put(DefaultColumns.STD_INCHIKEY_FORMATTED, (s, cell) ->{
            if(s instanceof ChemicalSubstance){
                Structure.Stereo ster=((ChemicalSubstance)s).getStereochemistry();
                if(!ster.equals(Structure.Stereo.ABSOLUTE) && !ster.equals(Structure.Stereo.ACHIRAL)){
                    return;
                }

                try{
                    Chemical chem = s.toChemical();
                    cell.writeString( chem.export(Chemical.FORMAT_STDINCHIKEY).replace("InChIKey=",""));
                }catch(Exception e){

                }
            }
        });

        // DEFAULT_RECIPE_MAP.put(DefaultColumns.STD_INCHI, new  ChemicalExportRecipe(Chemical.FORMAT_STDINCHI));


        DEFAULT_RECIPE_MAP.put(DefaultColumns.CAS, new CodeSystemRecipe("CAS"));
        DEFAULT_RECIPE_MAP.put(DefaultColumns.EC, new CodeSystemRecipe("ECHA (EC/EINECS)"));
        DEFAULT_RECIPE_MAP.put(DefaultColumns.ITIS, ParentSourceMaterialRecipeWrapper.wrap(new CodeSystemRecipe("ITIS")));
        DEFAULT_RECIPE_MAP.put(DefaultColumns.NCBI, ParentSourceMaterialRecipeWrapper.wrap(new CodeSystemRecipe("NCBI TAXONOMY")));
        DEFAULT_RECIPE_MAP.put(DefaultColumns.USDA_PLANTS, ParentSourceMaterialRecipeWrapper.wrap(new CodeSystemRecipe("USDA PLANTS")));
        DEFAULT_RECIPE_MAP.put(DefaultColumns.INN, new CodeSystemRecipe("INN"));
        DEFAULT_RECIPE_MAP.put(DefaultColumns.NCI_THESAURUS, new CodeSystemRecipe("NCI_THESAURUS"));
        
        DEFAULT_RECIPE_MAP.put(DefaultColumns.RXCUI, new CodeSystemRecipe("RXCUI"));
        DEFAULT_RECIPE_MAP.put(DefaultColumns.PUBCHEM, new CodeSystemRecipe("PUBCHEM"));
        DEFAULT_RECIPE_MAP.put(DefaultColumns.MPNS, ParentSourceMaterialRecipeWrapper.wrap(new CodeSystemRecipe("MPNS")));
        DEFAULT_RECIPE_MAP.put(DefaultColumns.GRIN, ParentSourceMaterialRecipeWrapper.wrap(new CodeSystemRecipe("GRIN")));
        
        
        DEFAULT_RECIPE_MAP.put(DefaultColumns.INGREDIENT_TYPE, (s, cell) ->{
            cell.writeString(GinasUtils.getIngredientType(s));
        });


        //Lazy place to put new default columns
        DEFAULT_RECIPE_MAP.put(DefaultColumns.PROTEIN_SEQUENCE, (s, cell) ->{
            if(s instanceof ProteinSubstance){
                List<Subunit> subunits=((ProteinSubstance)s).protein.getSubunits();
                StringBuilder sb = new StringBuilder();
                for(Subunit su:subunits){
                    if(sb.length()!=0){
                        sb.append("|");
                    }
                    sb.append(su.sequence);
                }
                cell.writeString(sb.toString());
            }
        });

        DEFAULT_RECIPE_MAP.put(DefaultColumns.NUCLEIC_ACID_SEQUENCE, (s, cell) ->{
            if(s instanceof NucleicAcidSubstance){
                List<Subunit> subunits=((NucleicAcidSubstance)s).nucleicAcid.getSubunits();

                StringBuilder sb = new StringBuilder();

                for(Subunit su:subunits){
                    if(sb.length()!=0){
                        sb.append("|");
                    }
                    sb.append(su.sequence);
                }
                cell.writeString(sb.toString());
            }
        });
        DEFAULT_RECIPE_MAP.put(DefaultColumns.RECORD_ACCESS_GROUPS, (s, cell) ->{
            StringBuilder sb = new StringBuilder();
            for(Group g:s.getAccess()){
                if(sb.length()!=0){
                    sb.append("|");
                }
                sb.append(g.name);
            }
            cell.writeString(sb.toString());
        });



    }
    
    private static interface SubstanceColumnValueRecipe extends ColumnValueRecipe<Substance>{
    	
    	public default SubstanceFetcherRecipeWrapper wrapped(Function<Substance,Substance> trans){
    		return new SubstanceFetcherRecipeWrapper(this){
				@Override
				public Substance getSubstance(Substance s) {
					return trans.apply(s);
				}
    		};
    		
    	}
    }

    private static class ChemicalExportRecipe implements SubstanceColumnValueRecipe{

        private final  int chemicalFormat;

        public ChemicalExportRecipe(int chemicalFormat) {
            this.chemicalFormat = chemicalFormat;
        }

        @Override
        public void writeValue(Substance s, SpreadsheetCell cell) {
            if(s instanceof ChemicalSubstance){
                try{
                    Chemical chem = s.toChemical();
                    cell.writeString(chem.export(chemicalFormat));
                }catch(Exception e){

                }
            }
        }
    }
    
    
    
    
    /**
     * Wraps a {@link ColumnValueRecipe} to fetch a (possibly) different object before applying 
     * the recipe.
     * 
     * @author tyler
     *
     */
    private static abstract class SubstanceFetcherRecipeWrapper implements SubstanceColumnValueRecipe{

    	ColumnValueRecipe<Substance> _delegate;
    	
    	public SubstanceFetcherRecipeWrapper(ColumnValueRecipe<Substance>  del){
    		this._delegate=del;
    		
    	}
    	
    	public abstract Substance getSubstance(Substance s);
    	
		@Override
		public void writeValue(Substance object, SpreadsheetCell cell) {
			this._delegate.writeValue(getSubstance(object), cell);
		}
		
    }
    
    private static class ParentSourceMaterialRecipeWrapper extends SubstanceFetcherRecipeWrapper{

		public ParentSourceMaterialRecipeWrapper(ColumnValueRecipe<Substance> del) {
			super(del);
		}

		@Override
		public Substance getSubstance(Substance s) {
			
			if(s instanceof StructurallyDiverseSubstance){
				StructurallyDiverseSubstance sdiv = (StructurallyDiverseSubstance)s;
			    SubstanceReference sr=sdiv.structurallyDiverse.parentSubstance;
			    if(sr!=null){
			    	Substance full = SubstanceFactory.getFullSubstance(sr);
			    	if(full!=null){
			    		return full;
			    	}
			    }
			}
			return s;
		}
		
		/**
		 * Fetches the parent substance (if one exists) rather than the given substance
		 * for use in column recipes.
		 * @param col
		 * @return
		 */
		public static ParentSourceMaterialRecipeWrapper wrap(ColumnValueRecipe<Substance> col){
			return new ParentSourceMaterialRecipeWrapper(col);
		}
    	
    }
    
    private static class CodeSystemRecipe implements SubstanceColumnValueRecipe{

        private final String codeSystemToFind;
        private final boolean publicOnly;
        

        public CodeSystemRecipe(String codeSystemToFind) {
            this(codeSystemToFind, false);
        }



        public CodeSystemRecipe(String codeSystemToFind, boolean publicOnly) {
            this.codeSystemToFind = codeSystemToFind;
            this.publicOnly = publicOnly;
        }


        public CodeSystemRecipe asPublicOnly(){
            return new CodeSystemRecipe(codeSystemToFind, true);
        }

        @Override
        public void writeValue(Substance s, SpreadsheetCell cell) {
            String bestCode=null;
            for(Code cd: s.codes){
                if(publicOnly && !cd.isPublic()){
                    continue;
                }
                if(cd.codeSystem.equals(codeSystemToFind)){
                    if("PRIMARY".equals(cd.type)){
                        bestCode = cd.code;
                        break;
                    }else{
                        if(bestCode==null){
                            bestCode=cd.code + " [" + cd.type + "]";
                        }
                    }
                }
            }

            if(bestCode !=null){
                cell.writeString(bestCode);
            }
        }
        
        
    }

    /**
     * Builder class that makes a SpreadsheetExporter.  By default, the default columns are used
     * but these may be modified using the add/remove column methods.
     *
     */
    public static class Builder{
        private final Map<String, ColumnValueRecipe<Substance>> columns = new LinkedHashMap<>();
        private final Spreadsheet spreadsheet;

        private boolean publicOnly = false;

        /**
         * Create a new Builder that uses the given Spreadsheet to write to.
         * @param spreadSheet the {@link Spreadsheet} object that will be written to by this exporter. can not be null.
         *
         * @throws NullPointerException if spreadsheet is null.
         */
        public Builder(Spreadsheet spreadSheet){
            Objects.requireNonNull(spreadSheet);
            this.spreadsheet = spreadSheet;

            for(Map.Entry<Column, ColumnValueRecipe<Substance>> entry : DEFAULT_RECIPE_MAP.entrySet()){
                columns.put(entry.getKey().name(), entry.getValue());
            }
        }

        public Builder addColumn(Column column, ColumnValueRecipe<Substance> recipe){
            return addColumn(column.name(), recipe);
        }
        public Builder addColumn(String columnName, ColumnValueRecipe<Substance> recipe){
            Objects.requireNonNull(columnName);
            Objects.requireNonNull(recipe);
            columns.put(columnName, recipe);

            return this;
        }

        public Builder removeColumn(Column column){
            return removeColumn(column.name());
        }

        public Builder removeColumn(String columnName){
            columns.remove(columnName);
            return this;
        }

        public SubstanceSpreadsheetExporter build(){

            if(publicOnly){
                for(Map.Entry<String, ColumnValueRecipe<Substance>> entry : columns.entrySet()){
                    ColumnValueRecipe<Substance> value = entry.getValue();
                    if(value instanceof CodeSystemRecipe){
                        entry.setValue(((CodeSystemRecipe) value).asPublicOnly());
                    }
                }
            }

            return new SubstanceSpreadsheetExporter(this);
        }

        public Builder includePublicDataOnly(boolean publicOnly){
            this.publicOnly = publicOnly;
            return this;
        }

    }
}