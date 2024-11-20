import java.time.LocalDate;

public class DateDepart {
    private LocalDate dateDep;
    //Boolean carriereLongue;
    private int trimRachat;
    private Boolean retraiteProg;
    
    private Age ageDep;
    private int cumulTrim;
    private int trimManquant;
    private int trimSurcote;
    private int trimSurcoteParent;

    //CONSTRUCTEUR
    DateDepart(LocalDate dateDep, int trimRachat, Boolean retraiteProg, Individu individu, String[][] AnnualDataTab) throws Exception{
        this.dateDep = dateDep;
        this.trimRachat = trimRachat;
        this.retraiteProg = retraiteProg;
        this.ageDep = Tools.DateDiffDNN(individu.getDateNaissance(), dateDep);
        this.cumulTrim = CalculCumulTrim(dateDep, trimRachat, individu, AnnualDataTab);
        this.trimManquant = CalculTrimManquant(individu);
        this.trimSurcote = CalculTrimSurcote(dateDep, individu, AnnualDataTab);
    }

    
    //cumul des trimestres à la date de départ considérée
    private int CalculCumulTrim(LocalDate dateDep, int trimRachat, Individu individu, String[][] AnnualDataTab) throws Exception {
        //détermination du nombre de trimestres pour enfants à ajouter au total
        int trimEnfants;
        int result = 0;
        try {     
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
    
            //nombre de trim initial avant ajout des trimestres de la carrière
            int trimAjout = trimEnfants + trimRachat;
            
            //ajout des trimestres du début de la carrière à l'année prédédant la date de départ
            int anneeDep = dateDep.getYear();
            int moisDep = dateDep.getMonthValue();
            int cumulTrimCarriere = 0;
            int i = 1; //en 0 -> en-tetes du tableau
            
            while(AnnualDataTab[i][0] != null && Integer.parseInt(AnnualDataTab[i][0]) < anneeDep) {
                cumulTrimCarriere = cumulTrimCarriere + Integer.parseInt(AnnualDataTab[i][1]);
                i++;
            } 
            int indLigneAnneeDep = i;
            //ajout des trimestres de l'année de la date de départ
            int trimAnneeDep = (moisDep - 1) * Integer.parseInt(AnnualDataTab[indLigneAnneeDep][1]) / 12;            
            result = cumulTrimCarriere + trimAnneeDep + trimAjout;
        } catch (Exception e) {
            System.out.println("donnee Trim AnnualData incorrecte: " + e.getMessage());
        }
        
        return result;
    }

       
    private int CalculTrimManquant(Individu individu) {
        //calcul trim manquant par rapport au trim requis
        int trimManquantRequis = Math.max(0, individu.getTrimRequis() - this.cumulTrim);  
        
        //calcul trim manquant par rapport à l'age taux plein auto
        int trimManquantAgeAuto = Math.max(0, Tools.AgeDiffTrim(this.ageDep, individu.getAgeTxPleinAuto()));
        int result = Math.min(trimManquantRequis, trimManquantAgeAuto);
        return result;
    }

    private int CalculTrimSurcote(LocalDate dateDep, Individu individu, String[][] AnnualDataTab) {
        int nbTrimSurcote;
        //si la date de départ est avant l'age legal (cas carrière longue) -> pas de surcote
        if(this.dateDep.compareTo(individu.getDateAgeLegal()) <= 0){
            nbTrimSurcote = 0;
        }
        else {
            //calcul nb trim surcote max en fonction des trimestres cotisés entre age légal et date depart
            int nbTrimSurcoteMax = 0;
            int anneeAgeLegal = individu.getDateAgeLegal().getYear();
            int moisAgeLegal = individu.getDateAgeLegal().getMonthValue();
            int anneeDep = dateDep.getYear();
            int moisDep = dateDep.getMonthValue();
            for (int i = 1; i < AnnualDataTab.length; i++) {
                if (AnnualDataTab[i][0] == null) break;
                if (Integer.parseInt(AnnualDataTab[i][0]) == anneeAgeLegal) {
                    nbTrimSurcoteMax = nbTrimSurcoteMax + Integer.parseInt(AnnualDataTab[i][2]) / 12 * (12 - moisAgeLegal + 1);
                }
                else {
                    if (Integer.parseInt(AnnualDataTab[i][0]) == anneeDep) {
                        nbTrimSurcoteMax = nbTrimSurcoteMax + Integer.parseInt(AnnualDataTab[i][2]) / 12 * moisDep;
                    }
                    else {
                        if (Integer.parseInt(AnnualDataTab[i][0]) > anneeAgeLegal && Integer.parseInt(AnnualDataTab[i][0]) < anneeDep) {
                            nbTrimSurcoteMax = nbTrimSurcoteMax + Integer.parseInt(AnnualDataTab[i][2]);
                        }
                    }
                }

            }
            nbTrimSurcote = Math.min(nbTrimSurcoteMax, Math.max(0, this.cumulTrim - individu.getTrimRequis()));
        }
        return nbTrimSurcote;
    }

    public int trimSurcoteParent() {
        return 12; //TODO calcul surcote parentale
    }

    //GETTER
    public LocalDate GetDateDep() {
        return dateDep;
    }

    public int GetTrimRachat() {
        return trimRachat;
    }

    public Boolean GetRetraiteProg() {
        return retraiteProg;
    }

    public Age GetAgeDep() {
        return ageDep;
    }

    public int GetCumulTrim() {
        return cumulTrim;
    }

    public int GetTrimManquant() {
        return trimManquant;
    }

    public int GetTrimSurcote() {
        return trimSurcote;
    }

    public int GetTrimSurcoteParent() {
        return trimSurcoteParent;
    }
}


