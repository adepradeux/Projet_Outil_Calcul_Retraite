import java.time.LocalDate;

public class Tools {
    
    //méthode pour créer le tableau des régimes à calculer MAJ à chaque nouveau régime programmé
    public static Regime[] CreateRegimesTab (int length, Data data ) throws Exception {
        Regime[] RegimesTab = new Regime[length];
        int j = 0;
        for (int i = 0; i < RegimesTab.length; i++) {
            String nomReg = data.GetCumulDroitsTab()[0][i + 1];  
            String nomRegOutput = data.GetCumulDroitsTab()[1][i + 1];     
            if (nomReg.equals("rci")) {
                RegimesTab[j] = new RegimeRCI(nomReg, nomRegOutput, data);
            }
            else if (nomReg.equals("agirc_arrco")) {
                RegimesTab[j] = new RegimeAgircArrco(nomReg, nomRegOutput, data);
            }
            else if (nomReg.equals("regime_general")) {
                RegimesTab[j] = new RegimeRG(nomReg, nomRegOutput, data);
            }
            else if (nomReg.equals("cnavpl")) {
                RegimesTab[j] = new RegimeCnavpl(nomReg, nomRegOutput, data);
            }
            else if (nomReg.equals("carcdsf_rc")) {
                RegimesTab[j] = new RegimeCarcdsfRc(nomReg, nomRegOutput, data);
            }
            else if (nomReg.equals("carcdsf_pcv")) {
                RegimesTab[j] = new RegimeCarcdsfPcv(nomReg, nomRegOutput, data);
            }
            else if (nomReg.equals("carcdsf_pcv_avant_2006")) {
                RegimesTab[j] = new RegimeCarcdsfPcvAvant2006(nomReg, nomRegOutput, data);
            }
            else if (nomReg.equals("ircantec")) {
                RegimesTab[j] = new RegimeIrcantec(nomReg, nomRegOutput, data);
            }
            else if (nomReg.equals("carmf_rc")) {
                RegimesTab[j] = new RegimeCarmfRc(nomReg, nomRegOutput, data);
            }
            else if (nomReg.equals("carmf_asv")) {
                RegimesTab[j] = new RegimeCarmfAsv(nomReg, nomRegOutput, data);
            }
            else {
                System.out.println("Nom de régime invalide!");
            }

            j++;
        }
        return RegimesTab;
    }
    
    //méthode pour obtenir une date à partir d'un string type jj/mm/aaaa
    public static LocalDate dateFromString(String dateString) {
        String[] dateTab = dateString.split("/");
        Integer jour = Integer.valueOf(dateTab[0]);
        Integer mois = Integer.valueOf(dateTab[1]);
        Integer annee = Integer.valueOf(dateTab[2]);
        LocalDate date = LocalDate.of(annee, mois, jour);
        return date;
    }

    //méthode pour obtenir l'age sous la forme d'un string "xx ans et xx mois"
    public static String getStringOfAge(Age age) {
        String s1 = String.valueOf(age.ageAnnee);
        String s2 = String.valueOf(age.ageMois);
        String result;
        if (age.ageMois == 0) {
            result = s1.concat(" ans");
        }
        else {
            result = s1.concat(" ans et ").concat(s2).concat(" mois");
        }
      
        return result;
    }

    //méthode pour obtenir une date à partir d'une date de naissance et d'un age de la classe age
    public static LocalDate DDNAddAge(LocalDate DDN, Age age) {
        int anneeDDN = DDN.getYear();
        int moisDDN = DDN.getMonthValue();
        int jourDDN = DDN.getDayOfMonth();
        LocalDate result;
        //Si jour naissance différent de 1, on rajoute +1mois
        if (jourDDN == 1) {
            result = DDN.plusMonths(age.duree);  
        }
        else{
            result = LocalDate.of(anneeDDN, moisDDN, 1).plusMonths(age.duree + 1);
        }
        return result;
        
    }

    //méthode pour obtenir un age à partir d'une date de naissance et d'une date
    public static Age DateDiffDNN(LocalDate DDN, LocalDate date){
         //on récupère la date de naissance et la saisie de l'utilisateur de la date de départ
    int jourDDN = DDN.getDayOfMonth();
    int moisDDN = DDN.getMonthValue();
    int anneeDDN = DDN.getYear();
    int moisDate = date.getMonthValue();
    int anneeDate = date.getYear();
    int ageAnnee = anneeDate - anneeDDN - 1; //age l'année précédent la date de départ
    int ageMois = 0;
    
    //on définit le mois suivant celui la date de naissance ou le meme si né le 1er jour du moi
    if (jourDDN != 1) {
        moisDDN = moisDDN + 1;
    }
    
    if (moisDDN <= moisDate) {
        ageAnnee = ageAnnee + 1;
        ageMois = (12 - (moisDDN - moisDate)) % 12;
        
    }
    else {
        ageMois = (12 - (moisDDN - moisDate));
    }
    Age result = new Age(ageAnnee, ageMois);
    return result;
    }

    //retourne le nombre de trimestres entre 2 ages, arrondi au sup pour calcul decote (age1<age2)
    public static int AgeDiffTrim(Age age1, Age age2){
        int diffAnnee = age2.ageAnnee - age1.ageAnnee;
        int diffMois = age2.ageMois - age1.ageMois;
        double nbDiffMois = diffAnnee * 12 + diffMois;
        double resultInit = Math.ceil(nbDiffMois / 3);
        int diffTrim = (int) resultInit;
        int result = diffTrim;
        return result;
    }

    //retourne le nombre de trimestres entre 2 ages, arrondi à l'inf pour calcul surcote (age1<age2)
    public static int AgeDiffTrimInf(Age age1, Age age2){
        int diffAnnee = age2.ageAnnee - age1.ageAnnee;
        int diffMois = age2.ageMois - age1.ageMois;
        double nbDiffMois = diffAnnee * 12 + diffMois;
        double resultInit = Math.floor(nbDiffMois / 3);
        int diffTrim = (int) resultInit;
        int result = diffTrim;
        return result;
    }

    //calcul du nombre de trimestres civils entiers entre 2 dates pour calcul de la surcote (Date1 : date age legal Date2 : date depart)
    public static int DiffDateTrimCivil (LocalDate date1, LocalDate date2){
            int moisDate1 = date1.getMonthValue();
            LocalDate dateDebutSurcote;
                
            if (moisDate1 == 2 || moisDate1 == 5 || moisDate1 == 8 || moisDate1 == 11) {
                dateDebutSurcote = date1.plusMonths(2);  //on ajoute 2 mois pour nouvelle date au 1er jour du trim civil suivant
            }
            else {
                if (moisDate1 == 3 || moisDate1 == 6 || moisDate1 == 9 || moisDate1 == 12) {
                    dateDebutSurcote = date1.plusMonths(1);  //on ajoute 1 mois pour nouvelle date au 1er jour du trim civil suivant
                }
                else {
                    dateDebutSurcote = date1;
                }
            }
        
            int diffAnnee = date2.getYear() - dateDebutSurcote.getYear();
            int diffMois = date2.getMonthValue() - dateDebutSurcote.getMonthValue();
            int diffTrim = (diffAnnee * 12 + diffMois) / 3;
            int result;
            if(date2.compareTo(date1) >= 0){
                result = diffTrim;
            }
            else {
                result = -diffTrim;
            }
            return result;
    }

    //methode pour trouver l'indice de la ligne du tableau correspondant au nom cherché (dans la première colonne)
    public static int TrouverIndiceLigne(String[][] tab, String nomATrouver){
        int indLgn = 0;    
        for (int i =0; i < tab.length; i++) {
            if (tab[i][0] == null) break;
            if (tab[i][0].equals(nomATrouver)){
                indLgn = i;
            }
        }
        return indLgn;    
    }

      //methode pour trouver l'indice de la colonne du tableau correspondant au nom cherché (dans la première ligne)
    public static int TrouverIndiceColonne(String[][] tab, String nomATrouver){
        int indCol = 0;    
        for (int i =0; i < tab[0].length; i++) {
            if (tab[0][i] == null) break;
            if (tab[0][i].equals(nomATrouver)){
                indCol = i;
            }
        }
        return indCol;    
    }

    //methode pour trouver l'indice de la colonne du tableau de coeff revalo correspondant à la période du la date de départ
    public static int TrouverIndiceColonneRevalo(String[][] tab, LocalDate dateDep){
        if (dateDep.compareTo(LocalDate.of(2021, 1, 1)) <= 0) {
            System.out.println("calcul coeff revalo incorrect pour les date d'effet avant le 01/01/2021");
            return 1;
        }
        else {
            int indCol = 0; 
            for (int i = 2; i < tab[0].length; i++) {
                if (tab[0][i] == null) break;
                
                if (dateDep.compareTo(dateFromString(tab[0][i])) <= 0) {
                    return i - 1;
                }
                else {
                    indCol = i;
                }
            }

            return indCol;    
        }
    }


    //méthode pour décaler une date de départ au 1er jour du trim civil suivant
    public static DateDepart DecalerTrimCivil (DateDepart dateDepInit, Individu individu, String[][] AnnualDataTab ) throws Exception {
        int moisDep = dateDepInit.GetDateDep().getMonthValue();
        int anneeDep = dateDepInit.GetDateDep().getYear();
        DateDepart dateDepNew = null;
        if ((moisDep == 1) || (moisDep == 4) || (moisDep == 7) || (moisDep == 10)) {
            dateDepNew = dateDepInit;
        }
        else {
            if ((moisDep == 2) || (moisDep == 5) || (moisDep == 8) || (moisDep == 11)) {
                LocalDate dateNew = dateDepInit.GetDateDep().plusMonths(2);
                dateDepNew = new DateDepart("", dateNew, dateDepInit.GetTrimRachat(), dateDepInit.GetRetraiteProg(), individu, AnnualDataTab);
            }
            else {
                LocalDate dateNew = dateDepInit.GetDateDep().plusMonths(1);
                dateDepNew = new DateDepart("", dateNew, dateDepInit.GetTrimRachat(), dateDepInit.GetRetraiteProg(), individu, AnnualDataTab);
            }
        }
       
        return dateDepNew;
    }

    //méthode pour renvoyer une des données du régime dans le tableau regroupant les paramètre des régimes
    public static float TrouverDonneeRegime (String nomregime, String[][] InstParamRegimesTab, String donneeATrouver) throws Exception {
        float result = 0;
        try {
            int indLgn = Tools.TrouverIndiceLigne(InstParamRegimesTab, nomregime);
            int indCol = Tools.TrouverIndiceColonne(InstParamRegimesTab, donneeATrouver);
            result = Float.parseFloat(InstParamRegimesTab[indLgn][indCol]);
        } catch (NumberFormatException e) {
            System.out.println("donnee paramètre régime incorrecte: " + e.getMessage());
        }
        return result;
    }

    //méthode pour renvoyer le cumul d'une colonne (trim) dans le tableau AnnualDataTab
    public static int CumulTrimAnnualData (DateDepart dateDep, Data data, int indCol) {
        int anneeDep = dateDep.GetDateDep().getYear();
        int moisDep = dateDep.GetDateDep().getMonthValue();    
        int cumulTrim = 0;
        int i = 2; // en-tetes de colonne en 0 et 1
        while(data.GetAnnualDataTab()[i][indCol] != null && Integer.parseInt(data.GetAnnualDataTab()[i][0]) < anneeDep) {
            cumulTrim = cumulTrim + Integer.parseInt(data.GetAnnualDataTab()[i][indCol]);
            i++;
        }
        int indLigneAnneeDep = i;
        int trimAnneeDep = (moisDep - 1) * Integer.parseInt(data.GetAnnualDataTab()[indLigneAnneeDep][indCol]) / 12; 
        int result = cumulTrim + trimAnneeDep;
        return result;
    }
    
    public static int NbTrimEnfant (Individu individu) throws Exception {
        int trimEnfants = 0;
        try {     
            if (individu.getTrimEnfantsSpecifique() == 0) {
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
        } catch (Exception e) {
        System.out.println("donnee Trim AnnualData incorrecte: " + e.getMessage());
        }
        return trimEnfants;
    }

    //calcul du nombre de trim cotises entre 2 dates pour calcul surcote
    public static int DiffTrimCotises (LocalDate date1, LocalDate date2, String[][] AnnualDataTab) throws Exception {
        int nbTrimSurcoteMax = 0;
        int anneeDate1 = date1.getYear();
        int moisDate1 = date1.getMonthValue();
        int anneeDate2 = date2.getYear();
        int moisDate2 = date2.getMonthValue();
        for (int i = 2; i < AnnualDataTab.length; i++) {  
            if (AnnualDataTab[i][0] == null) break;
            if (Integer.parseInt(AnnualDataTab[i][0]) == anneeDate1) {
                nbTrimSurcoteMax = nbTrimSurcoteMax + (int)Math.floor(Float.parseFloat(AnnualDataTab[i][2]) / (float)12 * (12 - moisDate1 + 1));
            }
            else {
                if (Integer.parseInt(AnnualDataTab[i][0]) == anneeDate2) {
                    nbTrimSurcoteMax = nbTrimSurcoteMax + (int)Math.floor(Float.parseFloat((AnnualDataTab[i][2])) / (float)12 * moisDate2);
                }
                else {
                    if (Integer.parseInt(AnnualDataTab[i][0]) > anneeDate1 && Integer.parseInt(AnnualDataTab[i][0]) < anneeDate2) {
                        nbTrimSurcoteMax = nbTrimSurcoteMax + Integer.parseInt(AnnualDataTab[i][2]);
                    }
                }
            }

        }
        return nbTrimSurcoteMax;
    }

    //méthode pour vérifier si une année fait partie des années sur lesquelles il y a un trimestre racheté
    public static Boolean EstAnneeRachat(DateDepart dateDep, int annee, Data data) {
        Boolean anneeRachat = false;
        String nomDate = dateDep.GetNomDate();
        int indLigne = TrouverIndiceLigne(data.GetDateRetraiteTab(), nomDate);
        //si le nombre de trimestre rachetés n'est pas nul
        if (Integer.parseInt(data.GetDateRetraiteTab()[indLigne][3]) > 0) {
            //on fait une boucle sur la ligne de date de départ pour vérifier si les années rachetés indiquées correspondent à l'année en cours
            for (int i = 4; i < 8; i++) {  
                if (data.GetDateRetraiteTab()[indLigne][i] == null) break;
                if (Integer.parseInt(data.GetDateRetraiteTab()[indLigne][i]) == annee) {
                    anneeRachat = true;
                }
    
            }    
        }
        return anneeRachat;
    }

    //Méthode pour créer un tableau d'élément de classe Salaire à partir de AnnualDataTab
    public static Salaire[] CreerTabSalaireRevalo (DateDepart dateDep, Data data) throws Exception {
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
                        salaireRevalo = Math.round(salaire * coeffRevalo * 100) / (float)100;
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

    //Méthode pour créer le tableau de salaire pour affichage output (année, salaire, coeff revalo, salaire revalo)
    public static String[][] CreerTabSalaireOuput (DateDepart dateDep, Data data, Regime[] RegimesTab) throws Exception {
        Salaire TabSalaireRevalo[] = Tools.CreerTabSalaireRevalo(dateDep, data);
        //calcul du nombre de lignes jusqu'à l'année précédant l'année de départ
        int anneeDep = dateDep.GetDateDep().getYear();
        int longTabSalaires = 0;
        for (int i = 0; i < TabSalaireRevalo.length; i++) {
            if (TabSalaireRevalo[i].GetAnnee() == anneeDep) break;
            longTabSalaires++;
        }

        //création du tableau de résultat
        String [][] ResultatSalaires = new String[longTabSalaires][4];
        for (int i = 0; i < longTabSalaires; i++) {
            if (TabSalaireRevalo[i].GetAnnee() > dateDep.GetDateDep().getYear()) break;
            for (int j = 0; j < RegimesTab.length; j++) {
                if (RegimesTab[j].GetNom().equals("regime_general")) {
                    int anneeSalaire = Tools.CreerTabSalaireRevalo(dateDep, data)[i].GetAnnee();
                    float salaire = Tools.CreerTabSalaireRevalo(dateDep, data)[i].GetSalaire();
                    float salaireRevalo = Tools.CreerTabSalaireRevalo(dateDep, data)[i].GetSalaireRevalo();
                    //on cherche coeff revalo
                    int indAnneeRevalo = Tools.TrouverIndiceLigne(data.GetInstCoeffRevaloTab(), String.valueOf(anneeSalaire));  
                    int indColRevalo = Tools.TrouverIndiceColonneRevalo(data.GetInstCoeffRevaloTab(), dateDep.GetDateDep());  
                    float coeffRevalo = Float.parseFloat(data.GetInstCoeffRevaloTab()[indAnneeRevalo][indColRevalo]);  
                    //on remplit le tableau
                    ResultatSalaires[i][0] = String.valueOf(anneeSalaire);
                    ResultatSalaires[i][1] = String.valueOf(salaire);
                    ResultatSalaires[i][2] = String.valueOf(coeffRevalo);
                    ResultatSalaires[i][3] = String.valueOf(salaireRevalo);
                }
            }
        }
        return ResultatSalaires;
    }

}
