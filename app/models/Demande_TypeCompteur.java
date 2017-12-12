package models;

import javax.persistence.*;
import play.db.jpa.*;

/**
 *
 * @author TICBUILDER
 */
@Entity
@Table(name = "demande_typecompteur")

public class Demande_TypeCompteur extends Model {

    @ManyToOne
    public TypeCompteur typeCompteur;   
    @ManyToOne
    public Demande demande;
    public int QteCompteur;
	       
    public Demande_TypeCompteur() {
    }

    public Demande_TypeCompteur(TypeCompteur typeCompteur, Demande demande, int QteCompteur) {
        this.QteCompteur = QteCompteur;
        this.typeCompteur = typeCompteur;
        this.demande = demande;
    }

}
