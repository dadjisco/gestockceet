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
 * Created by TICBUILDER
 */
@With({SessionHandler.class, Secure.class})
public class Magasins extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "magasin");
    }

    public static void index() {
        List<models.Magasin> magasins = models.Magasin.findAll();
        render(magasins);
    }

    public static void newMagasin() {
        List<models.Agence> agences = Agence.findAll();
        render(agences);
    }

    public static void saveNewMagasin(@Required(message = "Code Magasin obligatoire") String codeMagasin, @Required(message = "Nom Magasin obligatoire") String nomMagasin, String phone, String adresse,
            double longitude, double latitude, @Valid long agence) {
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            newMagasin();
        }

        Magasin magasin = new Magasin(codeMagasin, nomMagasin, adresse, phone, latitude, longitude);
        saveMagasin(magasin, agence);
        //flash.put("success",true);  
        newMagasin();
        //return ok("/newprofil");
    }

    public static void saveMagasin(Magasin magasin, long agence) {
        try {
            magasin.save();
            Agence aceet = Agence.findById(agence);
            if(aceet!=null){
                magasin.agence = aceet;
                magasin.save();
            }
            flash("success", "L'agence a été créée avec succès");
            newMagasin();
        } catch (PersistenceException ex) {
            Logger.error("Le point " + magasin.codeMagasin + " existe déjà");
            Validation.addError("errors", "Une erreur s'est produit lors de l'enregistrement veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
            newMagasin();
        }
    }

    public static void deleteMagasin(Long magasin) {
        Magasin a = Magasin.findById(magasin);
        try {
            a.delete();
            flash("success", "L'agence a été supprimé avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer ce magasin car elle est déjà utilisé", new String[]{"errors"});
            params.flash();
            validation.keep();
            Logger.fatal(ex, null, (Object) null);
        } finally {
            index();
        }
    }

    public static void editMagasin(Long a) {
        Magasin magasin = Magasin.findById(a);
        List<models.Agence> agences = Agence.findAll();
        render(magasin, agences);
    }

    public static void saveEditMagasin(@Valid(message = "Les informations entrées sont incompletes") Magasin magasin, long agence) {
        //validation.valid(point);
        if(Validation.hasErrors()){
            params.flash();
            validation.keep();
            index();
        }        
        try {
            Agence aceet = Agence.findById(agence);
            magasin.agence = aceet;
            magasin.save();
            flash("success", "Magasin modifiée avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer ce magasin car elle est déjà en utilisation", new String[]{"errors"});
            Logger.error(ex.getMessage());
            params.flash();
            validation.keep();
        } finally {
            index();
        }
    }
}
