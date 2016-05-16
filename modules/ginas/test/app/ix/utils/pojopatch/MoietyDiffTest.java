package app.ix.utils.pojopatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.diff.JsonDiff;
import ix.ginas.models.v1.GinasChemicalStructure;
import ix.ginas.models.v1.Moiety;
import ix.test.ix.test.server.GinasTestServer;
import ix.utils.pojopatch.PojoDiff;
import ix.utils.pojopatch.PojoPatch;
import org.junit.Rule;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Created by katzelda on 5/13/16.
 */
public class MoietyDiffTest {

    ObjectMapper mapper = new ObjectMapper();

    @Rule
    public GinasTestServer ts = new GinasTestServer();

    @Test
    public void changeNestedField() throws Exception{
        Moiety old = new Moiety();
        UUID uuid = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        System.out.println("uuid = " + uuid);
        old.uuid = uuid;
        old.structure = new GinasChemicalStructure();
        old.structure.smiles = "c1cccc1";
        old.structure.id = uuid2;

        old.enforce();

        Moiety newMoiety = new Moiety();
        newMoiety.uuid = uuid;
        newMoiety.structure = new GinasChemicalStructure();
        newMoiety.structure.smiles = "c1cccc1OH";
        newMoiety.structure.id = uuid2;

        newMoiety.enforce();


        System.out.println("old json");
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.valueToTree(old)));

        PojoPatch<Moiety> patch = PojoDiff.getDiff(old, newMoiety);
        patch.apply(old);

        JsonMatches(newMoiety, old);
    }

    private void JsonMatches(Object expected, Object actual){
        JsonNode js1=mapper.valueToTree(expected);
        JsonNode js2=mapper.valueToTree(actual);
        try{
            assertEquals(js1,js2);
        }catch(Throwable e){
            System.out.println(JsonDiff.asJson(js1, js2));
            throw e;
        }
    }
}