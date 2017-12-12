package controllers.portals.admin;

import controllers.Secure;
import controllers.SessionHandler;
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
public class TypeCompteurs extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "type_compteur");
    }

    public static void index() {
        List<models.TypeCompteur> typeCompteurs = models.TypeCompteur.findAll();
        render(typeCompteurs);
    }

    public static void newTypecompteur() {
        render();
    }

    public static void saveNewTypecompteur(@Required(message = "Type compteur obligatoire") String libelleCompteur) {
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            newTypecompteur();
        }
        TypeCompteur a = new TypeCompteur(libelleCompteur);
        saveTypecompteur(a);
        //flash.put("success",true);  
        newTypecompteur();
        //return ok("/newprofil");
    }

    public static void saveTypecompteur(TypeCompteur tc) {
        try {
            tc.save();
            flash("success", "Type compteur créée avec succès");
            newTypecompteur();
        } catch (PersistenceException ex) {
            Logger.error("Type compteur " + tc.libelleCompteur + " existe déjà");
            Validation.addError("errors", "Une erreur s'est produite lors de l'enregistrement veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
            newTypecompteur();
        }
    }

    public static void deleteTypecompteur(Long typeCompteur) {
        TypeCompteur a = TypeCompteur.findById(typeCompteur);
        try {
            a.delete();
            flash("success", "typeCompteur a été supprimé avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer cet elémént car il est déjà en utilisation", new String[]{"errors"});
            params.flash();
            validation.keep();
            Logger.fatal(ex, null, (Object) null);
        } finally {
            index();
        }
    }

    public static void editTypecompteur(Long a) {
        TypeCompteur typeCompteur = TypeCompteur.findById(a);
        render(typeCompteur);
    }

    public static void saveEditTypecompteur(@Valid(message = "Les informations entrées sont incompletes") TypeCompteur typeCompteur) {
        //validation.valid(point);
        if(Validation.hasErrors()){
            params.flash();
            validation.keep();
            index();
        }
//        PointService p = PointService.findById(point.id);
//        p.id_account = point.id_account;
//        p.etat = point.etat;
//        p.libPoint = point.libPoint;
        try {
            typeCompteur.save();
            flash("success", "Type compteur modifiée avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer cet elément car il est déjà en utilisation", new String[]{"errors"});
            Logger.error(ex.getMessage());
            params.flash();
            validation.keep();
        } finally {
            index();
        }
    }
}
