package models;

import javax.persistence.*;
import play.db.jpa.*;

/**
 *
 * @author TICBUILDER
 */
@Entity
@Table(name = "typecontroleur")

public class TypeControleur extends Model {

    public String designation;

    public TypeControleur() {
    }
    
    public TypeControleur(String designation) {
        this.designation = designation;
    }


}
