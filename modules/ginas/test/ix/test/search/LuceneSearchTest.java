package ix.test.search;

import static ix.test.SubstanceJsonUtil.ensurePass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import ix.test.builder.SubstanceBuilder;
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
            
			new SubstanceBuilder()
				.addName(theName)
				.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
            
            String html=api.getTextSearchHTML(theName);
            assertRecordCount(html, 1);
        }
   	}
    
    @Test    
   	public void testSearchForWordPresentIn2RecordsNamesShouldReturnBoth() throws Exception {
        //JsonNode entered = parseJsonFile(resource);
        try( RestSession session = ts.newRestSession(ts.getFakeUser1())) {
        	String aspirinCalcium = "ASPIRIN CACLIUM";
        	String aspirin = "ASPIRIN";
            SubstanceAPI api = new SubstanceAPI(session);
            
            
            new SubstanceBuilder()
			.addName(aspirinCalcium)
			.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
            
            new SubstanceBuilder()
			.addName(aspirin)
			.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
            
            String html=api.getTextSearchHTML(aspirin);
            assertRecordCount(html, 2);
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
            
            

            new SubstanceBuilder()
			.addName(aspirinCalcium)
			.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
            
            new SubstanceBuilder()
			.addName(aspirin)
			.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
            
            
            String html=api.getTextSearchHTML(q);
            assertRecordCount(html, 1);
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

            new SubstanceBuilder()
			.addName(aspirinCalcium)
			.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
            
            new SubstanceBuilder()
			.addName(aspirin)
			.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
            
            String html=api.getTextSearchHTML(q);
            assertRecordCount(html, 1);
        }catch(Throwable e){
        	e.printStackTrace();
        	throw e;
        }
   	}
    
    
    @Test   
   	public void testSearchForNameFieldWorks() throws Exception {
        //JsonNode entered = parseJsonFile(resource);
        try( RestSession session = ts.newRestSession(ts.getFakeUser1())) {
        	String aspirin = "ASPIRIN";
        	String q = "root_names_name:\"" + aspirin + "\"";
            SubstanceAPI api = new SubstanceAPI(session);

            new SubstanceBuilder()
			.addName(aspirin)
			.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
            
            String html=api.getTextSearchHTML(q);
            assertRecordCount(html, 1);
        }catch(Throwable e){
        	e.printStackTrace();
        	throw e;
        }
   	}
    
    @Test   
   	public void testSearchForNameInCodeFieldDoesntWork() throws Exception {
        //JsonNode entered = parseJsonFile(resource);
        try( RestSession session = ts.newRestSession(ts.getFakeUser1())) {
        	String aspirin = "ASPIRIN";
        	String q = "root_codes_code:\"" + aspirin + "\"";
            SubstanceAPI api = new SubstanceAPI(session);

            new SubstanceBuilder()
			.addName(aspirin)
			.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
            
            String html=api.getTextSearchHTML(q);
            assertRecordCount(html, -1);
        }catch(Throwable e){
        	e.printStackTrace();
        	throw e;
        }
   	}
    
    @Test   
   	public void testSearchForNameInNameFieldDoesntReturnCodeMatches() throws Exception {
        //JsonNode entered = parseJsonFile(resource);
        try( RestSession session = ts.newRestSession(ts.getFakeUser1())) {
        	String aspirin = "ASPIRIN";
        	String otherName = "GLEEVEC";
        	String q = "root_names_name:\"" + aspirin + "\"";
            SubstanceAPI api = new SubstanceAPI(session);

            new SubstanceBuilder()
			.addName(aspirin)
			.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
            
            new SubstanceBuilder()
			.addName(otherName)
			.addCode("CAS",aspirin)
			.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
            
            assertRecordCount(api.getTextSearchHTML(q), 1);
            assertRecordCount(api.getTextSearchHTML(aspirin), 2);
        }catch(Throwable e){
        	e.printStackTrace();
        	throw e;
        }
   	}
    
    @Test
   	public void testCodeSystemDynamicFieldMatches() throws Exception {
        //JsonNode entered = parseJsonFile(resource);
        try( RestSession session = ts.newRestSession(ts.getFakeUser1())) {
        	String cas = "50-00-0";
        	String defName = "AAA";
        	
        	
        	String qCas = "root_codes_CAS:\"" + cas + "\"";
        	String qCasFake = "root_codes_CASFAKE:\"" + cas + "\"";
        	String qAll = "\"" + cas + "\"";
        	String qCode= "root_codes_code:\"" + cas + "\"";
        	
            SubstanceAPI api = new SubstanceAPI(session);

            new SubstanceBuilder()
			.addName(defName + "A")
			.addCode("CASFAKE",cas)
			.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
            
            new SubstanceBuilder()
			.addName(defName + "B")
			.addCode("CAS",cas)
			.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
            
            new SubstanceBuilder()
			.addName(cas)
			.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
            
            assertRecordCount(api.getTextSearchHTML(qCas), 1);
            assertRecordCount(api.getTextSearchHTML(qCasFake), 1);
            assertRecordCount(api.getTextSearchHTML(qAll), 3);
            assertRecordCount(api.getTextSearchHTML(qCode), 2);
        }catch(Throwable e){
        	e.printStackTrace();
        	throw e;
        }
   	}
    
    
    public static void assertRecordCount(String html, int expected){
    	int rc=getRecordCountFromHtml(html);
        assertEquals("Should have " + expected + " results, but found:" + rc, rc,expected);
    }
    // should be moved to some holder object
    public static int getRecordCountFromHtml(String html){
    	String recStart = "<span id=\"record-count\" class=\"label label-default\">";
    	int io=html.indexOf(recStart);
    	int ei=html.indexOf("<", io + 3);
    	if(ei>0 && io >0){
    		String c=html.substring(io + recStart.length(),ei);
    		try{
    		return Integer.parseInt(c.trim());
    		}catch(Exception e){}
    	}
    	return -1;
    }

    @Test  
   	public void testSearchForQuotedExactPhraseShouldReturnOnlyThatPhraseNotSuperString() throws Exception {
        //JsonNode entered = parseJsonFile(resource);
        try( RestSession session = ts.newRestSession(ts.getFakeUser1())) {
        	String aspirinCalcium = "ASPIRIN CACLIUM";
        	String aspirinCalciumHydrate = "ASPIRIN CACLIUM HYDRATE";
        	String q = "\"^" + aspirinCalcium + "$\"";
            SubstanceAPI api = new SubstanceAPI(session);
            
            new SubstanceBuilder()
			.addName(aspirinCalcium)
			.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
            
            new SubstanceBuilder()
			.addName(aspirinCalciumHydrate)
			.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
            
            
            String html=api.getTextSearchHTML(q);
            assertRecordCount(html, 1);
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
        	
			"ABCDEFGHIJKLMNOPQRSTUVWXYZ".chars().mapToObj(i -> ((char) i) + prefix).forEach(n -> {
				addedName.add(n);
				new SubstanceBuilder()
					.addName(n)
					.buildJsonAnd(j->{
						ensurePass(api.submitSubstance(j));
					});
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
        				 new SubstanceBuilder()
	     					.addName(n)
	     					.buildJsonAnd(j->{
	     						ensurePass(api.submitSubstance(j));
	     					});
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
        	throw e;
        }
   	}
}