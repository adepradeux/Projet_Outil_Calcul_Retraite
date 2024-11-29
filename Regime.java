

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
    public Regime(String nom, String nomOutput, Data data) throws Exception {
        this.nom = nom;
        this.nomOutput = nomOutput;
        this.txDecote1 = Tools.TrouverDonneeRegime(this.nom, data.GetInstParamRegimesTab(), "tx_decote_1");
        this.txDecote2 = Tools.TrouverDonneeRegime(this.nom, data.GetInstParamRegimesTab(), "tx_decote_2");
        this.txSurcote1 = Tools.TrouverDonneeRegime(this.nom, data.GetInstParamRegimesTab(), "tx_surcote_1");
        this.txSurcote2 = Tools.TrouverDonneeRegime(this.nom, data.GetInstParamRegimesTab(), "tx_surcote_2");
        this.txPlvtSociaux = Tools.TrouverDonneeRegime(this.nom, data.GetInstParamRegimesTab(), "tx_plvt_sociaux");
        this.majoTroisEnfants = Tools.TrouverDonneeRegime(this.nom, data.GetInstParamRegimesTab(), "majo_trois_enfants");
        this.depTrimCivil = Tools.TrouverDonneeRegime(this.nom, data.GetInstParamRegimesTab(), "depart_trim_civil");
        
    }
    
    //METHODES
    

    //Méthode pour retourner le cumul points ou trim pour affichage resultat - A définir dans les classes régimes héritières
    abstract float calculCumulPointsTrim(Individu individu, Data data, DateDepart dateDep) throws Exception;  

    //Méthode pour calcul du taux de calcul - A définir dans les classes régimes héritières
    abstract float calculTaux (DateDepart dateDep) throws Exception;

    //Méthode pour calcul du taux de surcote - A définir dans les classes régimes héritières
    abstract float calculSurcote (DateDepart dateDep) throws Exception;

    //Méthode pour calcul SAM - A définir dans la classe Régime Général
    abstract  float calculSam (DateDepart dateDep, Data data) throws Exception;

    //Méthode pour retourner la valeur du point - overwrite dans regimePoints
    abstract float TrouverValeurPtRegime (String[][] InstPassPointsRegimesTab, DateDepart dateDep) throws Exception; 

    //Méthode pour calcul du taux de majoration pour enfants - A définir dans les classes régimes héritières
    abstract float calculMajoEnfants (Individu individu);

    //Méthode déterminer le versement se fait en capital unique (si nb points ou montant inf à un certain seuil)
    abstract Boolean estVersementUnique (Individu individu, Data data, DateDepart dateDep);

    //Méthode pour calcul du montant annuel brut - A définir dans les classes régimes héritières
    abstract  int calculAnnuelBrut (Individu individu, DateDepart dateDep, Data data) throws Exception;

    //Méthode pour calcul du montant annuel net
    public int calculAnnuelNet (Individu individu, DateDepart dateDep, Data data) throws Exception {
        float montant = this.calculAnnuelBrut(individu, dateDep, data) * (1 - this.txPlvtSociaux);
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
