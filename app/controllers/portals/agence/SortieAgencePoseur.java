package controllers.portals.agence;

import controllers.Secure;
import controllers.Security;
import controllers.SessionHandler;
import java.text.SimpleDateFormat;
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
import play.data.validation.Validation;
import play.db.jpa.JPA;

/**
 * Created by TICBUILDER
 */
@With({SessionHandler.class, Secure.class})
public class SortieAgencePoseur extends Controller {

    public List<Long> listCptSelect = new ArrayList<Long>();
    
    @Before
    static void setActionMenu() {
        renderArgs.put("action", "sortie_stock");
    }

    public static void index() {
        List<Compteur> compteurs= new ArrayList<Compteur>();
        List<EntreeEnStock> entreeEnStocks = EntreeEnStock.find("user.id=? and nbRestant>0", Security.getUser().id).fetch();
        for(EntreeEnStock e: entreeEnStocks){
            for(Compteur c: e.getCompteurs()){
                compteurs.add(c);
            }
        }
        List<Compteur> compteursAffecter=new ArrayList<Compteur>();
        List<LotCompteur> listAff=LotCompteur.find("agentPoseur.account.id=?1", Security.getAccount().id).fetch();
        for(LotCompteur lot:listAff){
           for(Compteur c: lot.getCompteurs()){
                compteursAffecter.add(c);
            }
        }
        if(!listAff.isEmpty()){
            compteurs.removeAll(compteursAffecter);
        }
        Integer cpt = JPA.em().createQuery("select count(*) from LotCompteur l").getFirstResult()+1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String libelleLotCompteur = "LOT_".concat(sdf.format(new Date())+"_").concat(cpt.toString());
        
//LISTE DES AGENTS POSEURS AGENCE + SOUS-TRAITANTS
        List<models.User> agents = new ArrayList<models.User>();
        //LISTE DES AGENTS POSEURS
        List<models.User> agents1 = models.User.find("profil.libProfil=?1 and account.id=?2", "POSEUR", Security.getAccount().id).fetch();
        for(models.User u:agents1){
            models.User user=models.User.findById(u.id);
            agents.add(user);
        } 
        //LISTE DES SOUS TRAITANTS
        List<models.Soustraitant> agents2 = models.Soustraitant.find("agence.id=?1", Security.getAccount().id).fetch();
        for(models.Soustraitant st:agents2){
            if(st.agence.id==Security.getAccount().id){
                models.User u=models.User.find("account.id=?1", st.id).first();
                if(!u.profil.libProfil.isEmpty() && u.profil.libProfil.equalsIgnoreCase("POSEUR")){
                    agents.add(u);
                }               
            }
        }
        render(compteurs, libelleLotCompteur, agents);
    }

    public static void newSortie() {
        render();
    }

    public static void saveNewSortie(@Required(message = "Compteur Obligatoire") List<Long> idCompteur, 
        @Valid(message="LibelleLot ne doit pas être null") String libelleLotCompteur,
        @Required(message = "AgentPoseur obligatoire") long agentPoseur) {
        if (validation.hasErrors()) {
            params.flash();
            validation.keep();
            index();
        }
        try {
            //1- ENREGISTREMENT DE 1ème DE CREATION DE LOT DE COMPTEUR
                models.User user = models.User.findById(agentPoseur);
                List<Compteur> compteurs=new ArrayList<Compteur>();
                for(int x=0; x<idCompteur.size();x++){
                    Compteur compt=Compteur.findById(idCompteur.get(x));
                    compteurs.add(compt);
                }
                LotCompteur lc = new LotCompteur(libelleLotCompteur, compteurs, user, new Date());
                lc.save();
                //2- MISE A JOUR DU STOCK COTE AGENCE
                updateStock(Security.getUser(), compteurs.size());
                flash("success", "Opération éffectuée avec succès");
        } catch (PersistenceException ex) {
            Logger.error("Opération d'affectation de compteur échouée!");
            Validation.addError("errors", "Une erreur s'est produite lors de l'enregistrement veuillez consulter les logs", new String[]{"errors"});
            params.flash();
            validation.keep();
        }finally{
                index();
        }              
    }
    
    public static boolean updateStock(models.User user, int QteAPrelever){
        List<EntreeEnStock> list = EntreeEnStock.find("user.account.id=?1", user.account.id).fetch();
        for(EntreeEnStock e: list){
            while (QteAPrelever!=0) {                
                if(e.nbRestant==QteAPrelever||e.nbRestant>QteAPrelever){
                e.nbRestant -=QteAPrelever;
                QteAPrelever = 0;
            }else{
                    e.nbRestant = 0;
                    QteAPrelever -=e.nbRestant;
            }
            e.save();
        }
            }
        return true;
    }

}
