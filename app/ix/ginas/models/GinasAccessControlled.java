package ix.ginas.models;

import ix.core.models.Group;

import java.util.Collection;
import java.util.Set;

public interface GinasAccessControlled {
	Set<Group> getAccess();
	void setAccess(Set<Group> access);
	void addRestrictGroup(Group p);
	void addRestrictGroup(String group);
	
}
