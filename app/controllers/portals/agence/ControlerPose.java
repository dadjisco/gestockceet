package controllers.portals.agence;

import controllers.Secure;
import controllers.SessionHandler;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 * Created by TICBUILDER
 */
@With({SessionHandler.class, Secure.class})
public class ControlerPose extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "poser_compteur");
    }

    public static void index() {
        render();
    }


}
