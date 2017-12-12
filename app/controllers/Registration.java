package controllers;

import java.util.Date;
import models.*;
import play.libs.Codec;
import play.mvc.Controller;

public class Registration extends Controller {

    private static void doLoginLogic(String username) {
        session.put("user", username);
    }

    /**
     * Display register page Generate a unique ID. Then we will modify the HTML
     * form to integrate a captcha image using this ID, and add the ID to
     * another hidden field.
     */
    public static void register() {
        String randomID = Codec.UUID();
        render(randomID);
    }
     



    public static void activation(String u, String activateToken) {
        String result;
        if (activateToken != null || !activateToken.isEmpty() || !u.isEmpty()) {
            User user = User.find("username = ?", u).first();
            if (user == null) { //Username incorrect
                result = "Error";
                render(result);
            }
            Activation a = Activation.find("user = ? and dateActivation IS NULL", user).first();
            if (a == null) {//Utilisateur déja activer ou utilisateur inexistant
                result = "NoActivation";
                render(result);
            }
            //renderHtml(Codec.hexMD5(a.activationCode));
            if (activateToken.equals(Codec.hexMD5(a.activationCode))) {
                a.dateActivation = new Date();
                a.save();
                user.account.status = true;
                user.account.save();
                result = "oK";
                renderArgs.put("user_", user);
                render(result);
            } else {
                result = "Invalid";
                User user_ = User.findById(108L);
                renderArgs.put("user_", user);
                render(result);
            }
        } else {
            render("Error");
        }
    }

    public static void confirmAccount(String code) {

    }

}
