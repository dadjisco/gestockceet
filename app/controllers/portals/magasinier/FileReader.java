package controllers.portals.magasinier;

import controllers.Secure;
import controllers.SessionHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import org.apache.commons.io.FileUtils;
import play.Logger;
import play.Play;
import play.mvc.Controller;
import play.mvc.With;

/**
 * Created by TICBUILDER
 */
@With({SessionHandler.class, Secure.class})
public class FileReader extends Controller {

    public static void readingFile(File compteurFile){
    
        String chemin = "/data/compteurs/";
        String fileName = compteurFile.getName();
        try {
            Scanner inputStream = new Scanner(compteurFile);
            inputStream.next(); //POUR IGNORER L'ENTETE DU FICHIER
            while (inputStream.hasNext()) {                
                String data = inputStream.next();
                String[] values = data.split(",");
                System.out.println("===> data: "+data);
            }
            inputStream.close();
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(FileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        controllers.portals.magasinier.Compteurs.index();
    }

    public static boolean uploadFile(File fichierJoint, String nom) throws IOException {
        File uploadDir = new File(Play.applicationPath, "/data/compteurs");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        Logger.info("FILENAME : " + fichierJoint.getName());
        File uploadedFile = new File(uploadDir, nom);
        FileUtils.copyFile(fichierJoint, uploadedFile);
        return true;
    }

}
