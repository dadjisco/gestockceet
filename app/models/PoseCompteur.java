package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.*;
import play.data.validation.Required;

/**
 *
 * @author TICBUILDER
 */
@Entity
@Table(name = "posecompteur")
public class PoseCompteur extends Model {

    public double latitude;
    public double longitude;
    public String libelleZone;
    public String capturePosePath;
    public Date savePoseDate;
    @Required
    public String numeroBranchement;

    @ManyToOne
    public Compteur compteur;

    /**
     * Le controleur doit avoir un compte agence ou sous_traitant
     */
    @ManyToOne
    public User poseur;

    public Date dateAffectation;
    /**
     * Le controleur doit avoir un compte agence ou sous_traitant
     */
    @ManyToOne
    public User controleur;
    

    @OneToMany(mappedBy = "poseCompteur", fetch = FetchType.LAZY)
    private List<PoseCompteurAppreciation> compteurAppreciations;

    public PoseCompteur() {
    }

    public PoseCompteur(double latitude, double longitude, String libelleZone, String numeroBranchement, Compteur compteur, User poseur, Date savePoseDate) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.libelleZone = libelleZone;
        this.numeroBranchement = numeroBranchement;
        this.compteur = compteur;
        this.poseur = poseur;
        this.savePoseDate = savePoseDate;
    }

    
    public List<PoseCompteurAppreciation> getCompteurAppreciations() {
        compteurAppreciations = PoseCompteurAppreciation.find("poseCompteur = ?1", this).fetch();
        return compteurAppreciations;
    }

    public void setCompteurAppreciations(List<PoseCompteurAppreciation> compteurAppreciations) {
        this.compteurAppreciations = compteurAppreciations;
    }
}
