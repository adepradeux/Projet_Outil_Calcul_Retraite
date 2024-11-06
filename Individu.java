
import java.time.LocalDate;

public class Individu {
    LocalDate dateNaissance;
    String sexe;
    int nbEnfants;
    Boolean inaptitude;
    Boolean salarie;

    //constructeur test
   /* Individu (String[][] IndTab) {

    }*/
    
    //TODO
    public int trimRequis () {
        return 12;
    } 
    public int ageLegal () {
        return 12;
    }

    public LocalDate dateAgeLegal () {
        return LocalDate.of(2022, 12, 12);
    }  
    
    public int ageTxPleinAuto () {
        return 12;
    }

    public LocalDate dateTxPleinAuto () {
        return LocalDate.of(2022, 12, 12);
    }


    public int ageTxPlein () {
        return 12;
    }

    public LocalDate dateTxPlein () {
        return LocalDate.of(2022, 12, 12);
    }

}
