package controllers.portals.magasinier;

import controllers.Secure;
import controllers.Security;
import controllers.SessionHandler;
import java.io.File;
import java.util.List;
import models.Demande;
import models.Demande_TypeCompteur;
import play.Play;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;


/**
 * Created by yasser
 */
@With({SessionHandler.class, Secure.class})
public class Demandes extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "liste_des_demandes");
    }

    public static void index() {
        List<Demande> demandes=Demande.find("accountDestinataire.id=?1", Security.getAccount().id).fetch();
        render(demandes);
    }

    public static void details(long demande){
        Demande demandeInitial=Demande.findById(demande);
        List<Demande_TypeCompteur> demandes = Demande_TypeCompteur.find("demande.id=?", demande).fetch();
        int cumul=0;
        for(Demande_TypeCompteur dtc: demandes){
            cumul+=dtc.QteCompteur;
        }
        
        render(demandeInitial, demandes, cumul);
    }

    public static void getFile(long ref) {
        models.Demande demande = models.Demande.findById(ref);
            File pj = new File(Play.applicationPath, demande.getFile());
            renderBinary(pj,pj.getName());       
    }


}
