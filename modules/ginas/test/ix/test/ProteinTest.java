package ix.test;

import static ix.test.SubstanceJsonUtil.ensurePass;
import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import ix.test.server.GinasTestServer;
import ix.test.server.RestSession;
import ix.test.server.SubstanceAPI;
import util.json.JsonUtil;

public class ProteinTest extends AbstractGinasServerTest {

	final File resource = new File("test/testJSON/toedit.json");


	private GinasTestServer.User fakeUser1, fakeUser2;

	@Before
	public void getUsers() {
		fakeUser1 = ts.getFakeUser1();
		fakeUser2 = ts.getFakeUser2();
	}

	@Test
	public void testProteinExportAsFAS() throws Exception {
		System.out.println("RUNNING?");
		try (RestSession session = ts.newRestSession(fakeUser1)) {
			SubstanceAPI api = new SubstanceAPI(session);
			JsonNode entered = SubstanceJsonUtil
					.prepareUnapprovedPublic(JsonUtil.parseJsonFile(resource));
			ensurePass(api.submitSubstance(entered));
			String uuid=entered.at("/uuid").asText();
			String export=api.exportHTML(uuid,"fas");
			String fastaout=">" +uuid +"|SUBUNIT_1\n" + 
			"MRLAVGALLVCAVLGLCLAVPDKTVRWCAVSEHEATKCQSFRDHMKSVIPSDGPSVACVKKASYLDCIRAIAANEADAVT\n" + 
			"LDAGLVYDAYLAPNNLKPVVAEFYGSKEDPQTFYYAVAVVKKDSGFQMNQLRGKKSCHTGLGRSAGWNIPIGLLYCDLPE\n" + 
			"PRKPLEKAVANFFSGSCAPCADGTDFPQLCQLCPGCGCSTLNQYFGYSGAFKCLKDGAGDVAFVKHSTIFENLANKADRD\n" + 
			"QYELLCLDNTRKPVDEYKDCHLAQVPSHTVVARSMGGKEDLIWELLNQAQEHFGKDKSKEFQLFSSPHGKDLLFKDSAHG\n" + 
			"FLKVPPRMDAKMYLGYEYVTAIRNLREGTCPEAPTDECKPVKWCALSHHERLKCDEWSVNSVGKIECVSAETTEDCIAKI\n" + 
			"MNGEADAMSLDGGFVYIAGKCGLVPVLAENYNKSDNCEDTPEAGYFAVAVVKKSASDLTWDNLKGKKSCHTAVGRTAGWN\n" + 
			"IPMGLLYNKINHCRFDEFFSEGCAPGSKKDSSLCKLCMGSGLNLCEPNNKEGYYGYTGAFRCLVEKGDVAFVKHQTVPQN\n" + 
			"TGGKNPDPWAKNLNEKDYELLCLDGTRKPVEEYANCHLARAPNHAVVTRKDKEACVHKILRQQQHLFGSNVTDCSGNFCL\n" + 
			"FRSETKDLLFRDDTVCLAKLHDRNTYEKYLGEEYVKAVGNLRKCSTSSLLEACTFRRP\n" + 
			">" +uuid + "|SUBUNIT_2\n" + 
			"GADDVVDSSKSFVMENFSSYHGTKPGYVDSIQKGIQKPKSGTQGNYDDDWKGFYSTDNKYDAAGYSVDNENPLSGKAGGV\n" + 
			"VKVTYPGLTKVLALKVDNAETIKKELGLSLTEPLMEQVGTEEFIKRFGDGASRVVLSLPFAEGSSSVEYINNWEQAKALS\n" + 
			"VELEINFETRGKRGQDAMYEYMAQACAGNRVRRSVGSSLSCINLDWDVIRDKTKTKIESLKEHGPIKNKMSESPNKTVSE\n" + 
			"EKAKQYLEEFHQTALEHPELSELKTVTGTNPVFAGANYAAWAVNVAQVIDSETADNLEKTTAALSILPGIGSVMGIADGA\n" + 
			"VHHNTEEIVAQSIALSSLMVAQAIPLVGELVDIGFAAYNFVESIINLFQVVHNSYNRPAYSPGHKTQPFLHDGYAVSWNT\n" + 
			"VEDSIIRTGFQGESGHDIKITAENTPLPIAGVLLPTIPGKLDVNKSKTHISVNGRKIRMRCRAIDGDVTFCRPKSPVYVG\n" + 
			"NGVHANLHVAFHRSSSEKIHSNEISSDSIGVLGYQKTVDHTKVNFKLSLFFEIKS\n";
			
			assertEquals(fastaout, export);
		}
	}

}
