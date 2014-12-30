package ix.idg.models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

import ix.core.models.Indexable;
import ix.core.models.Value;
import ix.core.models.Keyword;
import ix.core.models.Publication;
import ix.core.models.XRef;

@Entity
@Table(name="ix_idg_entity")
@Inheritance
@DiscriminatorValue("ENT")
public class EntityModel extends Model {
    static private final String JOIN = "_ix_idg_e8dead8d";
    
    @Id
    public Long id;

    @Column(length=1024)
    @Indexable(suggest=true,name="Entity")
    public String name;

    @Lob
    public String description;

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name="ix_idg_entity_synonym",
	       joinColumns=@JoinColumn(name="ix_idg_entity_synonym_id",
				       referencedColumnName="id")
	       )
    public List<Keyword> synonyms = new ArrayList<Keyword>();

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name="ix_idg_entity_gene",
	       joinColumns=@JoinColumn(name="ix_idg_entity_gene_id",
				       referencedColumnName="id")
	       )
    public List<Keyword> genes = new ArrayList<Keyword>();

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name="ix_idg_entity_property")
    public List<Value> properties = new ArrayList<Value>();

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name="ix_idg_entity_link")
    public List<XRef> links = new ArrayList<XRef>();

    @ManyToMany(cascade=CascadeType.ALL)
    @JoinTable(name="ix_idg_entity_publication")
    public List<Publication> publications = new ArrayList<Publication>();

    public EntityModel () {}
}
