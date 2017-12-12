package models;

import javax.persistence.*;
import play.db.jpa.*;

/**
 *
 * @author TICBUILDER
 */
@Entity
@Table(name = "niveauappreciation")

public class NiveauAppreciation extends Model {

    public String appreciation;

    public NiveauAppreciation() {
    }

    public NiveauAppreciation(String appreciation) {
        this.appreciation = appreciation;
    }
    

}
