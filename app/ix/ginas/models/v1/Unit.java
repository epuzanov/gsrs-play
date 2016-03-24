package ix.ginas.models.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import ix.core.models.VIntArray;
import ix.ginas.models.GinasCommonSubData;
import ix.ginas.models.IntArrayDeserializer;
import ix.ginas.models.IntArraySerializer;

@Entity
@Table(name="ix_ginas_unit")
public class Unit extends GinasCommonSubData {
    @OneToOne(cascade=CascadeType.ALL)
    @JsonSerialize(using = IntArraySerializer.class)
    @JsonDeserialize(using = IntArrayDeserializer.class)
    public VIntArray amap;

    @OneToOne(cascade=CascadeType.ALL)
    public Amount amount;
    public Integer attachmentCount;
    public String label;
    
    
    @Lob
    @Basic(fetch=FetchType.EAGER)
    public String structure;  //TODO: should be changed to be a structure
    
    public String type;
    
    @Lob
    private String _attachmentMap;
    
    public Map<String,LinkedHashSet<String>> getAttachmentMap(){
    	ObjectMapper om = new ObjectMapper();
    	Map<String, LinkedHashSet<String>> amap=null;
		try {
			amap = om.readValue(_attachmentMap, new TypeReference<Map<String, LinkedHashSet<String>>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return amap;
    }
    
    public void setAttachmentMap(Map<String,LinkedHashSet<String>> amap){
    	ObjectMapper om = new ObjectMapper();
    	_attachmentMap=null;
    	try {
			_attachmentMap=om.writeValueAsString(amap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    }
    
    @JsonIgnore
    //TODO:Make this inspect the structure itself
    //there are
    public List<String> getContainedConnections(){
    	System.err.println("WARNING: SRU structure not validated to check for connection points");
    	return getMentionedConnections();
    }
    
    @JsonIgnore
    public List<String> getMentionedConnections(){
    	Map<String,LinkedHashSet<String>> mymap=this.getAttachmentMap();
    	List<String> conset=new ArrayList<String>();
		if(mymap!=null){
			for(String k:mymap.keySet()){
				conset.add(k);
			}
		}
		return conset;
    }
    
    public void addConnection(String rgroup1, String rgroup2){
    	Map<String,LinkedHashSet<String>> amap=this.getAttachmentMap();
    	if(amap==null){
    		amap=new HashMap<String,LinkedHashSet<String>>();
    	}
    	LinkedHashSet<String> set1=amap.get(rgroup1);
    	if(set1==null){
    		set1=new LinkedHashSet<String>();
    		amap.put(rgroup1, set1);
    	}
    	set1.add(rgroup2);
    	setAttachmentMap(amap);
    }
    /*
    public Map<String,LinkedHashSet<String>> getAttachmentMap(){
    	ObjectMapper om = new ObjectMapper();
    	Map<String, LinkedHashSet<String>> amap=null;
    	
		try {
			amap = om.readValue(_attachmentMap, new TypeReference<Map<String, LinkedHashSet<String>>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    	return amap;
    }
    
    public void setAttachementMap(Map<String,LinkedHashSet<String>> amap){
    	ObjectMapper om = new ObjectMapper();
    	_attachmentMap=null;
    	try {
			_attachmentMap=om.writeValueAsString(amap);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
    }
    */
    

    public Unit () {}
}
