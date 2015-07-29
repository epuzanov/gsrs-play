package ix.ginas.models.v1;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.OneToOne;

@SuppressWarnings("serial")
@Entity
@Inheritance
@DiscriminatorValue("PRO")
public class ProteinSubstance extends Substance {

	@OneToOne(cascade=CascadeType.ALL)
    public Protein protein;

    
    public ProteinSubstance () {
        super (SubstanceClass.protein);
    }
    @Override
    public boolean hasModifications(){
    	if(this.protein.modifications!=null){
    		if(this.protein.modifications.agentModifications.size()>0 || this.protein.modifications.physicalModifications.size()>0 || this.protein.modifications.structuralModifications.size()>0){
    			return true;
    		}
    	}
		return false;
    	
    }
    @Override
    public int getModificationCount(){
    	int ret=0;
    	if(this.protein.modifications!=null){
    		ret+=this.protein.modifications.agentModifications.size();
    		ret+=this.protein.modifications.physicalModifications.size();
    		ret+=this.protein.modifications.structuralModifications.size();
    	}
    	return ret;
    }
    @Override
    public Modifications getModifications(){
    	return this.protein.modifications;
    }
}