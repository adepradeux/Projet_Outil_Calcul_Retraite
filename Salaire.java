public class Salaire implements Comparable<Salaire> {
    private int annee;
    private float salaire;
    private float salaireRevalo;

    //CONSTRUCTEUR
    public Salaire(int annee, float salaire, float salaireRevalo) throws Exception {
        this.annee = annee;
        this.salaire = salaire;
        this.salaireRevalo = salaireRevalo;

    }

    @Override
    public int compareTo(Salaire salaireAComparer) {
        if (this.GetSalaireRevalo() > salaireAComparer.GetSalaireRevalo()) {
            return 1;
        }
        else {
           return -1;
        }
    }

    
    //GETTER
    public int GetAnnee() {
        return annee;
    }

    public float GetSalaire() {
        return salaire;
    }

    public float GetSalaireRevalo() {
        return salaireRevalo;
    }
}
