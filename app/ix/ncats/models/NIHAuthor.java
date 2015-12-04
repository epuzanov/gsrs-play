package ix.ncats.models;

import play.db.ebean.Model;

import javax.persistence.*;

import ix.core.models.Author;
import ix.core.models.Figure;
import ix.core.models.Indexable;

@Entity
@DiscriminatorValue("NIH")
public class NIHAuthor extends Author {
    @Indexable(facet=true, name="NCATS Employee")
    public boolean ncatsEmployee;

    @Column(length=1024)
    public String dn; // distinguished name
    
    @Column(name = "u_id")
    public Long uid; // unique id

    @Column(length=32)
    public String phone;

    @Lob
    public String biography;
    public String title;

    @Lob
    public String research;

    public NIHAuthor () {}
    public NIHAuthor (String lastname, String forename) {
        super (lastname, forename);
    }
}
