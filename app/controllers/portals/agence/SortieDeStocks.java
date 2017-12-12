package controllers.portals.agence;

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
 * Created by TICBUILDER
 */
@With({SessionHandler.class, Secure.class})
public class SortieDeStocks extends Controller {

    public List<Long> listCptSelect = new ArrayList<Long>();
    
    @Before
    static void setActionMenu() {
        renderArgs.put("action", "affecter_compteur");
    }

    public static void index(long id) {
        List<Compteur> compteurs= new ArrayList<Compteur>();
        List<EntreeEnStock> entreeEnStocks = EntreeEnStock.find("user.account.id=?1 and nbRestant>0", Security.getAccount().id).fetch();
        for(EntreeEnStock e: entreeEnStocks){
            for(Compteur c: e.getCompteurs()){
                compteurs.add(c);
            }
        }
        List<Compteur> comp= new ArrayList<Compteur>();
        models.Agence agence=models.Agence.findById(id);
        List<SortieEnStock> sortieEnStocks=SortieEnStock.find("user.account.id=?1", Security.getAccount().id).fetch();
        for(SortieEnStock s: sortieEnStocks){
            for(Compteur c: s.getCompteurs()){
                comp.add(c);
            }
        }
        compteurs.removeAll(comp);
        render(compteurs, agence);
    }

    public static void newSortieDeStock() {
        render();
    }

    public static void saveNewSortieDeStock(
        @Required(message = "Compteur Obligatoire") List<Long> idCompteur, 
        @Required(message="Bénéficiaire obligatoie") long agenceSelect,
        @Required(message = "Date sortie obligatoire") Date dateAffectation) {        
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            index(agenceSelect);
        }
        try {
                List<Compteur> compteurs = new ArrayList<Compteur>();
                models.Agence beneficiaire = models.Agence.findById(agenceSelect);
                for (int x = 0; x < idCompteur.size(); x++) {
                    Compteur cpt = Compteur.findById(idCompteur.get(x));
                    compteurs.add(cpt);
                }
//(1)==> Save Sortie de stock
                SortieEnStock sortieEnStock = new SortieEnStock(beneficiaire, compteurs.size(), dateAffectation, Security.getUser(), compteurs);
                sortieEnStock.save();
//(2)==> Diminuer l'entrée par la quantité puisée
                
                flash("success", "Opération éffectuée avec succès");
        } catch (PersistenceException ex) {
            System.err.println(ex);
            Logger.error("Opération d'affectation de compteur échouée!");
            Validation.addError("errors", "Une erreur s'est produite lors de l'enregistrement veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
        }
        finally{
            index(agenceSelect);
        }
    }

    public static boolean stockUpdate(List<Long> compteur, long agence, Date date){
        List<Compteur> compteurs= new ArrayList<Compteur>();
        models.Agence beneficiaire = models.Agence.findById(agence);
        long idEntree=0;
        for(int x=0; x<compteur.size(); x++){
            Compteur cpt=Compteur.findById(compteur.get(x));
            compteurs.add(cpt);
            //idEntree = cpt.entreeEnStock.id;
            //livraisonId = cpt.entreeEnStock.sortieEnStock.livraison.id;
        }
        //Save Sortie de stock
        SortieEnStock sortie = new SortieEnStock(beneficiaire, compteur.size(), date, Security.getUser());
        sortie.setCompteurs(compteurs);
        //sortie.livraison.id=livraisonId;
        sortie.save();
        //Mis à jour du Stock disponible pour l'entité expéditeur
        EntreeEnStock ees=EntreeEnStock.findById(idEntree);
        ees.nbRestant-=compteur.size();
        ees.save();
        return true;
    }
        
    public static void deleteSortieDeStock(Long compteur) {
        Compteur a = Compteur.findById(compteur);
        try {
            a.delete();
            flash("success", "L'agence a été supprimé avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer ce magasin car elle est déjà utilisé", new String[]{"errors"});
            params.flash();
            validation.keep();
            Logger.fatal(ex, null, (Object) null);
        } finally {
            index(0);
        }
    }

    public static void editEntreeEnStock(Long a) {
        Compteur compteur = Compteur.findById(a);
        List<models.Compteur> compteurs = Compteur.findAll();
        List<SortieEnStock> sortieEnStocks = SortieEnStock.find("beneficiaire=?1 and nbCompteurRestant > ?2", Security.getAccount(), 0).fetch();
        render(compteur, compteurs, sortieEnStocks);
    }

    public static void saveEditEntreeEnStock(@Valid(message = "Les informations entrées sont incompletes") Compteur compteur, long typeCompteur, long livraison) {

        if (Validation.hasErrors()) {
            params.flash();
            validation.keep();
            index(0);
        }
        try {
            TypeCompteur typeCompt = TypeCompteur.findById(typeCompteur);
            Livraison command = Livraison.findById(livraison);
            compteur.typeCompteur = typeCompt;
            //compteur.livraison = command;
            compteur.save();
            flash("success", "Compteur modifiée avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer ce Compteur car elle est déjà en utilisation", new String[]{"errors"});
            Logger.error(ex.getMessage());
            params.flash();
            validation.keep();
        } finally {
            index(0);
        }
    }
}
