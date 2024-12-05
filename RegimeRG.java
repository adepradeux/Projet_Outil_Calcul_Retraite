import java.util.Arrays;

public class RegimeRG extends Regime {
    //CONSTRUCTEUR
    public RegimeRG(String nom, String nomOutput, Data data) throws Exception {
        super(nom, nomOutput, data);
    }

     //Méthode pour obtenir cumul de trimestres validés RG à la date donnée
     @Override
     public float calculCumulPointsTrim(Individu individu, Data data, DateDepart dateDep) throws Exception {
         float result;
         try {
            //calcul trim pour enfants
            int trimEnfants;
            if (individu.getTrimEnfantsSpecifique() == 0){
                if(individu.getSexe().equals("H") ){
                    trimEnfants = 0;
                }
                else {
                    trimEnfants = individu.getNbEnfants() * 8;
                }
            }
            else {
                trimEnfants = individu.getTrimEnfantsSpecifique();
            }
            
            //calcul du cumul des trimestres validés du début de la carrière à l'année de départ
            int cumulTrim = Tools.CumulTrimAnnualData(dateDep, data, 3);
           
            //calcul du cumul des trimestres équivalents (= trim qui sont comptés pour le calcul du taux mais pas en tant que trim validés dans la formule)
            int cumulTrimEquiv = Tools.CumulTrimAnnualData(dateDep, data, 5);

            result = cumulTrim - cumulTrimEquiv + trimEnfants;
            
         } catch (Exception e) {
             throw new Exception("donnee cumul droits régime incorrecte: " + e.getMessage()) ;
         }
         return result;
     }

    @Override
     public float TrouverValeurPtRegime (String[][] InstPassPointsRegimesTab, DateDepart dateDep) throws Exception {
        return 0; 
    }

    //Méthode pour obtenir taux de calcul avec decote (rappel taux max 50%)
    @Override
    public float calculTaux (DateDepart dateDep) {
        float taux = (float)0.5 - dateDep.GetTrimManquant() * (this.GetTx_decote_1()/ (float)2);
        float result = Math.round(taux * 100000) / (float)100000;
        return result;
    }

    //Méthode pour obtenir taux surcote
    @Override
    public float calculSurcote (DateDepart dateDep) {
        float surcote = (dateDep.GetTrimSurcote() + dateDep.GetTrimSurcoteParent()) * this.GetTx_surcote_1();
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
        float sam = calculSam(dateDep, data);
        float montant = sam * Math.min(individu.getTrimRequis(), calculCumulPointsTrim(individu, data, dateDep)) / individu.getTrimRequis()
        * this.calculTaux(dateDep) * (1 + this.calculSurcote(dateDep)) * (1 + this.calculMajoEnfants(individu));
        int result = Math.round(montant);
        return result;
    }

    @Override
    //calcul du Salaire Annuel Moyen (SAM)
    public float calculSam (DateDepart dateDep, Data data) throws Exception {
        
        Salaire tabSalaireRevalo[] = Tools.CreerTabSalaireRevalo(dateDep, data);
        ComparateurSalaireRevalo salaireComparator = new ComparateurSalaireRevalo();
        Arrays.sort(tabSalaireRevalo, salaireComparator);
        int nbSalaire = 0;
        float sommeSalaireRevalo = 0;
        for (Salaire element : tabSalaireRevalo) {
            
            if (element == null) break;
            if (nbSalaire > 24) break;
            if (element.GetAnnee() < dateDep.GetDateDep().getYear() && element.GetSalaireRevalo() != 0) {
                sommeSalaireRevalo = sommeSalaireRevalo + element.GetSalaireRevalo();
                nbSalaire++;
            }
        }
        float result = Math.round(sommeSalaireRevalo / (float)nbSalaire);
        
        return result;
    }


    @Override
    public Boolean estVersementUnique (Individu individu, Data data, DateDepart dateDep) {
        return false;
    }

}
