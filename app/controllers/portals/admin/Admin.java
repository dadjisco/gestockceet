package controllers.portals.admin;

import controllers.Secure;
import controllers.SessionHandler;
import models.Administrator;
import models.Adress;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 * Created by yasser
 */

@With({SessionHandler.class, Secure.class})
public class Admin extends Controller{

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "infos");
    }

    public static void profile(){
        String linkCancel = "portals.admin.Admin.profile";
        String linkProfile = "portals.admin.Admin.saveProfile";
        render(linkCancel, linkProfile);
    }

    public static void account(){
        String linkCancel = "portals.admin.Admin.account";
        String linkAccount = "portals.admin.Admin.saveAccount";
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
            flash.put("success", true);
        }
        profile();
    }

    public static void saveAccount(Long account, Adress address){

            Administrator admin = Administrator.findById(account);
            address.save();
            admin.address = address;
            admin.save();
            flash.put("success", true);
        
        account();
    }
}
