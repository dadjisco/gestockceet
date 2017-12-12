package models;

import java.util.List;
import javax.persistence.*;
import play.data.validation.Required;

/**
 *
 * @author TICBUILDER
 */
@Entity
public class Agence extends Account {

    @Required(message = "Le nom de l'agence est requis")
    public String codeAgence;

    public String nomAgence;
    
    public boolean principale;
    
    @ManyToOne
    public Agence agencePrincipale;

    @OneToMany(mappedBy = "agence", fetch = FetchType.LAZY,cascade = CascadeType.MERGE)
    public List<Soustraitant> sousTraitants;

    public Agence() {
    }

    public Agence(String codeAgence) {
        this.codeAgence = codeAgence;
    }

    public Agence(String codeAgence, String nomAgence, boolean principale) {
        this.codeAgence = codeAgence;
        this.nomAgence = nomAgence;
        this.principale = principale;
    }
    
    public Agence(String codeAgence, String nomAgence, boolean principale, Agence agence) {
        this.codeAgence = codeAgence;
        this.nomAgence = nomAgence;
        this.principale = principale;
        this.agencePrincipale = agence;
    }

    public int getNombreCompteur(long idAgence){
        int cumulCompteur=0;
        List<EntreeEnStock> entrees = EntreeEnStock.find("user.account.id=?1 and nbRestant >?2", idAgence, 0).fetch();
        for(EntreeEnStock e:entrees){
            cumulCompteur+=e.nbRestant;
        }
        return cumulCompteur;
    }
        
    public List<Soustraitant> getSousTraitants() {
        sousTraitants = Soustraitant.find("agence = ?1", this).fetch();
        return sousTraitants;
    }

    public void setSousTraitants(List<Soustraitant> sousTraitants) {
        this.sousTraitants = sousTraitants;
    }

    @Override
    public String toString() {
        return "Agence";
    }

}
