package models;

import javax.persistence.*;
import play.db.jpa.*;
import play.data.validation.Required;

/**
 *
 * @author TICBUILDER
 */
@Entity
@Table(name = "magasin")
public class Magasin extends Model {

    @Required(message = "Nom magasin obligatoire")
    public String nomMagasin;
    public String codeMagasin;
    public String adresse;
    public String phone;
    public double latitude;
    public double longitude;
    public boolean enable;
 
    @ManyToOne
    public Agence agence;

    public Magasin() {
        this.enable = true;
    }

    public Magasin(String nomMagasin) {
        this.nomMagasin = nomMagasin;
        this.enable = true;
    }

    public Magasin(String nomMagasin, Agence agence) {
        this.nomMagasin = nomMagasin;
        this.agence = agence;
        this.enable = true;
    }

    public Magasin(String code, String nomMagasin, String adresse, String phone, Double latitude, Double longitude) {
        this.codeMagasin = code;
        this.nomMagasin = nomMagasin;
        this.adresse = adresse;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.enable = true;
    }

    public Magasin(String code, String nomMagasin, String adresse, String phone, Double latitude, Double longitude, Agence agence) {
        this.codeMagasin = code;
        this.nomMagasin = nomMagasin;
        this.adresse = adresse;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.agence = agence;
        this.enable = true;
    }

}
