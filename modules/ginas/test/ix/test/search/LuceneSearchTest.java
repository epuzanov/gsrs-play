package ix.test.search;

import static ix.test.SubstanceJsonUtil.ensurePass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import ix.AbstractGinasServerTest;
import ix.core.util.ExpectFailureChecker.ExpectedToFail;
import ix.ginas.models.v1.Substance;
import ix.test.builder.SubstanceBuilder;
import ix.test.query.builder.SimpleQueryBuilder;
import ix.test.query.builder.SubstanceCondition;
import ix.test.server.BrowserSession;
import ix.test.server.GinasTestServer.User;
import ix.test.server.RestSession;
import ix.test.server.SearchResult;
import ix.test.server.SubstanceAPI;
import ix.test.server.SubstanceReIndexer;
import ix.test.server.SubstanceSearcher;

public class LuceneSearchTest extends AbstractGinasServerTest {

	RestSession session;
	BrowserSession browserSession;
	User u;
	SubstanceAPI api;
	SubstanceSearcher searcher;

	@Before
	public void setup() {
		u = ts.getFakeUser1();
		session = ts.newRestSession(u);
		browserSession = ts.newBrowserSession(u);
		api = new SubstanceAPI(session);
		searcher= new SubstanceSearcher(browserSession);
	}
	
	@After
	public void tearDown() {
		try{
			session.close();
		}catch(Throwable t){
			t.printStackTrace();
		}
		try{
			browserSession.close();
		}catch(Throwable t){
			t.printStackTrace();
		}
	}
	
	public void reindex() throws IOException{
		try (BrowserSession browserSession = ts.newBrowserSession(ts.createAdmin("adminguy", "admin"))) {
			new SubstanceReIndexer(browserSession).reindex();
		}
	}

	@Test
	public void testTwoWordLuceneNameSearchShouldReturn() throws Exception {
			String theName = "ASPIRIN CACLIUM";
			new SubstanceBuilder()
				.addName(theName)
				.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
			String html = api.getTextSearchHTML(theName);
			assertRecordCount(html, 1);
	}

	@Test
	public void testSearchForWordPresentIn2RecordsNamesShouldReturnBoth() throws Exception {
		
			String aspirinCalcium = "ASPIRIN CACLIUM";
			String aspirin = "ASPIRIN";

			new SubstanceBuilder().addName(aspirinCalcium).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			new SubstanceBuilder().addName(aspirin).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			String html = api.getTextSearchHTML(aspirin);
			assertRecordCount(html, 2);
	}

	@Test
	public void testExactSearchForWordPresentIn2RecordsNamesShouldReturnOnlyExact() throws Exception {
			String aspirinCalcium = "ASPIRIN CACLIUM";
			String aspirin = "ASPIRIN";
			String q = new SimpleQueryBuilder().where().globalMatchesExact(aspirin).build();

			new SubstanceBuilder().addName(aspirinCalcium).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			new SubstanceBuilder().addName(aspirin).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			String html = api.getTextSearchHTML(q);
			assertRecordCount(html, 1);
			SearchResult r = searcher.exactSearch(aspirin);
			assertEquals(1, r.getUuids().size());
	}

	@Test
	public void exactNormalNameSearchWhenlevosIndexedTooShouldNotReturnLevo() throws Exception {
			String ibuprofen = "IBUPROFEN";
			
			new SubstanceBuilder().addName(ibuprofen).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			new SubstanceBuilder().addName("(-)-" + ibuprofen).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			
				SearchResult r = searcher.exactSearch(ibuprofen);
				assertEquals(1, r.getUuids().size());

				ts.doAsUser(u, () -> {
					Substance s = r.getSubstances().findFirst().get();

					assertEquals(ibuprofen, s.getName());
				});
	}

	@Test
	public void prefixSearch() throws Exception {

			String prefix = "fooLong";

			new SubstanceBuilder().addName(prefix + "bar").buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			new SubstanceBuilder().addName(prefix + "baz").buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			new SubstanceBuilder().addName("notA" + prefix).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			
				SearchResult r = searcher.nameSearch(prefix);
				// assertEquals(3, r.getUuids().size());
				System.out.println("===========");
				SearchResult r2 = searcher.nameRawSearch(prefix + "*");
				assertEquals(2, r2.getUuids().size());

				ts.doAsUser(u, () -> {

					long count = r2.getSubstances().map(Substance::getName).filter(n -> n.startsWith(prefix)).count();

					assertEquals(2, count);
				});
			
	}

	@Test
	public void exactLevoNameSearchWhenLevosIndexedTooShouldOnlyReturnLevo() throws Exception {
		
			String ibuprofen = "IBUPROFEN";
			String levo = "(-)-" + ibuprofen;


			new SubstanceBuilder().addName(ibuprofen).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			new SubstanceBuilder().addName(levo).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			new SubstanceBuilder().addName("(+)-" + ibuprofen).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			
				SearchResult r = searcher.exactSearch(levo);
				assertEquals(1, r.getUuids().size());

				ts.doAsUser(u, () -> {
					Substance s = r.getSubstances().findFirst().get();

					assertEquals(levo, s.getName());
				});
			

	}

	@Test
	public void exactDextroNameSearchWhenLevosIndexedTooShouldOnlyReturnDextro() throws Exception {
			String ibuprofen = "IBUPROFEN";
			String levo = "(-)-" + ibuprofen;
			String dextro = "(+)-" + ibuprofen;

			new SubstanceBuilder().addName(ibuprofen).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			new SubstanceBuilder().addName(levo).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			new SubstanceBuilder().addName(dextro).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			
				SearchResult r = searcher.exactSearch(dextro);
				assertEquals(1, r.getUuids().size());

				ts.doAsUser(u, () -> {
					Substance s = r.getSubstances().findFirst().get();

					assertEquals(dextro, s.getName());
				});
	}

	@Test
	public void ensureSuggestFieldWorks() throws Exception {
			String pre = "IBUP";
			String ib2 = "IBUPROFEN";

			new SubstanceBuilder().addName(ib2).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			JsonNode suggest = api.getSuggestPrefixJson(pre);
			assertEquals(1, suggest.at("/Name").size());
			assertEquals(ib2, suggest.at("/Name/0/key").asText());

	}

	@Test
	@ExpectedToFail
	@Ignore
	public void ensureSuggestFieldDisappearsAfterNameRemoved() throws Exception {
	
			String pre = "IBUP";
			String ib2 = "IBUPROFEN";
			String name2 = "ASPIRIN";

			JsonNode submit = new SubstanceBuilder().addName(ib2).generateNewUUID().buildJson();
			ensurePass(api.submitSubstance(submit));

			JsonNode suggestBefore = api.getSuggestPrefixJson(pre);
			assertEquals(1, suggestBefore.at("/Name").size());
			assertEquals(ib2, suggestBefore.at("/Name/0/key").asText());

			JsonNode update = SubstanceBuilder.from(api.fetchSubstanceJsonByUuid(submit.at("/uuid").asText()))
					.andThenMutate(s -> s.names.get(0).name = name2)
					.buildJson();

			ensurePass(api.updateSubstance(update));

			JsonNode suggestLater = api.getSuggestPrefixJson(pre);
			assertTrue(suggestLater.at("/Name").isMissingNode());

	}

	@Test
	public void ensureSuggestFieldDisappearsAfterNameRemovedAndReindexed() throws Exception {
			String pre1 = "IBUP";
			String pre2 = "ASP";
			String ib2 = "IBUPROFEN";
			String name2 = "ASPIRIN";

			JsonNode submit = new SubstanceBuilder().addName(ib2).generateNewUUID().buildJson();
			ensurePass(api.submitSubstance(submit));

			JsonNode suggestBefore = api.getSuggestPrefixJson(pre1);
			assertEquals(1, suggestBefore.at("/Name").size());
			assertEquals(ib2, suggestBefore.at("/Name/0/key").asText());

			JsonNode update = SubstanceBuilder.from(api.fetchSubstanceJsonByUuid(submit.at("/uuid").asText()))
					.andThenMutate(s -> s.names.get(0).name = name2)
					.buildJson();

			ensurePass(api.updateSubstance(update));

			
			reindex();
			

			assertTrue(api.getSuggestPrefixJson(pre1).at("/Name").isMissingNode());

			JsonNode suggestLater = api.getSuggestPrefixJson(pre2);
			assertEquals(1, suggestLater.at("/Name").size());
			assertEquals(name2, suggestLater.at("/Name/0/key").asText());
	}

	@Test
	public void ensureSuggestFieldDisappearsAfterNameRemovedAndNewSubstanceAddedAndReindexed() throws Exception {
			String pre1 = "IBUP";
			String pre2 = "ASP";
			String ib2 = "IBUPROFEN";
			String name2 = "ASPIRIN";

			JsonNode submit = new SubstanceBuilder().addName(ib2).generateNewUUID().buildJson();
			ensurePass(api.submitSubstance(submit));

			JsonNode suggestBefore = api.getSuggestPrefixJson(pre1);
			assertEquals(1, suggestBefore.at("/Name").size());
			assertEquals(ib2, suggestBefore.at("/Name/0/key").asText());

			JsonNode update = SubstanceBuilder.from(api.fetchSubstanceJsonByUuid(submit.at("/uuid").asText()))
					.andThenMutate(s -> s.names.get(0).name = name2)
					.buildJson();

			ensurePass(api.updateSubstance(update));

			new SubstanceBuilder().addName("Just another name")
									.generateNewUUID()
									.buildJsonAnd(s -> ensurePass(api.submitSubstance(s)));

			
			
			reindex();

			assertTrue(api.getSuggestPrefixJson(pre1).at("/Name").isMissingNode());

			JsonNode suggestLater = api.getSuggestPrefixJson(pre2);

			SearchResult r = searcher.nameSearch(name2);
			assertEquals("Name search should return 1 result", 1, r.getUuids().size());
			
			assertEquals(1, suggestLater.at("/Name").size());
			assertEquals(name2, suggestLater.at("/Name/0/key").asText());

	}

	@Test
	public void ensureUpdating2RecordsAndReindexingResultsIn2SubstancesInSearch() throws Exception {
		
			List<String> toSearch = new ArrayList<>();
			for (int i = 0; i < 2; i++) {
				String name = "ABC" + i;
				toSearch.add(name);
				JsonNode submit = new SubstanceBuilder().addName(name).generateNewUUID().buildJson();
				ensurePass(api.submitSubstance(submit));
				SubstanceBuilder.from(api.fetchSubstanceJsonByUuid(submit.at("/uuid").asText()))
						.andThenMutate(s -> s.names.get(0).name = name + " changed")
						.buildJsonAnd(s -> ensurePass(api.updateSubstance(s)));
			}

			
				for (String search : toSearch) {
					SearchResult r = searcher.nameSearch(search);
					assertEquals("Pre-reindex Name search for " + search + " should return 1 result", 1,
							r.getUuids().size());
				}

				reindex();

				for (String search : toSearch) {
					SearchResult r = searcher.nameSearch(search);
					assertEquals("Post-reindex Name search for " + search + " should return 1 result", 1,
							r.getUuids().size());
				}

	}

	@Test
	public void ensureUpdatingARecordThreeTimesIsStillSearchable() throws Exception {
		
			Consumer<String> searchFor = (s) -> {
				try{
					SearchResult r = searcher.nameSearch(s);
					assertEquals("Search for " + s + " should return 1 result", 1, r.getUuids().size());
				}catch(Exception e){
					throw new IllegalStateException(e);
				}
			};

			JsonNode submit = new SubstanceBuilder().addName("START1").generateNewUUID().buildJson();
			ensurePass(api.submitSubstance(submit));

			SubstanceBuilder.from(api.fetchSubstanceJsonByUuid(submit.at("/uuid").asText()))
					.andThenMutate(s -> s.names.get(0).name = "START2").buildJsonAnd(s -> {
						ensurePass(api.updateSubstance(s));
						searchFor.accept("START2");
					});

			SubstanceBuilder.from(api.fetchSubstanceJsonByUuid(submit.at("/uuid").asText()))
					.andThenMutate(s -> s.names.get(0).name = "START3").buildJsonAnd(s -> {
						ensurePass(api.updateSubstance(s));
						searchFor.accept("START3");
					});

			SubstanceBuilder.from(api.fetchSubstanceJsonByUuid(submit.at("/uuid").asText()))
					.andThenMutate(s -> s.names.get(0).name = "START4").buildJsonAnd(s -> {
						ensurePass(api.updateSubstance(s));
						searchFor.accept("START4");
					});

	}

	@Test
	public void normalNameSearchWhenlevosAndDextrosIndexedTooShouldOnlyReturnAll3() throws Exception {
		
			String ibuprofen = "IBUPROFEN";
			String levo = "(-)-" + ibuprofen;
			String dextro = "(+)-" + ibuprofen;

			new SubstanceBuilder().addName(ibuprofen).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			new SubstanceBuilder().addName(levo).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			new SubstanceBuilder().addName(dextro).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
				SearchResult r = searcher.nameSearch(ibuprofen);
				assertEquals(3, r.getUuids().size());

				ts.doAsUser(u, () -> {
					Set<String> actual = r.getSubstances().map(s -> s.getName()).collect(Collectors.toSet());

					Set<String> expected = setOf(ibuprofen, levo, dextro);
					assertEquals(expected, actual);
				});
	}

	private Set<String> setOf(String... values) {
		Set<String> set = new HashSet<>(values.length);
		for (String v : values) {
			set.add(v);
		}
		return set;
	}

	@Test
	public void testSearchForQuotedPhraseShouldReturnOnlyRecordWithThatOrder() throws Exception {
		
			String aspirinCalcium = "ASPIRIN CACLIUM";
			String aspirin = "CALCIUM ASPIRIN";
			
			
			String q = new SimpleQueryBuilder()
						.where()
						.condition(SubstanceCondition.name(aspirin).phrase())
						.build();

			new SubstanceBuilder().addName(aspirinCalcium).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			new SubstanceBuilder().addName(aspirin).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			String html = api.getTextSearchHTML(q);
			assertRecordCount(html, 1);
	}

	@Test
	public void testSearchForNameFieldWorks() throws Exception {
			String aspirin = "ASPIRIN";
			String q = new SimpleQueryBuilder().where().condition(SubstanceCondition.name(aspirin).phrase()).build();


			new SubstanceBuilder().addName(aspirin).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			String html = api.getTextSearchHTML(q);
			assertRecordCount(html, 1);
				SearchResult r = searcher.nameSearch(aspirin);
				assertEquals(1, r.getUuids().size());
			
	}

	@Test
	public void testSearchForNameInCodeFieldDoesntWork() throws Exception {
		
			String aspirin = "ASPIRIN";
			String q = new SimpleQueryBuilder().where().condition(SubstanceCondition.code(aspirin).phrase()).build();
		
			new SubstanceBuilder().addName(aspirin).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			String html = api.getTextSearchHTML(q);
			assertRecordCount(html, -1);
	}

	@Test
	public void testSearchForNameInNameFieldDoesntReturnCodeMatches() throws Exception {
			String aspirin = "ASPIRIN";
			String otherName = "GLEEVEC";
			String q = new SimpleQueryBuilder().where().condition(SubstanceCondition.name(aspirin).phrase()).build();
		
			new SubstanceBuilder().addName(aspirin).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			new SubstanceBuilder().addName(otherName).addCode("CAS", aspirin)
					.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			assertRecordCount(api.getTextSearchHTML(q), 1);
			assertRecordCount(api.getTextSearchHTML(aspirin), 2);
	}

	@Test
	public void testCodeSystemDynamicFieldMatches() throws Exception {
			String cas = "50-00-0";
			String defName = "AAA";

			String qCas =     new SimpleQueryBuilder().where().fieldMatchesPhrase("root_codes_CAS", cas).build();
			String qCasFake = new SimpleQueryBuilder().where().fieldMatchesPhrase("root_codes_CASFAKE", cas).build();
			String qCode =    new SimpleQueryBuilder().where().fieldMatchesPhrase("root_codes_code", cas).build();
			String qAll =     new SimpleQueryBuilder().where().globalMatchesPhrase(cas).build();
			
			SubstanceAPI api = new SubstanceAPI(session);

			new SubstanceBuilder().addName(defName + "A").addCode("CASFAKE", cas)
					.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			new SubstanceBuilder().addName(defName + "B").addCode("CAS", cas)
					.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			new SubstanceBuilder().addName(cas).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			assertRecordCount(api.getTextSearchHTML(qCas), 1);
			assertRecordCount(api.getTextSearchHTML(qCasFake), 1);
			assertRecordCount(api.getTextSearchHTML(qAll), 3);
			assertRecordCount(api.getTextSearchHTML(qCode), 2);
		
	}

	public static void assertRecordCount(String html, int expected) {
		int rc = getRecordCountFromHtml(html);
		assertEquals("Should have " + expected + " results, but found:" + rc, rc, expected);
	}

	// should be moved to some holder object
	public static int getRecordCountFromHtml(String html) {
		String recStart = "<span id=\"record-count\" class=\"label label-default\">";
		int io = html.indexOf(recStart);
		int ei = html.indexOf("<", io + 3);
		if (ei > 0 && io > 0) {
			String c = html.substring(io + recStart.length(), ei);
			try {
				return Integer.parseInt(c.trim());
			} catch (Exception e) {
			}
		}
		return -1;
	}

	@Test
	public void testSearchForQuotedExactPhraseShouldReturnOnlyThatPhraseNotSuperString() throws Exception {
			String aspirinCalcium = "ASPIRIN CACLIUM";
			String aspirinCalciumHydrate = "ASPIRIN CACLIUM HYDRATE";

			String q = new SimpleQueryBuilder().where().globalMatchesExact(aspirinCalcium).build();

			
			new SubstanceBuilder().addName(aspirinCalcium).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			new SubstanceBuilder().addName(aspirinCalciumHydrate).buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			String html = api.getTextSearchHTML(q);
			assertRecordCount(html, 1);
	}
	
	@Test
	public void andNameSearchShouldReturnRecordsMatchingBothButNotIndependently() throws Exception {
			String name1 = "ASPIRIN";
			String name2 = "IBUPROFEN";

			String q = new SimpleQueryBuilder().where()
						.condition(SubstanceCondition.name(name1).phrase())
						.and()
						.condition(SubstanceCondition.name(name2).phrase())
						.build();

			AtomicInteger ai = new AtomicInteger(0);
			
			Set<String> uuids = new HashSet<>();
			
			new SubstanceBuilder()
					.addName(name1 + " " + ai.incrementAndGet())
					.addName(name2 + " " + ai.incrementAndGet())
					.generateNewUUID()
					.andThenMutate(s->uuids.add(s.getUuid().toString().split("-")[0]))
					.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
			
			
			new SubstanceBuilder()
					.addName(name1 + " " + ai.incrementAndGet())
					.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
			
			new SubstanceBuilder()
					.addName(name2 + " " + ai.incrementAndGet())
					.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			assertEquals(uuids,searcher.query(q).getUuids());
	}
	
	@Test
	public void orNameSearchShouldReturnAllRecordsMatchingEither() throws Exception {
			String name1 = "ASPIRIN";
			String name2 = "IBUPROFEN";

			String q = new SimpleQueryBuilder().where()
						.condition(SubstanceCondition.name(name1).phrase())
						.or()
						.condition(SubstanceCondition.name(name2).phrase())
						.build();

			AtomicInteger ai = new AtomicInteger(0);
			
			Set<String> uuids = new HashSet<>();
			
			new SubstanceBuilder()
					.addName(name1 + " " + ai.incrementAndGet())
					.addName(name2 + " " + ai.incrementAndGet())
					.generateNewUUID()
					.andThenMutate(s->uuids.add(s.getUuid().toString().split("-")[0]))
					.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
			
			
			new SubstanceBuilder()
					.addName(name1 + " " + ai.incrementAndGet())
					.generateNewUUID()
					.andThenMutate(s->uuids.add(s.getUuid().toString().split("-")[0]))
					.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));
			
			new SubstanceBuilder()
					.addName(name2 + " " + ai.incrementAndGet())
					.generateNewUUID()
					.andThenMutate(s->uuids.add(s.getUuid().toString().split("-")[0]))
					.buildJsonAnd(j -> ensurePass(api.submitSubstance(j)));

			assertEquals(uuids,searcher.query(q).getUuids());
	}

	@Test
	public void testDefaultBrowseOrderShouldShowMostRecentlyEdittedFirst() throws Exception {
			final String prefix = "MYSPECIALSUFFIX";
			List<String> addedName = new ArrayList<String>();

			"ABCDEFGHIJKLMNOPQRSTUVWXYZ".chars().mapToObj(i -> ((char) i) + prefix).forEach(n -> {
				addedName.add(n);
				new SubstanceBuilder().addName(n).buildJsonAnd(j -> {
					ensurePass(api.submitSubstance(j));
				});
			});

			String html = api.fetchSubstancesUIBrowseHTML();
			assertFalse("First page shouldn't show oldest record by default. But found:" + addedName.get(0),
					html.contains(addedName.get(0)));
			int rows = 16;
			Collections.reverse(addedName);
			addedName.stream().limit(rows).forEachOrdered(
					n -> assertTrue("First page should show newest 16 records by default.", html.contains(n)));
			addedName.stream().skip(rows).forEachOrdered(
					n -> assertFalse("First page shouldn't show oldest records by default.", html.contains(n)));

	}

	@Test
	public void testBrowsingWithDisplayNameOrderingShouldOrderAlphabetically() throws Exception {
		
			final String prefix = "MYSPECIALSUFFIX";
			List<String> addedName = new ArrayList<String>();

			"ABCDEFGHIJKLMNOPQRSTUVWXYZ".chars().mapToObj(i -> ((char) i) + prefix).forEach(n -> {
				addedName.add(n);
				new SubstanceBuilder().addName(n).buildJsonAnd(j -> {
					ensurePass(api.submitSubstance(j));
				});
			});

			String html = api.fetchSubstancesUISearchHTML(null, null, "^Display Name");
			String rhtml = api.fetchSubstancesUISearchHTML(null, null, "$Display Name");
			int rows = 16;

			// System.out.println(html);
			// Collections.reverse(addedName);
			addedName.stream().limit(rows)
					.forEachOrdered(n -> assertTrue("Sorting alphabetical should show:" + n, html.contains(n)));
			addedName.stream().skip(rows)
					.forEachOrdered(n -> assertFalse("Sorting alphabetical shouldn't show:" + n, html.contains(n)));

			Collections.reverse(addedName);
			addedName.stream().limit(16)
					.forEachOrdered(n -> assertTrue("Sorting rev alphabetical should show:" + n, rhtml.contains(n)));
			addedName.stream().skip(16).forEachOrdered(
					n -> assertFalse("Sorting rev alphabetical shouldn't show:" + n, rhtml.contains(n)));

	}
}
