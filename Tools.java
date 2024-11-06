import java.time.LocalDate;

public class Tools {
    //méthode pour obtenir une date à partir d'un string type jj/mm/aaaa
    public static LocalDate dateFromString(String dateString) {
        String[] dateTab = dateString.split("/");
        Integer jour = Integer.valueOf(dateTab[0]);
        Integer mois = Integer.valueOf(dateTab[1]);
        Integer annee = Integer.valueOf(dateTab[2]);
        LocalDate date = LocalDate.of(annee, mois, jour);
        return date;
    }
}
