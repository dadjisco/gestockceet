package controllers.portals.magasinier;

import controllers.Secure;
import controllers.Security;
import controllers.SessionHandler;
import java.util.ArrayList;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import models.*;
import java.util.List;

/**
 * Created by yasser
 */
@With({SessionHandler.class, Secure.class})
public class StatistiquesEntreesSorties extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "statistique_entree_sortie");
    }
    
    public static void index() {
        List<Livraison> stockRestants = Livraison.find("nbCompteurRestant>0").fetch();
        List<Livraison> list1 = Livraison.find("nombreCompteur<>nbCompteurRestant").fetch();
        List<Long> list2 = new ArrayList<Long>();
        for(Livraison l: list1){
            list2.add(l.id);
        }
        
        //System.out.println("==> list1: "+list1.size()+"==> list2: "+list2.size());
        List<Livraison> stockEntrees = Livraison.findAll();
        List<SortieEnStock> stockSorties = SortieEnStock.find("user=?1",Security.getUser()).fetch();
        int stockRestant=0, stockEntree=0, stockSortie=0;
        
        for(Livraison liv: stockRestants){
            stockEntree +=liv.nombreCompteur;
            stockRestant += liv.nbCompteurRestant;
        }
        stockSortie = stockEntree - stockRestant;
        render(stockRestants, stockEntrees, stockEntree, stockRestant, stockSortie, stockSorties);
    }
    


}
