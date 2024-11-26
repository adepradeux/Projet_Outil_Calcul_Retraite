public class RegimeCarcdsfPcvAvant2006 extends RegimePoints {
    
    ////regime pour les points CARCDSF PCV acquis entre le 01/01/1995 et le 31/12/2005

    //CONSTRUCTEUR
    public RegimeCarcdsfPcvAvant2006(String nom, String nomOutput, String [][] InstParamRegimesTab) throws Exception {
        super(nom, nomOutput, InstParamRegimesTab);
    }

    //Méthode pour obtenir taux de calcul avec decote en prenant compte de l'age taux plein à 67 ans
    @Override
    public float calculTaux (DateDepart dateDep) {
        float decote = dateDep.GetTrimManquantAgeAuto() * this.GetTx_decote_1();
        float result = 1 - Math.round(decote * 10000) / (float)10000;
        return result;
    }
}
