

public class RegimePoints extends Regime {

    private final float seuilPointCapUnique;
    
    //CONSTRUCTEUR
    public RegimePoints(String nom, String nomOutput, Data data) throws Exception {
        super(nom, nomOutput, data);
        seuilPointCapUnique = Tools.TrouverDonneeRegime(this.GetNom(), data.GetInstParamRegimesTab(), "seuil_point_cap_unique");
    }

    
    //METHODES
    
    //Méthode pour obtenir cumul de points à la date donnée
    @Override
    public float calculCumulPointsTrim(Individu individu, String[][] CumulDroitsTab, DateDepart dateDep) throws Exception {
        float result;
        try {
           
            int anneeDep = dateDep.GetDateDep().getYear();
            int moisDep = dateDep.GetDateDep().getMonthValue();
            //on cherche l'indice de la colonne correspondant au regime  
              
            int indCol = Tools.TrouverIndiceColonne(CumulDroitsTab, this.GetNom());
            
            float cumulPointsInit = Float.parseFloat(CumulDroitsTab[2][indCol]);
            int anneeCumul = Tools.dateFromString(CumulDroitsTab[4][indCol]).getYear();
            float cumulPointsProjetes = 0;
            int i = 5; //en 5 -> début projection des points
            while(CumulDroitsTab[i][indCol] != null && Integer.parseInt(CumulDroitsTab[i][0]) < anneeDep) {
                if (Integer.parseInt(CumulDroitsTab[i][0]) > anneeCumul){
                    cumulPointsProjetes = cumulPointsProjetes + Float.parseFloat(CumulDroitsTab[i][indCol]);
                }
                i++;
            }
            int indLigneAnneeDep = i;
            float pointsAnneeDep =  (moisDep - 1) * Float.parseFloat(CumulDroitsTab[indLigneAnneeDep][indCol]) /12;
            pointsAnneeDep = Math.round(pointsAnneeDep * 100) / (float)100;
            result = cumulPointsInit + cumulPointsProjetes + pointsAnneeDep;
        } catch (Exception e) {
            throw new Exception("donnee cumul droits régime incorrecte: " + e.getMessage()) ;
        }
        return result;
    }

    
    //Méthode pour obtenir la valeur de point du régime selon la date de départ donnée
    @Override
    public float TrouverValeurPtRegime (String[][] InstPassPointsRegimesTab, DateDepart dateDep) throws Exception {
        String anneeValeur = TrouverAnneeValeurRegime(InstPassPointsRegimesTab, dateDep);
        String donneeATrouver = "VP_";
        donneeATrouver = donneeATrouver.concat(this.GetNom());
        float result = 0;
        try {
            int indLgn = Tools.TrouverIndiceLigne(InstPassPointsRegimesTab, anneeValeur);
            int indCol = Tools.TrouverIndiceColonne(InstPassPointsRegimesTab, donneeATrouver);
            result = Float.parseFloat(InstPassPointsRegimesTab[indLgn][indCol]);
        } catch (Exception e) {
            System.out.println("donnee ponits régime incorrecte: " + e.getMessage());
        }
        return result;
    }

    public float TrouverSalaireRefRegime (String[][] InstPassPointsRegimesTab, DateDepart dateDep) throws Exception {
        String anneeValeur = TrouverAnneeValeurRegime(InstPassPointsRegimesTab, dateDep);
        String donneeATrouver = "SR_";
        donneeATrouver = donneeATrouver.concat(this.GetNom());
        float result = 0;
        try {
            int indLgn = Tools.TrouverIndiceLigne(InstPassPointsRegimesTab, anneeValeur);
            int indCol = Tools.TrouverIndiceColonne(InstPassPointsRegimesTab, donneeATrouver);
            result = Float.parseFloat(InstPassPointsRegimesTab[indLgn][indCol]);
        } catch (Exception e) {
            System.out.println("donnee ponits régime incorrecte: " + e.getMessage());
        }
        return result;
    }

    //methode pour retourner l'année (String) à prendre en compte dans le tableau InstPassPointsRegimesTab
    public String TrouverAnneeValeurRegime (String[][] InstPassPointsRegimesTab, DateDepart dateDep) throws Exception {
        int longTab = InstPassPointsRegimesTab.length;
        //trouver l'indice de la dernière valeur non nulle du tableau
        int indLgnFin = 0;    
        for (int i =0; i < longTab; i++) {
            if (InstPassPointsRegimesTab[i][0] == null) break;
            indLgnFin = i;
        }
        int anneeFin = Integer.parseInt(InstPassPointsRegimesTab[indLgnFin][0]);
        int anneeDep = dateDep.GetDateDep().getYear();
        String anneeValeur; //annee de la valeur pt à chercher dans le tableau
        //si annee depart sup à la dernière ligne du tab -> on utilise la valeur de la dernière ligne
        if (anneeDep >= anneeFin) {
            anneeValeur = String.valueOf(anneeFin);
        }
        else {
            anneeValeur = String.valueOf(anneeDep);
        }
        return anneeValeur;
    }

    //Méthode pour obtenir taux de calcul avec decote si 1 seul taux de decote
    @Override
    public float calculTaux (DateDepart dateDep) {
        float decote = dateDep.GetTrimManquant() * this.GetTx_decote_1();
        float result = 1 - Math.round(decote * 10000) / (float)10000;
        return result;
    }

    //Méthode pour obtenir taux surcote si 1 seul taux de surcote
    @Override
    public float calculSurcote (DateDepart dateDep) {
        float surcote = dateDep.GetTrimSurcote() * this.GetTx_surcote_1();
        float result = Math.round(surcote * 10000) / (float)10000;
        return result;
    }

    //Méthode pour obtenir taux majoration si 3 enfants ou plus
    @Override
    public float calculMajoEnfants (Individu individu) {
        int nbEnfants = individu.getNbEnfants();
        float result = 0;
        if (nbEnfants >= 3) {
            result = this.GetMajoTroisEnfants();
        }
        return result;
    }

    //Méthode pour calcul du montant annuel brut
    @Override
    public int calculAnnuelBrut (Individu individu, DateDepart dateDep, Data data) throws Exception {
        float ValPt = TrouverValeurPtRegime(data.GetInstPassPointsRegimesTab(), dateDep);
        float montant = ValPt * calculCumulPointsTrim(individu, data.GetCumulDroitsTab(), dateDep) * this.calculTaux(dateDep) * (1 + this.calculSurcote(dateDep)) * (1 + this.calculMajoEnfants(individu));
        int result = Math.round(montant);
        return result;
    }

    @Override
    public float calculSam (DateDepart dateDep, Data data) throws Exception {
        return 0;
    }

     //Méthode déterminer le versement se fait en capital unique (si nb points ou montant inf à un certain seuil)
    @Override
    public Boolean estVersementUnique (Individu individu, Data data, DateDepart dateDep) {
        Boolean result = false;
        float cumulPoints = 0;
        try {
            cumulPoints = calculCumulPointsTrim(individu, data.GetCumulDroitsTab(), dateDep);
        } catch (Exception ex) {
        }
        if (cumulPoints < this.seuilPointCapUnique) {
            result = true;
        }
        return result;
    }

    //GETTER

    public float GetSeuilPointCapUnique() {
        return seuilPointCapUnique;
    }


    

}
