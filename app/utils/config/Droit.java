package utils.config;

/**
 * Created by birkhoff
 */

public enum  Droit {
    TDR("TDR"),
    CREER_COMPTEUR("CREER_COMPTEUR"),
    AFFECTER_COMPTEUR("AFFECTER_COMPTEUR"),
    ENTREE_STOCK("ENTREE_STOCK"),
    SORTIE_STOCK("SORTIE_STOCK"),
    DEMANDER_COMPTEUR("DEMANDER_COMPTEUR"),
    LISTE_DES_DEMANDES("LISTE_DES_DEMANDES"),
    POSER_COMPTEUR("POSER_COMPTEUR"),
    CONTROLER_POSE("CONTROLER_POSE"),
    AFFECTER_TACHE("AFFECTER_TACHE"),
    TACHE_CONFIRMEE("TACHE_CONFIRMEE"),
    LISTE_DES_TACHES("LISTE_DES_TACHES"),
    LISTE_COMPTEURS_AFFECTER("LISTE_COMPTEURS_AFFECTER"),
    CONTENTIEUX("CONTENTIEUX"),
    CREER_LOT_COMPTEUR("CREER_LOT_COMPTEUR"),
    STATISTIQUES("STATISTIQUES");

    private String type;

    Droit (String type){
        this.type = type;
    }

    public String getType(){
        return type;
    }

    @Override
    public String toString() {
        return  type;
    }
}
