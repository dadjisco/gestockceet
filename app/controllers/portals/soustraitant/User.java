package controllers.portals.soustraitant;

import controllers.Secure;
import controllers.SessionHandler;
import models.Adress;
import models.Magasinier;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 * Created by yasser
 */

@With({SessionHandler.class, Secure.class})
public class User extends Controller{

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "infos");
    }

    public static void profile(){
        String linkCancel = "portals.ceet.User.profile";
        String linkProfile = "portals.ceet.User.saveProfile";
        render(linkCancel, linkProfile);
    }

    public static void account(){
        String linkCancel = "portals.ceet.User.account";
        String linkAccount = "portals.ceet.User.saveAccount";
        render(linkCancel, linkAccount);
    }

    /**
     * TODO valid password equals passwordCheck
     * @param user
     * @param editPassword
     * @param currentPassword
     * @param passwordCheck
     */
    public static void saveProfile(models.User user,
                                   Boolean editPassword,
                                   String currentPassword,
                                   String passwordCheck){
        if(editPassword != null){
            validation.required(currentPassword);
            validation.required(passwordCheck);
            validation.valid(user);
            if(	!validation.hasErrors() && user.isValidPwdById(user.id, currentPassword)){
                user.validateAndSave();
            }
        }else{
            user.validateAndUpdate(false);
        }
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
        }else{
            // Edit session and keep user as connected
            session.put("username", user.username);
            flash.put("success", true);
        }
        profile();
    }

    public static void saveAccount(Long account, String companyName, Adress address){
            Magasinier magasinier = Magasinier.findById(account);
            address.save();
            magasinier.address = address;
            magasinier.save();
            flash.put("success", true);
        account();
    }
}
