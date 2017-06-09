package ix.ginas.initializers;

import ix.core.adapters.EntityPersistAdapter;
import ix.core.initializers.Initializer;
import ix.core.models.UserProfile;
import ix.core.plugins.CronExpressionBuilder;
import ix.core.plugins.SchedulerPlugin.ScheduledTask;
import ix.core.plugins.TextIndexerPlugin;
import ix.core.utils.executor.ProcessExecutionService;
import ix.core.utils.executor.ProcessExecutionService.CommonConsumers;
import ix.core.utils.executor.ProcessExecutionService.CommonStreamSuppliers;
import ix.core.utils.executor.ProcessListener;
import ix.ginas.controllers.GinasApp;
import ix.ginas.controllers.GinasAppAdmin;
import ix.ginas.utils.GinasGlobal;
import ix.ncats.controllers.auth.Authentication;
import play.Application;
import play.Play;

public class ReindexTaskInitializer implements Initializer{

    @Override
    public void onStart(Application app) {
        ScheduledTask.of((l) -> {
            try {
                l.message("Initializing reindexing");
                ProcessListener listen = ProcessListener.onCountChange((sofar,total)->{
                    if(total!=null){
                        l.message("Indexed:" + sofar + " of " + total);
                    }else{
                        l.message("Indexed:" + sofar);
                    }
                })
                .and(GinasApp.getReindexListener())
                .and(Play.application().plugin(TextIndexerPlugin.class).getIndexer())
                .and(EntityPersistAdapter.getInstance().getProcessListener());
                
                new ProcessExecutionService(5, 10).buildProcess(Object.class)
                        .consumer(CommonConsumers.REINDEX_FAST)
                        .streamSupplier(CommonStreamSuppliers.allBackups())
                        .before(ProcessExecutionService::nukeEverything)
                        .listener(listen)
                        .build()
                        .execute();
                
                
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        })
        .wrap((r)->{
            GinasGlobal.runWithIntercept(r, (req)->{
                UserProfile p = Authentication.getUserProfile();
                return !GinasAppAdmin.isAdminRequest(req, p);
            });
        })
        .at(CronExpressionBuilder.builder()
                .on()
                .first()
                .saturdayOfEveryMonth()
                .atHourAndMinute(2, 15))
        .description("Reindex All Entities") //Brief description of the task
        .disable()                           //do not activate the schedule by default
        .submit();                           //submit to scheduler
        
    }

}