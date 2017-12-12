package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.*;

/**
 *
 * @author TICBUILDER
 */
@Entity
@Table(name = "entree_stock")

public class EntreeEnStock extends Model {

    @ManyToOne
    public User user;
    public int nbCompteur;
    public int nbRestant;
    public Date date;
    public String filePath;
    
    @ManyToOne
    public SortieEnStock sortieEnStock;
    
    @OneToMany
    private List<Compteur> compteurs;

    public EntreeEnStock() {
    }

    public EntreeEnStock(User user, int nbCompteur, Date date) {
        this.user = user;
        this.nbCompteur = nbCompteur;
        this.nbRestant = nbCompteur;
        this.date = date;
    }

    public EntreeEnStock(User user, int nbCompteur, Date date, SortieEnStock sortieEnStock) {
        this.user = user;
        this.nbCompteur = nbCompteur;
        this.nbRestant = nbCompteur;
        this.date = date;
        this.sortieEnStock = sortieEnStock;
    }
        
    public List<Compteur> getCompteurs() {
        return compteurs;
    }

    public void setCompteurs(List<Compteur> compteurs) {
        this.compteurs = compteurs;
    }
    
    

}
