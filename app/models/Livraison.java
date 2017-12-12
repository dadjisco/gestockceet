package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.*;

/**
 *
 * @author TICBUILDER
 */
@Entity
@Table(name = "livraison")
public class Livraison extends Model {

    public String referenceLivraison;
    public Date dateReception;
    public String logistique;
    public String description;
    public int nombreCompteur;
    public int nbCompteurRestant;
    @ManyToOne
    public Commande commande;
    @OneToMany(mappedBy = "livraison",fetch = FetchType.LAZY)
    List<Compteur> compteurs;
    

    public Livraison() {
    }

    public Livraison(Commande commande, Date dateReception, String logistique) {
        this.commande = commande;
        this.dateReception = dateReception;
        this.logistique = logistique;
    }
        
    public Livraison(Commande commande, String referenceLivraison, Date dateReception, String logistique, String description, int nombreCompteur) {
        this.commande = commande;
        this.referenceLivraison = referenceLivraison;
        this.dateReception = dateReception;
        this.logistique = logistique;
        this.description = description;
        this.nombreCompteur = nombreCompteur;
        this.nbCompteurRestant = nombreCompteur;
    }

    public List<Compteur> getCompteurs() {
        compteurs = Compteur.findAll();
        return compteurs;
    }

    public void setCompteurs(List<Compteur> compteurs) {
        this.compteurs = compteurs;
    }
    
    
    
}
