package ix.test.ix.test.server;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;

import static play.test.Helpers.testServer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
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
import ix.core.models.Role;
import ix.core.models.UserProfile;
import ix.ginas.controllers.v1.SubstanceFactory;
import ix.ginas.utils.validation.Validation;
import ix.ncats.controllers.App;
import ix.ncats.controllers.auth.Authentication;
import ix.ncats.controllers.security.IxDynamicResourceHandler;
import ix.seqaln.SequenceIndexer;
import net.sf.ehcache.CacheManager;
import org.junit.rules.ExternalResource;

import com.fasterxml.jackson.databind.JsonNode;

import org.w3c.dom.Document;
import play.libs.ws.WS;
import play.libs.ws.WSCookie;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import play.test.TestServer;

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
    	UserProfile up=UserProfileFactory.addActiveUser(username, password, roles, Collections.singletonList(fakeUserGroup));
    	return new User(up.getIdentifier(), password);
    }
    


    @Override
    protected void before() throws Throwable {
        deleteH2Db();
        //This cleans out the old eh-cache
        //and forces us to use a new one with each test
        CacheManager.getInstance().shutdown();

        ts = testServer(port);
        ts.start();
        initializeControllers();
        //we have to wait to create the users until after Play has started.
        createInitialFakeUsers();
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
    }

    private void deleteH2Db() throws IOException {
        Config load = ConfigFactory.load();
        //System.out.println(load.entrySet());
        Path path = new File(load.getString("ix.home")).toPath();
        if(!path.toFile().exists()){
            return;
        }
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {


            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                //we've have NFS problems where there are lock
                //objects that we can't delete
                //should be safe to keep them and delete every other file.
                if(!file.toFile().getName().startsWith(".nfs")){
                    //use new delete method which throws IOException
                    //if it can't delete instead of returning flag
                    //so we will know the reason why it failed.
                    try{
                        Files.delete(file);
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }
                else{
                    System.out.println("found nfs file " + file.toString());
                }


                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    protected void after() {
        for(AbstractSession session : sessions){
            session.logout();
        }
        sessions.clear();
        //explicitly shutdown indexer to clear file locks
        App._textIndexer.shutdown();
        ts.stop();
    }







}