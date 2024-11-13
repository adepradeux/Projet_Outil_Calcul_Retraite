public class RegimeAgircArrco extends RegimePoints {
   

    //CONSTRUCTEUR
    public RegimeAgircArrco(String nom, String [][] InstParamRegimesTab) throws Exception {
        super(nom, InstParamRegimesTab);
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


   
}