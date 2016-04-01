package ix.test.ix.seqaln;

import ix.seqaln.SequenceIndexer;
import net.sf.ehcache.CacheManager;
import org.h2.schema.Sequence;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by katzelda on 3/30/16.
 */
public class SequenceIndexerTest {


    private static final double DELTA = 0.001D;

    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    private SequenceIndexer indexer;

    @Before
    public void setUp() throws IOException{
        clearCache();
        SequenceIndexer.init();

        indexer = SequenceIndexer.open(tmpDir.newFolder());
    }

    @After
    public void tearDown(){
        indexer.shutdown();
        clearCache();
    }

    private void clearCache(){
        CacheManager.getInstance().clearAll();
    }


    @Test
    public void noRecordsInIndex(){
        SequenceIndexer.ResultEnumeration results = indexer.search("ATCGATCG");
        assertFalse(results.hasMoreElements());
    }

    @Test
    public void lowIdentityShouldNotHaveHit() throws IOException {
        String seq = "ACGTTTGCCG";
        String rev  = "TGCAAACGGA"; // rev comp
        indexer.add("foo", seq);

        SequenceIndexer.ResultEnumeration results = indexer.search(rev, .9);
        assertFalse(results.hasMoreElements());
    }

    @Test
    public void oneRecord100Identity() throws IOException{
        String seq = "ACGTTTGC";

        indexer.add("foo", seq);

        SequenceIndexer.ResultEnumeration results = indexer.search(seq);
        assertTrue(results.hasMoreElements());
        SequenceIndexer.Result result = results.nextElement();

        assertEquals("foo", result.id);
        assertEquals(seq, result.query);
        assertEquals(seq, result.target);
        List<SequenceIndexer.Alignment> alignments = result.alignments;

        assertEquals(1, alignments.size());

        SequenceIndexer.Alignment alignment = alignments.get(0);

        assertEquals( 1, alignment.iden, DELTA);

        SequenceIndexer.SEG expected = new  SequenceIndexer.SEG(0,seq.length(), 0, seq.length());
        assertEquals(expected, alignment.segment);

        assertFalse(results.hasMoreElements());
    }

    @Test
    public void twoRecords100Identity() throws IOException{
        String seq = "ACGTTTGC";

        indexer.add("bar", "GGGGGGGGGGGGG");
        indexer.add("foo", seq);

        SequenceIndexer.ResultEnumeration results = indexer.search(seq);
        assertTrue(results.hasMoreElements());
        SequenceIndexer.Result result = results.nextElement();

        assertEquals("foo", result.id);
        assertEquals(seq, result.query);
        assertEquals(seq, result.target);
        List<SequenceIndexer.Alignment> alignments = result.alignments;

        assertEquals(1, alignments.size());

        assert100PercentIdentity(alignments.get(0), seq);

        assertFalse(results.hasMoreElements());
    }

    private void assert100PercentIdentity(SequenceIndexer.Alignment alignment, String seq){
        assertEquals( 1, alignment.iden, DELTA);


        SequenceIndexer.SEG expected = new  SequenceIndexer.SEG(0,seq.length(), 0, seq.length());
        assertEquals(expected, alignment.segment);
    }

    @Test
    public void repeatShouldHaveMultipleHits() throws IOException{

        String seq =  "ACGTACGT";
        indexer.add("foo",seq);

        SequenceIndexer.ResultEnumeration results = indexer.search(seq, .5);


        assertTrue(results.hasMoreElements());

        SequenceIndexer.Result result = results.nextElement();
        assertEquals("foo", result.id);
        assertEquals(seq, result.query);
        assertEquals(seq, result.target);
        //50 % identity should report multiple hits to the same sequence?
        //full length hit
        //and 2 partial hits one for each part of the repeat?

        //not sure if the order is guarenteed so we will make a set
        Set<SequenceIndexer.SEG> actual = getSegmentsFrom(result);

        Set<SequenceIndexer.SEG> expected = new HashSet<>();

        expected.add(new  SequenceIndexer.SEG(0,8,0,8));
        expected.add(new  SequenceIndexer.SEG(0,4,4,8));
        expected.add(new  SequenceIndexer.SEG(4,8,0,4));

       assertEquals(expected,actual);

        assertFalse(results.hasMoreElements());

    }

    private Set<SequenceIndexer.SEG> getSegmentsFrom( SequenceIndexer.Result result) {
        Set<SequenceIndexer.SEG> set = new HashSet<>();
        for(SequenceIndexer.Alignment alignment : result.alignments){
            set.add(alignment.segment);
        }
        return set;
    }
}
