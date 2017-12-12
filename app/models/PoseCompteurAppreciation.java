package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.*;

/**
 *
 * @author TICBUILDER
 */
@Entity
@Table(name = "posecompteur_appreciation")
public class PoseCompteurAppreciation extends Model {

    @ManyToOne
    public PoseCompteur poseCompteur;

    public String commentaire;

    public Date dateAppreciation;

    @ManyToOne
    /**
     * NiveauAppreciation::OK
     * NiveauAppreciation::NON_OK
     */
    public NiveauAppreciation appreciation;

    @ManyToOne
    /**
     * Le controleur doit avoir l'account Agence ou sous_traitant controleur et un profil controleur
     */
    public User controleur;
    public boolean archiver;
    
    public PoseCompteurAppreciation() {
    }

    public PoseCompteurAppreciation(PoseCompteur poseCompteur, String commentaire, Date dateAppreciation, NiveauAppreciation appreciation, User controleur) {
        this.poseCompteur = poseCompteur;
        this.commentaire = commentaire;
        this.dateAppreciation = dateAppreciation;
        this.appreciation = appreciation;
        this.controleur = controleur;
        this.archiver = false;
    }
    
    

}
