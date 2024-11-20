

public class RegimePoints extends Regime {

    //CONSTRUCTEUR
    public RegimePoints(String nom, String [][] InstParamRegimesTab) throws Exception {
        super(nom, InstParamRegimesTab);
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
              
            int indCol = Tools.TrouverIndiceColonne(CumulDroitsTab, this.nom);
            
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
        //si annee depart sup à la dernière ligne du tab -> on utilise la valeur de la dernière ligne
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
        if (anneeDep >= anneeFin) {
            anneeValeur = String.valueOf(anneeFin);
        }
        else {
            anneeValeur = String.valueOf(anneeDep);
        }
        String donneeATrouver = "VP_";
        donneeATrouver = donneeATrouver.concat(this.nom);
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
    public float calculAnnuelBrut (Individu individu, DateDepart dateDep, String[][] InstPassPointsRegimesTab, String[][] CumulDroitsTab, String[][] AnnualDataTab, String[][] InstCoeffRevaloTab) throws Exception {
        float ValPt = TrouverValeurPtRegime(InstPassPointsRegimesTab, dateDep);
        float montant = ValPt * calculCumulPointsTrim(individu, CumulDroitsTab, dateDep) * this.calculTaux(dateDep) * (1 + this.calculSurcote(dateDep)) * (1 + this.calculMajoEnfants(individu));
        float result = Math.round(montant * 100) / (float)100;
        return result;
    }

    @Override
    public float calculSam (DateDepart dateDep, String[][] InstPassPointsRegimesTab, String[][] AnnualDataTab, String[][] InstCoeffRevaloTab) throws Exception {
        return 0;
    }


    

}
