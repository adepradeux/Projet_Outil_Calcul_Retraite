

abstract  class Regime {
    private final String nom;       //nom pour tableau de paramètres des régimes
    private final String nomOutput; //nom pour l'affichage dans le tableau de résultats
    private final float txDecote1;
    private final float txDecote2;
    private final float txSurcote1;
    private final float txSurcote2;
    private final float txPlvtSociaux;
    private final float majoTroisEnfants;
    private final float depTrimCivil;

    //CONSTRUCTEUR
    public Regime(String nom, String nomOutput, String [][] InstParamRegimesTab) throws Exception {
        this.nom = nom;
        this.nomOutput = nomOutput;
        this.txDecote1 = Tools.TrouverDonneeRegime(this.nom, InstParamRegimesTab, "tx_decote_1");
        this.txDecote2 = Tools.TrouverDonneeRegime(this.nom, InstParamRegimesTab, "tx_decote_2");
        this.txSurcote1 = Tools.TrouverDonneeRegime(this.nom, InstParamRegimesTab, "tx_surcote_1");
        this.txSurcote2 = Tools.TrouverDonneeRegime(this.nom, InstParamRegimesTab, "tx_surcote_2");
        this.txPlvtSociaux = Tools.TrouverDonneeRegime(this.nom, InstParamRegimesTab, "tx_plvt_sociaux");
        this.majoTroisEnfants = Tools.TrouverDonneeRegime(this.nom, InstParamRegimesTab, "majo_trois_enfants");
        this.depTrimCivil = Tools.TrouverDonneeRegime(this.nom, InstParamRegimesTab, "depart_trim_civil");
        
    }
    
    //METHODES
    //TEST a supprimer :  méthode mise dans Tools
    //méthode pour renvoyer une des données du régime dans le tableau regroupant les paramètre des régimes
    /*public static float TrouverDonneeRegime (String nomregime, String[][] InstParamRegimesTab, String donneeATrouver) throws Exception {
        float result = 0;
        try {
            int indLgn = Tools.TrouverIndiceLigne(InstParamRegimesTab, nomregime);
            int indCol = Tools.TrouverIndiceColonne(InstParamRegimesTab, donneeATrouver);
            result = Float.parseFloat(InstParamRegimesTab[indLgn][indCol]);
        } catch (NumberFormatException e) {
            System.out.println("donnee paramètre régime incorrecte: " + e.getMessage());
        }
        return result;
    }*/

    //Méthode pour retourner le cumul points ou trim pour affichage resultat - A définir dans les classes régimes héritières
    abstract float calculCumulPointsTrim(Individu individu, String[][] CumulDroitsTab, DateDepart dateDep) throws Exception;  

    //Méthode pour calcul du taux de calcul - A définir dans les classes régimes héritières
    abstract float calculTaux (DateDepart dateDep) throws Exception;

    //Méthode pour calcul du taux de surcote - A définir dans les classes régimes héritières
    abstract float calculSurcote (DateDepart dateDep) throws Exception;

    //Méthode pour calcul SAM - A définir dans la classe Régime Général
    abstract  float calculSam (DateDepart dateDep, String[][] InstPassPointsRegimesTab, String[][] AnnualDataTab, String[][] InstCoeffRevaloTab) throws Exception;

    //Méthode pour retourner la valeur du point - overwrite dans regimePoints
    abstract float TrouverValeurPtRegime (String[][] InstPassPointsRegimesTab, DateDepart dateDep) throws Exception; 

    //Méthode pour calcul du taux de majoration pour enfants - A définir dans les classes régimes héritières
    abstract float calculMajoEnfants (Individu individu);

    //Méthode déterminer le versement se fait en capital unique (si nb points ou montant inf à un certain seuil)
    abstract Boolean estVersementUnique (Individu individu, String[][] CumulDroitsTab, DateDepart dateDep);

    //Méthode pour calcul du montant annuel brut - A définir dans les classes régimes héritières
    abstract  int calculAnnuelBrut (Individu individu, DateDepart dateDep, String[][] InstPassPointsRegimesTab, String[][] CumulDroitsTab, String[][] AnnualDataTab, String[][] InstCoeffRevaloTab) throws Exception;

    //Méthode pour calcul du montant annuel net
    public int calculAnnuelNet (Individu individu, DateDepart dateDep, String[][] InstPassPointsRegimesTab, String[][] CumulDroitsTab, String[][] AnnualDataTab, String[][] InstCoeffRevaloTab) throws Exception {
        float montant = this.calculAnnuelBrut(individu, dateDep, InstPassPointsRegimesTab, CumulDroitsTab, AnnualDataTab, InstCoeffRevaloTab) * (1 - this.txPlvtSociaux);
        int result = Math.round(montant);
        return result;
    }

    //GETTER

    public String GetNom() {
        return nom;
    }

    public String GetNomOutput() {
        return nomOutput;
    }

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

    public float GetDepTrimCivil() {
        return depTrimCivil;
    }
}
