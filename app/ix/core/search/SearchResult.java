package ix.core.search;

import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import ix.core.CacheStrategy;
import ix.core.search.LazyList.NamedCallable;
import ix.core.search.text.TextIndexer.Facet;
import ix.core.util.TimeUtil;
import ix.core.util.EntityUtils.EntityWrapper;
import play.Logger;

@CacheStrategy(evictable=false)
public class SearchResult {
	


    /**
     * Returns a list of FieldFacets which help to explain why and how
     * matches were done for this particular query.
     * 
     * @return
     */
    public List<FieldedQueryFacet> getFieldFacets(){
        return suggestFacets;
    }
    
    String key;
    String query;
    List<Facet> facets = new ArrayList<Facet>();
    List<FieldedQueryFacet> suggestFacets = new ArrayList<FieldedQueryFacet>();
    
    LazyList<Object> matches = new LazyList<> (o->(EntityWrapper.of(o)).getKey().getIdString());
    //List<NamedCallable> matches = new ArrayList<>();
    List<?> result; // final result when there are no more updates
    
    
    private int count;
    SearchOptions options;
    final long timestamp = TimeUtil.getCurrentTimeMillis();
    AtomicLong stop = new AtomicLong ();
    Comparator<String> idComparator = null;
    
    
    private List<SoftReference<SearchResultDoneListener>> listeners = new ArrayList<>();
    

    
    public SearchResult (SearchOptions options, String query) {
        this.options = options;
        this.query = query;
    }

    public void setRank (final Map<String, Integer> idRank) {
    	Objects.requireNonNull(idRank);
        
        idComparator = (id1,id2) ->{
            Integer r1 = idRank.get(id1), r2 = idRank.get(id2);
            if (r1 != null && r2 != null)
                return r1 - r2;
            if (r1 == null)
                Logger.error("Unknown rank for "+id1);
            if (r2 == null)
                Logger.error("Unknown rank for "+id2);
            return 0;
        };
        
    }

    public void addListener(SearchResultDoneListener listener){
    	synchronized(listeners){
    		listeners.add(new SoftReference<>(listener));
    	}
    }
    
    public void removeListener(SearchResultDoneListener listener){
    	synchronized(listeners){
        	Iterator<SoftReference<SearchResultDoneListener>> iter =listeners.iterator();
        	while(iter.hasNext()){
        		SoftReference<SearchResultDoneListener> l = iter.next();
        		SearchResultDoneListener actualListener = l.get();
        		//if get() returns null then the object was garbage collected
        		if(actualListener ==null || listener.equals(actualListener)){
        			iter.remove();
        			//keep checking in the unlikely event that
        			//a listener was added twice?
        		}
        	}
    	}
    }
    
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getQuery () { return query; }
    public SearchOptions getOptions () { return options; }
    public List<Facet> getFacets () { return facets; }
    public Facet getFacet (String name) {
    	return facets.stream()
    		.filter(n->n.getName().equalsIgnoreCase(name))
    		.findAny()
    		.orElse(null);
    	
    }
    public int size () { return matches.size(); }
    public Object get (int index) {
        throw new UnsupportedOperationException
            ("get(index) is no longer supported; please use copyTo()");
    }
    
    /**
     * Copies from the search results to the specified list
     * with specified offset (for the master results), and
     * total count of records to be copied.
     * 
     * Note: This method will block and wait for the "correct" 
     * answer only if the final <pre>wait</pre> parameter is 
     * <pre>true</pre>. Otherwise it will return whatever
     * is available.
     * 
      * @param list 
     * 	Destination list to copy into
     * @param start
     * 	Offset starting location from master list
     * @param count
     * 	Total number of records to be copied
     * @param wait
     * 	set to true for blocking for correct answer,
     *  false will return available records immediately
     * @return
     */
    public int copyTo (List list, int start, int count, boolean wait) {

    	
    	// It may be that the page that is being fetched is not yet
    	// complete. There are 2 options here then. The first is to
    	// return whatever is ready now immediately, and report the
    	// number of results (that is what had been done before).
    	
    	// The second way is to wait for the fetching to be completed
    	// which is what is demonstrated below. 
    	List matches;
    	if(wait){
    		try {
                matches =this.getMatchesFuture(start+count).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                //interrupted...make empty list?
                matches = Collections.emptyList();
            }
        }else{
    		matches = getMatches();
    	}
    	
    	//Question:
    	// Does this ever cause a problem if we're searching for
    	// something that starts beyond where we've gotten to?
    	// Like if we try to page before getting all results?
    	
    	//Answer: 
    	// not anymore. Use "wait" if that's a problem.
    	
    	if (start >= matches.size()) {
            return 0;
        }

        Iterator it = matches.listIterator(start);

        int i = 0;
        for (; i < count && it.hasNext(); ++i) {
            list.add(it.next());
        }
        
        return i;
    	
    }
    /**
     * Copies from the search results to the specified list
     * with specified offset (for the master results), and
     * total count of records to be copied.
     * 
     * Note: It may be that the search is still running, 
     * and haven't fully returned yet. In that case, this 
     * method will not wait for the results, but will return
     * immediately with any records that are ready so far.
     * 
     * To have a blocking search which waits for the "correct"
     * answer, use: <pre>copyTo(list,start,count, true)</pre>
     *
     * 
     * @param list 
     * 	Destination list to copy into
     * @param start
     * 	Offset starting location from master list
     * @param count
     * 	Total number of records to be copied
     * @return
     */
    // fill the given list with value starting at start up to start+count
    public int copyTo (List list, int start, int count) {
        return copyTo (list,start,count,false);
    }
    /**
     * Get the result of {@link #getMatches()}}
     * as a Future which runs in a background thread
     * and will block the call to Future#get() until
     * at the list is fully populated.
     *
     * @return a Future will never be null.
     */
    public Future<List> getMatchesFuture(){
        SearchResultFuture future= new SearchResultFuture(this);
       // new Thread(future).start();
        ForkJoinPool.commonPool().submit(future);
        return future;
    }

    /**
     * Get the result of {@link #getMatches()}}
     * as a Future which runs in a background thread
     * and will block the call to Future#get() until
     * at least numberOfRecords is fetched.
     * @param numberOfRecords the minimum number of records (or the list is done populating)
     *                        in the List to wait to get populated
     *                        before Future#get() unblocks.
     * @return a Future will never be null.
     */
    public Future<List> getMatchesFuture(int numberOfRecords){
    	SearchResultFuture future= new SearchResultFuture(this, numberOfRecords);
        ForkJoinPool.commonPool().submit(future);
       // new Thread(future).start();
        return future;
    }

    /**
     * Get the result of {@link #getMatches()}}
     * as a Future which runs in a background thread
     * and will block the call to Future#get() until
     * at least numberOfRecords is fetched.
     * @param numberOfRecords the minimum number of records (or the list is done populating)
     *                        in the List to wait to get populated
     *                        before Future#get() unblocks.
     *
     * @param executorService the ExecutorService to submit the Future to.
     * @return a Future will never be null.
     */
    public Future<List> getMatchesFuture(int numberOfRecords, ExecutorService executorService){
    	SearchResultFuture future= new SearchResultFuture(this, numberOfRecords);
        executorService.submit(future);
        return future;
    }
    
    public List getSponsoredMatches() {
        LazyList lazylist = new LazyList(c->c.toString());
        for(NamedCallable nc: sponsored.values()) {
            lazylist.addCallable(nc);
        }
        return lazylist;
    }

    public List getMatches () {
    	if (result != null) return result; // return if ready
        boolean finished=finished();
        
        List list = matches;
        
        if (finished) {
        	if(idComparator!=null){
        		if(list instanceof LazyList){
        			((LazyList<Object>) list).sortByNames(idComparator);
        		}else{
        			//This may take a long time in certain cases
            		Collections.sort(list,(o1,o2)->{
            			String id1 = EntityWrapper.of(o1).getKey().getIdString();
    	                String id2 = EntityWrapper.of(o2).getKey().getIdString();
    	                return idComparator.compare(id1, id2);
            		});
        		}
        	}
            result = list;
        }
        return list;
    }
    public boolean isEmpty () { return matches.isEmpty(); }
    public int count () { return getCount(); }
    public long getTimestamp () { return timestamp; }
    public long elapsed () { return stop.get() - timestamp; }
    public long getStopTime () { return stop.get(); }
    public boolean finished () { return stop.get() >= timestamp; }
    

    private Map<String, NamedCallable> sponsored = new HashMap<>();

    public void addNamedCallable (NamedCallable c) {
    	if(!sponsored.containsKey(c.getName()))
        {
            matches.addCallable(c);
    	    processAddition(c);
        }
    }

    public void addSponsoredNamedCallable (NamedCallable c) {
        //System.out.println("Sponsored record: " + c.getName());
        sponsored.put(c.getName(),c);
    	matches.addCallable(c);
    	processAddition(c);
    }
    
    protected void add (Object obj) {
    	matches.add(obj);
    	processAddition(()->obj);
    }
    
    private void processAddition(NamedCallable o){
    	notifyAdd(o);
    }
    

    private void notifyAdd(Object o){

        notifyListeners(l-> l.added(o));

    }
    

    public void done () {
        stop.set(TimeUtil.getCurrentTimeMillis());
        notifyListeners(l -> l.searchIsDone());
    }

    /**
     * Notify all listeners that haven't yet been
     * GC'ed by performing the given consumer
     * on each one.
     *
     * @param consumer the Consumer function to perform
     *                 on each listener; can not be null.
     *
     * @throws NullPointerException if consumer is null.
     */
    private void notifyListeners(Consumer<SearchResultDoneListener> consumer){
    	synchronized(listeners){
            Iterator<SoftReference<SearchResultDoneListener>> iter = listeners.iterator();
            while(iter.hasNext()){
                SearchResultDoneListener l = iter.next().get();
                //if object pointed to by soft reference
                //has been GC'ed then it is null
                if(l ==null){
                    iter.remove();
                }else{
                   consumer.accept(l);
                }

            }
    	}
    }

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void addFacet(Facet f) {
		this.facets.add(f);
	}

	public void addFieldQueryFacet(FieldedQueryFacet ff) {
		if(ff.getDisplayField()!=null){
			this.suggestFacets.add(ff);
		}
	}
}