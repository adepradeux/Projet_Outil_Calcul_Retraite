import java.util.Comparator;

public class ComparateurSalaireRevalo implements Comparator<Salaire>  {
    @Override
    public int compare(Salaire firstSalaire, Salaire secondSalaire) {
       return Float.compare(secondSalaire.GetSalaireRevalo(), firstSalaire.GetSalaireRevalo());
    }
}
