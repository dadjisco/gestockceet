package controllers.portals.agence;

import controllers.Secure;
import controllers.SessionHandler;
import static controllers.portals.agence.EnvoyerDemande.getFileType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.PersistenceException;
import models.Compteur;
import org.apache.commons.io.FileUtils;
import play.Logger;
import play.Play;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;


/**
 * Created by yasser
 */
@With({SessionHandler.class, Secure.class})
public class Contentieux extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "contentieux");
    }

    public static void index() {
        List<models.Contentieux> listContentieux = models.Contentieux.findAll();
        render(listContentieux);
    }
    
    public static void details(long idCont){
        models.Contentieux contentieux=models.Contentieux.findById(idCont);
        render(contentieux);
    }

    public static void correctionEcart(
            @Required(message = "Aucun fichier trouvé") File filePath,
            long refContentieux, 
            @Required(message = "Choisissez les compteurs") List<Long> idCompteur) throws IOException{
    if (validation.hasErrors()) {
            params.flash();
            validation.keep();
        }
        models.Contentieux contentieux=models.Contentieux.findById(refContentieux);        
        String chemin = "/data/contentieux/";
        List<Compteur> compteurs = new ArrayList<Compteur>();
        for(int x=0; x<idCompteur.size(); x++){
            Compteur c=Compteur.findById(idCompteur.get(x));
        //(1)->LISTE DES COMPTEURS SELECTIONNEES
            compteurs.add(c);
        }
        try {
            String extension = null;
            if(getFileType(filePath.getName()).compareToIgnoreCase("pdf")==0){
                extension = ".pdf";
            }else if(getFileType(filePath.getName()).compareToIgnoreCase("png")==0){
                extension = ".png";
            }else if(getFileType(filePath.getName()).compareToIgnoreCase("jpg")==0 || getFileType(filePath.getName()).compareToIgnoreCase("jpeg")==0){
                extension = ".jpg";
            }
            uploadFile(filePath, "PV_Contentieux-" + contentieux.id + extension);
            contentieux.pvFilePath = chemin.concat("PV_Contentieux-" + contentieux.id + extension);
            contentieux.traiter = true;
            contentieux.ecart +=compteurs.size();
            contentieux.save();          
            flash("success", "Reglement enregistré avec succès");
        } catch (PersistenceException ex) {
            Logger.error("Interruption de l'enregistrement de la demande ==> " + contentieux.id);
            validation.addError("errors", "Une erreur s'est produite lors de l'enregistrement, veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
        }
        index();
    }

    public static boolean uploadFile(File fichierJoint, String nom) throws IOException {
        File uploadDir = new File(Play.applicationPath, "/data/entrees");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        Logger.info("FILENAME : " + fichierJoint.getName());
        File uploadedFile = new File(uploadDir, nom);
        FileUtils.copyFile(fichierJoint, uploadedFile);
        return true;
    }
    
    public static void getFile(long ref) {
        models.Contentieux contentieux = models.Contentieux.findById(ref);
            File pj = new File(Play.applicationPath, contentieux.getPvFile());
            renderBinary(pj,pj.getName());       
    }

}
