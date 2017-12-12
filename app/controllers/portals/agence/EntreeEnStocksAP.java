package controllers.portals.agence;

import controllers.Secure;
import controllers.Security;
import controllers.SessionHandler;
import static controllers.portals.agence.EnvoyerDemande.getFileType;
import groovyjarjarcommonscli.ParseException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import models.*;
import play.Logger;
import play.data.validation.Required;
import play.data.validation.Valid;
import java.util.List;
import javax.persistence.PersistenceException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import play.Play;
import play.data.validation.Validation;

/**
 * Created by TICBUILDER
 */
@With({SessionHandler.class, Secure.class})
public class EntreeEnStocksAP extends Controller {

    @Before
    static void setActionMenu() {
        renderArgs.put("action", "entree_stock");
    }

    //Stock sélectionné
    public static List<Long> stockSelect = new ArrayList<Long>();
    
    
    public static void index() {
        List<SortieEnStock> stockList =SortieEnStock.find("beneficiaire.id=?1", Security.getAccount().id).fetch();
        
        render(stockList);
    }

    public static void newEntreeEnStock(List<Long> idSortie) {
        stockSelect = idSortie;
        int qteTotaleSelect=0;
        for(int i=0; i<stockSelect.size();i++){
            List<EntreeEnStock> entreeEnStock=EntreeEnStock.find("sortieEnStock.id=?1 and nbRestant is not null", stockSelect.get(i)).fetch();
            for(EntreeEnStock e:entreeEnStock){
                qteTotaleSelect +=e.nbRestant;
            }
        }        
        List<models.SortieEnStock> listSortie = SortieEnStock.find("beneficiaire.id=?1", Security.getAccount().id).fetch();
        render(listSortie, qteTotaleSelect);
    }

    public static int stockUpdate(long idCde, Date date, int nbreSaisie, int nbreTotale) {
        int resteAPrelever=0;
        SortieEnStock stock=SortieEnStock.findById(idCde);       
        if(stock.nbCompteur>=nbreSaisie){
            EntreeEnStock nouvelEntree = new EntreeEnStock(Security.getUser(), nbreSaisie, date, stock);
            nouvelEntree.save();
            resteAPrelever = nbreTotale - nbreSaisie;
        }
        return resteAPrelever;
    }

    public static void detailsStockSend(long id){
        SortieEnStock sortieEnStock=SortieEnStock.findById(id);
        List<Compteur> compteurs = sortieEnStock.getCompteurs();
        EntreeEnStock entreeEnStock = EntreeEnStock.find("sortieEnStock.id=?1 and user.id=?2", sortieEnStock.id, Security.getUser().id).first();
        if(entreeEnStock!=null){
            compteurs.removeAll(entreeEnStock.getCompteurs());
        }
        render(sortieEnStock, compteurs);
    }
        
    public static void saveEntreeEnStock(
            @Required(message = "Aucun fichier trouvé") File filePath, 
            @Required(message = "refSortie obligatoire") long refSortie,
            @Required(message = "Sélectionner les compteurs") List<Long> idCompteur,
            @Required(message = "nbreConfirmer obligatoire") String nbreConfirmer) throws IOException, ParseException {
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
        }      
        String chemin = "/data/entrees/";
        if (!FilenameUtils.isExtension(filePath.getName(), "pdf")) {
            validation.addError("files", "Le format du fichier " + filePath.getName() + " est incorrect... Format jpg ou png attendu", new String[]{"files"});
            params.flash();
            validation.keep();
        }
        List<Compteur> compteurs = new ArrayList<Compteur>();
        for(int x=0; x<idCompteur.size(); x++){
            Compteur c=Compteur.findById(idCompteur.get(x));
        //(1)->LISTE DES COMPTEURS SELECTIONNEES
            compteurs.add(c);
        }
        SortieEnStock sortieEnStock=SortieEnStock.findById(refSortie);
        EntreeEnStock entreeEnStock=new EntreeEnStock(Security.getUser(), Integer.parseInt(nbreConfirmer), new Date(), sortieEnStock);
        entreeEnStock.setCompteurs(compteurs);
        entreeEnStock.save();
        
        //  ==> ENREGISTREMENT D'UN CONTENTIEUX
        int Qsend = sortieEnStock.getCompteurs().size();
        if(Integer.parseInt(nbreConfirmer)<sortieEnStock.getCompteurs().size()){
        //(2)->LISTE DES COMPTEURS ENVOYES
            List<Compteur> listSend= sortieEnStock.getCompteurs();        
        //(2)-(1)-> LISTE DES COMPTEURS A DECLARER DANS CONTENTIEUX
            boolean rep = listSend.removeAll(compteurs);
            if(rep==true){
                //Ecart mis en négatif pour être plus expressif
                int ecart = -(Qsend-Integer.parseInt(nbreConfirmer));
                models.Contentieux contentieux= new models.Contentieux(ecart, new Date(), entreeEnStock);
                List<Compteur> list = new ArrayList<Compteur>();
                for(Compteur c: listSend){
                    list.add(c);
                }
                contentieux.setCompteurs(list);
                contentieux.save();
            }            
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
            uploadFile(filePath, "ENT-" + entreeEnStock.id + extension);
            entreeEnStock.filePath = chemin.concat("ENT-" + entreeEnStock.id + extension);
            entreeEnStock.save();          
            flash("success", "Demande envoyé avec succès");
        } catch (PersistenceException ex) {
            Logger.error("Interruption de l'enregistrement de la demande ==> " + entreeEnStock.date);
            validation.addError("errors", "Une erreur s'est produite lors de l'enregistrement, veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
        }
        index();
    }
    
    public static void upDateDemande(long account, int quantite){
        Demande demande=Demande.find("user.account.id=?1", account).first();
        
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
    
    public static void deleteEntreeEnStock(Long entree) {
        EntreeEnStock a = EntreeEnStock.findById(entree);
        try {
            a.delete();
            flash("success", "L'entrée a été supprimée avec succès");
        } catch (PersistenceException ex) {
            Validation.addError("errors", "Impossible de supprimer cet élement car il est déjà en utilisation", new String[]{"errors"});
            params.flash();
            validation.keep();
            Logger.fatal(ex, null, (Object) null);
        } finally {
            index();
        }
    }

    public static void editEntreeEnStock(Long a) {
        Compteur compteur = Compteur.findById(a);
        List<models.Compteur> compteurs = Compteur.findAll();
        List<SortieEnStock> sortieEnStocks = SortieEnStock.find("beneficiaire=?1 and nbCompteurRestant > ?2", Security.getAccount(), 0).fetch();
        render(compteur, compteurs, sortieEnStocks);
    }

    public static void saveEditEntreeEnStock(@Valid(message = "Les informations entrées sont incompletes") Compteur compteur, long typeCompteur, long livraison) {

        if (Validation.hasErrors()) {
            params.flash();
            validation.keep();
            index();
        }
        try {
            TypeCompteur typeCompt = TypeCompteur.findById(typeCompteur);
            Livraison command = Livraison.findById(livraison);
            compteur.typeCompteur = typeCompt;
            //compteur.livraison = command;
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
}
