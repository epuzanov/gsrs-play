package ix.core.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiConsumer;

import play.Logger;

public class SearchOptions {
    public static class TermFilter{
    	String field;
    	String term;
    	public TermFilter(String field, String term){
    		this.field=field;
    		this.term=term;
    	}
    	public String getField(){
    		return this.field;
    	}
    	public String getTerm(){
    		return this.term;
    	}
    }
    
    
    public static class FacetLongRange {
        public String field;
        public Map<String, long[]> range = new TreeMap<String, long[]>();

        public FacetLongRange (String field) {
            this.field = field;
        }

        public void add (String title, long[] range) {
            this.range.put(title, range);
        }
    }
    
    public static final int DEFAULT_TOP = 10;
    public static final int DEFAULT_FDIM = 10;
    
    
    // default number of elements to fetch while blocking
    public static final int DEFAULT_FETCH_SIZE = 100; // 0 means all
    
    public Class<?> kind; // filter by type

    public int top = DEFAULT_TOP;
    public int skip;
    public int fetch = DEFAULT_FETCH_SIZE;
    public int fdim = DEFAULT_FDIM; // facet dimension
    // whether drilldown (false) or sideway (true)
    public boolean sideway = true;
    public String filter;
    
    /**
     * Facet is of the form: DIMENSION/VALUE...
     */
    public List<String> facets = new ArrayList<String>();
    public List<FacetLongRange> longRangeFacets =new ArrayList<FacetLongRange>();
    public List<String> order = new ArrayList<String>();
    public List<String> expand = new ArrayList<String>();
    public List<TermFilter> termFilters = new ArrayList<TermFilter>();

    public SearchOptions () { }
    public SearchOptions (Class<?> kind) {
        this.kind = kind;
    }
    public SearchOptions (Class<?> kind, int top, int skip, int fdim) {
        this.kind = kind;
        this.top = Math.max(1, top);
        this.skip = Math.max(0, skip);
        this.fdim = Math.max(1, fdim);
    }
    public SearchOptions (Map<String, String[]> params) {
        parse (params);
    }

    public void setFacet (String facet, String value) {
        facets.clear();
        addFacet (facet, value);
    }
    public void addFacet (String facet, String value) {
        facets.add(facet+"/"+value);
    }

    public int max () { return skip+top; }

    public SearchOptions parse (Map<String, String[]> params) {
        for (Map.Entry<String, String[]> me : params.entrySet()) {
            if ("facet".equalsIgnoreCase(me.getKey())) {
                for (String s : me.getValue()){
                    facets.add(s);
                }
            }
            else if ("order".equalsIgnoreCase(me.getKey())) {
                for (String s : me.getValue())
                    order.add(s);
            }
            else if ("expand".equalsIgnoreCase(me.getKey())) {
                for (String s : me.getValue())
                    expand.add(s);
            }
            else if ("drill".equalsIgnoreCase(me.getKey())) {
                for (String s : me.getValue())
                    sideway = "sideway".equalsIgnoreCase(s);
            }
            else if ("kind".equalsIgnoreCase(me.getKey())) {
                if (this.kind == null) {
                    for (String kind: me.getValue()) {
                        if (kind.length() > 0) {
                            try {
                                this.kind = Class.forName(kind);
                                break; // there should only be one!
                            }
                            catch (Exception ex) {
                                Logger.error
                                    ("Unable to load class: "+kind, ex);
                            }
                        }
                    }
                }
            }
            else if ("fetch".equalsIgnoreCase(me.getKey())) {
                for (String s : me.getValue()) {
                    try {
                        fetch = Integer.parseInt(s);
                        
                    }
                    catch (NumberFormatException ex) {
                        Logger.error("Not a valid number: "+s);
                    }
                }
            }
            else if ("top".equalsIgnoreCase(me.getKey())) {
                for (String s : me.getValue()) {
                    try {
                        top = Integer.parseInt(s);
                    }
                    catch (NumberFormatException ex) {
                        Logger.error("Not a valid number: "+s);
                    }
                }
            }
            else if ("skip".equalsIgnoreCase(me.getKey())) {
                for (String s : me.getValue()) {
                    try {
                        skip = Integer.parseInt(s);
                    }
                    catch (NumberFormatException ex) {
                        Logger.error("Not a valid number: "+s);
                    }
                }
            }
            else if ("fdim".equalsIgnoreCase(me.getKey())) {
                for (String s : me.getValue()) {
                    try {
                        fdim = Integer.parseInt(s);
                    }
                    catch (NumberFormatException ex) {
                        Logger.error("Not a valid number: "+s);
                    }
                }
            }else if ("filter".equalsIgnoreCase(me.getKey())){
            	for (String s : me.getValue()) {
                    //TODO
                }
            }
        }
        return this;
    }
    
    
    public void removeAndConsumeRangeFilters(BiConsumer<String,long[]> cons){
    	//Map<String, List<Filter>> filters = new HashMap<String, List<Filter>>();
		List<String> remove = new ArrayList<String>();
		for (String f : this.facets) {
			int pos = f.indexOf('/');
			if (pos > 0) {
				String facet = f.substring(0, pos);
				String value = f.substring(pos + 1);
				// options.longRangeFacets.stream()
				for (SearchOptions.FacetLongRange flr : this.longRangeFacets) {
					if (facet.equals(flr.field)) {
						long[] range = flr.range.get(value);
						if (range != null) {
							// add this as filter..
							cons.accept(facet, range);
						}
						remove.add(f);
					}
				}
			}
		}
		this.facets.removeAll(remove);
    }
    
    public List<String> getFacets(){
    	return this.facets;
    }
    
    public String toString () {
        StringBuilder sb = new StringBuilder
            ("SearchOptions{kind="+(kind!=null ? kind.getName():"")
             +",top="+top+",skip="+skip+",fdim="+fdim+",fetch="+fetch
             +",sideway="+sideway+",filter="+filter+",facets={");
        for (Iterator<String> it = facets.iterator(); it.hasNext(); ) {
            sb.append(it.next());
            if (it.hasNext()) sb.append(",");
        }
        sb.append("},order={");
        for (Iterator<String> it = order.iterator(); it.hasNext(); ) {
            sb.append(it.next());
            if (it.hasNext()) sb.append(",");
        }
        sb.append("},expand={");
        for (Iterator<String> it = expand.iterator(); it.hasNext(); ) {
            sb.append(it.next());
            if (it.hasNext()) sb.append(",");
        }
        sb.append("}}");
        return sb.toString();
    }
    
    private DrillAndPath parseDrillAndPath(String dd){
		int pos = dd.indexOf('/');
		if (pos > 0) {
			String facet = dd.substring(0, pos);
			String value = dd.substring(pos + 1);
			String[] drill = value.split("/");
			for (int i = 0; i < drill.length; i++) {
				drill[i] = drill[i].replace("$$", "/");
			}
			return new DrillAndPath(facet,drill);
		}
		return null;
    } 
    public Map<String,List<DrillAndPath>> getDrillDownsMap(){
    	Map<String,List<DrillAndPath>> providedDrills = new HashMap<String,List<DrillAndPath>>();
    	// the first term is the drilldown dimension
		this.facets.stream()
			.map(dd->parseDrillAndPath(dd))
			.filter(Objects::nonNull)
			.forEach(dp->
				providedDrills.computeIfAbsent(dp.getDrill(),t->new ArrayList<DrillAndPath>()).add(dp)
			);
		return providedDrills;
    }
    
    public class DrillAndPath{
		private String drill;
		private String[] paths;
		public DrillAndPath(String drill, String[] path){
			this.drill=drill;
			this.paths=path;
		}
		
		public String asLabel(){
			return String.join("/", getPaths());
		}

		public String getDrill() {
			return drill;
		}

		public String[] getPaths() {
			return paths;
		}

	}
}
