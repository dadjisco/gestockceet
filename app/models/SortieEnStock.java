package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.*;

/**
 *
 * @author TICBUILDER
 */
@Entity
@Table(name = "sortie_stock")

public class SortieEnStock extends Model {

    @ManyToOne
    public Account beneficiaire; //Magasinier, Agence, soustraitant,...
    public int nbCompteur;
    public Date dateSortie;
    @ManyToOne
    public User user;
    @OneToMany
    List<Compteur> compteurs;

    public SortieEnStock() {
    }

    public SortieEnStock(Account beneficiaire, int nbCompteur, Date dateSortie) {
        this.beneficiaire = beneficiaire;
        this.nbCompteur = nbCompteur;
        this.dateSortie = dateSortie;
    }
    
    public SortieEnStock(Account beneficiaire, int nbCompteur, Date dateSortie, User user) {
        this.beneficiaire = beneficiaire;
        this.nbCompteur = nbCompteur;
        this.dateSortie = dateSortie;
        this.user = user;
    }

    public SortieEnStock(Account beneficiaire, int nbCompteur, Date dateSortie, User user, List<Compteur> compteurs) {
        this.beneficiaire = beneficiaire;
        this.nbCompteur = nbCompteur;
        this.dateSortie = dateSortie;
        this.user = user;
        this.compteurs = compteurs;
    }

    public List<Compteur> getCompteurs() {
        return compteurs;
    }

    public void setCompteurs(List<Compteur> compteurs) {
        this.compteurs = compteurs;
    }

    public int getQteEntrer(long idSortie){
    int quantite=0;
        List<EntreeEnStock> entreeEnStocks = EntreeEnStock.find("sortieEnStock.id=?1", idSortie).fetch();
        for(EntreeEnStock e: entreeEnStocks){
            quantite +=e.nbRestant;
        }
    return quantite;
    }
}
