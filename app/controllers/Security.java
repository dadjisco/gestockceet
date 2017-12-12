package controllers;

import models.*;
import play.mvc.Before;
import utils.config.AccountType;

public class Security extends Secure.Security {

    /**
     * Check Authenticated inside Secure class directly Each controller which
     * uses @With(Secure.class) will apply this rule
     */
    @Before
    static void checkAuthenticated() {
        if (!Security.isConnected()) {
            Application.unAuthorized();
        }
    }

    static boolean authenticate(String username, String password) {
        return User.isValidLogin(username,password);
    }

    static boolean check(String profile) {
        User user = User.find("byUsername", connected()).first();
        String accountType = user.account.getClass().getSimpleName();
        switch (AccountType.valueOf(accountType.toUpperCase())) {

            case MAGASINIER:
                return (accountType.equals(profile));
            case AGENCE:
                return (accountType.equals(profile));
            case SOUSTRAITANT:
                return (accountType.equals(profile));
            case ADMINISTRATOR:
                return (accountType.equals(profile));
            default:
                return false;
        }
    }

    /**
     * This method is called after a successful authentication. You need to
     * override this method if you with to perform specific actions (eg. Record
     * the time the user signed in)
     */
    static void onAuthenticated() {
        User user = getUser();
        String url = session.get("url");
        if (url != null) {
            session.remove("url");
            redirect(url);
        }
        if (Security.check("Magasinier")) {
            controllers.portals.magasinier.Reception.index();
        }
        if (Security.check("Agence")){
            if(user.profil.libProfil.compareToIgnoreCase("chefagence")==0){
            controllers.portals.agence.Dashboard.accueil();
            }else{
            controllers.portals.agence.Dashboard.index();
            }
            
        }
        if (Security.check("Soustraitant")) {
            controllers.portals.soustraitant.Dashboard.index();
        }
        if (Security.check("Administrator")) {
            if (user.account.status == true) {
                controllers.portals.admin.Dashboard.index();
            } 
        }
    }

    /**
     * Gets the connected user.
     *
     * @return the user
     */
    public static User getUser() {
        // username is an email
        User user = User.find("byUsername", Security.connected()).first();
        return user;
    }

    public static Account getAccount() {
        return (getUser() == null) ? null : getUser().account;
    }
}
