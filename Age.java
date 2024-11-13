
import java.time.LocalDate;

public class Age {
    int ageAnnee;
    int ageMois;
    int duree;
    
    public Age(int ageAnnee, int ageMois) {
        this.ageAnnee = ageAnnee;
        this.ageMois = ageMois;
        this.duree = ageAnnee * 12 + ageMois;
    }

}


