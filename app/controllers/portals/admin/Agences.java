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
public class Agences extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "agence");
    }

    public static void index() {
        List<models.Agence> agences = models.Agence.findAll();
        render(agences);
    }

    public static void newAgence() {
        List<Agence> agences = Agence.find("principale=?1", true).fetch();
        render(agences);
    }

    public static void saveNewAgence(@Required(message = "Le partenaire est obligatoire") String codeAgence,
            @Required(message = "Le nom de l'agence est obligatoire") String nomAgence, boolean principale, long agencePrincipale) {
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            newAgence();
        }
        Agence a = new Agence(codeAgence.toUpperCase(),nomAgence.toUpperCase(), principale);
        saveAgence(a, agencePrincipale);
        //flash.put("success",true);  
        newAgence();
        //return ok("/newprofil");
    }

    public static void saveAgence(Agence agence, long idParent) {
        try {
            agence.save();
            Agence ag = Agence.findById(idParent);
            if(ag != null){
                agence.agencePrincipale = ag;
                agence.save();
            }
            flash("success", "L'agence a été créée avec succès");
            newAgence();
        } catch (PersistenceException ex) {
            Logger.error("Le point " + agence.codeAgence + " existe déjà");
            Validation.addError("errors", "Une erreur s'est produit lors de l'enregistrement veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
            newAgence();
        }
    }

    public static void deleteAgence(Long agence) {
        Agence a = Agence.findById(agence);
        try {
            a.delete();
            flash("success", "L'agence a été supprimé avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer cette agence car elle est déjà affectée aux utilisateurs", new String[]{"errors"});
            params.flash();
            validation.keep();
            Logger.fatal(ex, null, (Object) null);
        } finally {
            index();
        }
    }

    public static void editAgence(Long a) {
        Agence agence = Agence.findById(a);
        List<Agence> agences = Agence.find("principale=?1", true).fetch();
        render(agence, agences);
    }

    public static void saveEditAgence(@Valid(message = "Les informations entrées sont incompletes") Agence agence) {
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
            agence.save();
            flash("success", "L'agence a été modifiée avec succès");
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
