package ix.tox21.models;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonProperty;

import play.db.ebean.Model;
import ix.utils.Global;
import ix.core.models.*;

@Entity
@Table(name="ix_tox21_qcsample")
public class QCSample extends Model {
    public enum Grade {
        A ("MW Confirmed, Purity > 90%", "success"),
        Ac ("CAUTION, Low Concentration\n"
            +"Concentration 5-30% of expected value", "danger"),
        B ("MW Confirmed, Purity 75-90%", "success"),
        Bc ("CAUTION, Low Concentration\n"
            +"Concentration 5-30% of expected value", "danger"),
        C ("MW Confirmed, Purity 50-75%", "success"),
        Cc ("CAUTION, Low Concentration\n"
            +"Concentration 5-30% of expected value", "danger"),
        D ("CAUTION, Purity <50%", "warning"),
        F ("CAUTION, Incorrect MW\n"
           +"Biological Activity Unreliable", "danger"),
        Fc ("CAUTION, Very Low Concentration\n"
            +"Concentration <5% of expected value\n"
            +"Biological Activity Unreliable", "danger"),
        Fns ("CAUTION, No Sample Detected\n"
             +"Biological Activity Unreliable", "danger"),
        I ("ISOMERS\n"
           +"Two or more isomers detected", "info"),
        M ("DEFINED MIXTURE\n"
           +"Two or more components", "info"),
        ND ("Not Determined\n"
            +"Analytical analysis is in progress", "default"),
        W ("Sample Withdrawn", "warning"),
        Z ("MW Confirmed, No Purity Info", "warning");
        
        public final String desc;
        public final String label;
        Grade (String desc, String label) {
            this.desc = desc;
            this.label = label;
        }
    }
    
    @Id
    public Long id;

    @Lob
    @Basic(fetch=FetchType.EAGER)
    public String comments;
    
    @OneToOne
    public Sample sample;
    
    @Indexable(facet=true,name="QC Grade")
    public Grade grade;

    public QCSample () {
    }
}