public class RegimeCarcdsfRc extends RegimePoints {

    //CONSTRUCTEUR
    public RegimeCarcdsfRc(String nom, String nomOutput, Data data) throws Exception {
        super(nom, nomOutput, data);
    }

        
    //Méthode pour obtenir taux de calcul avec decote en prenant compte de l'age taux plein à 67 ans, diminué d'une année par enfant eus pour les femmes
    @Override
    public float calculTaux (DateDepart dateDep) {
        float decote = dateDep.GetTrimManquantCarcdsfRc() * this.GetTx_decote_1();
        float result = 1 - Math.round(decote * 10000) / (float)10000;
        return result;
    }

    //Méthode pour obtenir taux surcote en fonction de l'age de 67 ans ou moins pour les mères avec enfants 
    @Override
    public float calculSurcote (DateDepart dateDep) {
        float surcote = dateDep.GetTrimSurcoteCarcdsfRc() * this.GetTx_surcote_1();
        float result = Math.round(surcote * 10000) / (float)10000;
        return result;
    }
}
