package ix.ginas.processors;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import ix.core.EntityProcessor;
import ix.core.adapters.EntityPersistAdapter;
import ix.core.controllers.PayloadFactory;
import ix.core.models.Payload;
import ix.core.plugins.SequenceIndexerPlugin;
import ix.core.plugins.StructureIndexerPlugin;
import ix.core.plugins.StructureIndexerPlugin.StandardizedStructureIndexer;
import ix.ginas.controllers.v1.SubstanceFactory;
import ix.ginas.datasource.KewControlledPlantDataSet;
import ix.ginas.models.v1.Relationship;
import ix.ginas.models.v1.Substance;
import ix.ginas.models.v1.Substance.SubstanceDefinitionType;
import ix.ginas.models.v1.SubstanceReference;
import ix.seqaln.SequenceIndexer;
import org.jcvi.jillion.fasta.*;
import play.Logger;
import play.Play;
import play.db.ebean.Model;

public class SubstanceProcessor implements EntityProcessor<Substance>{

    private static Pattern PAYLOAD_UUID_PATTERN = Pattern.compile("payload\\((.+?)\\)");

    public static StandardizedStructureIndexer _strucIndexer =
            Play.application().plugin(StructureIndexerPlugin.class).getIndexer();
    KewControlledPlantDataSet kewData;

    public Model.Finder<UUID, Relationship> finder;

    public SubstanceProcessor(){
        try{
            kewData= new KewControlledPlantDataSet("kew.json");
        }catch(Exception e){
            e.printStackTrace();
        }
        finder = new Model.Finder(UUID.class, Relationship.class);
        //System.out.println("Made processor");


    }


    @Override
    public void postPersist(Substance obj) {
        System.out.println("Substance processor post persist for class " + obj.getClass());
    }

    public void addWaitingRelationships(Substance obj){
        List<Relationship> refrel = finder.where().eq("relatedSubstance.refuuid",
                obj.getOrGenerateUUID().toString()).findList();
        boolean changed=false;
        for(Relationship r:refrel){

            Relationship inv=RelationshipProcessor.getInstance().createAndAddInvertedRelationship(r,r.fetchOwner().asSubstanceReference(),obj);
            if(inv!=null){
                changed=true;
            }
        }
    }

    @Override
    public void postLoad(Substance obj) {
        //Logic here may be needed at certain times for rebuilding indexes
        //This will require some external information, not yet present
    }

    @Override
    public void postRemove(Substance obj) {
        //Could have logic here to remove things
    }

    @Override
    public void postUpdate(Substance obj) {
        postPersist(obj);
    }


    @Override
    public void prePersist(final Substance s) {


        Logger.debug("Persisting substance:" + s);
        if (s.isAlternativeDefinition()) {

            Logger.debug("It's alternative");
            //If it's alternative, find the primary substance (there should only be 1, but this returns a list anyway)
            List<Substance> realPrimarysubs=SubstanceFactory.getSubstanceWithAlternativeDefinition(s);
            Logger.debug("Got some relationships:" + realPrimarysubs.size());
            Set<String> oldprimary = new HashSet<String>();
            for(Substance pri:realPrimarysubs){
                oldprimary.add(pri.getUuid().toString());
            }


            SubstanceReference sr = s.getPrimaryDefinitionReference();
            if (sr != null) {

                Logger.debug("Enforcing bidirectional relationship");
                //remove old references
                for(final Substance oldPri: realPrimarysubs){
                    if(oldPri ==null){
                        continue;
                    }
                    Logger.debug("Removing stale bidirectional relationships");
                    //						EntityPersistAdapter.performChange(oldPri, new Callable(){
                    //
                    //							@Override
                    //							public Object call() throws Exception {
                    //								List<Relationship> related=oldPri.removeAlternativeSubstanceDefinitionRelationship(s);
                    //								for(Relationship r:related){
                    //									r.delete();
                    //								}
                    //								oldPri.forceUpdate();
                    //								return null;
                    //							}
                    //
                    //						});

                    EntityPersistAdapter.performChangeOn(oldPri, obj->{
                        List<Relationship> related=oldPri.removeAlternativeSubstanceDefinitionRelationship(s);
                        for(Relationship r:related){
                            r.delete();
                        }
                        oldPri.forceUpdate();
                        return Optional.of(obj);
                    }
                            );


                }
                Logger.debug("Expanding reference");
                Substance subPrimary=null;	
                try{
                    subPrimary = SubstanceFactory.getFullSubstance(sr);
                }catch(Exception e){
                    e.printStackTrace();
                }
                Logger.debug("Got parent sub, which is:" + subPrimary.getName());
                if (subPrimary != null) {
                    if (subPrimary.definitionType == SubstanceDefinitionType.PRIMARY) {
                        final Substance subPrimaryFinal=subPrimary;
                        Logger.debug("Going to save");

                        EntityPersistAdapter.performChangeOn(subPrimary, obj -> {
                            if (!obj.addAlternativeSubstanceDefinitionRelationship(s)) {
                                Logger.info("Saving alt definition, now has:"
                                        + obj.getAlternativeDefinitionReferences().size());
                            }
                            obj.forceUpdate();
                            return Optional.of(obj);
                        });

                    }
                }

            }else{
                Logger.error("Persist error. Alternative definition has no primary relationship");
            }
        }
        addKewIfPossible(s);
        addWaitingRelationships(s);
    }

    /**
     * Adds/remove Kew tag for controlled kew substances
     * @param s
     */
    public void addKewIfPossible(Substance s){
        if(kewData!=null){
            if(s.approvalID!=null && s.isPrimaryDefinition()){
                if(kewData.contains(s.approvalID)){
                    s.addTagString("KEW");
                }else{
                    if(s.hasTagString("KEW")){
                        s.removeTagString("KEW");
                    }
                }
            }
        }
    }

    @Override
    public void preUpdate(Substance obj) {
        prePersist(obj);
    }

    @Override
    public void preRemove(Substance obj) {}





}
