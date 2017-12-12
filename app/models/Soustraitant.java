package models;

import javax.persistence.*;
import play.data.validation.Required;

/**
 *
 * @author TICBUILDER
 */
@Entity
public class Soustraitant extends Account {

    @Required(message = "Raison Sociale obligatoire")
    public String raisonSociale;
    
    @ManyToOne
    public TypeSousTraitant typeSousTraitant;
    
    @ManyToOne
    public Agence agence;

    public Soustraitant() {
    }

    public Soustraitant(String raisonSociale) {
        this.raisonSociale = raisonSociale;
    }
    
    public Soustraitant(Adress adress) {
        super(adress);
    }
    

}
