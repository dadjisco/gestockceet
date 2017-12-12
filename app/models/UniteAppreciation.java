package models;

import javax.persistence.*;
import play.db.jpa.*;

/**
 *
 * @author TICBUILDER
 */
@Entity
@Table(name = "uniteappreciation")

public class UniteAppreciation extends Model {

    public String libelleUnite;//Caisse, Carton, Contenaire, Lot
    public int nbCompteur;

    public UniteAppreciation() {
    }

    public UniteAppreciation(String libelleUnite, int nbCompteur) {
        this.libelleUnite = libelleUnite;
        this.nbCompteur = nbCompteur;
    }
    
    


}
