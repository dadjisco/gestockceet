package models;


import javax.persistence.Entity;

/**
 * Created by yasser
 */

@Entity
public class Validateur extends Account{

    public Validateur() {
    }

    public Validateur(Adress address) {
        super(address);
    }

    @Override
    public String toString() {
        return "Validateur";
    }
}
