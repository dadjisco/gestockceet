package controllers;

import play.Logger;
import play.mvc.Before;
import play.mvc.Controller;

public class SessionHandler extends Controller {

    @Before
    static void setConnectedUser() {
        if (Security.isConnected()) {
            Logger.info("HANDLER SESSION");
            renderArgs.put("user", Security.getUser());

            Logger.info("..........." + Security.getUser().account.getClass().getSimpleName());
        }
        String url = request.url;
        Logger.info("URL " + url);
        if ((Security.isConnected() && url.contains("portals/admin") && !Security.check("Administrator"))
                || Security.isConnected() && url.contains("portals.admin") && !Security.check("Administrator")) {
            forbidden("Bad account, restricted zone, admin only");
        }
        if ((Security.isConnected() && url.contains("portals/magasinier") && !Security.check("Magasinier"))
                || Security.isConnected() && url.contains("portals.magasinier") && !Security.check("Magasinier")) {
            forbidden("Bad account, restricted zone, magasinier only");
        }
        if ((Security.isConnected() && url.contains("portals/agence") && !Security.check("Agence"))
                || Security.isConnected() && url.contains("portals.agence") && !Security.check("Agence")) {
            forbidden("Bad account, restricted zone, controleur only");
        }
        if ((Security.isConnected() && url.contains("portals/soustraitant") && !Security.check("Soustraitant"))
                || Security.isConnected() && url.contains("portals.soustraitant") && !Security.check("Soustraitant")) {
            forbidden("Bad account, restricted zone, sous-traitant only");
        }
    }
    
}
