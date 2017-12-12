package models;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.Model;

/**
 *
 * @author Birkhoff
 */

@Entity
@Table(name = "activation")
public class Activation extends Model {


    public String activationCode;

    public Date dateCreation;

    public Date dateActivation;

    public String destinataire;

    @ManyToOne
    public User user;

    public Activation(String activationCode, Date dateCreation, String destinataire, User user) {
        this.activationCode = activationCode;
        this.dateCreation = dateCreation;
        this.destinataire = destinataire;
        this.user = user;
    }

}
