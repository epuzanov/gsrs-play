package util.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.junit.Assert.*;
/**
 * Created by katzelda on 2/26/16.
 */
public class ChangesTest {

    @Test
    public void empty(){
        Changes sut = new Changes(new ChangesBuilder().build());
        assertTrue(sut.isEmpty());
        assertFalse(sut.getAllChanges().iterator().hasNext());
    }

    @Test
    public void notEmpty(){
        JsonNode node = new JsonUtil.JsonBuilder().toJson();

        Map<String, Change> map = new ChangesBuilder()
                .change("foo", node, Change.ChangeType.ADDED)
                .build();

        Changes sut = new Changes(map);

        assertFalse(sut.isEmpty());
        assertEquals(map.values(), sut.getAllChanges());
    }

    @Test
    public void multiple(){
        JsonNode node = new JsonUtil.JsonBuilder().toJson();

        Map<String, Change> map = new ChangesBuilder()
                .change("foo", node, Change.ChangeType.ADDED)
                .change("bar", node, Change.ChangeType.ADDED)
                .build();

        Changes sut = new Changes(map);

        assertFalse(sut.isEmpty());
        assertEquals(map.values(), sut.getAllChanges());
    }

    @Test
    public void differentTypes(){
        JsonNode node = new JsonUtil.JsonBuilder().toJson();

        Map<String, Change> map = new ChangesBuilder()
                .change("foo", node, Change.ChangeType.ADDED)
                .change("bar", node, Change.ChangeType.ADDED)
                .change("baz", node, Change.ChangeType.REMOVED)
                .build();

        Changes sut = new Changes(map);

        assertFalse(sut.isEmpty());
        assertEquals(map.values(), sut.getAllChanges());

        List<Change> added = new ArrayList<>();
        List<Change> removed = new ArrayList<>();

        for(Change change : map.values()){
            switch(change.getType()){
                case ADDED: added.add(change); break;
                case REMOVED: removed.add(change); break;
            }
        }

        assertEquals(added, sut.getChangesByType(Change.ChangeType.ADDED));
        assertEquals(removed, sut.getChangesByType(Change.ChangeType.REMOVED));
    }


    private static class ChangesBuilder{
        private Map<String, Change> map = new HashMap<>();

        public ChangesBuilder change(String key, JsonNode node, Change.ChangeType type){
            map.put(key, new Change(key, node, Change.ChangeType.ADDED));

            return this;

        }
        public Map<String, Change> build(){
            //copy map so future changes don't affect already built objs
            return new HashMap<>(map);
        }
    }
}
