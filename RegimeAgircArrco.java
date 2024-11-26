
public class RegimeAgircArrco extends RegimePoints {
   


    //CONSTRUCTEUR
    public RegimeAgircArrco(String nom, String nomOutput, String [][] InstParamRegimesTab) throws Exception {
        super(nom, nomOutput, InstParamRegimesTab);
    }


    //METHODES

    //calcul taux de calcul avec decote : 1% pour les trim manquants de 1 à 12, puis 1,25% pour trim manquant à partir de 13 - decote 22% max
    @Override
    public float calculTaux (DateDepart dateDep) {
        float decoteP1 = Math.min(dateDep.GetTrimManquant(), (float)12)  * this.GetTx_decote_1();
        float decoteP2 = Math.max((float)0, dateDep.GetTrimManquant() - (float)12) * this.GetTx_decote_2();
        float decote = Math.min((float)0.22, decoteP1 + decoteP2);
        float result = 1 - Math.round(decote * 10000) / (float)10000;
        return result;
    }

    //TODO Calcul versement unique Versement unique = Montant brut annuel de la retraite x Coefficient
    //Méthode pour calcul du montant annuel brut
    @Override
    public int calculAnnuelBrut (Individu individu, DateDepart dateDep, String[][] InstPassPointsRegimesTab, String[][] CumulDroitsTab, String[][] AnnualDataTab, String[][] InstCoeffRevaloTab) throws Exception {
        int result = 0;
        if (estVersementUnique(individu, CumulDroitsTab, dateDep)) {
            // TODO trouver le coeff en fonction de l'âge à la date de départ et modifier la formule ci-dessous
            float montantVersement = calculCumulPointsTrim(individu, CumulDroitsTab, dateDep) * this.TrouverSalaireRefRegime(InstPassPointsRegimesTab, dateDep);
            result = Math.round(montantVersement);
        }
        else {
            float ValPt = TrouverValeurPtRegime(InstPassPointsRegimesTab, dateDep);
            float montant = ValPt * calculCumulPointsTrim(individu, CumulDroitsTab, dateDep) * this.calculTaux(dateDep) * (1 + this.calculSurcote(dateDep)) * (1 + this.calculMajoEnfants(individu));
            result = Math.round(montant);
        }
        return result;
    }



    
    //TODO Calcul de majoration si 3 enfants ou plus à préciser (selon date d'acquisition des points)


   
}