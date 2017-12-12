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
public class TypeControleurs extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "type_controleur");
    }

    public static void index() {
        List<models.TypeControleur> typeControleurs = models.TypeControleur.findAll();
        render(typeControleurs);
    }

    public static void newTypecontroleur() {
        render();
    }

    public static void saveNewTypecontroleur(@Required(message = "designation obligatoire") String designation) {
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            newTypecontroleur();
        }
        TypeControleur a = new TypeControleur(designation);
        saveTypecontroleur(a);
        //flash.put("success",true);  
        newTypecontroleur();
        //return ok("/newprofil");
    }

    public static void saveTypecontroleur(TypeControleur tc) {
        try {
            tc.save();
            flash("success", "Type controleur créée avec succès");
            newTypecontroleur();
        } catch (PersistenceException ex) {
            Logger.error("type controleur " + tc.designation + " existe déjà");
            Validation.addError("errors", "Une erreur s'est produite lors de l'enregistrement veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
            newTypecontroleur();
        }
    }

    public static void deleteTypecontroleur(Long typeControleur) {
        TypeControleur a = TypeControleur.findById(typeControleur);
        try {
            a.delete();
            flash("success", "type controleur a été supprimé avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer cet elémént car il est déjà en utilisation", new String[]{"errors"});
            params.flash();
            validation.keep();
            Logger.fatal(ex, null, (Object) null);
        } finally {
            index();
        }
    }

    public static void editTypecontroleur(Long a) {
        TypeControleur typeControleur = TypeControleur.findById(a);
        render(typeControleur);
    }

    public static void saveEditTypecontroleur(@Valid(message = "Les informations entrées sont incompletes") TypeControleur typeControleur) {
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
            typeControleur.save();
            flash("success", "Type controleur modifiée avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer cet elément car il est déjà en utilsation", new String[]{"errors"});
            Logger.error(ex.getMessage());
            params.flash();
            validation.keep();
        } finally {
            index();
        }
    }
}
