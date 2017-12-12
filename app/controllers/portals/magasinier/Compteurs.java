package controllers.portals.magasinier;

import com.opencsv.CSVReader;
import controllers.Secure;
import controllers.Security;
import controllers.SessionHandler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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
public class Compteurs extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "creer_compteur");
    }

    public static void index() {
        List<models.Compteur> compteurs = models.Compteur.find("affecter=?1", false).fetch();
        render(compteurs);
    }

    public static void newCompteur() {
        List<TypeCompteur> typeCompteurs = TypeCompteur.findAll();
        List<Livraison> livraisons = Livraison.find("nbCompteurRestant<>0").fetch();
        render(typeCompteurs, livraisons);
    }

    public static void saveNewCompteur(@Required(message = "Réference livraison obligatoire") long refLivraison, @Required(message = "N° Compteur bligatoire") String numeroCompteur, String numeroSerieCompteur, String puissance, @Required(message = "Caractéristique obligatoie") String caracterisques,
            long typeCompteur, File compteurFile) {
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            newCompteur();
        }
        Livraison livraison = Livraison.findById(refLivraison);
        TypeCompteur typeCompteur1 = TypeCompteur.findById(typeCompteur);
        Compteur compteur = new Compteur(numeroCompteur, numeroSerieCompteur, puissance, caracterisques, typeCompteur1, Security.getAccount());
        if (typeCompteur1 == null) {
            compteur.typeCompteur = null;
        } else {
            compteur.typeCompteur = typeCompteur1;
        }
        if (livraison.id != null) {
            compteur.livraison = livraison;
        }
        try {
            compteur.save();
            livraison.nbCompteurRestant -= 1;
            livraison.save();
            flash("success", "Le compteur a été créée avec succès");
            newCompteur();
        } catch (PersistenceException ex) {
            Logger.error("Le compteur " + compteur.toString() + " existe déjà");
            Validation.addError("errors", "Une erreur s'est produite lors de l'enregistrement veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
            newCompteur();
        }
        newCompteur();
    }
    
    public static void AffectationCompteur(){
        
    }

    public static void deleteCompteur(Long compteur) {
        Compteur a = Compteur.findById(compteur);
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

    public static void editCompteur(Long a) {
        Compteur compteur = Compteur.findById(a);
        List<models.Compteur> compteurs = Compteur.findAll();
        //List<SortieEnStock> sortieEnStocks = SortieEnStock.find("beneficiaire=?1 and nbCompteurRestant > ?2", Security.getAccount(), 0).fetch();
        render(compteur, compteurs);
    }

    public static void saveEditCompteur(@Valid(message = "Les informations entrées sont incompletes") Compteur compteur, long typeCompteur, long livraison) {

        if (Validation.hasErrors()) {
            params.flash();
            validation.keep();
            index();
        }
        try {
            TypeCompteur typeCompt = TypeCompteur.findById(typeCompteur);
            compteur.typeCompteur = typeCompt;
            compteur.save();
            flash("success", "Compteur modifiée avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer ce Compteur car elle est déjà en utilisation", new String[]{"errors"});
            Logger.error(ex.getMessage());
            params.flash();
            validation.keep();
        } finally {
            index();
        }
    }
    
    public static void chargerCompteur(){
        renderArgs.put("action", "charger_compteur");
        List<TypeCompteur> typeCompteurs = TypeCompteur.findAll();
        List<Livraison> livraisons = Livraison.find("nbCompteurRestant<>0").fetch();
        render(typeCompteurs, livraisons);
    }
    
    public static void readingFile(File compteurFile, 
        @Required(message = "Sélectionnez la livraison") long refLivraison) throws FileNotFoundException, IOException{
        if (Validation.hasErrors()||getFileType(compteurFile.getName()).compareToIgnoreCase("csv")!=0) {
            params.flash();
            validation.keep();
            index();
        }
        try {
            Livraison livraison=Livraison.findById(refLivraison);
            Account account=Security.getAccount();
            //STRUCTURE DU FICHIER COMPTEUR DATA
            //numeroCompteur,numeroSerieCompteur,puissance,caracteristiques,typeCompteur_id,account_id,livraison_id
                String csvFilename = compteurFile.getAbsolutePath();
                CSVReader csvReader = new CSVReader(new java.io.FileReader(csvFilename), ',');
                csvReader.readNext(); //POUR IGNORER LA PREMIERE LIGNE QUI CORRESPOND A L'ENTETE
                String[] row = null;
                List<Compteur> compteurs=new ArrayList<Compteur>();
                while((row = csvReader.readNext()) != null) {
                    TypeCompteur tc=TypeCompteur.findById(Long.parseLong(row[4]));
                    Compteur compteur = new Compteur(row[0], row[1], row[2], row[3],tc,account , livraison);
                    compteur.save();
                    compteurs.add(compteur);
                    //System.out.println(row[0]+" # " + row[1]+ " # " + row[2]+ " # " + row[3]+ " # " + row[4]);
                }
                livraison.nbCompteurRestant-=compteurs.size();
                livraison.save();
                csvReader.close();
                flash("success", "Chargement de "+ compteurs.size()+" Compteur(s) effectué avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Chargement impossible. Veuillez consulter les log...", new String[]{"errors"});
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
            if (ext.compareToIgnoreCase(".csv") == 0) {
                type = "csv";
            }
        }
        return type;
    }
}
