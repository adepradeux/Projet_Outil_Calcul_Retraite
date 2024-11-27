
import java.time.LocalDate;

public class Individu {
    private LocalDate dateNaissance;
    private String sexe;
    private int nbEnfants;
    private Boolean inaptitude;
    private Boolean salarie;
    private int trimEnfantSpecifique;
    private int trimRequis;
    private Age ageLegal;
    private LocalDate dateAgeLegal;
    private Age ageTxPleinAuto;
    private LocalDate dateAgeTxPleinAuto;

    //CONSTRUCTEUR
    Individu(Data data) throws Exception {
        try{
            this.dateNaissance = Tools.dateFromString(data.GetIndTab()[0][1]);
            this.sexe = data.GetIndTab()[1][1];
            this.nbEnfants = Integer.parseInt(data.GetIndTab()[2][1]);
            this.trimEnfantSpecifique = Integer.parseInt(data.GetIndTab()[5][1]);
            this.trimRequis = CalculTrimRequis(data.GetInstAgeTrimTab());
            this.ageLegal = CalculAgeLegal(data.GetInstAgeTrimTab());
            this.dateAgeLegal = Tools.DDNAddAge(this.dateNaissance, this.ageLegal);
            this.ageTxPleinAuto = CalculAgeTxPleinAuto(data.GetInstAgeTrimTab());
            this.dateAgeTxPleinAuto = Tools.DDNAddAge(this.dateNaissance, this.ageTxPleinAuto);
            this.inaptitude = Integer.parseInt(data.GetIndTab()[3][1]) == 1;
            this.salarie = Integer.parseInt(data.GetIndTab()[4][1]) == 1;
        } catch (Exception e)  {
            throw new Exception("donnee individu incorrecte: " + e.getMessage()) ;
        }
    }
    
    
    //MEHTODES
    private int CalculTrimRequis ( String[][] InstAgeTrimTab) throws Exception { 
        int k = 0;
        Integer anneeDDN = this.dateNaissance.getYear();
        Integer moisDDN = this.dateNaissance.getMonthValue();
        int result = 0;
        try {  
            //on cherche l'indice de la ligne correspondant à l'année de naissance  
            for (int i = 1; i < InstAgeTrimTab.length; i++) {
                Integer anneeTab = 0;
                if (InstAgeTrimTab[i][0] != null) {
                    anneeTab = Integer.valueOf(InstAgeTrimTab[i][0]);
                }
            if (anneeTab.equals(anneeDDN)) {
                k = i;
            }
            }
            // on récupère le nombre de trim requis pour le taux plein
            int trimestresRequis = Integer.parseInt(InstAgeTrimTab[k][3]);    
            //cas particulier de la génération 1961 selon le mois de naissance
            if (anneeDDN == 1961) {
                if (moisDDN < 9) {
                    result = trimestresRequis;
                }
                else {
                    result = trimestresRequis + 1;
                }
            }    
            else {
                result = trimestresRequis;
            }    
            
        } catch (Exception e) {
            System.out.println("donnee age_trim incorrecte: " + e.getMessage());
        }
        return result;
    } 

    private Age CalculAgeLegal (String[][] InstAgeTrimTab) throws Exception {
        int anneeDDN = this.dateNaissance.getYear();
        int moisDDN = this.dateNaissance.getMonthValue();
        int k = 0;
        Age result = new Age(0, 0);
        try {
            
            //on cherche l'indice de la ligne correspondant à l'année de naissance
            for (int i = 1; i < InstAgeTrimTab.length; i++) {
                Integer anneeTab = 0;
                if (InstAgeTrimTab[i][0] != null) {                 //on rajoute cette condition car le tableau InstAgeTrimTab contient des lignes vides et Integer.valueOf ne marche pas sur les lignes vides
                    anneeTab = Integer.valueOf(InstAgeTrimTab[i][0]);
                }
                if (anneeTab.equals(anneeDDN)) {
                    k = i;
                }
            }
            
            // on récupère l'année et le mois de l'age légal
            int ageAnnee = Integer.parseInt(InstAgeTrimTab[k][1]);
            int ageMois = 0;
            //cas particulier de la génération 1961 selon le mois de naissance
            if (anneeDDN == 1961) {
                if (moisDDN < 9) {
                    ageMois = 0;
                }
                else {
                    ageMois = 3;
                }
            }
            else {
                ageMois = Integer.parseInt(InstAgeTrimTab[k][2]);
            }
            
            result = new Age(ageAnnee, ageMois);
        } catch (NumberFormatException e) {
            System.out.println("donnee age_trim incorrecte: " + e.getMessage());
        }
        return result;
    }

    
     private Age CalculAgeTxPleinAuto (String[][] InstAgeTrimTab) {
        int anneeDDN = this.dateNaissance.getYear();
        int k = 0;
        Age result = new Age(0, 0);
        try {
            //on cherche l'indice de la ligne correspondant à l'année de naissance
            for (int i = 1; i < InstAgeTrimTab.length; i++) {
                Integer anneeTab = 0;
                if (InstAgeTrimTab[i][0] != null) {                 //on rajoute cette condition car le tableau InstAgeTrimTab contient des lignes vides et Integer.valueOf ne marche pas sur les lignes vides
                    anneeTab = Integer.valueOf(InstAgeTrimTab[i][0]);
                }
                if (anneeTab.equals(anneeDDN)) {
                    k = i;
                }
            }
            
            // on récupère l'année et le mois de l'age du taux plein auto
            int ageAnnee = Integer.parseInt(InstAgeTrimTab[k][4]);
            int ageMois = Integer.parseInt(InstAgeTrimTab[k][5]);
            result = new Age(ageAnnee, ageMois);
        } catch (NumberFormatException e) {
            System.out.println("donnee age_trim incorrecte: " + e.getMessage());
        }
        return result;
    }

    

    // GETTERS pour les variables privés
    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public String getSexe() {                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
        return sexe;
    }

    public int getNbEnfants() {
        return nbEnfants;
    }                                        

    public Boolean getInaptitude() {
        return inaptitude;
    }

   public Boolean getSalarie() {                                                                                                                                      
      return this.salarie;
   }

   public int getTrimEnfantsSpecifique() {                                                                                                                                      
    return this.trimEnfantSpecifique;
    }

   public int getTrimRequis() {
    return this.trimRequis;
   }

   public Age getAgeLegal() {
    return this.ageLegal;
   }

   public LocalDate getDateAgeLegal() {
    return this.dateAgeLegal;
   }

   public Age getAgeTxPleinAuto() {
    return this.ageTxPleinAuto;
   }

   public LocalDate getDateAgeTxPleinAuto() {
    return this.dateAgeTxPleinAuto;
   }
    
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  