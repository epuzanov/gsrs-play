package ix.ginas.models;

import java.util.UUID;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import play.db.ebean.Model;
import play.Logger;
import ix.core.models.Indexable;
import ix.core.models.Principal;
import ix.utils.Global;

@MappedSuperclass
public class Ginas extends Model {
    static public final String REFERENCE = "GInAS Reference";
    static public final String TAG = "GInAS Tag";
    
    @Id
    public UUID uuid;

    public final Date created = new Date ();
    public Date lastModified;
    
    @OneToOne(cascade=CascadeType.ALL)
    @JsonSerialize(using = PrincipalSerializer.class)
    @JsonDeserialize(using = PrincipalDeserializer.class)
    public Principal lastEditedBy;
    public boolean deprecated;
    
    public Ginas () {
    }

    @PrePersist
    @PreUpdate
    public void modified () {
        this.lastModified = new Date ();
    }

    @Indexable(indexed=false)
    public String getSelf () {
        if (uuid != null) {
            try {
                String ref = Global.getRef(this);
                if (ref != null)
                    return ref+"?view=full";
            }
            catch (Exception ex) {
                ex.printStackTrace();
                Logger.error("Not a valid persistence Entity", ex);
            }
        }
        return null;
    }
}
