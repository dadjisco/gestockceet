package controllers.portals.soustraitant;

import controllers.Secure;
import controllers.Security;
import controllers.SessionHandler;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.PersistenceException;
import models.Compteur;
import models.LotCompteur;
import models.PoseCompteur;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import play.Logger;
import play.Play;
import play.data.validation.Required;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 * Created by TICBUILDER
 */
@With({SessionHandler.class, Secure.class})
public class PoserCompteurs extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "poser_compteur");
    }

    public static void index() {
        renderArgs.put("action", "liste_compteurs_affecter");
        List<LotCompteur> lotCompteurs = LotCompteur.find("agentPoseur.id=?1", Security.getUser().id).fetch();
        List<Compteur> compteurs = new ArrayList<Compteur>();
        for(LotCompteur lc:lotCompteurs){
            for(Compteur c: lc.getCompteurs()){
                compteurs.add(c);
            }
        }
        List<Compteur> compteursPose=new ArrayList<Compteur>();
        List<PoseCompteur> list=PoseCompteur.find("poseur.account.id=?1", Security.getAccount().id).fetch();
        for(PoseCompteur pose:list){
                compteursPose.add(pose.compteur);
        }
        if(!list.isEmpty()){
            compteurs.removeAll(compteursPose);
        }
        render(compteurs);
    }

    public static void effectuerPose(long id){
        Compteur compteur=Compteur.findById(id);
        render(compteur);
    }
    
    public static void saveNewPose(
        @Required(message = "Aucun fichier trouvé") File capturePosePath,
        @Required(message = "Libelle Zone Obligatoire") String libelleZone,
        Compteur compteur, String numeroBranchement, double latitude, double longitude) throws IOException{
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
        }
        String chemin = "/data/captures/";
        if (FilenameUtils.isExtension(capturePosePath.getName(), "pdf")) {
            validation.addError("files", "Le format du fichier " + capturePosePath.getName() + " est incorrect... Format jpg ou png attendu", new String[]{"files"});
            params.flash();
            validation.keep();
        }
        PoseCompteur pc=new PoseCompteur(latitude, longitude, libelleZone, numeroBranchement, compteur, Security.getUser(), new Date());
        pc.save();
        try {
            String extension = null;
            if(getFileType(capturePosePath.getName()).compareToIgnoreCase("png")==0){
                extension = ".png";
            }else if(getFileType(capturePosePath.getName()).compareToIgnoreCase("jpg")==0 || getFileType(capturePosePath.getName()).compareToIgnoreCase("jpeg")==0){
                extension = ".jpg";
            }
            uploadFile(capturePosePath, "CAP-" + pc.id + extension);
            pc.capturePosePath = chemin.concat("CAP-" + pc.id + extension);
            pc.save();
            flash("success", "Pose enregistrée avec succès");
        } catch (PersistenceException ex) {
            Logger.error("Interruption de l'enregistrement de la pose ==> " + pc.id);
            validation.addError("errors", "Une erreur s'est produite lors de l'enregistrement, veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
        }
        index();
    }

    public static boolean uploadFile(File fichierJoint, String nom) throws IOException {
        File uploadDir = new File(Play.applicationPath, "/data/captures");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        Logger.info("FILENAME : " + fichierJoint.getName());
        File uploadedFile = new File(uploadDir, nom);
        FileUtils.copyFile(fichierJoint, uploadedFile);
        return true;
    }
    
    public static String getFileType(String FilePath) {
        String type = null;
        if (FilePath.lastIndexOf(".") > 0) {
            String ext = FilePath.substring(FilePath.lastIndexOf("."));
            if (ext.compareToIgnoreCase(".png") == 0) {
                type = "png";
            }
            if (ext.compareToIgnoreCase(".jpg") == 0) {
                type = "jpg";
            }
            if (ext.compareToIgnoreCase(".pdf") == 0) {
                type = "pdf";
            }
            if (ext.compareToIgnoreCase(".mp4") == 0 || ext.compareToIgnoreCase(".wmv") == 0) {
                type = "video";
            }
            if (ext.compareToIgnoreCase(".mp3") == 0) {
                type = "audio";
            }
        }
        return type;
    }

}
