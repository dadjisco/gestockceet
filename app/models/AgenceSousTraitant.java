package models;

import javax.persistence.*;

/**
 *
 * @author TICBUILDER
 */
//@Entity
//@Table(name = "agence_soustraitant")

public class AgenceSousTraitant {//extends Model {

    @ManyToOne
    public Agence agence;

    @ManyToOne
    public Soustraitant sousTraitant;

    public AgenceSousTraitant() {
    }
}
