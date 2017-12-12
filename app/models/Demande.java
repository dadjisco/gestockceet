package models;

import java.util.Date;
import java.util.List;
import javax.persistence.*;
import play.db.jpa.*;

/**
 *
 * @author TICBUILDER
 */
@Entity
@Table(name = "demande")

public class Demande extends Model {

    public String filePath;
    public Date dateEnvoie;
    public int nivoTraitement;   
    @ManyToOne
    public User user;
    @ManyToOne
    public Account accountDestinataire;
    public int satisfait;
	       
    public Demande() {
    }

    public Demande(Date dateEnvoie, User user, int nivoTraitement, Account destinataire) {
        this.dateEnvoie = dateEnvoie;
        this.user = user;
        this.nivoTraitement = nivoTraitement;
        this.accountDestinataire = destinataire;
        this.satisfait = 0;
    }

    public Demande(String filePath, Date dateEnvoie, int nivoTraitement, User user, Account destinataire) {
        this.filePath = filePath;
        this.dateEnvoie = dateEnvoie;
        this.nivoTraitement = nivoTraitement;
        this.user = user;
        this.accountDestinataire = destinataire;
        this.satisfait = 0;
    }

    public String getFile() {
        return filePath;
    }

    public int getQteCptDemander(long d){
        int cumul=0;
        List<Demande_TypeCompteur> list=Demande_TypeCompteur.find("demande.id=?1", d).fetch();
        for(Demande_TypeCompteur dtc: list){
            cumul+=dtc.QteCompteur;
        }
        return cumul;
    }
    
    public int getQteCptRestant(long d){
        Demande demande=Demande.findById(d);
        int reste=getQteCptDemander(d) - demande.satisfait;
        return reste;
    }
    
    public List<Demande_TypeCompteur> getDetails(){
        List<Demande_TypeCompteur> list=Demande_TypeCompteur.find("demande.id=?1", this.id).fetch();
        return list;
    }
    
    public String getFileType(String FilePath){
        String type=null;
        if (FilePath.lastIndexOf(".") > 0) {
            String ext = FilePath.substring(FilePath.lastIndexOf("."));
                if (ext.compareToIgnoreCase(".png") == 0 || ext.compareToIgnoreCase(".jpg") == 0) {
                    type = "image";
                }
                if (ext.compareToIgnoreCase(".mp4") == 0 || ext.compareToIgnoreCase(".wmv") == 0) {
                    type = "video";
                }
                if (ext.compareToIgnoreCase(".mp3") == 0) {
                    type = "audio";
                }
                if (ext.compareToIgnoreCase(".pdf") == 0) {
                    type = "pdf";
                }
        }
        return type;
    }


}
