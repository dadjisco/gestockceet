package controllers.portals.agence;

import controllers.Secure;
import controllers.SessionHandler;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 * Created by yasser
 */
@With({SessionHandler.class, Secure.class})
public class Agence extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "agence");
    }

    public static void index() {
        render();
    }


}
