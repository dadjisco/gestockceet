package models;

import javax.persistence.*;
import play.db.jpa.*;

/**
 *
 * @author TICBUILDER
 */
@Entity
@Table(name = "typecompteur")

public class TypeCompteur extends Model {

    public String libelleCompteur;

    public TypeCompteur() {
    }
    
    public TypeCompteur(String libelleCompteur) {
        this.libelleCompteur = libelleCompteur;
    }


}
