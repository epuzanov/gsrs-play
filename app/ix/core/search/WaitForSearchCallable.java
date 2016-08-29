package ix.core.search;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

class WaitForSearchCallable implements Callable<List>, SearchResultDoneListener{

	private final SearchResult result;
	
	private final CountDownLatch latch;
	private boolean waitForAll;

	public WaitForSearchCallable(final SearchResult result){
		Objects.requireNonNull(result);
		this.result = result;
		this.result.addListener(this);
        waitForAll = true;
        latch = new CountDownLatch(1);
	}
    public WaitForSearchCallable(final SearchResult result, int numberOfRecords){
        Objects.requireNonNull(result);
        this.result = result;
        this.result.addListener(this);
        //can't have negative counts
        int count = Math.max(0, numberOfRecords - result.matches.size());
        latch = new CountDownLatch(count);
        waitForAll = false;
    }
	@Override
	public List call() throws Exception {
		if(latch.getCount()>0 && !result.finished()){
			latch.await();
		}
        result.removeListener(this);
		return result.getMatches();
	}
	@Override
	public void searchIsDone() {
		while(latch.getCount()>0){
            latch.countDown();
        }
	}

    @Override
    public void added(Object o) {
        if(!waitForAll){
            latch.countDown();
        }
    }
}