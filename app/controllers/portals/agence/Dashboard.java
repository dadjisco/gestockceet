package controllers.portals.agence;

import controllers.Secure;
import controllers.Security;
import controllers.SessionHandler;
import java.util.List;
import models.EntreeEnStock;
import models.SortieEnStock;
import play.mvc.Controller;
import play.mvc.With;

@With({SessionHandler.class, Secure.class})
public class Dashboard extends Controller {

    public static void dashboard() {
        render();
    }
    
    public static void index(){
        render();
    }
    
    public static void accueil(){
        renderArgs.put("action", "tdr");
        List<Agence> agences = models.Agence.find("principale=?1", false).fetch();
        int lesEntrees = getCumulEntree(Security.getAccount().id);
        int lesSorties = getCumulSortie(Security.getAccount().id);
        int lesRestes = lesEntrees - lesSorties;
        render(agences, lesEntrees, lesSorties, lesRestes);
    }
    
    public static int getCumulEntree(long idAgence){
        int cumul =0;
        List<EntreeEnStock> stock = EntreeEnStock.find("user.account.id=?1", idAgence).fetch();
        for(EntreeEnStock e: stock){
            cumul+=e.nbCompteur;
        }
    return cumul;
    }
      
//    public static int getCumulSortie(long idAgence){
//            int cumul =0;
//        List<SortieEnStock> stock = SortieEnStock.find("beneficiaire.id=?1", idAgence).fetch();
//        for(SortieEnStock e: stock){
//            cumul+=e.nbCompteur;
//        }
//    return cumul;
//    }
      
    public static int getCumulSortie(long idAgence){
        int cumul =0;
        List<SortieEnStock> stock = SortieEnStock.find("user.account.id=?1", idAgence).fetch();
        for(SortieEnStock s: stock){
            cumul+=s.nbCompteur;
        }
    return cumul;
    }

}
