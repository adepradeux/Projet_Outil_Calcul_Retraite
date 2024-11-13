public class Regime {
    String nom;
    private final float txDecote1;
    private final float txDecote2;
    private final float txSurcote1;
    private final float txSurcote2;
    private final float txPlvtSociaux;
    private final float majoTroisEnfants;

    //CONSTRUCTEUR
    public Regime(String nom, String [][] InstParamRegimesTab) throws Exception {
        this.nom = nom;
        this.txDecote1 = TrouverDonneeRegime(this.nom, InstParamRegimesTab, "tx_decote_1");
        this.txDecote2 = TrouverDonneeRegime(this.nom, InstParamRegimesTab, "tx_decote_2");
        this.txSurcote1 = TrouverDonneeRegime(this.nom, InstParamRegimesTab, "tx_surcote_1");
        this.txSurcote2 = TrouverDonneeRegime(this.nom, InstParamRegimesTab, "tx_surcote_2");
        this.txPlvtSociaux = TrouverDonneeRegime(this.nom, InstParamRegimesTab, "tx_plvt_sociaux");
        this.majoTroisEnfants = TrouverDonneeRegime(this.nom, InstParamRegimesTab, "majo_trois_enfants");
        
    }
    
    //METHODES
    
    //méthode pour renvoyer une des données du régime dans le tableau regroupant les paramètre des régimes
    public static float TrouverDonneeRegime (String nomregime, String[][] InstParamRegimesTab, String donneeATrouver) throws Exception {
        float result = 0;
        try {
            int indLgn = Tools.TrouverIndiceLigne(InstParamRegimesTab, nomregime);
            int indCol = Tools.TrouverIndiceColonne(InstParamRegimesTab, donneeATrouver);
            result = Float.parseFloat(InstParamRegimesTab[indLgn][indCol]);
        } catch (NumberFormatException e) {
            System.out.println("donnee paramètre régime incorrecte: " + e.getMessage());
        }
        return result;
    }

    //Méthode pour calcul du montant annuel brut
    public float calculAnnuelBrut (Individu individu, DateDepart dateDep, String[][] InstPassPointsRegimesTab, String[][] CumulDroitsTab) throws Exception {
        float result = 0;
        return result;
    }

    //GETTER
    public float GetTx_decote_1() {
        return txDecote1;
    }

    public float GetTx_decote_2() {
        return txDecote2;
    }

    public float GetTx_surcote_1() {
        return txSurcote1;
    }

    public float GetTx_surcote_2() {
        return txSurcote2;
    }

    public float GetTx_plvt_sociaux() {
        return txPlvtSociaux;
    }

    public float GetMajoTroisEnfants() {
        return majoTroisEnfants;
    }
}
