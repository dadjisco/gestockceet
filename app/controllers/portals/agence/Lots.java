package controllers.portals.agence;

import controllers.Secure;
import controllers.Security;
import controllers.SessionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import models.*;
import play.Logger;
import play.data.validation.Required;
import play.data.validation.Valid;
import java.util.List;
import javax.persistence.PersistenceException;
import play.data.validation.Validation;
import play.db.jpa.JPA;

/**
 * Created by TICBUILDER
 */
@With({SessionHandler.class, Secure.class})
public class Lots extends Controller {

    public List<Long> listCptSelect = new ArrayList<Long>();
    
    @Before
    static void setActionMenu() {
        renderArgs.put("action", "creer_lot_compteur");
    }

    public static void index() {
        List<Compteur> compteurs= new ArrayList<Compteur>();
        List<EntreeEnStock> entreeEnStocks = EntreeEnStock.find("user.id=? and nbRestant>0", Security.getUser().id).fetch();
        for(EntreeEnStock e: entreeEnStocks){
            for(Compteur c: e.getCompteurs()){
                compteurs.add(c);
            }
        }
        Integer cpt = JPA.em().createQuery("select count(*) from LotCompteur l").getFirstResult()+1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String libelleLotCompteur = "LOT_".concat(sdf.format(new Date())+"_").concat(cpt.toString());
        List<User> agents = models.User.find("profil.libProfil=?1 and account.id=?2", "POSEUR", Security.getAccount().id).fetch();
        render(compteurs, libelleLotCompteur, agents);
    }

    public static void newSortie() {
        render();
    }

    public static void saveNewSortie(@Required(message = "Compteur Obligatoire") List<Long> idCompteur, 
        @Valid(message="LibelleLot ne doit pas être null") String libelleLot,
        @Required(message = "AgentPoseur obligatoire") long agentPoseur) {
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            index();
        }
        try {
            //1- 2ème FORME DE CREATION DE LOT DE COMPTEUR
                models.User user = models.User.findById(agentPoseur);
                List<Compteur> compteurs=new ArrayList<Compteur>();
                for(int x=0; x<idCompteur.size();x++){
                    Compteur compt=Compteur.findById(idCompteur.get(x));
                    compteurs.add(compt);
                }
                LotCompteur lc = new LotCompteur(libelleLot, compteurs, user, new Date());
                lc.save();
                //2- ENREGISTREMENT DE LA SORTIE DE STOCK COTE AGENCE
                SortieEnStock nvlleSortie=new SortieEnStock(user.account, compteurs.size(), new Date(), Security.getUser(), compteurs);
                nvlleSortie.save();
                flash("success", "Opération éffectuée avec succès");
        } catch (PersistenceException ex) {
            Logger.error("Opération d'affectation de compteur échouée!");
            Validation.addError("errors", "Une erreur s'est produite lors de l'enregistrement veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
        }finally{
                index();
        }              
    }
    
//    public static void saveSortie(Compteur compteur) {
//        try {
//            compteur.save();
//            //newStockUpdate(idEntreeStock);
//            flash("success", "Le compteur a été créée avec succès");
//            newSortie();
//        } catch (PersistenceException ex) {
//            Logger.error("Le compteur " + compteur.toString() + " existe déjà");
//            Validation.addError("errors", "Une erreur s'est produite lors de l'enregistrement veuillez consulter les logs", new String[]{"errors"});
//            params.flash();
//            validation.keep();
//            newSortie();
//        }
//    }

//    public static void deleteSortie(Long compteur) {
//        Compteur a = Compteur.findById(compteur);
//        try {
//            a.delete();
//            flash("success", "L'agence a été supprimé avec succès");
//        } catch (PersistenceException ex) {
//            Validation.addError("errors", "Impossible de supprimer ce magasin car elle est déjà utilisé", new String[]{"errors"});
//            params.flash();
//            validation.keep();
//            Logger.fatal(ex, null, (Object) null);
//        } finally {
//            index(0);
//        }
//    }
//
//    public static void editEntreeEnStock(Long a) {
//        Compteur compteur = Compteur.findById(a);
//        List<models.Compteur> compteurs = Compteur.findAll();
//        List<SortieEnStock> sortieEnStocks = SortieEnStock.find("beneficiaire=?1 and nbCompteurRestant > ?2", Security.getAccount(), 0).fetch();
//        render(compteur, compteurs, sortieEnStocks);
//    }
//
//    public static void saveEditEntreeEnStock(@Valid(message = "Les informations entrées sont incompletes") Compteur compteur, long typeCompteur, long livraison) {
//
//        if (Validation.hasErrors()) {
//            params.flash();
//            validation.keep();
//            index(0);
//        }
//        try {
//            TypeCompteur typeCompt = TypeCompteur.findById(typeCompteur);
//            Livraison command = Livraison.findById(livraison);
//            compteur.typeCompteur = typeCompt;
//            //compteur.livraison = command;
//            compteur.save();
//            flash("success", "Compteur modifiée avec succès");
//        } catch (PersistenceException ex) {
//            Validation.addError("errors", "Impossible de supprimer ce Compteur car elle est déjà en utilisation", new String[]{"errors"});
//            Logger.error(ex.getMessage());
//            params.flash();
//            validation.keep();
//        } finally {
//            index(0);
//        }
//    }
}
