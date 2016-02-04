import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.running;
import static play.test.Helpers.stop;
import static play.test.Helpers.testServer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import play.libs.ws.WS;
import play.libs.ws.WSResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class SubstanceSubmitFailTest {

    private static final String VALIDATE_URL = "http://localhost:9001/ginas/app/api/v1/substances/@validate";
	private static long timeout= 10000L;;
    
    @Parameters(name="{0}")
    static public Collection<Object[]> findstuff(){
    	List<Object[]> mylist  =  new ArrayList<Object[]>();
    	
    	final Collection<String> list = ResourceList.getResources(Pattern.compile(".*testJSON/fail/.*json"));
    	for(String s:list){
    		mylist.add(new Object[]{s});
    	}
    	return mylist;
    }

    String resource;
    public SubstanceSubmitFailTest(String f){
    	this.resource=f.replaceAll(".*testJSON", "testJSON");
    }
        
    @Test
    public void testAPIValidateSubstance() {
    	    	
        running(testServer(9001), new Runnable() {
            public void run() {
            	final InputStream is = SubstanceSubmitFailTest.class.getResourceAsStream(resource);

            	JsonNode js=null;
            	try {
					js = (new ObjectMapper()).readTree(is);
				} catch (Exception e) {
					e.printStackTrace();
					throw new IllegalStateException(e);
				}
            	
            	System.out.println("Running: " + resource);
                WSResponse wsResponse1 = WS.url(SubstanceSubmitFailTest.VALIDATE_URL).post(js).get(timeout);
                JsonNode jsonNode1 = wsResponse1.asJson();
                assertThat(wsResponse1.getStatus()).isEqualTo(OK);
                assertThat(!jsonNode1.isNull()).isEqualTo(true);
                assertThat(jsonNode1.get("valid").asBoolean()).isEqualTo(false);
            }
        });
        
        stop(testServer(9001));
    }
    


    public static class ResourceList{

        /**
         * for all elements of java.class.path get a Collection of resources Pattern
         * pattern = Pattern.compile(".*"); gets all resources
         * 
         * @param pattern
         *            the pattern to match
         * @return the resources in the order they are found
         */
        public static Collection<String> getResources(
            final Pattern pattern){
            final ArrayList<String> retval = new ArrayList<String>();
            final String classPath = System.getProperty("java.class.path", ".");
            final String[] classPathElements = classPath.split(System.getProperty("path.separator"));
            for(final String element : classPathElements){
                retval.addAll(getResources(element, pattern));
            }
            return retval;
        }

        private static Collection<String> getResources(
            final String element,
            final Pattern pattern){
            final ArrayList<String> retval = new ArrayList<String>();
            final File file = new File(element);
            if(file.isDirectory()){
                retval.addAll(getResourcesFromDirectory(file, pattern));
            } else{
                retval.addAll(getResourcesFromJarFile(file, pattern));
            }
            return retval;
        }

        private static Collection<String> getResourcesFromJarFile(
            final File file,
            final Pattern pattern){
            final ArrayList<String> retval = new ArrayList<String>();
            ZipFile zf;
            try{
                zf = new ZipFile(file);
            } catch(final ZipException e){
                throw new Error(e);
            } catch(final IOException e){
                throw new Error(e);
            }
            final Enumeration e = zf.entries();
            while(e.hasMoreElements()){
                final ZipEntry ze = (ZipEntry) e.nextElement();
                final String fileName = ze.getName();
                final boolean accept = pattern.matcher(fileName).matches();
                if(accept){
                    retval.add(fileName);
                }
            }
            try{
                zf.close();
            } catch(final IOException e1){
                throw new Error(e1);
            }
            return retval;
        }

        private static Collection<String> getResourcesFromDirectory(
            final File directory,
            final Pattern pattern){
            final ArrayList<String> retval = new ArrayList<String>();
            final File[] fileList = directory.listFiles();
            for(final File file : fileList){
                if(file.isDirectory()){
                    retval.addAll(getResourcesFromDirectory(file, pattern));
                } else{
                    try{
                        final String fileName = file.getCanonicalPath();
                        final boolean accept = pattern.matcher(fileName).matches();
                        if(accept){
                            retval.add(fileName);
                        }
                    } catch(final IOException e){
                        throw new Error(e);
                    }
                }
            }
            return retval;
        }

        /**
         * list the resources that match args[0]
         * 
         * @param args
         *            args[0] is the pattern to match, or list all resources if
         *            there are no args
         */
        public static void main(final String[] args){
            Pattern pattern;
            if(args.length < 1){
                pattern = Pattern.compile(".*");
            } else{
                pattern = Pattern.compile(args[0]);
            }
            final Collection<String> list = ResourceList.getResources(pattern);
            for(final String name : list){
                System.out.println(name);
            }
        }
    }  
    @AfterClass
    public static void tearDown(){
        // Stop the server
       // stop(testServer);
    }
}
