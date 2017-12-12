package controllers.portals.magasinier;

import controllers.Secure;
import controllers.Security;
import controllers.SessionHandler;
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

/**
 * Created by yasser
 */
@With({SessionHandler.class, Secure.class})
public class Affectation extends Controller {

    //variable globale pour garder en memoire le nombre de ligne choisie pour affectation
    public static List<Long> StockDeBase = new ArrayList<Long>();

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "affectation");
    }

    public static void index() {
        
        int stockRestant=0, stockEntree=0, stockSortie=0;
        List<Livraison> stockInitials = Livraison.find("nbCompteurRestant>0").fetch();
        
        for(Livraison liv: stockInitials){
            stockEntree +=liv.nombreCompteur;
            stockRestant += liv.nbCompteurRestant;
        }
        stockSortie = stockEntree - stockRestant;
        
        List<models.SortieEnStock> sortieEnStocks = SortieEnStock.find("user=?1", Security.getUser()).fetch();
        System.out.println("===> sortieEnStocks: "+sortieEnStocks.size());
        render(sortieEnStocks, stockRestant, stockSortie, stockEntree);
    }

    public static void newAffectation(List<Long> idCompteur) {
        StockDeBase = idCompteur;
        int qteTotaleSelect=0;
        List<Agence> agences = Agence.find("principale=?1", true).fetch();
        List<UniteAppreciation> lesunites = UniteAppreciation.findAll();
        qteTotaleSelect = idCompteur.size();
        //List<Demande> demandes=Demande.find("accountDestinataire.id=?1 and ", Security.getAccount().id).fetch();
        render(agences, lesunites, qteTotaleSelect);
    }

    public static void saveNewAffectation(@Required(message = "Date obligatoire") Date dateAffectation, 
            @Valid String nombreCompteur,String qteTotaleSelect, long agence) {
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            newAffectation(StockDeBase);
        }
        if(Integer.parseInt(nombreCompteur) > Integer.parseInt(qteTotaleSelect)){
            params.flash("Dépassement de quantité non autorisé!");
            validation.keep();
        }
        //Enregistrement de la sortie
        List<Compteur> compteurs = new ArrayList<Compteur>();           
        for(int x=0; x<StockDeBase.size(); x++) {
            Compteur c = Compteur.findById(StockDeBase.get(x));
            c.affecter = true;
            c.save();
            compteurs.add(c);
        }
        stockUpdate(compteurs, agence, dateAffectation, Integer.parseInt(nombreCompteur));
        flash("success", "Affectation effectuée avec succès");
        index();
    }

    public static boolean stockUpdate(List<Compteur> compteurs, long agence, Date date, int nombre) {
        Agence aceet = Agence.findById(agence);
        SortieEnStock nvlleSortie = new SortieEnStock(aceet, nombre, date, Security.getUser(), compteurs); 
        nvlleSortie.save();
        return true;
    }
        
    public static void saveAffectation(Livraison stockInitial) {
        try {
            stockInitial.save();
            flash("success", "Le stock a été créée avec succès");
            //newAffectation();
        } catch (PersistenceException ex) {
            Logger.error("Le point " + stockInitial.referenceLivraison + " existe déjà");
            Validation.addError("errors", "Une erreur s'est produit lors de l'enregistrement veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
            //newAffectation();
        }
    }

    public static void deleteAffectation(Long a) {
        SortieEnStock sortieEnStock = SortieEnStock.findById(a);
        try {
            //Livraison initial = Livraison.findById(sortieEnStock.livraison.id);
            //Livraison initial = Livraison.findById(sortieEnStock.getCompteurs().get(0).livraison.id);
            if(sortieEnStock.user == Security.getUser()){
//                initial.nbCompteurRestant = sortieEnStock.nbCompteur;
//                initial.save();
                sortieEnStock.delete();
                flash("success", "Le stock a été supprimée avec succès");
            }
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer cet élément car il est déjà en utilisation", new String[]{"errors"});
            params.flash();
            validation.keep();
            Logger.fatal(ex, null, (Object) null);
        } finally {
            index();
        }
    }

    public static void editAffectation(Long a) {
        SortieEnStock sortieEnStock = SortieEnStock.findById(a);
        List<Agence> agences = Agence.find("principale=?1", true).fetch();
        List<UniteAppreciation> lesunites = UniteAppreciation.findAll();
        render(sortieEnStock, agences, lesunites);
    }

    public static void saveEditAffectation(@Valid(message = "Les informations entrées sont incompletes") SortieEnStock sortieEnStock) {
        //validation.valid(point);
        if (Validation.hasErrors()) {
            params.flash();
            validation.keep();
            index();
        }
        try {
            sortieEnStock.save();
            flash("success", "Stock modifiée avec succès");
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
