package ix.core.plugins;

import java.io.File;
import java.io.IOException;

import play.Logger;
import play.Play;
import play.Plugin;
import play.Application;

import ix.core.search.TextIndexer;

public class TextIndexerPlugin extends Plugin {
    private final Application app;
    private IxContext ctx;
    private TextIndexer indexer;
    private boolean closed=false;
    private static int initCount=0;

    private static boolean updateStoarageCount = true;

    public TextIndexerPlugin (Application app) {
        this.app = app;
    }

    public synchronized void onStart () {

        Logger.info("Loading plugin "+getClass().getName()+"...");
        ctx = app.plugin(IxContext.class);
        if (ctx == null){
            throw new IllegalStateException
                ("IxContext plugin is not loaded!");
        }
        try {
            indexer = TextIndexer.getInstance(getStorageDir(ctx));
        }
        catch (IOException ex) {
            Logger.trace("Can't initialize text indexer", ex);
        }
    }

    private static synchronized File getStorageDir(IxContext ctx){
        File storage=ctx.text();
        System.out.println(updateStoarageCount);
        if(Play.isTest()){
            String newStorage=storage.getAbsolutePath() + initCount;
            Logger.info("Making new text index folder for test:" + newStorage);
            storage = new File(newStorage);
            storage.mkdirs();
            //Sometimes tests may hold on to folders they shouldn't
            //Here, we side-step the issue by changing the directory
            if(updateStoarageCount){
                initCount++;
            }
            //always update the storage count from now
            //on because it will usually be a restart
            updateStoarageCount=true;
        }
        System.out.println(updateStoarageCount);
        System.out.println("storage path " + storage.getAbsolutePath());
        return storage;
    }

    public static synchronized void prepareTestRestart(){
        updateStoarageCount = false;
    }

    public synchronized void onStop () {
        //We don't want to shutdown during testing
        //because the indexes get messed up
        //TODO find root cause of this issue
       // if (indexer != null && !Play.isTest()) {
        if (indexer != null) {
            indexer.shutdown();
            Logger.info("Plugin " + getClass().getName() + " stopped!");
        }
        

        closed=true;
        indexer=null;
    }
    

    public synchronized boolean enabled () { return !closed; }
    public synchronized TextIndexer getIndexer () { return indexer; }
}
