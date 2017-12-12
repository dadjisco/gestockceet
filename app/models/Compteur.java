package models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import play.db.jpa.*;

/**
 *
 * @author TICBUILDER
 */
@Entity
@Table(name = "compteur", uniqueConstraints={@UniqueConstraint(columnNames={"numeroCompteur"})})

public class Compteur extends Model {
    
    @Column(unique=true)
    public String numeroCompteur;
    public String numeroSerieCompteur;
    public String puissance;
    public String caracterisques;
    public boolean affecter;

    @ManyToOne
    public TypeCompteur typeCompteur;

    @ManyToOne
    public Account account;
    
//    @ManyToOne
//    public EntreeEnStock entreeEnStock;
    
    @ManyToOne
    @NotNull
    public Livraison livraison;

    public Compteur() {
    }

    public Compteur(String numeroCompteur, String numeroSerieCompteur, String puissance, String caracterisques, TypeCompteur typeCompteur) {
        this.numeroCompteur = numeroCompteur;
        this.numeroSerieCompteur = numeroSerieCompteur;
        this.puissance = puissance;
        this.caracterisques = caracterisques;
        this.typeCompteur = typeCompteur;
        this.affecter = false;
    }
    
    public Compteur(Livraison livraison, String numeroCompteur, String numeroSerieCompteur, String puissance, String caracterisques, TypeCompteur typeCompteur) {
        this.numeroCompteur = numeroCompteur;
        this.numeroSerieCompteur = numeroSerieCompteur;
        this.puissance = puissance;
        this.caracterisques = caracterisques;
        this.typeCompteur = typeCompteur;
        this.affecter = false;
    }
    
    public Compteur(String numeroCompteur, String numeroSerieCompteur, String puissance, String caracterisques, TypeCompteur typeCompteur, Account account) {
        this.numeroCompteur = numeroCompteur;
        this.numeroSerieCompteur = numeroSerieCompteur;
        this.puissance = puissance;
        this.caracterisques = caracterisques;
        this.typeCompteur = typeCompteur;
        this.account = account;
        this.affecter = false;
    }
    
    public Compteur(String numeroCompteur, String numeroSerieCompteur, String puissance, String caracterisques, TypeCompteur typeCompteur, Account account, Livraison livraison) {
        this.numeroCompteur = numeroCompteur;
        this.numeroSerieCompteur = numeroSerieCompteur;
        this.puissance = puissance;
        this.caracterisques = caracterisques;
        this.typeCompteur = typeCompteur;
        this.account = account;
        this.livraison = livraison;
        this.affecter = false;
    }
    

}
