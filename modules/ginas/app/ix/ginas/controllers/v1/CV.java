package ix.ginas.controllers.v1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ix.ginas.models.v1.ControlledVocabulary;
import ix.ginas.models.v1.VocabularyTerm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sheilstk on 6/29/15.
 */
public class CV {


    public CV() {

    }


    public String getField(String domain) {
        ObjectMapper mapper = new ObjectMapper();
        ControlledVocabulary cv1 = ControlledVocabularyFactory
                .getControlledVocabulary(domain);
        if (cv1 != null) {
            JsonNode j = mapper.valueToTree(cv1.terms);
            return j.toString();
        }
        
        return "[{display: 'no values', value: '{null}'}]";
    }

    public List<String>  getDomain () {
        ObjectMapper mapper = new ObjectMapper();
        List<ControlledVocabulary> cv1 = ControlledVocabularyFactory
                .getDomain();
        if (cv1 != null) {
            List<String> domains = new ArrayList<String>();
            for(ControlledVocabulary c : cv1){
                domains.add(c.domain);
            }
            JsonNode j = mapper.valueToTree(domains);
            return domains;
        }

/*
        return "[{display: 'no values', value: '{null}'}]";
*/
        return null;
    }

    public static String getCV (String domain) {
        return new CV().getField(domain);
    }

    public static List<String> getCVDomains (){
        return new CV().getDomain();
    }
    /**
     * Returns the proper display term for value.
     * <p/>
     * If there is none found, display itself.
     *
     * @param domain
     * @param value
     * @return
     */
    public String getDisplay(String domain, String value) {
        ControlledVocabulary cv1 = ControlledVocabularyFactory
                .getControlledVocabulary(domain);
        if (cv1 != null) {
            for (VocabularyTerm v : cv1.terms) {
                if (v.value.equals(value)) {
                    return v.display;
                }
            }
        }
        return value;
    }

    public int size() {
        return ControlledVocabularyFactory.size();
    }

//    public CV (InputStream is) throws IOException {
//        String line = "";
//        String cvsSplitBy = "\t";
//
//        BufferedReader br = new BufferedReader(new InputStreamReader (is));
//        while ((line = br.readLine()) != null) {
//            String[] cvTerm = line.split(cvsSplitBy);
//            String category = cvTerm[0];
//            VocabularyTerm cv = new VocabularyTerm();
//            int l = cvTerm.length;
//            if (l >= 2) {
//                cv.value = cvTerm[1];
//            } else {
//                cv.value = null;
//            }
//            if (l >= 3) {
//                cv.display = cvTerm[2];
//            } else {
//                cv.display = null;
//            }
//            if (l >= 4) {
//                cv.description = cvTerm[3];
//            } else {
//                cv.description = null;
//            }
//            if (l >= 5) {
//                cv.origin = cvTerm[4];
//            } else {
//                cv.origin = null;
//            }
//            List<VocabularyTerm> temp = map.get(category);
//            if (temp == null) {
//                temp = new ArrayList<VocabularyTerm>();
//                map.put(category, temp);
//            }
//            temp.add(cv);
//        }
//    }

//    public Map<String, List<VocabularyTerm>> map =
//            new TreeMap<String, List<VocabularyTerm>>();

//    public String getField (String domain) {
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode j = mapper.valueToTree(map.get(domain));
//        return j.toString();
//    }
//
//    public String getCV (String domain) {
//        ObjectMapper mapper = new ObjectMapper();
//        JsonNode j = mapper.valueToTree(map.get(domain));
//        return j.toString();
//    }
//
//    /**
//     * Returns the proper display term for value.
//     * 
//     * If there is none found, display itself.
//     * @param domain
//     * @param value
//     * @return
//     */
//    public String getDisplay (String domain, String value) {
//        List<VocabularyTerm> domainList = map.get(domain);
//        if(domainList!=null){
//	        for(VocabularyTerm v : domainList){
//	            if(v.value.equals(value)){
//	            	return v.display;
//	            }
//	        }
//        }
//        return value;
//    }
//
//    public int size () { 
//    	retrun map.size();
//    }
}



