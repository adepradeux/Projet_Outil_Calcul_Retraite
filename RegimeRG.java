import java.util.Arrays;

public class RegimeRG extends Regime {
    //CONSTRUCTEUR
    public RegimeRG(String nom, String nomOutput, Data data) throws Exception {
        super(nom, nomOutput, data);
    }

     //Méthode pour obtenir cumul de trimestres validés RG à la date donnée
     @Override
     public float calculCumulPointsTrim(Individu individu, String[][] CumulDroitsTab, DateDepart dateDep) throws Exception {
         float result;
         try {
            //calcul trim pour enfants
            int trimEnfants;
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
            
            //calcul cumul+trim projetés
            int anneeDep = dateDep.GetDateDep().getYear();
            int moisDep = dateDep.GetDateDep().getMonthValue();
            //on cherche l'indice de la colonne correspondant au regime  
            int indCol = Tools.TrouverIndiceColonne(CumulDroitsTab, this.GetNom());
            int cumulTrimInit = Integer.parseInt(CumulDroitsTab[2][indCol]) - Integer.parseInt(CumulDroitsTab[3][indCol]);
            int anneeCumul = Tools.dateFromString(CumulDroitsTab[4][indCol]).getYear();
            int cumulTrimProjetes = 0;
            int i = 5; //en 5 -> début projection des points
            while(CumulDroitsTab[i][indCol] != null && Integer.parseInt(CumulDroitsTab[i][0]) < anneeDep) {
                if (Integer.parseInt(CumulDroitsTab[i][0]) > anneeCumul){
                    cumulTrimProjetes = cumulTrimProjetes + Integer.parseInt(CumulDroitsTab[i][indCol]);
                }
                i++;
            }
            int indLigneAnneeDep = i;
            int trimAnneeDep = (moisDep - 1) * Integer.parseInt(CumulDroitsTab[indLigneAnneeDep][indCol]) / 12;  
            result = cumulTrimInit + trimEnfants + cumulTrimProjetes + trimAnneeDep;
            
         } catch (Exception e) {
             throw new Exception("donnee cumul droits régime incorrecte: " + e.getMessage()) ;
         }
         return result;
     }

    @Override
     public float TrouverValeurPtRegime (String[][] InstPassPointsRegimesTab, DateDepart dateDep) throws Exception {
        return 0; 
    }

    //Méthode pour obtenir taux de calcul avec decote (rappel taux max 50%)
    @Override
    public float calculTaux (DateDepart dateDep) {
        float taux = (float)0.5 - dateDep.GetTrimManquant() * (this.GetTx_decote_1()/ (float)2);
        float result = Math.round(taux * 100000) / (float)100000;
        return result;
    }

    //Méthode pour obtenir taux surcote
    @Override
    public float calculSurcote (DateDepart dateDep) {
        float surcote = (dateDep.GetTrimSurcote() + dateDep.GetTrimSurcoteParent()) * this.GetTx_surcote_1();
        float result = Math.round(surcote * 10000) / (float)10000;
        return result;
    }

     //Méthode pour obtenir taux majoration si 3 enfants ou plus
     @Override
     public float calculMajoEnfants (Individu individu) {
         int nbEnfants = individu.getNbEnfants();
         float result = 0;
         if (nbEnfants >= 3) {
             result = this.GetMajoTroisEnfants();
         }
         return result;
     }

     //Méthode pour calcul du montant annuel brut
    @Override
    public int calculAnnuelBrut (Individu individu, DateDepart dateDep, Data data) throws Exception {
        float sam = calculSam(dateDep, data);
        float montant = sam * Math.min(individu.getTrimRequis(), calculCumulPointsTrim(individu, data.GetCumulDroitsTab(), dateDep)) / individu.getTrimRequis()
        * this.calculTaux(dateDep) * (1 + this.calculSurcote(dateDep)) * (1 + this.calculMajoEnfants(individu));
        int result = Math.round(montant);
        return result;
    }

    @Override
    //calcul du Salaire Annuel Moyen (SAM)
    public float calculSam (DateDepart dateDep, Data data) throws Exception {
        
        Salaire tabSalaireRevalo[] = CreerTabSalaireRevalo(dateDep, data);
        ComparateurSalaireRevalo salaireComparator = new ComparateurSalaireRevalo();
        Arrays.sort(tabSalaireRevalo, salaireComparator);
        int nbSalaire = 0;
        float sommeSalaireRevalo = 0;
        for (Salaire element : tabSalaireRevalo) {
            
            if (element == null) break;
            if (nbSalaire > 24) break;
            if (element.GetAnnee() < dateDep.GetDateDep().getYear() && element.GetSalaireRevalo() != 0) {
                sommeSalaireRevalo = sommeSalaireRevalo + element.GetSalaireRevalo();
                nbSalaire++;
            }
        }
        float result = Math.round(sommeSalaireRevalo / (float)nbSalaire);
        
        return result;
    }

    //Méthode pour créer un tableau d'élément de classe Salaire à partir de AnnualDataTab
    public Salaire[] CreerTabSalaireRevalo (DateDepart dateDep, Data data) throws Exception {
        //trouver le nombre de lignes de salaire
        int nbLigne = 0;
        for (int i = 2; i < data.GetAnnualDataTab().length; i++) { 
            if (data.GetAnnualDataTab()[i][0] == null) break;
            nbLigne = nbLigne + 1;
        }
        Salaire[] tabSalaireRevalo = new Salaire[nbLigne];
        for (int i = 0; i < tabSalaireRevalo.length; i++) {
            if (data.GetAnnualDataTab()[i + 2][0] == null) break;   
            //calcul du salaire revalo 
            float salaireRevalo;
            int annee = Integer.parseInt(data.GetAnnualDataTab()[i + 2][0]); 
            float salaire = Float.parseFloat(data.GetAnnualDataTab()[i + 2][6]);  
      
            //si l'année comporte un rachat de trimestres -> salaire non pris en compte donc on le met à 0
            if (Tools.EstAnneeRachat(dateDep, annee, data)) { 
                salaireRevalo = 0;
            }
            else {
                //si l'année comporte 0 trimestres -> salaire non pris en compte donc on le met à 0
                if (Integer.parseInt(data.GetAnnualDataTab()[i + 2][4]) == 0) {   
                    salaireRevalo = 0;
                }    
                else {
                    int indAnneeRevalo = Tools.TrouverIndiceLigne(data.GetInstCoeffRevaloTab(), String.valueOf(data.GetAnnualDataTab()[i + 2][0]));  
                    int indColRevalo = Tools.TrouverIndiceColonneRevalo(data.GetInstCoeffRevaloTab(), dateDep.GetDateDep());  
                    float coeffRevalo = Float.parseFloat(data.GetInstCoeffRevaloTab()[indAnneeRevalo][indColRevalo]);  
                    int indAnneePass = Tools.TrouverIndiceLigne(data.GetInstPassPointsRegimesTab(), String.valueOf(data.GetAnnualDataTab()[i + 2][0]));  
                    float pass = Float.parseFloat(data.GetInstPassPointsRegimesTab()[indAnneePass][1]);
                    //si année >= 2005 plafond au pass
                    if (annee < 2005) {
                        salaireRevalo = salaire * coeffRevalo;
                    }
                    else {
                        salaireRevalo = Math.round(Math.min(pass, salaire) * coeffRevalo * 100) / (float)100;  
                    }
                }
            }
            //création d'une instance de la classe Salaire
            tabSalaireRevalo[i] = new Salaire(annee, salaire, salaireRevalo);
        } 
        return tabSalaireRevalo;
    
    }

    @Override
    public Boolean estVersementUnique (Individu individu, Data data, DateDepart dateDep) {
        return false;
    }

}
