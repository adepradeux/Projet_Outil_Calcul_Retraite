import java.time.LocalDate;

public class DateDepart {
    private final LocalDate dateDep;
    //Boolean carriereLongue;
    private final int trimRachat;
    private final Boolean retraiteProg;
    
    private final Age ageDep;
    private final int cumulTrim;
    private final int trimManquant;
    private final int trimManquantAgeAuto;
    private final int trimManquantCarcdsfRc;
    private final int trimSurcoteCarcdsfRc;
    private final int trimSurcote;
    private int trimSurcoteParent;

    //CONSTRUCTEUR
    DateDepart(LocalDate dateDep, int trimRachat, Boolean retraiteProg, Individu individu, String[][] AnnualDataTab) throws Exception{
        this.dateDep = dateDep;
        this.trimRachat = trimRachat;
        this.retraiteProg = retraiteProg;
        this.ageDep = Tools.DateDiffDNN(individu.getDateNaissance(), dateDep);
        this.cumulTrim = CalculCumulTrim(dateDep, trimRachat, individu, AnnualDataTab);
        this.trimManquant = CalculTrimManquant(individu);
        this.trimManquantAgeAuto = CalculTrimManquantAgeAuto(individu);
        this.trimManquantCarcdsfRc = CalculTrimManquantCarcdsfRc(individu);
        this.trimSurcoteCarcdsfRc = CalculTrimSurcoteCarcdsfRc(individu);
        this.trimSurcote = CalculTrimSurcote(dateDep, individu, AnnualDataTab);
    }

    
    //cumul des trimestres à la date de départ considérée
    @SuppressWarnings("UseSpecificCatch")
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

       
    //calcul des trimestres manquants pour régimes avec calcul decote selon le cumul de trimestres
    private int CalculTrimManquant(Individu individu) {
        //calcul trim manquant par rapport au trim requis
        int trimManquantRequis = Math.max(0, individu.getTrimRequis() - this.cumulTrim);  
        
        //calcul trim manquant par rapport à l'age taux plein auto
        int trimManquantAuto = Math.max(0, Tools.AgeDiffTrim(this.ageDep, individu.getAgeTxPleinAuto()));
        int result = Math.min(trimManquantRequis, trimManquantAuto);
        return result;
    }

    //calcul des trimestres manquants pour régimes avec calcul decote selon age avec taux plein à l'âge taux plein auto
    private int CalculTrimManquantAgeAuto(Individu individu) {
        int trimManquantAuto = Math.max(0, Tools.AgeDiffTrim(this.ageDep, individu.getAgeTxPleinAuto()));
        return trimManquantAuto;
    }

    //calcul des trimestres manquants pour CARCDSF RC avec age taux plein à 67 ans, diminué d'une année par enfant eus
    private int CalculTrimManquantCarcdsfRc(Individu individu) { 
        //determination de l'age taux plein
        Age ageTxPlein;
        if (individu.getSexe().equals("F")) {
            ageTxPlein = new Age(individu.getAgeTxPleinAuto().ageAnnee - individu.getNbEnfants(), individu.getAgeTxPleinAuto().ageMois);
        }
        else {
            ageTxPlein = individu.getAgeTxPleinAuto();
        }
        int result = Math.max(0, Tools.AgeDiffTrim(this.ageDep, ageTxPlein));
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
                    nbTrimSurcoteMax = nbTrimSurcoteMax + (int)Math.floor(Float.parseFloat(AnnualDataTab[i][2]) / (float)12 * (12 - moisAgeLegal + 1));
                }
                else {
                    if (Integer.parseInt(AnnualDataTab[i][0]) == anneeDep) {
                        nbTrimSurcoteMax = nbTrimSurcoteMax + (int)Math.floor(Float.parseFloat((AnnualDataTab[i][2])) / (float)12 * moisDep);
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

    //calcul des trimestres de surcote pour CARCDSF RC avec age taux plein à 67 ans, diminué d'une année par enfant eus
    //surcote uniquement si poursuite d'activité libérale
    private int CalculTrimSurcoteCarcdsfRc(Individu individu) { 
        //determination de l'age taux plein
        Age ageTxPlein;
        int result = 0;
        if (!individu.getSalarie()) {
            if (individu.getSexe().equals("F")) {
                ageTxPlein = new Age(individu.getAgeTxPleinAuto().ageAnnee - individu.getNbEnfants(), individu.getAgeTxPleinAuto().ageMois);
            }
            else {
                ageTxPlein = individu.getAgeTxPleinAuto();
            }
            result = Math.max(0, Tools.AgeDiffTrimInf(ageTxPlein, this.ageDep));
        }
        return result;
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

    public int GetTrimManquantAgeAuto() {
        return trimManquantAgeAuto;
    }

    public int GetTrimManquantCarcdsfRc() {
        return trimManquantCarcdsfRc;
    }

    public int GetTrimSurcote() {
        return trimSurcote;
    }

    public int GetTrimSurcoteCarcdsfRc() {
        return trimSurcoteCarcdsfRc;
    }

    public int GetTrimSurcoteParent() {
        return trimSurcoteParent;
    }
}


