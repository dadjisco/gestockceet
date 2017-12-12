package models;
import javax.persistence.*;
import play.data.validation.Required;

/**
 *
 * @author Afribills Groups Developper
 */
@Entity
public class Administrator extends Account {

    @Required
    public String name = "Administrateur";

    public Administrator(Adress address) {
        super(address);
    }

}