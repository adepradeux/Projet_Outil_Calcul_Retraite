public class RegimeCarmfRc extends RegimePoints {

    //CONSTRUCTEUR
    public RegimeCarmfRc(String nom, String nomOutput, Data data) throws Exception {
        super(nom, nomOutput, data);
    }

    
    //Méthode pour obtenir taux surcote (1,25% entre 62 et 65 ans et 0.75% entre 65 et 70 ans)
    @Override
    public float calculSurcote (DateDepart dateDep) {
        Age ageDep = dateDep.GetAgeDep();
        Age agePlancherS1 = new Age (62, 0);
        Age agePlancherS2 = new Age (65, 0);
        float result = 0;
        float surcote1 = 0;
        float surcote2 = 0;
        if (ageDep.duree > agePlancherS1.duree) {  //pas de surcote si départ avant 62 ans
            surcote1 = Math.min(12, Tools.AgeDiffTrimInf(agePlancherS1, ageDep)) * this.GetTx_surcote_1();
            surcote2 = Math.max(0, Math.min(20, Tools.AgeDiffTrimInf(agePlancherS2, ageDep))) * this.GetTx_surcote_2();
        }
        
        result = Math.round((surcote1 + surcote2) * 10000) / (float)10000;
        return result;
    }

    //pas de decote
}
