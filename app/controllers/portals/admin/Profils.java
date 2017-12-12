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
import utils.config.Droit;
import java.util.Arrays;
import play.data.validation.Validation;

/**
 * Created by yasser
 */
@With({SessionHandler.class, Secure.class})
public class Profils extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "profil");
    }

    public static void index() {
        List<Profil> profils = Profil.findAll();
        render(profils);
    }

    public static void newProfil() {
        Droit[] d = Droit.values();
        List<Droit> droits = Arrays.asList(d);
        render(droits);
    }

    public static void saveNewProfil(@Required String libProfil, @Required String droit) {
    	
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            newProfil();
        }

        Profil p = new Profil(libProfil.toUpperCase(), droit);
        saveProfil(p);
        //flash.put("success",true);  
        newProfil();

        //return ok("/newprofil");
    }

    public static void saveProfil(Profil profil) {
        try {
            profil.save();
            flash("success", "Le profil a été créé avec succès");
            newProfil();
        } catch (PersistenceException ex) {
            Logger.error("Le profil " + profil.libProfil + " existe déjà");
            Validation.addError("errors", "Une erreur s'est produit lors de l'enregistrement veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
            newProfil();
        }
    }

    public static void deleteProfil(Long profil) {
        Profil p = Profil.findById(profil);
        try {
            p.delete();
            flash("success", "Le profil a été supprimé avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer ce profil car il est d�j� affect� � un utilisateur", new String[]{"errors"});
            params.flash();
            validation.keep();
        } finally {
            index();
        }
    }

    public static void editProfil(Long p) {
        Profil profil = Profil.findById(p);
        Droit[] d = Droit.values();
        List<Droit> droits = Arrays.asList(d);
        render(profil, droits);
    }

    public static void saveEditProfil(@Valid(message = "Les informations entr�es sont incompletes") Profil profil) {
        validation.valid(profil);
        if(Validation.hasErrors()){
            params.flash();
            validation.keep();
            index();
        }
        Profil p = Profil.findById(profil.id);
        p.droit = profil.droit;
        try {
            p.save();
            flash("success", "Le profil a été modifié avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer ce profil car il est déjà affecté un utilisateur", new String[]{"errors"});
            params.flash();
            validation.keep();
        } finally {
            index();
        }
    }
}
