package models;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import play.db.jpa.*;

/**
 *
 * @author TICBUILDER
 */
@Entity
@Table(name = "lotcompteur")

public class LotCompteur extends Model {

    @NotNull
    public String libelle;
    @OneToMany
    List<Compteur> compteurs;
    @ManyToOne
    public User agentPoseur;
    public Date dateAffectation;
    
    public LotCompteur() {
    }

    public LotCompteur(String libelle, List<Compteur> compteurs) {
        this.libelle = libelle;
        this.compteurs = compteurs;
    }

    public LotCompteur(String libelle, List<Compteur> compteurs, User agentPoseur, Date dateAffectation) {
        this.libelle = libelle;
        this.compteurs = compteurs;
        this.agentPoseur = agentPoseur;
        this.dateAffectation = dateAffectation;
    }

    public LotCompteur(List<Compteur> compteurs) {
        this.compteurs = compteurs;
    }

    public List<Compteur> getCompteurs() {
        return compteurs;
    }

    public void setCompteurs(List<Compteur> compteurs) {
        this.compteurs = compteurs;
    }
    
    


}
