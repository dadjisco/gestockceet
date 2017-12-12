package models;


import javax.persistence.Entity;

/**
 * Created by birkhoff
 */

@Entity
public class Magasinier extends Account{

    public Magasinier() {
    }

    public Magasinier(Adress address) {
        super(address);
    }

    @Override
    public String toString() {
        return "Magasinier";
    }
}
