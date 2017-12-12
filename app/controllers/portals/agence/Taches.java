package controllers.portals.agence;

import controllers.Secure;
import controllers.Security;
import controllers.SessionHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import models.NiveauAppreciation;
import models.PoseCompteur;
import models.PoseCompteurAppreciation;
import play.Logger;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 * Created by TICBUILDER
 */
@With({SessionHandler.class, Secure.class})
public class Taches extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "poser_compteur");
    }

    public static void index() {
        renderArgs.put("action", "liste_des_taches");
        List<PoseCompteur> poseCompteurs = PoseCompteur.find("controleur.id=?1", Security.getUser().id).fetch();
        List<PoseCompteur> pca=new ArrayList<PoseCompteur>();
        List<PoseCompteurAppreciation> list=PoseCompteurAppreciation.find("controleur.id=?1", Security.getUser().id).fetch();
        for(PoseCompteurAppreciation pose:list){
                pca.add(pose.poseCompteur);
        }
        if(!list.isEmpty()){
            poseCompteurs.removeAll(pca);
        }
        render(poseCompteurs);
    }

    public static void newNotification(long pose){
        renderArgs.put("action", "controler_pose");
        List<NiveauAppreciation> appreciations=NiveauAppreciation.findAll();
        PoseCompteur poseCompteur=PoseCompteur.findById(pose);
        render(appreciations, poseCompteur);
    }
    
    public static void saveNewNotification(@Required(message = "PoseCompteur Obligatoire") PoseCompteur poseCompteur, 
            @Required(message = "Appréciation Obligatoire") long appreciation,
            String commentaire){
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            index();
        }
        try {
            NiveauAppreciation app=NiveauAppreciation.findById(appreciation);
            models.PoseCompteurAppreciation pca= new PoseCompteurAppreciation(poseCompteur, commentaire, new Date(), app, Security.getUser());
            pca.save();
            flash("success", "Notification enregistrée avec succès");
        } catch (Exception e) {
            Logger.error("Interruption de l'envoie de notification");
            validation.addError("errors", "Une erreur s'est produite lors de l'enregistrement, veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
        }
         index();
    }

    
    
    
}
