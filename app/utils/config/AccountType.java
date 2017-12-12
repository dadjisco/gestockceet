package utils.config;

import models.*;

/**
 * Created by yasser  
 */
public enum AccountType {
    AGENCE(Agence.class.getSimpleName()),
    MAGASINIER(Magasinier.class.getSimpleName()),
    SOUSTRAITANT(Soustraitant.class.getSimpleName()),
    ADMINISTRATOR(Administrator.class.getSimpleName());

    private String name;

    AccountType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
