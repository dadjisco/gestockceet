package models;

import javax.persistence.*;
import play.db.jpa.*;

/**
 *
 * @author TICBUILDER
 */
@Entity
@Table(name = "typesoustraitant")
public class TypeSousTraitant extends Model {

    public String libelleTypeSousTraitant;

    public TypeSousTraitant() {
    }

    public TypeSousTraitant(String libelle) {
        this.libelleTypeSousTraitant = libelle;
    }
    

}
