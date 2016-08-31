package ix.test.search;

import static ix.test.SubstanceJsonUtil.ensurePass;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.fasterxml.jackson.databind.JsonNode;

import ix.core.controllers.EntityFactory;
import ix.ginas.models.v1.Substance;
import ix.test.SubstanceBuilder;
import ix.test.ix.test.server.GinasTestServer;
import ix.test.ix.test.server.RestSession;
import ix.test.ix.test.server.SubstanceAPI;
import ix.test.util.TestNamePrinter;

public class LuceneSearchTest {
	
    @Rule
    public TestNamePrinter printer = new TestNamePrinter();

    @Rule
    public GinasTestServer ts = new GinasTestServer(9001);
	
    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            System.out.println("Starting test: " + description.getMethodName());
        }
    };
    

    
    @Test  
   	public void testTwoWordLuceneNameSearchShouldReturn() throws Exception {
        //JsonNode entered = parseJsonFile(resource);
        try( RestSession session = ts.newRestSession(ts.getFakeUser1())) {
        	String theName = "ASPIRIN CACLIUM";
            SubstanceAPI api = new SubstanceAPI(session);
            Substance s= ((new SubstanceBuilder())
            		.withName(theName)
            		.withDefaultReference().build());
            JsonNode sjson = EntityFactory.EntityMapper.FULL_ENTITY_MAPPER().valueToTree(s);
            ensurePass( api.submitSubstance(sjson));
            
            String html=api.getTextSearchHTML(theName);
            assertTrue("Should have 1 result for simple name '" + theName + "' search, but couldn't find any",html.contains("<span id=\"record-count\" class=\"label label-default\">1</span>"));
        }
   	}
    
    @Test  
   	public void testSearchForWordPresentIn2RecordsNamesShouldReturnBoth() throws Exception {
        //JsonNode entered = parseJsonFile(resource);
        try( RestSession session = ts.newRestSession(ts.getFakeUser1())) {
        	String aspirinCalcium = "ASPIRIN CACLIUM";
        	String aspirin = "ASPIRIN";
            SubstanceAPI api = new SubstanceAPI(session);
            ensurePass(api.submitSubstance(
					EntityFactory.EntityMapper.FULL_ENTITY_MAPPER().valueToTree(
							((new SubstanceBuilder())
									.withName(aspirinCalcium)
									.withDefaultReference().build()))));
            ensurePass(api.submitSubstance(
					EntityFactory.EntityMapper.FULL_ENTITY_MAPPER().valueToTree(
							((new SubstanceBuilder())
									.withName(aspirin)
									.withDefaultReference().build()))));
            
            String html=api.getTextSearchHTML(aspirin);
            assertTrue("Should have 2 result for simple name '" + aspirin + "' search, but couldn't find any",html.contains("<span id=\"record-count\" class=\"label label-default\">2</span>"));
        }
   	}
    
    @Test 
   	public void testExactSearchForWordPresentIn2RecordsNamesShouldReturnOnlyExact() throws Exception {
        //JsonNode entered = parseJsonFile(resource);
        try( RestSession session = ts.newRestSession(ts.getFakeUser1())) {
        	String aspirinCalcium = "ASPIRIN CACLIUM";
        	String aspirin = "ASPIRIN";
        	String q = "\"^" + aspirin + "$\"";
            SubstanceAPI api = new SubstanceAPI(session);
            
            ensurePass(api.submitSubstance(
					EntityFactory.EntityMapper.FULL_ENTITY_MAPPER().valueToTree(
							((new SubstanceBuilder())
									.withName(aspirinCalcium)
									.withDefaultReference().build()))));
            ensurePass(api.submitSubstance(
					EntityFactory.EntityMapper.FULL_ENTITY_MAPPER().valueToTree(
							((new SubstanceBuilder())
									.withName(aspirin)
									.withDefaultReference().build()))));
            
            String html=api.getTextSearchHTML(q);
            assertTrue("Should have 1 result for simple name '" + q + "' search, but couldn't find any",html.contains("<span id=\"record-count\" class=\"label label-default\">1</span>"));
        }catch(Throwable e){
        	e.printStackTrace();
        	throw e;
        }
   	}
    
    @Test 
   	public void testSearchForQuotedPhraseShouldReturnOnlyRecordWithThatOrder() throws Exception {
        //JsonNode entered = parseJsonFile(resource);
        try( RestSession session = ts.newRestSession(ts.getFakeUser1())) {
        	String aspirinCalcium = "ASPIRIN CACLIUM";
        	String aspirin = "CALCIUM ASPIRIN";
        	String q = "\"" + aspirin + "\"";
            SubstanceAPI api = new SubstanceAPI(session);
            
            ensurePass(api.submitSubstance(
					EntityFactory.EntityMapper.FULL_ENTITY_MAPPER().valueToTree(
							((new SubstanceBuilder())
									.withName(aspirinCalcium)
									.withDefaultReference().build()))));
            ensurePass(api.submitSubstance(
					EntityFactory.EntityMapper.FULL_ENTITY_MAPPER().valueToTree(
							((new SubstanceBuilder())
									.withName(aspirin)
									.withDefaultReference().build()))));
            
            String html=api.getTextSearchHTML(q);
            assertTrue("Should have 1 result for simple name '" + q + "' search, but couldn't find any",html.contains("<span id=\"record-count\" class=\"label label-default\">1</span>"));
        }catch(Throwable e){
        	e.printStackTrace();
        	throw e;
        }
   	}
    
    @Test 
   	public void testDefaultBrowseOrderShouldShowMostRecentlyEdittedFirst() throws Exception {
        //JsonNode entered = parseJsonFile(resource);
        try( RestSession session = ts.newRestSession(ts.getFakeUser1())) {
        	SubstanceAPI api = new SubstanceAPI(session);
        	final String prefix="MYSPECIALSUFFIX";
        	List<String> addedName = new ArrayList<String>();
        	
        	"ABCDEFGHIJKLMNOPQRSTUVWXYZ".chars()
        			 .mapToObj(i->((char)i)+prefix)
        			 .forEach(n->{
        				 addedName.add(n);
        				 ensurePass(api.submitSubstance(
        							EntityFactory.EntityMapper.FULL_ENTITY_MAPPER().valueToTree(
        									((new SubstanceBuilder())
        											.withName(n)
        											.withDefaultReference().build()))));
        			 });
            
            String html=api.fetchSubstancesUIBrowseHTML();
            assertFalse("First page shouldn't show oldest record by default. But found:" + addedName.get(0),html.contains(addedName.get(0)));
            int rows=16;
            Collections.reverse(addedName);
            addedName.stream().limit(rows).forEachOrdered(n->
            			assertTrue("First page should show newest 16 records by default.",html.contains(n))
            		);
            addedName.stream().skip(rows).forEachOrdered(n->
            	assertFalse("First page shouldn't show oldest records by default.",html.contains(n))
        	);
            
        }catch(Throwable e){
        	e.printStackTrace();
        	throw e;
        }
   	}
    
    @Test 
   	public void testBrowsingWithDisplayNameOrderingShouldOrderAlphabetically() throws Exception {
        //JsonNode entered = parseJsonFile(resource);
        try( RestSession session = ts.newRestSession(ts.getFakeUser1())) {
        	SubstanceAPI api = new SubstanceAPI(session);
        	final String prefix="MYSPECIALSUFFIX";
        	List<String> addedName = new ArrayList<String>();
        	
        	"ABCDEFGHIJKLMNOPQRSTUVWXYZ".chars()
        			 .mapToObj(i->((char)i)+prefix)
        			 .forEach(n->{
        				 addedName.add(n);
        				 ensurePass(api.submitSubstance(
        							EntityFactory.EntityMapper.FULL_ENTITY_MAPPER().valueToTree(
        									((new SubstanceBuilder())
        											.withName(n)
        											.withDefaultReference().build()))));
        			 });
            
        	
            String html=api.fetchSubstancesUISearchHTML(null,null,"^Display Name");
            String rhtml=api.fetchSubstancesUISearchHTML(null,null,"$Display Name");
            int rows=16;
            
            //Collections.reverse(addedName);
            addedName.stream().limit(rows).forEachOrdered(n->
            	assertTrue("Sorting alphabetical should show:" + n ,html.contains(n))
            	);
            addedName.stream().skip(rows).forEachOrdered(n->
            	assertFalse("Sorting alphabetical shouldn't show:" + n ,html.contains(n))
            	);
            
            Collections.reverse(addedName);
			addedName.stream().limit(16)
					.forEachOrdered(n -> assertTrue("Sorting rev alphabetical should show:" + n, rhtml.contains(n)));
			addedName.stream().skip(16)
					.forEachOrdered(n -> assertFalse("Sorting rev alphabetical shouldn't show:" + n, rhtml.contains(n)));
            
        }catch(Throwable e){
        	//e.printStackTrace();
        	throw e;
        }
   	}
    
    
    
}
