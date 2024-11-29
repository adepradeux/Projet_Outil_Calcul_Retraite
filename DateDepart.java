import java.time.LocalDate;

public final class DateDepart {
    private final String nomDate;
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
    private final int trimSurcoteParent;

    //CONSTRUCTEUR
    DateDepart(String nomDate, LocalDate dateDep, int trimRachat, Boolean retraiteProg, Individu individu, String[][] AnnualDataTab) throws Exception{
        this.nomDate = nomDate;
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
        this.trimSurcoteParent = CalculTrimSurcoteParent(individu, AnnualDataTab, trimRachat);
    }

    
    //cumul des trimestres à la date de départ considérée
    @SuppressWarnings("UseSpecificCatch")
    private int CalculCumulTrim(LocalDate dateDep, int trimRachat, Individu individu, String[][] AnnualDataTab) throws Exception {
        //détermination du nombre de trimestres pour enfants à ajouter au total
        int trimEnfants;
        int result = 0;
        try {     
            trimEnfants = Tools.NbTrimEnfant(individu);
            //nombre de trim initial avant ajout des trimestres de la carrière
            int trimAjout = trimEnfants + trimRachat;
            
            //ajout des trimestres du début de la carrière à l'année prédédant la date de départ
            int anneeDep = dateDep.getYear();
            int moisDep = dateDep.getMonthValue();
            int cumulTrimCarriere = 0;

            //TEST new annual data tab
            int i = 2; //en 0 -> en-tetes du tableau
            
            while(AnnualDataTab[i][0] != null && Integer.parseInt(AnnualDataTab[i][0]) < anneeDep) {
                cumulTrimCarriere = cumulTrimCarriere + Integer.parseInt(AnnualDataTab[i][1]);
                i++;
            } 
            int indLigneAnneeDep = i;
            //ajout des trimestres de l'année de la date de départ
            int trimAnneeDep = (moisDep - 1) * Integer.parseInt(AnnualDataTab[indLigneAnneeDep][1]) / 12;   

            /* /////// INIT
            int i = 1; //en 0 -> en-tetes du tableau
            
            while(AnnualDataTab[i][0] != null && Integer.parseInt(AnnualDataTab[i][0]) < anneeDep) {
                cumulTrimCarriere = cumulTrimCarriere + Integer.parseInt(AnnualDataTab[i][1]);
                i++;
            } 
            int indLigneAnneeDep = i;
            //ajout des trimestres de l'année de la date de départ
            int trimAnneeDep = (moisDep - 1) * Integer.parseInt(AnnualDataTab[indLigneAnneeDep][1]) / 12;   */  /////////       
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

    //calcul du nombre de trimestres de surcote après la date d'acquisition du nombre de trim requis
    private int CalculTrimSurcote(LocalDate dateDep, Individu individu, String[][] AnnualDataTab) throws Exception {
        int nbTrimSurcote;
        //si la date de départ est avant l'age legal (cas carrière longue) -> pas de surcote
        if(this.dateDep.compareTo(individu.getDateAgeLegal()) <= 0){
            nbTrimSurcote = 0;
        }
        else {
            //calcul nb trim surcote max en fonction des trimestres cotisés entre age légal et date depart
            int nbTrimSurcoteMax = Tools.DiffTrimCotises(individu.getDateAgeLegal(), dateDep, AnnualDataTab);
            nbTrimSurcote = Math.min(nbTrimSurcoteMax, Math.max(0, this.cumulTrim - individu.getTrimRequis()));
        }
        return nbTrimSurcote;
    }

    //calcul des trimestres de surcote pour CARCDSF RC avec age taux plein à 67 ans, diminué d'une année par enfant eus
    //surcote uniquement si poursuite d'activité libérale
    //par prudence et interpretation stricte des statuts, déclenchement de la surcote qu'après 67 ans meme si le taux plein est avant 
    private int CalculTrimSurcoteCarcdsfRc(Individu individu) { 
        //determination de l'age taux plein
        Age ageTxPlein;
        int result = 0;
        if (!individu.getSalarie()) {
            /*if (individu.getSexe().equals("F")) {
                ageTxPlein = new Age(individu.getAgeTxPleinAuto().ageAnnee - individu.getNbEnfants(), individu.getAgeTxPleinAuto().ageMois);
            }
            else {*/
                ageTxPlein = individu.getAgeTxPleinAuto();
            //}
            result = Math.max(0, Tools.AgeDiffTrimInf(ageTxPlein, this.ageDep));
        }
        return result;
    }

    //calcul nb trim de surcote parentale : pour assurés nés à partir de 1964, si depart après l'age légal et si majo de trim pour enfant
    public int CalculTrimSurcoteParent(Individu individu, String[][] AnnualDataTab, int trimRachat) throws Exception {
        int trimSurcote = 0;
        int nbTrimEnfants = Tools.NbTrimEnfant(individu);
        LocalDate dateAgeLegal = Tools.DDNAddAge(individu.getDateNaissance(), individu.getAgeLegal());
        if (individu.getDateNaissance().getYear() >= 1964 && this.dateDep.compareTo(dateAgeLegal) >= 0 && nbTrimEnfants> 0) {
            //calcul du nombre de trimestres cumulés à l'age legal
            int cumulTrimAgeLegal = CalculCumulTrim(dateAgeLegal, this.trimRachat, individu, AnnualDataTab) + nbTrimEnfants + trimRachat;
            
            if (cumulTrimAgeLegal > individu.getTrimRequis()) {
                LocalDate dateDebutSurcote = LocalDate.of(dateAgeLegal.getYear() - 1, dateAgeLegal.getMonthValue(), dateAgeLegal.getDayOfMonth()); //age légal - 1 ans
                int trimSurcoteMax = Tools.DiffTrimCotises(dateDebutSurcote, dateAgeLegal, AnnualDataTab);
                trimSurcote = Math.min(trimSurcoteMax, cumulTrimAgeLegal - individu.getTrimRequis());
            }
        }
        int result = Math.min(4, trimSurcote);
        return result; 
    }

    //GETTER
    
    public String GetNomDate() {
        return nomDate;
    }

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


