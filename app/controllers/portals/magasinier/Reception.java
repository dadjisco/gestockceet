package controllers.portals.magasinier;

import controllers.Secure;
import controllers.SessionHandler;
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

/**
 * Created by yasser
 */
@With({SessionHandler.class, Secure.class})
public class Reception extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "reception");
    }
    
    public static void index() {
        int stockRestant=0, stockEntree=0, stockSortie=0;
        List<Livraison> stockInitials = Livraison.find("nbCompteurRestant>0").fetch();
        
        for(Livraison liv: stockInitials){
            stockEntree +=liv.nombreCompteur;
            stockRestant += liv.nbCompteurRestant;
        }
        stockSortie = stockEntree - stockRestant;
        
        render(stockInitials, stockSortie, stockEntree, stockRestant);
    }
    
    public static void newReception() {
        List<models.Livraison> stockInitials = Livraison.findAll();
        List<UniteAppreciation> lesunites = UniteAppreciation.findAll();
        //List<Commande> commandes = Commande.find("nbreCompteurRestant is not null").fetch();
        List<models.Commande> commandes = models.Commande.find("nbreCompteurRestant<>0").fetch();
        render(stockInitials, lesunites, commandes);
    }

    public static void saveNewReception(
            @Required(message = "Référence commande obligatoire") long refCommande,
            @Required(message = "Référence livraison obligatoire") String refLivraison, 
            @Required(message = "Date obligatoire") Date dateReception, 
            @Required String logistique,@Required List<String> nbDUnite, List<Long> uniteAppreciation) {
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            newReception();
        }
        //System.out.println("==> nombreCompteur: "+nombreCompteur.size()+" nbDUnite: "+nbDUnite.size()+" uniteAppreciation: "+uniteAppreciation.size());
        String comment="";
        int nbCompteurTotal = 0;
        for(int x=0; x<uniteAppreciation.size(); x++){
            UniteAppreciation unite = models.UniteAppreciation.findById(uniteAppreciation.get(x));
//            System.out.println("==> unite "+x+": "+unite.libelleUnite);
//            System.out.println("==> unite "+x+": "+nbDUnite.get(x));
            comment = comment.concat(nbDUnite.get(x)+" "+unite.libelleUnite+"; ");
            if(unite.libelleUnite.compareToIgnoreCase("Compteur")!=0){                
                nbCompteurTotal = nbCompteurTotal+(unite.nbCompteur*Integer.parseInt(nbDUnite.get(x)));
            }else{
                nbCompteurTotal = nbCompteurTotal+(Integer.parseInt(nbDUnite.get(x)));
            }
        }
        //System.out.println("==> nbCompteurTotal: "+nbCompteurTotal);
        Commande commande = Commande.findById(refCommande);
        Livraison stockInitial = new Livraison(commande, refLivraison, dateReception, logistique.toUpperCase(), comment, nbCompteurTotal); 
        try {
            stockInitial.save();
            commande.nbreCompteurRestant -= stockInitial.nombreCompteur;
            commande.save();
            flash("success", "Le stock a été créée avec succès");
            newReception();
        } catch (PersistenceException ex) {
            Logger.error("Livraison " + stockInitial.referenceLivraison + " existe déjà");
            Validation.addError("errors", "Une erreur s'est produite lors de l'enregistrement veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
            newReception();
        }
        //flash.put("success",true);  
        newReception();
        //return ok("/newprofil");
    }

//    public static void saveReception(Livraison stockInitial, Commande commande) {
//        try {
//            stockInitial.save();
//            commande.nbreCompteurRestant -= stockInitial.nombreCompteur;
//            commande.save();
//            flash("success", "Le stock a été créée avec succès");
//            newReception();
//        } catch (PersistenceException ex) {
//            Logger.error("Le point " + stockInitial.referenceLivraison + " existe déjà");
//            Validation.addError("errors", "Une erreur s'est produite lors de l'enregistrement veuillez consulter les logs", new String[]{"errors"});
//            params.flash();
//            validation.keep();
//            newReception();
//        }
//    }

    public static void deleteReception(Long stockInitial) {
        Livraison a = Livraison.findById(stockInitial);
        try {
            a.delete();
            flash("success", "Le stock a été supprimée avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer cet élément car il est déjà en utilisation", new String[]{"errors"});
            params.flash();
            validation.keep();
            Logger.fatal(ex, null, (Object) null);
        } finally {
            index();
        }
    }

    public static void editReception(Long a) {
        Livraison stockInitial = Livraison.findById(a);
        List<models.Livraison> stockInitials = Livraison.findAll();
        List<UniteAppreciation> lesunites = UniteAppreciation.findAll();
        render(stockInitial, stockInitials, lesunites);
    }

    public static void saveEditReception(@Valid(message = "Les informations entrées sont incompletes") Livraison stockInitial) {
        //validation.valid(point);
        if(Validation.hasErrors()){
            params.flash();
            validation.keep();
            index();
        }        
        try {
            stockInitial.save();
            flash("success", "Stock réceptionné modifiée avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer ce stock car elle est déjà en utilisation", new String[]{"errors"});
            Logger.error(ex.getMessage());
            params.flash();
            validation.keep();
        } finally {
            index();
        }
    }

}
