
import java.time.LocalDate;

public class RegimeIrcantec extends RegimePoints {
    
    //CONSTRUCTEUR
    public RegimeIrcantec (String nom, String nomOutput, String [][] InstParamRegimesTab) throws Exception {
        super(nom, nomOutput, InstParamRegimesTab);
    }

    //calcul taux de calcul avec decote : 1% pour les trim manquants de 1 à 12, puis 1,25% pour trim manquant à partir de 13 - decote 22% max
    @Override
    public float calculTaux (DateDepart dateDep) {
        float decoteP1 = Math.min(dateDep.GetTrimManquant(), (float)12)  * this.GetTx_decote_1();
        float decoteP2 = Math.max((float)0, dateDep.GetTrimManquant() - (float)12) * this.GetTx_decote_2();
        float decote = Math.min((float)0.22, decoteP1 + decoteP2);
        float result = 1 - Math.round(decote * 10000) / (float)10000;
        return result;
    }

    //taux majoration pour enfants : 10% pour 3 enfants, 15% pour 4, 20% pour 5, 25% pour 6, 30% pour 7 et+
    @Override
    public float calculMajoEnfants (Individu individu) {
        int nbEnfants = individu.getNbEnfants();
        float result = 0;
        if (nbEnfants == 3) {
            result = (float)0.1;
        }
        else if (nbEnfants == 4) {
            result = (float)0.15;
        }
        else if (nbEnfants == 5) {
            result = (float)0.2;
        }
        else if (nbEnfants == 6) {
            result = (float)0.25;
        }
        else if (nbEnfants >= 7) {
            result = (float)0.3;
        }
        return result;
    }

        
    //Méthode pour calcul du montant annuel brut
    @Override
    public int calculAnnuelBrut (Individu individu, DateDepart dateDep, String[][] InstPassPointsRegimesTab, String[][] CumulDroitsTab, String[][] AnnualDataTab, String[][] InstCoeffRevaloTab) throws Exception {
        int result = 0;
        if (estVersementUnique(individu, CumulDroitsTab, dateDep)) {
            LocalDate dateValeur = LocalDate.of(dateDep.GetDateDep().getYear() - 1, dateDep.GetDateDep().getMonthValue(), 1); //il faut prendre le salaire de ref de l'année précédant le départ
            DateDepart dateDepPourValeur = new DateDepart(dateValeur, dateDep.GetTrimRachat(), dateDep.GetRetraiteProg(), individu, AnnualDataTab);
            float montantVersement = calculCumulPointsTrim(individu, CumulDroitsTab, dateDep) * this.TrouverSalaireRefRegime(InstPassPointsRegimesTab, dateDepPourValeur);
            result = Math.round(montantVersement);
        }
        else {
            float ValPt = TrouverValeurPtRegime(InstPassPointsRegimesTab, dateDep);
            float montant = ValPt * calculCumulPointsTrim(individu, CumulDroitsTab, dateDep) * this.calculTaux(dateDep) * (1 + this.calculSurcote(dateDep)) * (1 + this.calculMajoEnfants(individu));
            result = Math.round(montant);
        }
        return result;
    }

    //Méthode pour calcul du montant annuel net

    @Override
    public int calculAnnuelNet (Individu individu, DateDepart dateDep, String[][] InstPassPointsRegimesTab, String[][] CumulDroitsTab, String[][] AnnualDataTab, String[][] InstCoeffRevaloTab) throws Exception {
        float montant = this.calculAnnuelBrut(individu, dateDep, InstPassPointsRegimesTab, CumulDroitsTab, AnnualDataTab, InstCoeffRevaloTab) * (1 - this.GetTx_plvt_sociaux());
        int result = Math.round(montant);
        return result;
    }

    
}
