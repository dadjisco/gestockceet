package controllers.portals.magasinier;

import controllers.Secure;
import controllers.SessionHandler;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import java.util.List;
import javax.persistence.PersistenceException;
import models.Commande;
import models.UniteAppreciation;
import org.apache.commons.io.FileUtils;
import play.Logger;
import play.Play;
import play.data.validation.Required;
import play.data.validation.Validation;

/**
 * Created by TICBUILDER
 */
@With({SessionHandler.class, Secure.class})
public class Commandes extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "creer_commande");
    }

    public static void index() {
        List<models.Commande> commandes = models.Commande.find("nbreCompteurRestant<>0").fetch();
        render(commandes);
    }

    public static void newCommande() {
        List<UniteAppreciation> lesunites = UniteAppreciation.findAll();
        render(lesunites);
    }

        public static void saveNewCommande(@Required(message = "Référence commande obligatoire") String refCommande, 
                                        @Required(message = "Date obligatoire") Date dateCommande, 
                                        @Required List<String> nbDUnite, List<Long> uniteAppreciation, 
                                        @Required(message = "Aucun fichier trouvé") File filePath) throws IOException {
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            newCommande();
        }
        //System.out.println("==> nombreCompteur: "+nombreCompteur.size()+" nbDUnite: "+nbDUnite.size()+" uniteAppreciation: "+uniteAppreciation.size());
        String comment="";
        int nbCompteurTotal = 0;
        for(int x=0; x<uniteAppreciation.size(); x++){
            UniteAppreciation unite = models.UniteAppreciation.findById(uniteAppreciation.get(x));
            comment = comment.concat(nbDUnite.get(x)+" "+unite.libelleUnite+"; ");
            if(unite.libelleUnite.compareToIgnoreCase("Compteur")!=0){                
                nbCompteurTotal = nbCompteurTotal+(unite.nbCompteur*Integer.parseInt(nbDUnite.get(x)));
            }else{
                nbCompteurTotal = nbCompteurTotal+(Integer.parseInt(nbDUnite.get(x)));
            }
        }
            Commande commande=new Commande(refCommande, dateCommande, comment, nbCompteurTotal);       
            commande.save();
                try {
            String extension = null;
            if(getFileType(filePath.getName()).compareToIgnoreCase("pdf")==0){
                extension = ".pdf";
            }else if(getFileType(filePath.getName()).compareToIgnoreCase("png")==0){
                extension = ".png";
            }else if(getFileType(filePath.getName()).compareToIgnoreCase("jpg")==0 || getFileType(filePath.getName()).compareToIgnoreCase("jpeg")==0){
                extension = ".jpg";
            }
            uploadFile(filePath, "CMD-" + commande.id + extension);
            String chemin = "/data/commandes/";
            commande.filePath = chemin.concat("CMD-" + commande.id + extension);
            commande.save();
            flash("success", "Demande envoyé avec succès");
        } catch (PersistenceException ex) {
            Logger.error("Interruption de l'enregistrement de la commande ==> " + commande.dateCommande);
            validation.addError("errors", "Une erreur s'est produite lors de l'enregistrement, veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
        }
        index();
    }
        
    public static boolean uploadFile(File fichierJoint, String nom) throws IOException {
        File uploadDir = new File(Play.applicationPath, "/data/commandes");
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
        


    public static void deleteCommande(Long commande) {
        Commande a = Commande.findById(commande);
        try {
            a.delete();
            flash("success", "Suppression effectuée avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer cet élement car il est déjà utilisé", new String[]{"errors"});
            params.flash();
            validation.keep();
            Logger.fatal(ex, null, (Object) null);
        } finally {
            index();
        }
    }

}
