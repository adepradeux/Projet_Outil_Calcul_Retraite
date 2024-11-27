
import java.time.LocalDate;

public class RegimeCnavpl extends RegimePoints {
    
    //CONSTRUCTEUR
    public RegimeCnavpl(String nom, String nomOutput, Data data) throws Exception {
        super(nom, nomOutput, data);
    }

    //METHODES

    //calcul taux de surcote : 0.75% pour les trimestres de surcote accomplis après le 01/09/2023 et 1.25% pour les trim après le 01/09/2023
    // décalage de la date au 01/10/2023 pour prendre en compte les trim civils entiers après le 01/09/2023
    @Override
    public float calculSurcote (DateDepart dateDep) {
        float surcote;
        LocalDate datePivot = LocalDate.of(2023, 10, 1);
        if (dateDep.GetDateDep().isBefore(datePivot)) {
            surcote = dateDep.GetTrimSurcote() * this.GetTx_surcote_1();
        }
        else {
            int trimAfterPivot = Tools.DiffDateTrimCivil(dateDep.GetDateDep(), datePivot);
            if (trimAfterPivot >= dateDep.GetTrimSurcote()) {
                surcote = dateDep.GetTrimSurcote() * this.GetTx_surcote_2();
            }
            else {
                float surcote1 = Math.max(0, (dateDep.GetTrimSurcote() - trimAfterPivot )) * this.GetTx_surcote_1();
                float surcote2 = trimAfterPivot * this.GetTx_surcote_2();
                surcote = surcote1 + surcote2;
            }
        }
        float result = Math.round(surcote * 10000) / (float)10000;
        return result;
    }
}
