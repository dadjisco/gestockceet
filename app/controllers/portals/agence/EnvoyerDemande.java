package controllers.portals.agence;

import controllers.Secure;
import controllers.Security;
import controllers.SessionHandler;
import groovyjarjarcommonscli.ParseException;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.persistence.PersistenceException;
import models.Account;
import models.Demande;
import models.Demande_TypeCompteur;
import models.TypeCompteur;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import play.Logger;
import play.Play;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.db.jpa.JPA;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;


/**
 * Created by yasser
 */
@With({SessionHandler.class, Secure.class})
public class EnvoyerDemande extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "envoyer_demande");
    }

    public static void index() {
        List<Demande> demandes=Demande.find("user.id=?1", Security.getUser().id).fetch();
        //List<Demande> demandes=Demande.find("user.id=?1 or accountDestinataire.id=?2", Security.getUser().id, Security.getAccount().id).fetch();
        render(demandes);
    }
    
    public static void newDemande(){
        List<TypeCompteur> typeCompteurs=TypeCompteur.findAll();
        List<Account> accounts= JPA.em().createNativeQuery("select * from Account a where a.dtype=:p1 or a.dtype=:p2", Account.class)
                .setParameter("p1", "Agence")
                .setParameter("p2", "Magasinier")
                .getResultList();
        render(typeCompteurs, accounts);
    }

    public static void saveNewDemande(@Required(message = "Aucun fichier trouvé") File filePath, @Required(message = "date demande obligatoire") Date dateDemande,
        List<Long> typeCompteur, List<String> nbDUnite, long destinataire) throws IOException, ParseException {
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
        }
        
        String chemin = "/data/demandes/";
        if (!FilenameUtils.isExtension(filePath.getName(), "pdf")) {
            validation.addError("files", "Le format du fichier " + filePath.getName() + " est incorrect... Format jpg ou png attendu", new String[]{"files"});
            params.flash();
            validation.keep();
        }
        Account account= Account.findById(destinataire);
        Demande demande = new Demande(dateDemande, Security.getUser(), 0, account);
        demande.save();
                try {
            String extension = null;
            if(getFileType(filePath.getName()).compareToIgnoreCase("pdf")==0){
                extension = ".pdf";
            }else if(getFileType(filePath.getName()).compareToIgnoreCase("png")==0){
                extension = ".png";
            }else if(getFileType(filePath.getName()).compareToIgnoreCase("jpg")==0 || getFileType(filePath.getName()).compareToIgnoreCase("jpeg")==0){
                extension = ".jpg";
            }
            uploadFile(filePath, "DEMC-" + demande.id + extension);
            demande.filePath = chemin.concat("DEMC-" + demande.id + extension);
            demande.save();           
            for(int x=0; x<typeCompteur.size(); x++){
                TypeCompteur type = TypeCompteur.findById(typeCompteur.get(x));
                Demande_TypeCompteur dtc=new Demande_TypeCompteur(type, demande, Integer.parseInt(nbDUnite.get(x)));
                dtc.save();
            }
            flash("success", "Demande envoyé avec succès");
        } catch (PersistenceException ex) {
            Logger.error("Interruption de l'enregistrement de la demande ==> " + demande.dateEnvoie);
            validation.addError("errors", "Une erreur s'est produite lors de l'enregistrement, veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
        }
        index();
    }

    public static boolean uploadFile(File fichierJoint, String nom) throws IOException {
        File uploadDir = new File(Play.applicationPath, "/data/demandes");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        Logger.info("FILENAME : " + fichierJoint.getName());
        File uploadedFile = new File(uploadDir, nom);
        FileUtils.copyFile(fichierJoint, uploadedFile);
        return true;
    }
    
    public static void deleteDemande(Long demande) {
        Demande demande1 = Demande.findById(demande);

        try {
            demande1.delete();
            flash("success", "La demande a été supprimé avec succès");
        } catch (PersistenceException ex) {
            validation.addError("errors", "Impossible de supprimer cette demande car il est déjà en utilisation", new String[]{"errors"});
            params.flash();
            validation.keep();
            Logger.fatal(ex, null, (Object) null);
        } finally {
            index();
        }
    }

    public static void editDemande(Long demand) {
        Demande demande = Demande.findById(demand);
        List<TypeCompteur> typeCompteurs = TypeCompteur.findAll();
        render(demande, typeCompteurs);
    }

    public static void saveEditDemande(@Valid(message = "Les informations entrées sont incompletes") Demande demande) {
        validation.valid(demande);
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            index();
        }
        try {
//            System.out.println(" ==> MODIFIER");
            demande.save();
            flash("success", "Votre demande a été modifiée avec succès");
        } catch (PersistenceException ex) {
            validation.addError("errors", "Impossible de supprimer cette demande car il est en utilisation", new String[]{"errors"});
            Logger.error(ex.getMessage());
            params.flash();
            validation.keep();
        } finally {
            index();
        }
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
