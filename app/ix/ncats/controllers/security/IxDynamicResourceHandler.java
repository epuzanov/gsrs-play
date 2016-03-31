package ix.ncats.controllers.security;

import be.objectify.deadbolt.core.DeadboltAnalyzer;
import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.AbstractDynamicResourceHandler;
import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import ix.core.models.Role;
import play.Logger;
import play.Play;
import play.mvc.Http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class IxDynamicResourceHandler implements DynamicResourceHandler {
    public static final String CAN_APPROVE = "canApprove";
    public static final String CAN_REGISTER = "canRegister";
    public static final String CAN_UPDATE = "canUpdate";
    public static final String CAN_SEARCH = "canSearch";
    public static final String IS_ADMIN = "isAdmin";
    public static final String IS_USER_PRESENT = "isUserPresent";
    private static Map<String, DynamicResourceHandler> HANDLERS;

    static {
       init();
    }

    public static void init(){
        HANDLERS = new HashMap<String, DynamicResourceHandler>();

        HANDLERS.put(IS_ADMIN,
                new AbstractDynamicResourceHandler() {
                    public boolean isAllowed(final String name,
                                             final String meta,
                                             final DeadboltHandler deadboltHandler,
                                             final Http.Context context) {

                        if(Play.application().configuration().getBoolean("ix.admin", false))return true;

                        Subject subject = deadboltHandler.getSubject(context);
                        boolean allowed=false;
                        DeadboltAnalyzer analyzer = new DeadboltAnalyzer();

                        if (analyzer.hasRole(subject, Role.Admin.toString())) {
                            allowed = true;
                        }
                        return allowed;
                    }
                });
        HANDLERS.put(CAN_APPROVE,
                new SimpleRoleDynamicResourceHandler(
                        Role.Updater,
                        Role.SuperUpdate
                ));
        HANDLERS.put(CAN_REGISTER,
                new SimpleRoleDynamicResourceHandler(
                        Role.DataEntry,
                        Role.SuperDataEntry
                ));
        HANDLERS.put(CAN_UPDATE,
                new SimpleRoleDynamicResourceHandler(
                        Role.Updater,
                        Role.SuperUpdate
                ));
        HANDLERS.put(CAN_SEARCH,  new AbstractDynamicResourceHandler() {
            public boolean isAllowed(final String name,
                                     final String meta,
                                     final DeadboltHandler deadboltHandler,
                                     final Http.Context ctx){

                return true;
            }

        });

        HANDLERS.put(IS_USER_PRESENT,
                new AbstractDynamicResourceHandler() {
                    public boolean isAllowed(final String name,
                                             final String meta,
                                             final DeadboltHandler deadboltHandler,
                                             final Http.Context context) {
                        Subject subject = deadboltHandler.getSubject(context);
                        boolean allowed=false;
                        if (subject != null &&(subject.getIdentifier() != null || !subject.getIdentifier().equals(""))) {
                            allowed = true;
                        }
                        return allowed;
                    }
                });


    }
    
    public static class SimpleRoleDynamicResourceHandler extends AbstractDynamicResourceHandler{
    	Role[] roles;
    	public SimpleRoleDynamicResourceHandler(Role... kind){
    		roles=kind;
    	}
    	public boolean isAllowed(final String name,
                final String meta,
                final DeadboltHandler deadboltHandler,
                final Http.Context context) {
    			//System.out.println("OK ... let's look then");
    			Subject subject=null;
    			try{
    				subject = deadboltHandler.getSubject(context);
    				
    			}catch(Exception e){
    				e.printStackTrace();
    			}
				boolean allowed=false;
				
				DeadboltAnalyzer analyzer = new DeadboltAnalyzer();
				
				for(Role k:roles){
					if (analyzer.hasRole(subject, k.toString())) {
						allowed = true;
						//System.out.println("Got it");
						break;
					}
				}
				return allowed;
		}
    	
    }
    
    

    //this will be invoked for Dynamic
    public boolean isAllowed(String name,
                             String meta,
                             DeadboltHandler deadboltHandler,
                             Http.Context context) {
    	//System.out.println("Authorizing:" + name);
        DynamicResourceHandler handler = HANDLERS.get(name);
        boolean result = false;
        if (handler == null) {
            Logger.error("No handler available for " + name);
        } else {
            result = handler.isAllowed(name,
                    meta,
                    deadboltHandler,
                    context);
        }
        return result;
    }

    //this will be invoked for custom Pattern checking
    public boolean checkPermission(final String permissionValue,
                                   final DeadboltHandler deadboltHandler,
                                   final Http.Context ctx) {
        Subject subject = deadboltHandler.getSubject(ctx);
        boolean permissionOk = false;

        if (subject != null) {
            List<? extends Permission> permissions = subject.getPermissions();
            for (Iterator<? extends Permission> iterator = permissions.iterator(); !permissionOk && iterator.hasNext(); ) {
                Permission permission = iterator.next();
                permissionOk = permission.getValue().contains(permissionValue);
            }
        }

        return permissionOk;
    }
}