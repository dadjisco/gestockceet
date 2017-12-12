package controllers.portals.soustraitant;

import controllers.Secure;
import controllers.SessionHandler;
import play.mvc.Controller;
import play.mvc.With;

@With({SessionHandler.class, Secure.class})
public class Dashboard extends Controller {

    public static void dashboard() {
        renderArgs.put("action", "");
        render();
    }

    public static void index() {
        render();
    }


}
