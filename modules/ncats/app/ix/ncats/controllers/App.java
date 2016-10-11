package ix.ncats.controllers;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.DatabaseMetaData;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.regex.Matcher;

import ix.core.util.Java8Util;
import ix.utils.UUIDUtil;
import play.Play;
import play.db.DB;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Call;
import play.mvc.BodyParser;
import play.libs.ws.*;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Http.Request;
import akka.actor.ActorSystem;
import akka.actor.UntypedActorFactory;
import akka.actor.PoisonPill;
import akka.actor.Inbox;
import akka.routing.Broadcast;
import akka.routing.RouterConfig;
import akka.routing.FromConfig;
import akka.routing.RoundRobinRouter;
import akka.routing.SmallestMailboxRouter;
import be.objectify.deadbolt.java.actions.Dynamic;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.sql.Connection;

import ix.seqaln.SequenceIndexer;
import ix.seqaln.SequenceIndexer.CutoffType;
import ix.utils.CallableUtil.TypedCallable;
import ix.utils.Global;
import ix.utils.Util;
import tripod.chem.indexer.StructureIndexer;

import static ix.core.search.text.TextIndexer.*;
import static tripod.chem.indexer.StructureIndexer.*;
import ix.core.plugins.TextIndexerPlugin;
import ix.core.plugins.StructureIndexerPlugin;
import ix.core.plugins.SequenceIndexerPlugin;
import ix.core.plugins.IxContext;
import ix.core.plugins.IxCache;
import ix.core.plugins.PersistenceQueue;
import ix.core.plugins.PayloadPlugin;
import ix.core.controllers.search.SearchFactory;
import ix.core.adapters.EntityPersistAdapter;
import ix.core.chem.ChemCleaner;
import ix.core.chem.EnantiomerGenerator;
import ix.core.chem.PolymerDecode;
import ix.core.chem.PolymerDecode.StructuralUnit;
import ix.core.chem.StructureProcessor;
import ix.core.chem.EnantiomerGenerator.Callback;
import ix.core.models.Structure;
import ix.core.models.VInt;
import ix.core.search.SearchOptions;
import ix.core.search.SearchResult;
import ix.core.search.SearchResultContext;
import ix.core.search.SearchResultProcessor;
import ix.core.search.text.TextIndexer;
import ix.core.controllers.StructureFactory;
import ix.core.controllers.EntityFactory;
import ix.core.controllers.PayloadFactory;
import chemaxon.formats.MolImporter;
import chemaxon.struc.Molecule;
import chemaxon.struc.MolAtom;
import chemaxon.struc.MolBond;
import chemaxon.util.MolHandler;

import java.awt.Dimension;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import org.freehep.graphicsio.svg.SVGGraphics2D;

import gov.nih.ncgc.chemical.Chemical;
import gov.nih.ncgc.chemical.ChemicalAtom;
import gov.nih.ncgc.chemical.ChemicalFactory;
import gov.nih.ncgc.chemical.ChemicalRenderer;
import gov.nih.ncgc.chemical.DisplayParams;
import gov.nih.ncgc.nchemical.NchemicalRenderer;
import gov.nih.ncgc.jchemical.Jchemical;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.ehcache.Element;
import ix.ncats.controllers.auth.*;
import ix.ncats.controllers.security.IxDynamicResourceHandler;
import ix.ncats.resolvers.*;


/**
 * Basic plumbing for an App
 */
public class App extends Authentication {
	private static final String KEY_DISPLAY_CD = "DISPLAY_CD";

	private static final String DISPLAY_CD_VALUE_RELATIVE = "RELATIVE";

	static final String APP_CACHE = App.class.getName();
	public static final int FACET_DIM = 20;
	public static final int MAX_SEARCH_RESULTS = 1000;

	public static PayloadPlugin _payloader;
	public static IxContext _ix;
	public static PersistenceQueue _pq;

    static {
        init();
    }

    public static void init() {

        _payloader = Play.application().plugin(PayloadPlugin.class);
       _ix = Play.application().plugin(IxContext.class);
       _pq = Play.application().plugin(PersistenceQueue.class);
    }

    
    public static class BogusPageException extends IllegalArgumentException{
		public BogusPageException(String string) {
			super(string);
		}
    }

    public static TextIndexer getTextIndexer(){
        return Play.application().plugin(TextIndexerPlugin.class).getIndexer();
    }
    /**
     * This returns links to up to 11 pages of interest.
     * 
     * The first few are always 1-3
     * 
     * The last 2 pages are always the last 2 possible
     * 
     * The middle pages are the pages around the current page
     * 
     * 
     * There will always be at least 1 page.
     * 
     * @param rowsPerPage
     * @param page
     * @param total
     * @return
     */
    public static int[] paging (int rowsPerPage, int page, int total) {
        int MAX_ARRAY=11;
        int MAX_SHOW=8;
        //last page
        int max = Math.max(1,(total+ rowsPerPage-1)/rowsPerPage);
        if (page < 0 || page > max) {
            throw new BogusPageException ("Bogus page " + page);
        }
        
        int[] pages;
        if (max <= MAX_ARRAY) {
            pages = new int[max];
            for (int i = 0; i < pages.length; ++i)
                pages[i] = i+1;
        }else if (page >= max-3) {
            pages = new int[MAX_ARRAY];
            pages[0] = 1;
            pages[1] = 2;
            pages[2] = 0; // inactive / spacer
            for (int i = pages.length; --i > 2; )
                pages[i] = max--;
        }else {
            pages = new int[MAX_ARRAY];
            int i = 0;
            //0-7 set to +1
            for (; i < MAX_SHOW; ++i)
                pages[i] = i+1;
            
            //1,2,3,4,5,6,7,0,0,0
            
            
            //if the page is larger than 7 (last 3 page)
            //
            if (page >= MAX_SHOW -1) {
                // now shift
            	
                pages[--i] = page+2;
                //1,2,3,4,5,6,9,0,0,0
                while (i-- > 0){
                    pages[i] = pages[i+1]-1;
                  //3,4,5,6,7,8,9,0,0,0
                }
                pages[0] = 1;
                pages[1] = 2;
                pages[2] = 0;
              //1,2,0,6,7,8,9,0,0,0
            }
            pages[MAX_ARRAY-2] = max-1;
            pages[MAX_ARRAY-1] = max;
            //1,2,0,5,6,7,8,0,99,100
        }
        return pages;
    }

    public static String sha1 (Facet facet, int value) {
        return Util.sha1(facet.getName(),
                         facet.getValues().get(value).getLabel());
    }

    /**
     * make sure if the argument doesn't have quote then add them
     */
    static Pattern REGEX_QUOTE = Pattern.compile("\"([^\"]+)");
    public static String quote (String s) {
        try {
            Matcher m = REGEX_QUOTE.matcher(s);
            if (m.find())
                return s; // nothing to do.. already has quote
            return "\""+URLEncoder.encode(s, "utf8")+"\"";
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return s;
    }
    
    public static String decode (String s) {
        try {
            return URLDecoder.decode(s, "utf8");
        }
        catch (Exception ex) {
            Logger.trace("Can't decode string "+s, ex);
        }
        return s;
    }

    public static String encode (String s) {
        try {
            return URLEncoder.encode(s, "utf8");
        }
        catch (Exception ex) {
            Logger.trace("Can't encode string "+s, ex);
        }
        return s;
    }

    public static String encode (Facet facet) {
    	return encode(facet.getName());
    }
    
    public static String encode (Facet facet, int i) {
        String value = facet.getValues().get(i).getLabel();
        try {
        	String newvalue=URLEncoder.encode(value.replace("/", "$$"), "utf8");
            return newvalue;
        }
        catch (Exception ex) {
            Logger.trace("Can't encode string "+value, ex);
        }
        return value;
    }

    public static String page (int rows, int page) {
       
        StringBuilder uri = new StringBuilder (request().path()+"?page="+page);
        
        getQueryParameters ().forEach((key, value)->{
        	if(!"page".equals(key)){
	       	 	for (String v : value) {
	                uri.append("&"+key+"="+v);
	            }
        	}
        });

        
        return uri.toString();
    }

    public static String truncate (String str, int size) {
        if (str.length() <= size) return str;
        return str.substring(0, size)+"...";
    }

    public static String url (String... remove) {
        return url (true, remove);
    }
    
    public static String url (boolean exact, String... remove) {
        //Logger.debug(">> uri="+request().uri());

        StringBuilder uri = new StringBuilder (request().path()+"?");
        Map<String, Collection<String>> params = getQueryParameters ();
        for (Map.Entry<String, Collection<String>> me : params.entrySet()) {
            boolean matched = false;
            for (String s : remove)
                if ((exact && s.equals(me.getKey()))
                    || me.getKey().startsWith(s)) {
                    matched = true;
                    break;
                }
            
            if (!matched) {
                for (String v : me.getValue())
                    if (v != null)
                        uri.append(me.getKey()+"="+v+"&");
            }
        }
        //Logger.debug("<< "+uri);
        
        return uri.substring(0, uri.length()-1);
    }

    public static Map<String, Collection<String>> removeIfMatch
        (Map<String, Collection<String>> params, String key, String value) {
        Collection<String> values = params.get(key);
        //Logger.debug("removeIfMatch: key="+key+" value="+value+" "+values);
        if (values != null) {
            List<String> keep = new ArrayList<String>();
            for (String v : values) {
                try {
                    String dv = URLDecoder.decode(v, "utf8");
                    //Logger.debug(v+" => "+dv);
                    if (!dv.startsWith(value)) {
                        keep.add(v);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            if (!keep.isEmpty()) {
                params.put(key, keep);
            }
            else {
                params.remove(key);
            }
        }
        return params;
    }

    public static Map<String, Collection<String>> removeIfMatch
        (String key, String value) {
        Map<String, Collection<String>> params = getQueryParameters ();
        return removeIfMatch (params, key, value);
    }

    public static String url (Map<String, Collection<String>> params,
                              String... remove) {
        for (String p : remove)
            params.remove(p);
        
        StringBuilder uri = new StringBuilder (request().path()+"?");
        for (Map.Entry<String, Collection<String>> me : params.entrySet())
            for (String v : me.getValue())
                if (v != null)
                    uri.append(me.getKey()+"="+v+"&");
        
        return uri.substring(0, uri.length()-1);
    }
    

    static Map<String, Collection<String>> getQueryParameters () {
        Map<String, Collection<String>> params =
            new TreeMap<String, Collection<String>>();
        String uri = request().uri();
        int pos = uri.indexOf('?');
        if (pos >= 0) {
            for (String p : uri.substring(pos+1).split("&")) {
                pos = p.indexOf('=');
                if (pos > 0) {
                    String key = p.substring(0, pos);
                    String value = p.substring(pos+1);
                    Collection<String> values = params.get(key);
                    if (values == null) {
                        params.put(key, values = new ArrayList<String>());
                    }
                    values.add(value);
                }
                else {
                    Logger.error("Bad parameter: "+p);
                }
            }
        }
        return params;
    }

    /**
     * more specific version that only remove parameters based on 
     * given facets
     */
    public static String url (FacetDecorator[] facets, String... others) {
        Logger.debug(">> uri="+request().uri());

        List<FacetDecorator> facetList = Arrays.asList(facets);
        
        StringBuilder uri = new StringBuilder (request().path()+"?");
        Map<String, Collection<String>> params = getQueryParameters ();
        
        params.forEach((key,value)->{
        	 if (key.equals("facet")) {
                 for (String v : value){
                     if (v != null) {
                         String s = decode (v);
                         boolean matched = facetList
             		 			.stream()
             		 			.anyMatch(f -> !f.hidden && s.startsWith(f.facet.getName()));
                         
                         if (!matched) {
                             uri.append(key+"="+v+"&");
                         }
                     }
                 }
             } else {
                 boolean matched = Arrays.asList(others)
                		 			.stream()
                		 			.anyMatch(s -> s.equals(key));
                 
                 if (!matched){
                	 value.stream()
                	 	.filter(v->(v!=null))
                	 	.forEach(v -> uri.append(key+"="+v+"&"));
                 }
             }
        });
        
        Logger.debug("<< uri="+uri);
        return uri.substring(0, uri.length()-1);
    }

    public static String queryString (String... params) {
        Map<String, String[]> query = new HashMap<String, String[]>();
        for (String p : params) {
            String[] values = request().queryString().get(p);
            if (values != null)
                query.put(p, values);
        }
        
        return query.isEmpty() ? "" : "?"+queryString (query);
    }
    
    public static String queryString (Map<String, String[]> queryString) {
        //Logger.debug("QueryString: "+queryString);
        StringBuilder q = new StringBuilder ();
        queryString.forEach((key,value)->{
        	for (String s : value) {
                if (q.length() > 0)
                    q.append('&');
                q.append(key+"="+encode (s));
            }
        });
        return q.toString();
    }

    public static boolean hasFacet (Facet facet, int i) {
        String[] facets = request().queryString().get("facet");
        if (facets != null) {
            for (String f : facets) {
                String[] toks = f.split("/");
                if (toks.length == 2) {
                    try {
                        String name = toks[0];
                        String value = toks[1].replace("$$", "/");
                        boolean matched = name.equals(facet.getName())
                            && value.equals(facet.getValues()
                                            .get(i).getLabel());
                        
                        if (matched)
                            return matched;
                    }
                    catch (Exception ex) {
                        Logger.trace("Can't URL decode string", ex);
                    }
                }
            }
        }
        
        return false;
    }

    public static List<Facet> getFacets (final Class kind, final int fdim) {
        try {
            SearchResult result =
                SearchFactory.search(kind, null, 0, 0, fdim, null);
            return result.getFacets();
        }
        catch (IOException ex) {
            Logger.trace("Can't retrieve facets for "+kind, ex);
        }
        return new ArrayList<Facet>();
    }

    public static List<Facet> getFacets (SearchOptions options) {
        try {
            SearchResult result = getTextIndexer().search(options, null, null);
            return result.getFacets();
        }
        catch (IOException ex) {
            Logger.trace("Can't retrieve facets for "+options.kind, ex);
        }
        
        return new ArrayList<Facet>();
    }

    public static List<String> getUnspecifiedFacets
        (final FacetDecorator[] decors) {
        String[] facets = request().queryString().get("facet");
        List<String> unspec = new ArrayList<String>();
        if (facets != null && facets.length > 0) {
            for (String f : facets) {
                int matches = 0;
                for (FacetDecorator d : decors) {
                    //Logger.debug(f+" <=> "+d.facet.getName());              
                    if (f.startsWith(d.facet.getName())) {
                        ++matches;
                    }
                }
                if (matches == 0)
                    unspec.add(f);
            }
        }
        return unspec;
    }

    public static Facet[] filter (List<Facet> facets, String... names) {
    	Map<String,Facet> facetMap = facets
    				.stream()
    				.collect(Collectors.toMap(Facet::getName, f->f));
       
    	return Arrays.stream(names)
        	.map(fn->facetMap.get(fn))
        	.filter(Objects::nonNull)
        	.toArray(len->new Facet[len]);
    }
    
    public static Facet[] filter (List<Facet> facets, Predicate<Facet> keepif) {
        return facets.stream()
        				.filter(keepif)
        				.toArray(len->new Facet[len]);
    }

    public static TextIndexer.Facet[] getFacets (final Class<?> cls,
                                                 final String... filters) {
        StringBuilder key = new StringBuilder (cls.getName()+".facets");
        for (String f : filters)
            key.append("."+f);
        try {
            TextIndexer.Facet[] facets = getOrElse
                (key.toString(), ()  -> filter (getFacets (cls, FACET_DIM), filters));
            return facets;
        }
        catch (Exception ex) {
            Logger.error("Can't get facets for "+cls, ex);
            ex.printStackTrace();
        }
        return new TextIndexer.Facet[0];
    }

    protected static Map<String, String[]> getRequestQuery () {
        Map<String, String[]> query = new HashMap<String, String[]>();
        query.putAll(request().queryString());
        // force to fetch everything at once
        //query.put("fetch", new String[]{"0"});
        return query;
    }
    
    public static SearchResult getSearchResult
        (final Class kind, final String q, final int total) {
        return getSearchResult (getTextIndexer(), kind, q, total);
    }

    public static SearchResult getSearchResult
        (final Class kind, final String q, final int total,
         Map<String, String[]> query) {
        return getSearchResult (getTextIndexer(), kind, q, total, query);
    }
    
    public static SearchResult getSearchResult
        (final TextIndexer indexer, final Class kind,
         final String q, final int total) {
        return getSearchResult (indexer, kind, q, total, getRequestQuery());
    }

    public static String signature (String q, Map<String, String[]> query) {
        List<String> qfacets = new ArrayList<String>();
        if (query.get("facet") != null) {
            for (String f : query.get("facet"))
                qfacets.add(f);
        
        }
        
        
        final boolean hasFacets = q != null
            && q.indexOf('/') > 0 && q.indexOf("\"") < 0;
        if (hasFacets) {
            // treat this as facet
            qfacets.add("MeSH/"+q);
            query.put("facet", qfacets.toArray(new String[0]));
        }
        //query.put("drill", new String[]{"down"});
        
        List<String> args = new ArrayList<String>();
        args.add(request().path());
        if (q != null)
            args.add(q);
        for (String f : qfacets)
            args.add(f);
        
        if (query.get("order") != null) {
            for (String f : query.get("order"))
                args.add(f);
        }
        
        args.add("dep" + query.get("showDeprecated"));
        
        
        
        Collections.sort(args);
        return Util.sha1(args.toArray(new String[0]));
    }

    public static SearchResult getSearchContext (String ctx) {
        Object result = IxCache.get(ctx);
        if (result != null) {
            if (result instanceof SearchResult) {
                return (SearchResult)result;
            }
        }
        Logger.warn("No context found: "+ctx);
        return null;
    }
        
    public static SearchResult getSearchFacets (final Class kind) {
        return getSearchFacets (kind, 100);
    }
    
    public static SearchResult getSearchFacets (final Class kind,
                                                final int fdim) {
        final String sha1 = Util.sha1(kind.getName()+"/"+fdim);
        try {
            return getOrElse (sha1,  ()  -> {
                        SearchResult result = SearchFactory.search
                            (kind, null, 0, 0, fdim, null);
                        return cacheKey (result, sha1);
                    });
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Logger.trace("Can't get search facets!", ex);
        }
        return null;
    }

    final static Pattern REGEX_RANGE = Pattern.compile
        ("([^:]+):\\[([^,]*),([^\\]]*)\\]");
    public static SearchResult getSearchResult
        (final TextIndexer indexer, final Class kind,
         final String q, final int total, final Map<String, String[]> query) {
        
        final String sha1 = signature (q, query);
        final boolean hasFacets = q != null
            && q.indexOf('/') > 0 && q.indexOf("\"") < 0;
        
        try {       
            long start = System.currentTimeMillis();
            SearchResult result;
            if (indexer != getTextIndexer()) {
                // if it's an ad-hoc indexer, then we don't bother caching
                //  the results
                result = SearchFactory.search
                    (indexer, kind, null, hasFacets ? null : q,
                     total, 0, FACET_DIM, query);
            }
            else {
                Logger.debug("Search request sha1: "+sha1+" cached? "
                             +IxCache.contains(sha1));

                if (q != null) {
                    // check to see if q is format like a range
                    Matcher m = REGEX_RANGE.matcher(q);
                    if (m.find()) {
                        final String field = m.group(1);
                        final String min = m.group(2);
                        final String max = m.group(3);
                        
                        Logger.debug
                            ("range: field="+field+" min="+min+" max="+max);
                        
                        return getOrElse (sha1, ()  -> {
                                    SearchOptions options =
                                        new SearchOptions (query);
                                    options.top = total;
                                    SearchResult sresult = getTextIndexer().range
                                        (options, field, min.isEmpty()
                                         ? null : Integer.parseInt(min),
                                         max.isEmpty()
                                         ? null : Integer.parseInt(max));
                                    return cacheKey (sresult, sha1);
                                });
                    }
                }

                result = getOrElse
                    (sha1, () -> {
                                SearchResult sresult = SearchFactory.search
                                (kind, hasFacets ? null : q,
                                 total, 0, FACET_DIM, query);
                                return cacheKey (sresult, sha1);
                            });
                Logger.debug(sha1+" => "+result);
            }
            double elapsed = (System.currentTimeMillis() - start)*1e-3;
            Logger.debug(String.format("Elapsed %1$.3fs to retrieve "
                                       +"search %2$d/%3$d results...",
                                       elapsed, result.size(),
                                       result.count()));
            
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Logger.trace("Unable to perform search", ex);
        }
        return null;
    }
    static protected String formatKey(String key){
    	return key;
    }
    
    static protected SearchResult cacheKey (SearchResult result, String key) {
    	key=formatKey(key);
//      IxCache.set(key, result); // create alias       
        result.setKey(key);
        return result;
    }

    public static Result getEtag (String key, TypedCallable<Result> callable)
        throws Exception {
        String ifNoneMatch = request().getHeader("If-None-Match");
        if (ifNoneMatch != null
            && ifNoneMatch.equals(key) && IxCache.contains(key))
            return status (304);

        response().setHeader(ETAG, key);
        return getOrElse (key, callable);
    }
    
    public static <T> T getOrElse(String key, TypedCallable<T> callable)
        throws Exception {
        return getOrElse (getTextIndexer().lastModified(), key, callable);
    }

    public static <T> T getOrElse (long modified,
                                   String key, TypedCallable<T> callable)
    throws Exception {
        return IxCache.getOrElse(modified, key, callable);
    }

    public static Result renderParam (final String value, final int size) {
        return render(value, size);
    }

    public static Result render (final String value, final int size) {
        String key = Util.sha1(value)+"::"+size;
        try {
        	
            response().setContentType("image/svg+xml");
            byte[] resp = getOrElse (0l, key, () ->{
                        MolHandler mh = new MolHandler (value);
                        Molecule mol = mh.getMolecule();
                        if (mol.getDim() < 2) {
                            mol.clean(2, null);
                        }
                        Logger.info("ok");
                        return render (mol, "svg", size, null);
                    });
            return ok(resp);
        }
        catch (Exception ex) {
            Logger.error("Not a valid molecule:\n"+value, ex);
            ex.printStackTrace();
            return badRequest ("Not a valid molecule: "+value);
        }
    }

    public static byte[] render (Molecule mol, String format,
                                 int size, int[] amap) throws Exception{
        return render(mol,format,size,amap,null);
    }

    public static byte[] render (Molecule mol, String format,
                                 int size, int[] amap, Map newDisplay)
        throws Exception {
        Chemical chem = new Jchemical (mol);
        Request r=request();
        if(r!=null){
        	if("true".equals(r.getQueryString("standardize"))){
        		chem.dearomatize();
        		chem.removeNonDescriptHydrogens();
        	}
        }
        
        DisplayParams dp = DisplayParams.DEFAULT();
        if(newDisplay!=null)
        dp.changeSettings(newDisplay);
       
        
        
        
        //chem.reduceMultiples();
        boolean highlight=false;
        if(amap!=null && amap.length>0){
                ChemicalAtom[] atoms = chem.getAtomArray();
                for (int i = 0; i < Math.min(atoms.length, amap.length); ++i) {
                    atoms[i].setAtomMap(amap[i]);
                    if(amap[i]!=0){
                        dp = dp.withSubstructureHighlight();
                    }
                }
        }else{
        	ChemicalAtom[] atoms = chem.getAtomArray();
            for (int i = 0; i < atoms.length; ++i) {
            	if(atoms[i].getAtomMap()!=0){
                    dp = dp.withSubstructureHighlight();
                }
            }
        }
        dp = preProcessChemical(chem,dp);
        if(size>250 && !highlight){
            if(chem.hasStereoIsomers())
                dp.changeProperty(DisplayParams.PROP_KEY_DRAW_STEREO_LABELS, true);
        }

        /*
        DisplayParams displayParams = new DisplayParams ();
        displayParams.changeProperty
            (DisplayParams.PROP_KEY_DRAW_STEREO_LABELS_AS_ATOMS, true);
        
        ChemicalRenderer render = new NchemicalRenderer (displayParams);
        */
        
       
        ChemicalRenderer render = new NchemicalRenderer ();
        
        render.setDisplayParams(dp);
        render.addDisplayProperty("TOP_TEXT");
        render.addDisplayProperty("BOTTOM_TEXT");
        ByteArrayOutputStream bos = new ByteArrayOutputStream ();       
        if (format.equals("svg")) {
            SVGGraphics2D svg = new SVGGraphics2D
                (bos, new Dimension (size, size));
            svg.startExport();
            render.renderChem(svg, chem, size, size, false);
            svg.endExport();
            svg.dispose();
        }
        else {
            BufferedImage bi = render.createImage(chem, size);
            ImageIO.write(bi, "png", bos); 
        }
        
        return bos.toByteArray();
    }
    
    public static byte[] render (Structure struc, String format, int size, int[] amap)
        throws Exception {
        Map newDisplay = new HashMap();
        if(struc.stereoChemistry == struc.stereoChemistry.RACEMIC){
                newDisplay.put(DisplayParams.PROP_KEY_DRAW_STEREO_LABELS_AS_RELATIVE, true);
        }
        MolHandler mh = new MolHandler
            (struc.molfile != null ? struc.molfile : struc.smiles);
        Molecule mol = mh.getMolecule();
        if (mol.getDim() < 2) {
            mol.clean(2, null);
        }
        if(struc.opticalActivity!= struc.opticalActivity.UNSPECIFIED
           && struc.opticalActivity!=null){
            if(struc.definedStereo>0){
                if(struc.opticalActivity==struc.opticalActivity.PLUS_MINUS){
                    if(struc.stereoChemistry==struc.stereoChemistry.EPIMERIC
                       ||struc.stereoChemistry==struc.stereoChemistry.RACEMIC
                       ||struc.stereoChemistry==struc.stereoChemistry.MIXED){
                        mol.setProperty("BOTTOM_TEXT","relative stereochemistry");
                    }
                }
            }
            if(struc.opticalActivity==struc.opticalActivity.PLUS){
                mol.setProperty("BOTTOM_TEXT","optical activity: (+)");
                if(struc.stereoChemistry == struc.stereoChemistry.UNKNOWN){
                    newDisplay.put(DisplayParams.PROP_KEY_DRAW_STEREO_LABELS_AS_STARRED, true);
                }
            } else if(struc.opticalActivity==struc.opticalActivity.MINUS) {
                mol.setProperty("BOTTOM_TEXT","optical activity: (-)");
                if(struc.stereoChemistry == struc.stereoChemistry.UNKNOWN){
                    newDisplay.put(DisplayParams.PROP_KEY_DRAW_STEREO_LABELS_AS_STARRED, true);
                }
            }               
        }
        
        if(size>250){
            if(struc.stereoChemistry != struc.stereoChemistry.ACHIRAL)
                newDisplay.put(DisplayParams.PROP_KEY_DRAW_STEREO_LABELS, true);
        }
        if(newDisplay.size()==0)newDisplay=null;
        return render (mol, format, size, amap,newDisplay);
    }

    public static int[] stringToIntArray(String amapString){
        int[] amap=null;
        if(amapString!=null){
            String[] amapb = null;
            amapb = amapString.split(",");
            amap = new int[amapb.length];
            for(int i=0;i<amap.length;i++){
                try{
                    amap[i]=Integer.parseInt(amapb[i]);
                }catch(Exception e){
                                
                }
            }
        }
        return amap;
    }
    
    /**
     * Renders a chemical structure from structure ID
     * atom map can be provided for highlighting
     * 
     * @param id
     * @param format
     * @param size
     * @param atomMap
     * @return
     */
    public static Result structure (final String id,
                                    final String format, final int size,
                                    final String atomMap) {
        
        final int[] amap = stringToIntArray(atomMap);
        if (format.equals("svg") || format.equals("png")) {
            final String key =
                Structure.class.getName()+"/"+size+"/"+id+"."+format
                + ":" + atomMap;
            String mime = format.equals("svg") ? "image/svg+xml" : "image/png";
            try {
                byte[] result = getOrElse (key, () -> {
                            Structure struc = StructureFactory.getStructure(id);
                            if (struc != null) {
                                return render (struc, format, size, amap);
                            }
                            return null;
                        });
                if (result != null) {
                    response().setContentType(mime);
                    return ok(result);
                }
            }catch (Exception ex) {
                Logger.error("Can't generate image for structure "
                             +id+" format="+format+" size="+size, ex);
                return internalServerError
                    ("Unable to retrieve image for structure "+id);
            }
        }
        else {
            final String key = Structure.class.getName()+"/"+id+"."+format;
            try {
                return getOrElse (key,  () ->{
                            Structure struc = StructureFactory.getStructure(id);
                            if (struc != null) {
                                response().setContentType("text/plain");
                                if (format.equals("mrv")) {
                                    MolHandler mh =
                                        new MolHandler (struc.molfile);
                                    if (mh.getMolecule().getDim() < 2) {
                                        mh.getMolecule().clean(2, null);
                                    }
                                    return ok (mh.getMolecule()
                                               .toFormat("mrv"));
                                }
                                else if (format.equals("mol")
                                         || format.equals("sdf")) {
                                    return struc.molfile != null
                                        ? ok (struc.molfile) : noContent ();
                                }
                                else {
                                    return struc.smiles != null
                                        ?  ok (struc.smiles) : noContent ();
                                }
                            }
                            else {
                                Logger.warn("Unknown structure: "+id);
                            }
                            return noContent ();
                        });
            }
            catch (Exception ex) {
                Logger.error("Can't convert format "+format+" for structure "
                             +id, ex);
                return internalServerError
                    ("Unable to convert structure "+id+" to format "+format);
            }
        }
        return notFound ("Not a valid structure "+id);
    }

    public static String getKeyForCurrentRequest(){
    	
    	 String query = request().getQueryString("q") + request().getQueryString("order");
         String type = request().getQueryString("type");

         Logger.debug("checkStatus: q=" + query + " type=" + type);
         if (type != null && query != null) {
             try {
                 String key = null;
                 if (type.equalsIgnoreCase("substructure")) {
                	 String sq = getSmiles(request().getQueryString("q"));
                	 key = "substructure/"+Util.sha1(sq + request().getQueryString("order"));
                 }
                 else if (type.equalsIgnoreCase("similarity")) {
                     String c = request().getQueryString("cutoff");
                     String sq = getSmiles(request().getQueryString("q"));
                     key = "similarity/"+getKey (sq + request().getQueryString("order"), Double.parseDouble(c));
                 }
                 else if (type.equalsIgnoreCase("sequence")) {
                 	String iden = request().getQueryString("identity");
                     if (iden == null) {
                         iden = "0.5";
                     }
                     String idenType = request().getQueryString("identityType");
                     if(idenType==null){
                     	idenType="GLOBAL";
                     }
                     key = "sequence/"+getKey (getSequence(request().getQueryString("q")) +idenType + request().getQueryString("order"), Double.parseDouble(iden));

                 }else if(type.equalsIgnoreCase("flex")) {
                	 String sq = getSmiles(request().getQueryString("q"));
                	 key = "flex/"+Util.sha1(sq + request().getQueryString("order"));
                 }else if(type.equalsIgnoreCase("exact")) {
                	 String sq = getSmiles(request().getQueryString("q"));
                	 key = "exact/"+Util.sha1(sq + request().getQueryString("order"));
                 }else{
                	 key = type + "/"+Util.sha1(query);
                 }

                 return key;
                 
             }catch (Exception ex) {
                 Logger.error("Error creating key for request" , ex);
             }
         }else {
        	 
             String key = signature (query, getRequestQuery ());
             return key;
         }
         return null;
    }

    /**
     * This method will return a proper Call only if the query isn't already
     * finished in one way or another
     */
    public static Call checkStatus () {
    	SearchResultContext ctx=checkStatusDirect();
    	if(ctx==null)return null;
    	
        switch (ctx.getStatus()) {
	        case Done:
	        case Failed:
	            break;
	        default:
	        	return ctx.getCall();
	    }
        return null;
    }
    
    public static SearchResultContext checkStatusDirect () {
    	String key = getKeyForCurrentRequest();
    	return SearchResultContext.getSearchResultContextForKey(key);
    }

    
    //TODO: Needs evaluation
    public static Result getSearchResultContext (String key) {
    	SearchResultContext ctx=SearchResultContext.getSearchResultContextForKey(key);
    	if (ctx != null) {
    		ObjectMapper mapper = new ObjectMapper ();
            return Java8Util.ok (mapper.valueToTree(ctx));
    	}
        return notFound ("No key found: "+key+"!");
    }

    public static SearchResultContext batch
        (final String q, final int rows, final Tokenizer tokenizer,
         final SearchResultProcessor processor) {
        try {
            final String key = "batch/"+Util.sha1(q);
            Logger.debug("batch: q="+q+" rows="+rows);
            return getOrElse (key, () ->{
                        processor.setResults(rows, tokenizer.tokenize(q));
                        return processor.getContext();
                });
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.error("Can't perform batch search", ex);
        }
        return null;
    }
    public static SearchResultContext sequence
        (final String seq, final double identity, final int rows,
         final int page, CutoffType ct, final SearchResultProcessor processor) {
        try {
            final String key = App.getKeyForCurrentRequest();
            return getOrElse
                (EntityPersistAdapter.getSequenceIndexer().lastModified(), key,
                  () -> {
                         processor.setResults
                             (rows, EntityPersistAdapter.getSequenceIndexer().search(seq, identity, ct));
                         return processor.getContext();
                 });
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Logger.error("Can't perform sequence identity search", ex);
        }
        return null;
    }

    public static SearchResultContext substructure
        (final String query, final int rows,
         final int page, final SearchResultProcessor processor) {
        try {
            final String key = App.getKeyForCurrentRequest();
            Logger.debug("substructure: query="+query
                         +" rows="+rows+" page="+page+" key="+key);
            return getOrElse
                (EntityPersistAdapter.getStructureIndexer().lastModified(),
                 key, ()->{
                             processor.setResults
                                 (rows, EntityPersistAdapter.getStructureIndexer().substructure(query, 0));
                             SearchResultContext ctx = processor.getContext();
                             ctx.setKey(key);
                             Logger.debug("## cache missed: "+key+" => "+ctx);
                             return ctx;
                     });
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Logger.error("Can't perform substructure search", ex);
        }
        return null;
    }

    static String getKey (String q, double t) {
        return Util.sha1(q) + "/"+String.format("%1$d", (int)(1000*t+.5));
    }

    public static SearchResultContext similarity
        (final String query, final double threshold,
         final int rows, final int page,
         final SearchResultProcessor processor) {
        try {
        	final String key = App.getKeyForCurrentRequest();
            //final String key = "similarity/"+getKey (query + request().getQueryString("order"), threshold);
            return getOrElse
                (EntityPersistAdapter.getStructureIndexer().lastModified(),
                 key, ()->{
                             processor.setResults
                                 (rows, 
                               EntityPersistAdapter.getStructureIndexer().similarity(query, threshold, 0)
                                		 );
                             SearchResultContext ctx = processor.getContext();
                             ctx.setKey(key);
                             return ctx;
                     });
        }
        catch (Exception ex) {
            ex.printStackTrace();
            Logger.error("Can't execute similarity search", ex);
        }
        return null;
    }
    
    /**
     * Check if the current request has a wait parameter included
     * @return
     */
	public static boolean isWaitSet() {
		String wait = request().getQueryString("wait");
		if (wait != null && wait.equalsIgnoreCase("true")) {
			return true;
		}
		return false;
	}
	
	
    public static <T> Result fetchResultImmediate
    (final SearchResult result, int rows,
     int page, final ResultRenderer<T> renderer) throws Exception {
    	 SearchResultContext src= new SearchResultContext(result);
    	 List<T> resultList = new ArrayList<T>();
    	 int[] pages = new int[0];
    	 if (result.count() > 0) {
    	             rows = Math.min(result.count(), Math.max(1, rows));
    	             pages = paging(rows, page, result.count());
    	             
    	             //block for results only if the request specifies this
    	             if(isWaitSet()){
    	            	 result.copyTo(resultList, (page-1)*rows, rows, true);
    	             }else{
    	            	 result.copyTo(resultList, (page-1)*rows, rows, false);
    	             }
    	 }
    	 return renderer.render(src, page, rows, result.count(),
    			 pages, result.getFacets(), resultList);
    }
    static String getKey (SearchResultContext context, String... params) {
        return "fetchResult/"+context.getId()
            +"/"+Util.sha1(request (), params);
    }
    public static <T> Result fetchResult
        (final SearchResultContext context, int rows,
         int page, final ResultRenderer<T> renderer) throws Exception {

        final String key = getKey (context, "facet");
        
        /**
         * If wait is set to be forced, we need to hold off going forward until
         * everything has been processed
         */
        if(!context.isFinished() ) {
            if (isWaitSet()) {
                context.getDeterminedFuture().get();
            }
        }
        SearchResultContext.Status stat=context.getStatus();
        boolean isDetermined=context.isDetermined();
        /**
         * we need to connect context.id with this key to have
         * the results of structure/sequence search context merged
         * together with facets, sorting, etc.
         */
      
        final SearchResult result = 
        		getOrElse(key,  () -> {
                        Collection results = context.getResults();
                        if (results.isEmpty()) {
                            return null;
                        }
                        SearchResult searchResult =
                        		SearchFactory.search (results, null, results.size(), 0,
                                              renderer.getFacetDim(),
                                              request().queryString());
                        Logger.debug("Cache misses: "
                                     +key+" size="+results.size()
                                     +" class="+searchResult);
                        // make an alias for the context.id to this search
                        // result
                        return cacheKey (searchResult, context.getId());
                });
        
        
       
        final List<T> results = new ArrayList<T>();
        final List<Facet> facets = new ArrayList<Facet>();
        int[] pages = new int[0];
        int count = 0;
        if (result != null) {
        	Long stop = context.getStop();
        	if(!isDetermined || (stop != null && stop >= result.getTimestamp())){
        		Logger.debug("** removing cache "+key);
        		IxCache.remove(key);
        	}
            
            count = result.count();
            
            Logger.debug(key+": "+count+"/"+result.count()
                         +" finished? "+context.isFinished()
                         +" stop="+stop);
            
            rows = Math.min(count, Math.max(1, rows));
            
            int i = (page - 1) * rows;
            
            if (i < 0 || i >= count) {
            	if(isDetermined){
            		 throw new BogusPageException ("Bogus page " + page);
            	}else{
            		flash("warning","Showing page 1. Page " + page + " is not available at this time. It will be loaded when the search is complete.");            		
            		page = 1;
                	i = 0;
            	}
            }
            pages = paging (rows, page, count);
            
            // If the requested page isn't ready yet, block until the page is ready.
            
            // This is different than the way that the text indexer allows ajaxing to 
            // check the status of unfinished jobs. The idea here is that it would be 
            // to confusing to have 2 levels of ajax waiting in the background
            // and since this method is only called for those complex external
            // searches, which typically get some smaller number of records back,
            // this may be an acceptable lack of responsiveness
            result.copyTo(results, i, rows, true);
            facets.addAll(result.getFacets());
            
            
            
            
            // If the context was determined, now we can mark it as done.
            if(stat == SearchResultContext.Status.Determined){
            	context.setStatus(SearchResultContext.Status.Done);
            }
            
            
            if (isDetermined && result.finished()) {
                final String k = getKey (context) + "result";
                final int _page = page;
                final int _rows = rows;
                final int _count = count;
                final int[] _pages = pages;
                

                // result is cached
				return getOrElse(result.getStopTime(), k, () -> {
					Logger.debug("Cache misses: " + k + " count=" + _count + " rows=" + _rows + " page=" + _page);
					return renderer.render(context, _page, _rows, _count, _pages, facets, results);
				});
            }
        }
        
        return renderer.render(context, page, rows, count,
                               pages, facets, results);
    }

    static ObjectNode toJson (Element elm) {
        return toJson (new ObjectMapper (), elm);
    }
    
    static ObjectNode toJson (ObjectMapper mapper, Element elm) {
        return toJson (mapper.createObjectNode(), elm);
    }

    static ObjectNode toJson (ObjectNode node, Element elm) {
        node.put("class", elm.getObjectValue().getClass().getName()
                 +"@"+String.format
                 ("%1$x", System.identityHashCode(elm.getObjectValue())));
        node.put("key", elm.getObjectKey().toString());
        node.put("creation", new Date (elm.getCreationTime()).toString());
        node.put("expiration", new Date (elm.getExpirationTime()).toString());
        node.put("lastAccess", new Date (elm.getLastAccessTime()).toString());
        node.put("lastUpdate", new Date (elm.getLastUpdateTime()).toString());
        node.put("timeToIdle", elm.getTimeToIdle());
        node.put("timeToLive", elm.getTimeToLive());
        node.put("isEternal", elm.isEternal());
        node.put("isExpired", elm.isExpired());
        return node;
    }

    @Dynamic(value = IxDynamicResourceHandler.IS_ADMIN, handler = ix.ncats.controllers.security.IxDeadboltHandler.class)
    public static Result cache (String key) {
        try {
            Element elm = IxCache.getElm(key);
            if (elm == null) {
                return notFound ("Unknown cache: "+key);
            }

            return ok (toJson (elm));
        }
        catch (Exception ex) {
            return internalServerError (ex.getMessage());
        }
    }

    @Dynamic(value = IxDynamicResourceHandler.IS_ADMIN, handler = ix.ncats.controllers.security.IxDeadboltHandler.class)
    public static Result cacheSummary () {
    	
    	Util.printAllExecutingStackTraces();
    	return ok (ix.ncats.views.html.cachestats.render
                   (IxCache.getStatistics()));
    }
    
    
    @Dynamic(value = IxDynamicResourceHandler.IS_ADMIN, handler = ix.ncats.controllers.security.IxDeadboltHandler.class)
    public static Result cacheList (int top, int skip) {

        ObjectMapper mapper = new ObjectMapper ();
        ArrayNode result =  IxCache.toJsonStream(top,skip)
                                    .collect( ()-> mapper.createArrayNode(),
                                             (nodes, elm) -> nodes.add(toJson (mapper, elm)),
                                             (nodes1, nodes2) -> nodes1.addAll(nodes2)
                                            );

        if(result.size() == 0){
            return ok ("No cache available!");
        }
        return ok(result);
    }

    @Dynamic(value = IxDynamicResourceHandler.IS_ADMIN, handler = ix.ncats.controllers.security.IxDeadboltHandler.class)
    public static Result cacheDelete (String key) {
        try {
            Element elm = IxCache.getElm(key);
            if (elm == null) {
                return notFound ("Unknown cache: "+key);
            }
                
            if (IxCache.remove(key)) {
                return ok (toJson (elm));
            }
            
            return ok ("Can't remove cache element: "+key);
        }
        catch (Exception ex) {
            return internalServerError (ex.getMessage());
        }
    }
    
    @Dynamic(value = IxDynamicResourceHandler.IS_ADMIN, handler = ix.ncats.controllers.security.IxDeadboltHandler.class)
    public static Result statistics (String kind) {
        if (kind.equalsIgnoreCase("cache")) {
            return ok (ix.ncats.views.html.cachestats.render
                       (IxCache.getStatistics()));
        }
        return badRequest ("Unknown statistics: "+kind);
    }

    public static int[] uptime () {
        int[] ups = null;
        if (Global.epoch != null) {
            ups = new int[3];
            // epoch in seconds
            long u = (System.currentTimeMillis()
                      - Global.epoch.getTime())/1000;
            ups[0] = (int)(u/3600); // hour
            ups[1] = (int)((u/60) % 60); // min
            ups[2] = (int)(u%60); // sec
        }
        return ups;
    }

    //TODO: Make sure this isn't called when not needed
    @BodyParser.Of(value = BodyParser.Text.class, maxLength = 1024 * 1024)
    public static Result molinstrument() {
        // String mime = request().getHeader("Content-Type");
        // Logger.debug("molinstrument: content-type: "+mime);

        ObjectMapper mapper = EntityFactory.EntityMapper.FULL_ENTITY_MAPPER();
        ObjectNode node = mapper.createObjectNode();
        try {
                String payload = request().body().asText();
            payload = ChemCleaner.getCleanMolfile(payload);
            if (payload != null) {
                List<Structure> moieties = new ArrayList<Structure>();
                
                
                try {
                    Structure struc = StructureProcessor.instrument
                        (payload, moieties, false); // don't standardize!
                    // we should be really use the PersistenceQueue to do this
                    // so that it doesn't block
                    struc.save();
                    ArrayNode an = mapper.createArrayNode();
                    for (Structure m : moieties){
                        m.save();
                        ObjectNode on = mapper.valueToTree(m);
                        an.add(on);
                    }
                    node.put("structure", mapper.valueToTree(struc));
                    node.put("moieties", an);
                } catch (Exception e) {
                        e.printStackTrace();
                }
                try {
                    Chemical c = ChemicalFactory.DEFAULT_CHEMICAL_FACTORY()
                        .createChemical(payload, Chemical.FORMAT_AUTO);

                    Collection<StructuralUnit> o = PolymerDecode
                        .DecomposePolymerSU(c, true);
                    
                    for (StructuralUnit su : o) {
                        Structure struc = StructureProcessor.instrument
                            (su.structure, null, false);
                        struc.save();
                        su._structure = struc;
                    }
                    node.put("structuralUnits", mapper.valueToTree(o));
                } catch (Exception e) {
                    Logger.error("Can't enumerate polymer", e);
                }
            }
        } catch (Exception ex) {
                ex.printStackTrace();
            Logger.error("Can't process payload", ex);
            return internalServerError("Can't process mol payload");
        }
        return ok(node);
    }

    public static Result enantiomer (final String id) {
        final String key = "enantiomer/"+id;
        try {
            Structure[] strucs = getOrElse (key, () -> {
                        Structure struc = StructureFactory.getStructure(id);
                        if (struc != null) {
                            return EnantiomerGenerator.enantiomersAsArray (struc);
                        }
                        return null;
                });
            if (strucs != null) {
                ObjectMapper mapper = EntityFactory.getEntityMapper();
                return Java8Util.ok (mapper.valueToTree(strucs));
            }
            return notFound ("Can't located structure "+id);
        } catch (Exception ex) {
            ex.printStackTrace();
            return internalServerError ("Can't generate enantiomer for "+id);
        }
    }
            
    public static String getSequence (String id) {
        return getSequence (id, 0);
    }
    
    public static String getSequence (String id, int max) {
        String seq = PayloadFactory.getString(id);
        if (seq != null) {
            seq = seq.replaceAll("[\n\t\\s]", "");
            if (max > 0 && max+3 < seq.length()) {
                return seq.substring(0, max)+"...";
            }
            return seq;
        }
        return null;
    }
    
    public static String getSmiles(String id) {
        return getSmiles(id, 0);
    }

    public static String getSmiles(String id, int max) {
    	 if (id != null) {
             String seq=null;
             if(!UUIDUtil.isUUID(id)){
         		seq= id;
         	 }else{
                 Structure structure=StructureFactory.getStructure(id);
            	 if(structure!=null){
            		 seq = structure.smiles;
            	 }
         	 }
        	 
             if (seq != null) {
                 seq = seq.replaceAll("[\n\t\\s]", "");
                 if (max > 0 && max + 3 < seq.length()) {
                     return seq.substring(0, max) + "...";
                 }
                 return seq;
             }
         }
         return id;
    }
    

    public static String getPayload (String id, int max) {
        String payload = PayloadFactory.getString(id);
        if (payload != null) {
            int len = payload.length();
            if (max <= 0 || len +3 <= max)
                return payload;
            return payload.substring(0, max)+"...";
        }
        return null;
    }

    public static List<VInt> scaleFacetCounts (Facet facet, int scale) {
        return scaleFacetCounts (facet, scale, false);
    }
    
    public static List<VInt> scaleFacetCounts
        (Facet facet, int scale, boolean inverse) {
        List<VInt> values = new ArrayList<VInt>();
        if (facet != null) {
            int max = 0, min = Integer.MAX_VALUE;
            for (FV fv : facet.getValues()) {
                if (fv.getCount() > max)
                    max = fv.getCount();
                if (fv.getCount() < min)
                    min = fv.getCount();
            }
            
            if ((max-min) <= scale/2) {
                scale += scale/2;
            }
            
            
            for (FV fv : facet.getValues()) {
                VInt v = new VInt ();
                v.label = fv.getLabel();
                if (max == min) {
                    v.intval = (long)scale/2;
                }
                else if (inverse) {
                    v.intval =
                        (long)(0.5+(1. - (double)fv.getCount()/max)*scale);
                }
                else {
                    v.intval = (long)(0.5+(double)fv.getCount()*scale/max);
                }
                values.add(v);
            }
        }
        
        return values;
    }

    public static JsonNode getFacetJson (Facet facet) {
        return getFacetJson (facet, 20);
    }
    
    public static JsonNode getFacetJson (Facet facet, int max) {
        ObjectMapper mapper = new ObjectMapper ();
        ArrayNode nodes = mapper.createArrayNode();
        
        for (TextIndexer.FV fv : facet.getValues()) {
            if (nodes.size() < max) {
                ObjectNode n = mapper.createObjectNode();
                n.put("label", fv.getLabel());
                n.put("value", fv.getCount());
                nodes.add(n);
            } else {
                break;
            }
        }
        return nodes;
    }

    public static Result resolve (final String name) {
        final String key = "resolve/"+name;
        
        try {
            return getOrElse (key, ()->_resolve (name));
        }catch (Exception ex) {
            ex.printStackTrace();
            Logger.error("Can't resolve \""+name+"\"!", ex);
        }
        
        return internalServerError
            ("Internal server error: unable to resolve \""+name+"\"!"); 
    }

    final static Resolver[] RESOLVERS = new Resolver[] {
        new NCIStructureResolver (),
        new PubChemStructureResolver ()
    };
    static Result _resolve (String name) throws Exception {
        ObjectMapper mapper = new ObjectMapper ();
        
        ArrayNode results = mapper.createArrayNode();
        
        for (Resolver r : RESOLVERS) {
            Object value = r.resolve(name);
            if (value != null) {
                ObjectNode node = mapper.createObjectNode();
                node.put("source", r.getName());
                node.put("kind", r.getType().getName());
                node.put("value", mapper.valueToTree(value));
                results.add(node);
            }
        }

        return ok (results);
    }
    
    
    
    
    //SHOULD MOVE
    //
    //===========================================================================================

    private static DisplayParams preProcessChemical(Chemical c, DisplayParams dp){
    	if(c!=null){
			boolean compColor=false;
			boolean fuse=false;
			boolean hasRgroups=hasRGroups(c);
			int rgroupColor=1;

			if(hasRgroups){
				if(fuse){
					compColor=colorChemicalComponents(c);
					if(compColor){
						dp=dp.withSpecialColor2();
					}
					if(fuseChemical(c)){
						c.clean2D();
					}
				}
				if(rgroupColor==1){
					if(!compColor && mapChemicalRgroup(c)){
						dp=dp.withSpecialColor();
					}
				}else if(rgroupColor==2){
					if(!compColor && mapChemicalRgroup(c)){
						dp=dp.withSpecialColorMON();
					}
				}
			}

		}
    	return dp;
    }
    public static boolean hasRGroups(Chemical c){
		boolean r=false;
		for(ChemicalAtom ca:c.getAtomArray()){
			int rindex=	ca.getRgroupIndex();
			if(rindex>0){
				r= true;
			}else{
				//r=true;
				if(ca.getAlias().startsWith("_")){
					ca.setRgroupIndex(Integer.parseInt(ca.getAlias().replace("_R", "")));
					ca.setAlias(ca.getAlias().replace("_",""));
					r= true;
				}
			}
		}
		return r;
	}
    public static boolean colorChemicalComponents(Chemical c){
		int[] mapAssign= new int[c.getAtomCount()];
		int i = 2;
		int con=80;
		Chemical c2 = c.cloneChemical();
		c2.generateAtomMap();
		Iterable<Chemical> components = c2.getComponents();
		for (Chemical c1 : components) {
			for (ChemicalAtom ca : c1.getAtomArray()) {
				mapAssign[ca.getAtomMap()-1] = i+con;
			}
			i--;
		}
		if(i>=1){
			return false;
		}
		int aindex=0;
		for(ChemicalAtom ca:c.getAtomArray()){
			ca.setAtomMap(mapAssign[aindex++]);
		}
		return true;
	}
    public static boolean mapChemicalRgroup(Chemical c){
		boolean change=false;
		for(ChemicalAtom ca:c.getAtomArray()){
			int rindex=	ca.getRgroupIndex();
			if(rindex>0){
				ca.setAtomMap(rindex);
				change=true;
			}
		}
		return change;
	}
	public static boolean fuseChemical(Chemical c){
		Map<Integer,ChemicalAtom> needLink = new HashMap<Integer,ChemicalAtom>();
		Set<ChemicalAtom> toRemove=new HashSet<ChemicalAtom>();


		for(ChemicalAtom ca:c.getAtomArray()){

			int rindex=	ca.getRgroupIndex();

			if(rindex>0){
				ChemicalAtom newNeighbor=needLink.get(rindex);
				if(newNeighbor==null){
					needLink.put(rindex,ca);
				}else{
					needLink.remove(rindex);
					for(ChemicalAtom ca2:ca.getNeighbors()){
						for(ChemicalAtom ca3:newNeighbor.getNeighbors()){
							c.addBond(ca2,ca3,1,0);
						}
					}
					toRemove.add(ca);
					toRemove.add(newNeighbor);

				}
			}
		}
		toRemove.forEach(ca -> c.removeAtom(ca));
		return toRemove.size()>0;
	}
	
	public static long getNumberOfRunningThreads(){
		return Thread.getAllStackTraces()
				.keySet()
				.stream()
				.filter(t-> (t.getState()==Thread.State.RUNNABLE))
				.count();
	}
	
}
