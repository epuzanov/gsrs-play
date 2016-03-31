package ix.test;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import ix.test.ix.test.server.GinasTestServer;
import ix.test.ix.test.server.RestSession;
import ix.test.ix.test.server.SubstanceAPI;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.diff.JsonDiff;

import ix.ginas.models.v1.Substance;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class ApprovalWorkflowTest {
	

    @Rule
    public GinasTestServer ts = new GinasTestServer(9001);

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            System.out.println("Starting test: " + getClass().getCanonicalName() + " . " + description.getMethodName());
        }
    };
    

    
	@Test
	public void testApprovalRoundTrip() throws Exception {
        String uuid;
        final File resource=new File("test/testJSON/toapprove.json");
        try(RestSession session = ts.newRestSession(ts.getFakeUser1());
            InputStream is = new FileInputStream(resource)){


                JsonNode js = new ObjectMapper().readTree(is);

                uuid = js.get("uuid").asText();

                SubstanceAPI api = new SubstanceAPI(session);

                JsonNode jsonNode2 = api.submitSubstanceJson(js);
                assertEquals(Substance.STATUS_PENDING, SubstanceJsonUtil.getApprovalStatus(jsonNode2));

                SubstanceJsonUtil.ensureFailure(api.approveSubstance(uuid));
            }


        try(RestSession session2 = ts.newRestSession(ts.getFakeUser2())){
            String approvalID;
            JsonNode before = null;
            JsonNode after = null;

            SubstanceAPI api2 = new SubstanceAPI(session2);
            //approval, CAN approve if different user

                before = api2.approveSubstanceJson(uuid);
                approvalID = SubstanceJsonUtil.getApprovalId(before);



              after = api2.fetchSubstanceJson(uuid);
                assertEquals(Substance.STATUS_APPROVED, SubstanceJsonUtil.getApprovalStatus(after));
                assertEquals(approvalID, SubstanceJsonUtil.getApprovalId(after));

                JsonNode jp = JsonDiff.asJson(before, after);
                int changes = 0;
                for (JsonNode jschange : jp) {
                    changes++;
                    System.out.println("CHANGED:" + jschange + " old: " + before.at(jschange.get("path").asText()));
                }
                assertEquals(0,changes);

        }

	}
	
	@Test
	public void testFailNonLoggedApprover() throws Exception {
        final File resource=new File("test/testJSON/toapprove.json");
        try(RestSession session = ts.newRestSession(ts.getFakeUser1());
            InputStream is = new FileInputStream(resource)) {

            SubstanceAPI api = new SubstanceAPI(session);
            JsonNode js = new ObjectMapper().readTree(is);

            String uuid = js.get("uuid").asText();

            JsonNode jsonNode2 = api.submitSubstanceJson(js);
            assertEquals(Substance.STATUS_PENDING, SubstanceJsonUtil.getApprovalStatus(jsonNode2));

            SubstanceJsonUtil.ensureFailure(api.approveSubstance(uuid));


            session.logout();
            SubstanceJsonUtil.ensureFailure(api.approveSubstance(uuid));
        }

	}
	
	@Test
	public void testFailDoubeApproved()  throws Exception {
        final File resource=new File("test/testJSON/toapprove.json");
        try(RestSession session = ts.newRestSession(ts.getFakeUser1());
            RestSession session2 = ts.newRestSession(ts.getFakeUser2());
            InputStream is = new FileInputStream(resource)) {

            JsonNode js = new ObjectMapper().readTree(is);
            SubstanceAPI api = new SubstanceAPI(session);
            SubstanceAPI api2 = new SubstanceAPI(session2);

            String uuid = js.get("uuid").asText();
            JsonNode jsonNode2 = api.submitSubstanceJson(js);
            assertEquals(Substance.STATUS_PENDING, SubstanceJsonUtil.getApprovalStatus(jsonNode2));

            JsonNode before = api2.approveSubstanceJson(uuid);
            String approvalID1 = SubstanceJsonUtil.getApprovalId(before);
            assertNotNull("Approval ID should not be null", approvalID1);

            SubstanceJsonUtil.ensureFailure(api2.approveSubstance(uuid));

            SubstanceJsonUtil.ensureFailure(api.approveSubstance(uuid));
            JsonNode sub = api.fetchSubstanceJson(uuid);
            String approvalID2 = SubstanceJsonUtil.getApprovalId(sub);

            assertEquals(approvalID1, approvalID2);
        }

	}
	
	
	
}