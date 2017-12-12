package models;

import javax.persistence.*;
import play.db.jpa.*;
import play.data.validation.Required;

/**
 *
 * @author Afribills Groups Developper
 */
@Entity
@Table(name = "adress")
public class Adress extends Model {

    public String complement;

    @Required(message = "validation.adress.city.requis")
    public String city;

    @Required(message = "validation.adress.state.requis")
    public String state;

    @Required(message = "validation.adress.bp.requis")
    public String postCode;

    @Required(message = "validation.adress.phone.requis")
    public String phone;


    public Adress(String complement, String city, String state, String postCode, String phone) {
        this.complement = complement;
        this.city = city;
        this.state = state;
        this.postCode = postCode;
        this.phone = phone;
    }

    public void updateFields(Adress address) {
        this.complement = address.complement;
        this.city = address.city;
        this.state = address.state;
        this.postCode = address.postCode;
    }

    @PrePersist
    public void onPersist() {
        this.id = 0L;
    }
}
