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
public class TypeSoustraitants extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "type_soustraitant");
    }

    public static void index() {
        List<models.TypeSousTraitant> typeSousTraitants = models.TypeSousTraitant.findAll();
        render(typeSousTraitants);
    }

    public static void newTypesoustraitant() {
        render();
    }

    public static void saveNewTypesoustraitant(@Required(message = "libelleTypeSousTraitant obligatoire") String libelleTypeSousTraitant) {
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            newTypesoustraitant();
        }
        TypeSousTraitant a = new TypeSousTraitant(libelleTypeSousTraitant);
        saveTypesoustraitant(a);
        //flash.put("success",true);  
        newTypesoustraitant();
        //return ok("/newprofil");
    }

    public static void saveTypesoustraitant(TypeSousTraitant tp) {
        try {
            tp.save();
            flash("success", "Type sous-traitant créée avec succès");
            newTypesoustraitant();
        } catch (PersistenceException ex) {
            Logger.error("Le point " + tp.libelleTypeSousTraitant + " existe déjà");
            Validation.addError("errors", "Une erreur s'est produite lors de l'enregistrement veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
            newTypesoustraitant();
        }
    }

    public static void deleteTypesoustraitant(Long typeSoustraitant) {
        TypeSousTraitant a = TypeSousTraitant.findById(typeSoustraitant);
        try {
            a.delete();
            flash("success", "L'agence a été supprimé avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer cet elémént car il est déjà en utilisation", new String[]{"errors"});
            params.flash();
            validation.keep();
            Logger.fatal(ex, null, (Object) null);
        } finally {
            index();
        }
    }

    public static void editTypesoustraitant(Long a) {
        TypeSousTraitant typeSousTraitant = TypeSousTraitant.findById(a);
        render(typeSousTraitant);
    }

    public static void saveEditTypesoustraitant(@Valid(message = "Les informations entrées sont incompletes") TypeSousTraitant typeSousTraitant) {
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
            typeSousTraitant.save();
            flash("success", "Type sous-traitant modifiée avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer cette agence car elle est déjà affectée à un utilisateur", new String[]{"errors"});
            Logger.error(ex.getMessage());
            params.flash();
            validation.keep();
        } finally {
            index();
        }
    }
}
