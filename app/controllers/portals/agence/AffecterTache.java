package controllers.portals.agence;

import controllers.Secure;
import controllers.SessionHandler;
import java.util.ArrayList;
import java.util.Date;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import models.*;
import play.Logger;
import play.data.validation.Required;
import java.util.List;
import javax.persistence.PersistenceException;
import play.data.validation.Validation;

/**
 * Created by TICBUILDER
 */
@With({SessionHandler.class, Secure.class})
public class AffecterTache extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "affecter_tache");
    }
    
    public static void index() {
        List<PoseCompteur> poseCompteurs=PoseCompteur.find("controleur is null and dateAffectation is null").fetch();
        render(poseCompteurs);
    }

    public static void details(long pose) {
        PoseCompteur poseCompteur=PoseCompteur.findById(pose);
        //List<User> controleurs = models.User.find("profil.libProfil=?1 and account.id=?2", "CONTROLEUR", poseCompteur.poseur.account.id).fetch();
        //System.out.println("==> controleurs: "+controleurs.size());
        //LISTE DES AGENTS CONTROLEURS AGENCE + SOUS-TRAITANTS
        List<models.User> controleurs = new ArrayList<models.User>();
        //LISTE DES CONTROLEURS POSEURS
        List<models.User> agents1 = models.User.find("profil.libProfil=?1 and account.id=?2", "CONTROLEUR", poseCompteur.poseur.account.id).fetch();
        for(models.User u:agents1){
            models.User user=models.User.findById(u.id);
            controleurs.add(user);
        } 
        //LISTE DES SOUS TRAITANTS
        List<models.Soustraitant> agents2 = models.Soustraitant.find("agence.id=?1", poseCompteur.poseur.account.id).fetch();
        for(models.Soustraitant st:agents2){
            if(st.agence.id==poseCompteur.poseur.account.id){
                models.User u=models.User.find("account.id=?1", st.id).first();
                if(!u.profil.libProfil.isEmpty() && u.profil.libProfil.equalsIgnoreCase("CONTROLEUR")){
                    controleurs.add(u);
                }               
            }
        }
        render(poseCompteur, controleurs);
    }

    public static void saveAffectationTache(PoseCompteur poseCompteur, 
            @Required(message = "Controleur Obligatoire") long userControleurId){
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            index();
        }
        try {
                models.User user = models.User.findById(userControleurId);
                poseCompteur.dateAffectation = new Date();
                poseCompteur.controleur = user;
                poseCompteur.save();
                flash("success", "Opération éffectuée avec succès");
        } catch (PersistenceException ex) {
            System.err.println(ex);
            Logger.error("Opération d'affectation de tâche échouée!");
            Validation.addError("errors", "Une erreur s'est produite lors de l'enregistrement veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
        }
        finally{
            index();
        }
    }
    
    public static void tachesConfirmees(){
        renderArgs.put("action", "tache_confirmee");
        List<PoseCompteurAppreciation> pcas=PoseCompteurAppreciation.find("archiver is not true").fetch();
        render(pcas);
    }
    
    public static void archiver(@Required(message = "Sélection d'une tâche obligatoire") List<Long> idPca){
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            index();
        }
        try {
                for(int x=0;x<idPca.size();x++){
                    PoseCompteurAppreciation pca=PoseCompteurAppreciation.findById(idPca.get(x));
                    pca.archiver = true;
                    pca.save();
                }
                flash("success", "Opération éffectuée avec succès");
        } catch (PersistenceException ex) {
            System.err.println(ex);
            Logger.error("Opération d'archivage échoué!");
            Validation.addError("errors", "Une erreur s'est produite lors de l'enregistrement veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
        }
        finally{
            index();
        }
    }
    
}
