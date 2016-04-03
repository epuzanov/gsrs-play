package ix.ginas.models.v1;

import ix.ginas.models.GinasCommonSubData;
import ix.ginas.models.utils.JSONEntity;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonDeserialize(using = MoietyDeserializer.class)
@JsonSerialize(using = MoietySerializer.class)
@JSONEntity(name = "moiety", title = "Moiety")
@Entity
@Table(name = "ix_ginas_moiety")
public class Moiety extends GinasCommonSubData {
	
//	add getters and setters to convert integers to amounts
//	add getters and setters to accept amounts
//	change form to send amount.
	
    @OneToOne(cascade=CascadeType.ALL)
    @Column(nullable=false)
    public GinasChemicalStructure structure;
    
    @JSONEntity(title = "Count")
    @OneToOne(cascade=CascadeType.ALL)
    private Amount count;

    public Moiety () {}
    
    @Column(unique=true)
    public String innerUuid;
    
    @PrePersist
    @PreUpdate
    public void enforce(){
    	if(structure.id==null){
    		structure.id=UUID.randomUUID();
    	}
    	this.innerUuid=structure.id.toString();
    }
    
    public void setCount(Integer i){
    	count=new Amount();
    	if(i!=null){
    		count.average=i.doubleValue();
    	}
    	count.type="MOL RATIO";
    	count.units="MOL RATIO";
    }
    public Integer getCount(){
    	if(count==null)return null;
    	if(count.average == null)return null;
    	return (int) count.average.longValue();
    }

	public void setCountAmount(Amount amnt) {
		count=amnt;
	}
	
	public Amount getCountAmount() {
		return count;
	}
    
}