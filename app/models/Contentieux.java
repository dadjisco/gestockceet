package models;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import play.db.jpa.*;

/**
 *
 * @author TICBUILDER
 */
@Entity
@Table(name = "contentieux")

public class Contentieux extends Model {

    public int ecart;
    public Date dateSave;
    @ManyToOne
    public EntreeEnStock entreeEnStock;
    @OneToMany
    private List<Compteur> compteurs;
    public boolean traiter;
    public String pvFilePath;
    
    public Contentieux() {
    }

    public Contentieux(int ecart, Date dateSave, EntreeEnStock entreeEnStock) {
        this.ecart = ecart;
        this.dateSave = dateSave;
        this.entreeEnStock = entreeEnStock;
        this.traiter = false;
    }

    public List<Compteur> getCompteurs() {
        return compteurs;
    }

    public String getPvFile() {
        return pvFilePath;
    }

    
    public void setCompteurs(List<Compteur> compteurs) {
        this.compteurs = compteurs;
    }

    

}
