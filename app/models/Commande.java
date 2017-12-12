package models;

import java.util.Date;
import javax.persistence.*;
import play.db.jpa.*;

/**
 *
 * @author TICBUILDER
 */
@Entity
@Table(name = "commande")

public class Commande extends Model {

    public String refCommande;
    public Date dateCommande;
    public Date dateReception;
    public String description;
    public String filePath;
    public int nbreCompteur;
    public int nbreCompteurRestant;

    public Commande() {
    }

    public Commande(String refCommande, Date dateCommande, String description, int nbreCompteur) {
        this.refCommande = refCommande;
        this.dateCommande = dateCommande;
        this.description = description;
        this.nbreCompteur = nbreCompteur;
        this.nbreCompteurRestant = nbreCompteur;
    }
        
    public Commande(String refCommande, Date dateCommande, String description, String filePath, int nbreCompteur) {
        this.refCommande = refCommande;
        this.dateCommande = dateCommande;
        this.description = description;
        this.filePath = filePath;
        this.nbreCompteur = nbreCompteur;
        this.nbreCompteurRestant = nbreCompteur;
    }
    
    
    




}
