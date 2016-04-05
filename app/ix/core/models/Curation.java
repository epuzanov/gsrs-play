package ix.core.models;

import java.util.Date;

import ix.core.util.TimeUtil;
import play.db.ebean.Model;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Curation is simply a marker that indicates an Entity
 * has been *validated* by curator. This is different than
 * the Edit history of an entity.
 */
@Entity
@Table(name="ix_core_curation")
public class Curation extends LongBaseModel {
    public enum Status {
        	Unknown,
            Pending,
            Rejected,
            Accepted
    }

    @Id
    public Long id;

    @ManyToOne(cascade=CascadeType.PERSIST)
    public Principal curator;
    
    public Status status;

    public final Date timestamp = TimeUtil.getCurrentDate();

    public Curation () {}
    public Curation (Principal curator) {
        this (curator, Status.Unknown);
    }
    public Curation (Principal curator, Status status) {
        this.curator = curator;
        this.status = status;
    }
}

