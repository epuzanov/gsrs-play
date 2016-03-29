package ix.core.models;

import play.db.ebean.Model;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonProperty;
import ix.utils.Global;

@Entity
@Table(name="ix_core_attribute")
public class Attribute extends LongBaseModel {
    @Id
    public Long id;

    public String name;
    @Column(length=1024)
    public String value;

    @ManyToOne(cascade=CascadeType.ALL)
    @JsonView(BeanViews.Full.class)
    public Namespace namespace;

    public Attribute () {}
    public Attribute (String name, String value) {
        this.name = name;
        this.value = value;
    }
    public Attribute (Namespace namespace, String name, String value) {
        this.namespace = namespace;
        this.name = name;
        this.value = value;
    }

    @JsonView(BeanViews.Compact.class)
    @JsonProperty("namespaceRef")
    public String namespaceRef () {
        return Global.getRef(namespace);
    }
}
