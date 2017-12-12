package controllers.portals.agence;

import controllers.Secure;
import controllers.Security;
import controllers.SessionHandler;
import static controllers.portals.agence.Dashboard.getCumulEntree;
import static controllers.portals.agence.Dashboard.getCumulSortie;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 * Created by yasser
 */
@With({SessionHandler.class, Secure.class})
public class Statistiques extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "statistiques");
    }
    
    public static void index() {
        int lesEntrees = getCumulEntree(Security.getAccount().id);
        int lesSorties = getCumulSortie(Security.getAccount().id);
        int lesRestes = lesEntrees - lesSorties;
        render(lesEntrees, lesSorties, lesRestes);
    }
    


}
