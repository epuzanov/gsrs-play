package ix.test.ix.test.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jolbox.bonecp.BoneCPDataSource;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import ix.core.adapters.EntityPersistAdapter;
import ix.core.controllers.AdminFactory;
import ix.core.controllers.EntityFactory;
import ix.core.controllers.PrincipalFactory;
import ix.core.controllers.UserProfileFactory;
import ix.core.controllers.search.SearchFactory;
import ix.core.controllers.v1.RouteFactory;
import ix.core.models.Group;
import ix.core.models.Principal;
import ix.core.models.Role;
import ix.core.models.UserProfile;
import ix.core.plugins.TextIndexerPlugin;
import ix.ginas.controllers.GinasFactory;
import ix.ginas.controllers.GinasLoad;
import ix.ginas.controllers.v1.SubstanceFactory;
import ix.ginas.utils.validation.Validation;
import ix.ncats.controllers.App;
import ix.ncats.controllers.auth.Authentication;
import ix.ncats.controllers.security.IxDynamicResourceHandler;
import ix.seqaln.SequenceIndexer;
import ix.test.util.TestUtil;
import net.sf.ehcache.CacheManager;
import org.apache.commons.io.FileUtils;
import org.junit.rules.ExternalResource;
import org.w3c.dom.Document;
import play.db.ebean.Model;
import play.libs.ws.WSCookie;
import play.libs.ws.WSResponse;
import play.test.TestServer;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import static play.test.Helpers.testServer;

/**
 * JUnit Rule to handle starting and stopping
 * a Ginas Server around each @Test method.
 *
 * <p>
 *     Example usage:
 *
 *     <pre>
 *         @Rule
 *         GinasTestServer ts = new GinasTestServer(9001);
 *
 *         ...
 *
 *         @Test
 *         public void myTest(){
 *             ts.run(new Callable<Void>() {
 *
 *                public Void call(){
 *                 //do stuff
 *                 return null;
 *             });
 *         }
 *
 *         @Test
 *         public void withJava8Lambda(){
 *             ts.run( () -> {
 *                //do stuff
 *                });
 *         }
 *     </pre>
 *
 *
 * </p>
 *
 *
 *
 *
 * Created by katzelda on 2/25/16.
 */
public class GinasTestServer extends ExternalResource{


    public static int DEFAULT_PORT = 9001;

     private static final String FAKE_USERNAME_PREFIX="FAKE";
     private static final String FAKE_PASSWORD_PREFIX="pa$$word";
     
	 public static final String FAKE_USER_1="fakeuser1";
	 public static final String FAKE_USER_2="fakeuser2";
	 public static final String FAKE_USER_3="fakeuser3";

	 public static final String FAKE_PASSWORD_1="madeup1";
	 public static final String FAKE_PASSWORD_2="madeup2";
	 public static final String FAKE_PASSWORD_3="madeup3";


    private final BrowserSession defaultBrowserSession;
    private final RestSession defaultRestSession;

    private List<AbstractSession> sessions = new ArrayList<>();

    private TestServer ts;
    private int port;
    private Group fakeUserGroup;

    private static final List<Role> superUserRoles = Role.roles(Role.SuperUpdate,Role.SuperDataEntry );
    private static final List<Role> normalUserRoles = Role.roles(Role.DataEntry,Role.Updater );

    private static final List<Role> adminUserRoles = Role.roles(Role.values() );

    private int userCount=0;

    private boolean running=false;

   private Model.Finder<Long, Principal> principleFinder;


    private File storage;

    public static class User{
    	private final String username;
    	private String password;

        public User(String username, String password) {
            Objects.requireNonNull(username);
            Objects.requireNonNull(password);

            this.username = username;
            this.password = password;
        }

        public String getPassword(){
            return password;
        }
        public String getUserName() {
            return username;
        }


    }

    /**
     * Create a new instance listening on the default port.
     */
    public GinasTestServer(){
        this(DEFAULT_PORT);
    }
    public GinasTestServer(int port){
       this.port = port;
        defaultBrowserSession = new BrowserSession(port){
            @Override
            protected WSResponse doLogout() {
                //no-op
                return new WSResponse() {
                    @Override
                    public Map<String, List<String>> getAllHeaders() {
                        return Collections.emptyMap();
                    }

                    @Override
                    public Object getUnderlying() {
                        return null;
                    }

                    @Override
                    public int getStatus() {
                        return 200;
                    }

                    @Override
                    public String getStatusText() {
                        return "";
                    }

                    @Override
                    public String getHeader(String key) {
                        return null;
                    }

                    @Override
                    public List<WSCookie> getCookies() {
                        return Collections.emptyList();
                    }

                    @Override
                    public WSCookie getCookie(String name) {
                        return null;
                    }

                    @Override
                    public String getBody() {
                        return "";
                    }

                    @Override
                    public Document asXml() {
                        return null;
                    }

                    @Override
                    public JsonNode asJson() {
                        try {
                            return new ObjectMapper().readTree("");
                        } catch (IOException e) {
                            //can't happen
                            return null;
                        }
                    }

                        @Override
                    public InputStream getBodyAsStream() {
                        return null;
                    }

                    @Override
                    public byte[] asByteArray() {
                        return new byte[0];
                    }

                    @Override
                    public URI getUri() {
                        return null;
                    }
                };
            }

            @Override
            public boolean isLoggedIn() {
                return true;
            }
        };
        defaultRestSession = new RestSession(port){
            @Override
            protected Void doLogout() {
                //no-op
                return null;
            }

            @Override
            public boolean isLoggedIn() {
                return true;
            }
        };
    }

    private void createInitialFakeUsers() {
        fakeUserGroup= AdminFactory.groupfinder.where().eq("name", "fake").findUnique();
        if(fakeUserGroup ==null){
            fakeUserGroup=new Group("fake");
        }
        List<Group> groups = Collections.singletonList(fakeUserGroup);

        UserProfileFactory.addActiveUser(FAKE_USER_1,FAKE_PASSWORD_1, superUserRoles,groups);
        UserProfileFactory.addActiveUser(FAKE_USER_2,FAKE_PASSWORD_2, superUserRoles,groups);

        UserProfileFactory.addActiveUser(FAKE_USER_3,FAKE_PASSWORD_3,normalUserRoles,groups);
    }


    public User getFakeUser1(){
        return new User(FAKE_USER_1,FAKE_PASSWORD_1);
    }
    public User getFakeUser2(){
        return new User(FAKE_USER_2,FAKE_PASSWORD_2);
    }
    public User getFakeUser3(){
        return new User(FAKE_USER_3,FAKE_PASSWORD_3);
    }






    public RestSession notLoggedInRestSession(){
        return defaultRestSession;
    }
    public BrowserSession notLoggedInBrowserSession(){
        return defaultBrowserSession;
    }
    public BrowserSession newBrowserSession(User user){
        BrowserSession session = new BrowserSession(user, port);
        sessions.add(session);
        return session;
    }

    public RestSession newRestSession(User user){
        RestSession session= new RestSession(user, port);
        sessions.add(session);
        return session;
    }
    public RestSession newRestSession(User user, RestSession.AUTH_TYPE type){
        RestSession session= new RestSession(user, port, type);
        sessions.add(session);
        return session;
    }


    public User createAdmin(String username, String password){
        return createUser(username, password, adminUserRoles);
    }

    public User createNormalUser(String username, String password){
        return createUser(username, password, normalUserRoles);
    }
    public User createUser(Role role, Role ... roles){
        userCount++;
        return createUser(
                FAKE_USERNAME_PREFIX + userCount,
                FAKE_PASSWORD_PREFIX + userCount, role, roles);
    }
    public User createUser(String username, String password, Role role, Role ... roles){
        List<Role> list = new ArrayList<>();
        list.add(role);
        for(Role r: roles){
            list.add(r);
        }
        return createUser(username, password,list);
    }
    public User createUser(String username, String password, List<Role> roles){
    	if(roles.isEmpty()){
            throw new IllegalArgumentException("roles can not be empty");
        }

        Principal existingUser = principleFinder.where().eq("username", username).findUnique();
        if(existingUser !=null){
            throw new IllegalArgumentException("user already exists: " + username);
        }
    	UserProfile up=UserProfileFactory.addActiveUser(username, password, roles, Collections.singletonList(fakeUserGroup));
    	return new User(up.getIdentifier(), password);
    }

    public boolean isOracleDB(){
        String dbUrl = ConfigUtil.getDefault().getValueAsString("db.default.url");
        return dbUrl.contains("jdbc:oracle:thin");
    }



    @Override
    protected void before() throws Throwable {
       if(isOracleDB()){
           System.out.println("in the Oracle db loop");
           dropOracleDb();
       }else { //h2 for now
           System.out.println("in the h2 db loop");
           deleteH2Db();
       }

        //This cleans out the old eh-cache
        //and forces us to use a new one with each test
        CacheManager.getInstance().shutdown();
        start();
   }

    private void initializeControllers() {

        App.init();

        Validation.init();
        AdminFactory.init();
        RouteFactory.init();
        Authentication.init();

        IxDynamicResourceHandler.init();

        UserProfileFactory.init();
        PrincipalFactory.init();

        SubstanceFactory.init();

        EntityFactory.init();
        SearchFactory.init();

        EntityPersistAdapter.init();

        SequenceIndexer.init();

        GinasLoad.init();
        GinasFactory.init();
        //our APIs
       // SubstanceLoader.init();
    }

    private void deleteH2Db() throws IOException {
        File home = ConfigUtil.getDefault().getValueAsFile("ix.home");
        TestUtil.tryToDeleteRecursively(home);
    }



    public void dropOracleDb() throws IOException {
        Config confFile = ConfigUtil.getDefault().getConfig();
        String source = "default";
        Config dbconf = confFile.getConfig("db");
        Config db = dbconf.getConfig(source);
        BoneCPDataSource ds = new BoneCPDataSource();
        ds.setJdbcUrl(db.getString("url"));
        ds.setUsername(db.getString("user"));
        ds.setPassword(db.getString("password"));
        DataSource datasource = ds;

        try {

            Connection con = datasource.getConnection();
            Statement stm = con.createStatement();

            String evolutionContent = FileUtils.readFileToString(new File("conf/evolutions/default/1.sql"));
            String[] splittedEvolutionContent = evolutionContent.split("# --- !Ups");
            String[] upsDowns = splittedEvolutionContent[1].split("# --- !Downs");
            String createDdl = upsDowns[0];
            String dropDdl = upsDowns[1];
            String[] ups = createDdl.split(";");
            String[] downs = dropDdl.split(";");
            List tableName = new ArrayList<String>();
            List seqName = new ArrayList<String>();



            PreparedStatement ps = con.prepareStatement("SELECT TABLE_NAME FROM USER_TABLES");
            ResultSet rs = ps.executeQuery();
            while(rs.next())
            {
                tableName.add( rs.getString(1).trim());
            }
            System.out.println("DROP TABLES XXXXXXXXXXXXXXXXXX");

            for(int i = 0; i<tableName.size(); i++)
            {
                String tName = tableName.get(i).toString();
                String query = "drop table " + tName + " cascade constraints purge";
                //System.out.println("query:" + query);
               try {
                   stm.executeQuery(query);
               }catch(Exception e){
                  System.out.println(e.getMessage());
               }
            }

            PreparedStatement ps1 = con.prepareStatement("select SEQUENCE_NAME from dba_sequences where SEQUENCE_OWNER = '" + ds.getUsername() +"'");
            ResultSet rs1 = ps1.executeQuery();
            while(rs1.next())
            {
                seqName.add( rs1.getString(1).trim());
            }
            System.out.println("DROP SEQUENCES XXXXXXXXXXXXXXXXXX");
            for(int i = 0; i<seqName.size(); i++)
            {
                String sName = seqName.get(i).toString();
                String query = "drop sequence " + sName;
                try {
                    stm.executeQuery(query);
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }

           System.out.println("CREATE TABLES XXXXXXXXXXXXXXXXXX");
           for(int i = 0; i<ups.length; i++)
            {
                try{
                   if(ups[i] != null && !ups[i].trim().equals("")) {
                       stm.executeQuery(ups[i]);
                   }
                }catch(Exception e){
                    System.out.println("query - " + ups[i] +" : " + e.getMessage());
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void after() {
        stop();
    }

    /**
     * Start the server, stopping the
     * old one if it's still running.
     *
     * This does not delete the h2 database.
     *
     * This is the same as calling:
     *
     * <pre>
     *     stop(true);
     *     start();
     *
     *
     * </pre>
     */
    public void restart(){
        System.out.println("restarting...");
        stop(true);

        start();
    }

    public void start() {
        if(running){
            return;
        }
        running = true;
        ts = testServer(port);
        ts.start();

        principleFinder =
                new Model.Finder(Long.class, Principal.class);

        initializeControllers();
        //we have to wait to create the users until after Play has started.
        createInitialFakeUsers();
    }

    /**
     * Stop the server and optionally preserve
     * the h2 databases and indexes computed so far
     * so that on the next start of the server, we re-use
     * them.
     * @param preserveDatabase {@code true} if the h2 database and indexes
     *                         should be preserved; {@code false} if they should be deleted.
     */
    public void stop(boolean preserveDatabase){
        if(running) {

            running = false;
            for (AbstractSession session : sessions) {
                session.logout();
            }
            sessions.clear();
            //explicitly shutdown indexer to clear file locks
            App._textIndexer.shutdown();
            ts.stop();

        }
        if(preserveDatabase){
            TextIndexerPlugin.prepareTestRestart();
        }
    }

    /**
     * Stop the server and do not preserve
     * the databases and indexes.  This is the same
     * as {@link #stop(boolean) stop(false}.
     *
     * @see #stop(boolean)
     */
    public void stop() {
       stop(false);
    }

    public File getStorageRootDir(){
        return TextIndexerPlugin.getStorageRootDir();
    }

}
