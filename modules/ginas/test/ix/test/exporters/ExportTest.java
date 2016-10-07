package ix.test.exporters;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import ix.core.CacheStrategy;
import ix.core.models.Role;
import ix.core.plugins.ConsoleFilterPlugin;
import ix.core.plugins.IxCache;
import ix.core.util.StopWatch;
import ix.ginas.controllers.GinasApp;
import ix.test.AbstractGinasTest;
import ix.test.builder.SubstanceBuilder;
import ix.test.server.BrowserSession;
import ix.test.server.GinasTestServer;
import ix.test.server.GinasTestServer.User;
import ix.test.server.RestSession;
import ix.test.server.SubstanceLoader;
import ix.test.server.SubstanceSearcher;
import ix.test.server.SubstanceSearcher.WebExportRequest;
import ix.test.util.TestUtil;
import play.libs.ws.WSResponse;
import play.mvc.*;


/**
 * Created by katzelda on 9/20/16.
 */
public class ExportTest extends AbstractGinasTest {

	@ClassRule
	public static GinasTestServer ts = new GinasTestServer(new HashMap<String, Object>() {
		{
			put("ix.cache.maxElementsNonEvictable", 10);

		}
	});

	@Before
	public void clearCache() {
		IxCache.clearCache();
		
	}

	static BrowserSession session;
	static SubstanceSearcher searcher;
	static RestSession restSession;

	@BeforeClass
	public static void setup() throws Exception {
		User u=ts.createAdmin("admin4", "password");
		restSession = ts.newRestSession(u);
		session = ts.newBrowserSession(u);
		SubstanceLoader loader = new SubstanceLoader(session);
		File f = new File("test/testdumps/rep90.ginas");
		loader.loadJson(f);
		searcher = new SubstanceSearcher(session);
	}

	@After
	public void tearDown() {
		session.close();
	}

	@Test 
	public void searchAll() throws IOException, InterruptedException {

		SubstanceSearcher.SearchResult searchResult = searcher.all();
		try (InputStream in = searchResult.export("csv");
				BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

			List<String> lines = reader.lines().collect(Collectors.toList());
			
			assertEquals(90, lines.size() - 1); // 1 line of header

			Set<String> uuids = parseUUids(lines);

			assertEquals(searchResult.getUuids(), uuids);
		}

	}
	@Before
	public void setUp() throws Exception {
	    Http.Context.current.set(getMockContext());
	}
	
	
	private Http.Context getMockContext() {
	    Http.Request mockRequest = mock(Http.Request.class);
	    when(mockRequest.remoteAddress()).thenReturn("127.0.0.1");
	    when(mockRequest.getHeader("User-Agent")).thenReturn("mocked user-agent");
	    Http.Context mockContext = mock(Http.Context.class);
	    when(mockContext.request()).thenReturn(mockRequest);

	    return mockContext;
	}
	
	@Test 
	public void exportBogusShouldBeAnError() throws IOException {
		WebExportRequest req = searcher.getExport("csv", "BOGUS");
		
		int status = req.getWSResponse().getStatus();
		assertTrue("Expected failure code, got:" + status, status != 200 && status != 201);
		assertFalse("Json response for deferred URL for bogus key should have isPresent=false",req.getMeta().at("/isPresent").asBoolean());		
	}

	private Set<String> parseUUids(List<String> lines) {
		Set<String> uuids = new HashSet<>(lines.size());
		Iterator<String> iter = lines.iterator();
		// skip header
		iter.next();
		Pattern p = Pattern.compile("\"([0-9a-f\\-]+)\"");
		while (iter.hasNext()) {
			String line = iter.next();
			Matcher m = p.matcher(line);
			if (m.find()) {
				// TODO the urls in the webpages only show the first 8 chars!
				uuids.add(m.group(1).substring(0, 8));
				// System.out.println( m.group(1));
			}
		}
		return uuids;
	}

	@Test 
	public void searchOne() throws IOException {

		SubstanceSearcher.SearchResult searchResult = searcher.query("GUIZOTIA ABYSSINICA (L. F.) CASS.");
		try (InputStream in = searchResult.export("csv");
				BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

			List<String> lines = reader.lines().collect(Collectors.toList());
			assertEquals(1, lines.size() - 1); // 1 line of header

			Set<String> uuids = parseUUids(lines);

			assertEquals(searchResult.getUuids(), uuids);
		}

	}

	@Test 
	public void searchAllWithFullCache() throws Exception {

		searchAll();
		for (int i = 0; i < 1000; i++) {
			IxCache.set(Integer.toString(i), new NonEvictable());
		}
		searchAll();

	}

	@Test 
	public void searchAllWithFullAfterLotsOfSearchesCache() throws Exception {

		searchAll();
		for (int i = 0; i < 100; i++) {
			SubstanceSearcher.SearchResult searchResult = searcher.query(UUID.randomUUID());
			// System.out.println("Key:" + searchResult.getKey());

		}

		searchAll();

	}

	@CacheStrategy(evictable = false)
	public static class NonEvictable implements Serializable {

	}
	
	@Test 
	public void exportOneSubstanceAsRequest() throws IOException {
		
		String givenName="THE NAME";
		String specialKey = "WAIT_SOME_TIME";
		GinasApp.registerSpecialStream(specialKey, ()->{
			return Stream.generate(()->{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Fetching next");
				return new SubstanceBuilder().addName(givenName).build();
			}).limit(1);
		});
		
		try (InputStream in = searcher.getExport("csv", specialKey).getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

			List<String> lines = reader.lines().collect(Collectors.toList());
			assertEquals(1, lines.size() - 1); // 1 line of header
			TestUtil.assertContains(lines.get(1),givenName);
		}
	}
	
	@Test
	public void execute2000VerySlowExportsStillAllowsBasicTraffic() throws Exception {
		int numberSlowExports=2000;
		String givenName="THE NAME";
		String specialKey = "WAITLIKEFOREVER";
		AtomicBoolean stillwait = new AtomicBoolean(true);
		try{
			searchAll();
			GinasApp.registerSpecialStream(specialKey, ()->{
				return Stream.generate(()->{
					try{
						while(stillwait.get()){
							Thread.sleep(1000);
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					//System.out.println("Fetching next");
					return new SubstanceBuilder().addName(givenName).build();
				}).limit(2);
			});
			
			
			
			for(int i=0;i<numberSlowExports;i++){
				new Thread(()->{
					try{
						WebExportRequest wer= searcher.getExport("csv", specialKey);
						//if(wer.isReady()){
							wer.setTimeout(20).getInputStream();
						//}
					}catch(Exception e){} //swallow timeout error
				}).start();
				try {
					//System.out.println("fetching it after :" + i + " attempts");
					long l = StopWatch.timeElapsed(() -> {
						try(RestSession rs=ts.newRestSession(ts.createUser(Role.Query))){
							String sha1 = rs
								.getSha1For("ginas/app/assets/javascripts/JSDraw4.0.7/Scilligence.JSDraw2.Pro.js");
							assertEquals("acc721331eba0826e4133b472c4f349c78566d19", sha1);
						}
						return null;
					});
					//System.out.println("=====>" + l);
				} catch (Exception e) {
					throw e;
				}
			}
			
		}catch(Throwable e){
			e.printStackTrace();
			throw e;
		}finally{
			stillwait.set(false);
		}
	}

}
