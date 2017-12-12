package controllers.portals.admin;

import controllers.Secure;
import controllers.SessionHandler;
import play.mvc.Controller;
import play.mvc.With;

@With({SessionHandler.class, Secure.class})
public class Dashboard extends Controller {

    public static void index() {
        AccountsAndUsers.index();
    }
}
