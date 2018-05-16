package ix.ncats.resolvers;

import ix.core.models.Structure;
import uk.ac.cam.ch.wwmm.opsin.NameToStructure;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import ix.ncats.resolvers.AbstractStructureResolver.UrlAndFormat;
/**
 * Created by katzelda on 3/29/18.
 */
public class OpsinResolver extends AbstractStructureResolver{

    private static final NameToStructure nts =  NameToStructure.getInstance();
    public OpsinResolver() {
        super("OPSIN");
    }

    @Override
    protected UrlAndFormat[] resolvers(String name) throws MalformedURLException {
        return new UrlAndFormat[0]; // unused
    }

    @Override
    public Structure resolve(String name) {
        String smiles = nts.parseToSmiles(name);
        if(smiles !=null){
            try {
                return this.resolve(new ByteArrayInputStream(smiles.getBytes()), "smiles");
            } catch (IOException e) {
                return null;
            }

        }
        return null;
    }
}
