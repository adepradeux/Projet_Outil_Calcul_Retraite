
import java.time.LocalDate;




public class CalculRetraite {
    public static void main(String[] args) throws Exception {

        //initialisation des fichiers avec les données d'entrée client
        String FILE_IND = "/Input_client/InputIndividu.csv";
        String FILE_ANNUAL_DATA = "/Input_client/InputAnnualData.csv";
        String FILE_CUMUL_DROITS = "/Input_client/InputCumulDroitsParRegime.csv";
        String FILE_DATE_RETRAITE = "/Input_client/InputDateRetraite.csv";

        //initialisation des fichiers avec les données institutionnelles
        String FILE_INST_AGE_TRIM = "/Input_data_institutionnelles/Input_age_trim.csv";
        String FILE_INST_PARAM_REGIMES = "/Input_data_institutionnelles/Input_parametres_regimes.csv";
        String FILE_INST_PASS_POINTS_REGIMES = "/Input_data_institutionnelles/Input_pass_points_regimes.csv";
        String FILE_INST_SALAIRE_MIN_TRIM = "/Input_data_institutionnelles/Input_salaire_min_trim.csv";
        String FILE_INST_COEFF_REVALO = "/Input_data_institutionnelles/Input_coeff_revalo.csv";

        
        //initialisation des tableaux pour importer les données d'entrée client
        String[][] IndTab = new String[10][2];           //TODO voir si on peut faire autrement que prévoir la taille du tableau
        String[][] AnnualDataTab = new String[300][7];
        String[][] CumulDroitsTab = new String[300][20]; 
        String[][] DateRetraiteTab = new String[20][5];
        
        //initialisation des tableaux pour importer les données institutionnelles
        String[][] InstAgeTrimTab = new String[150][6];           
        String[][] InstParamRegimesTab = new String[50][7];
        String[][] InstPassPointsRegimesTab = new String[150][50];
        String[][] InstSalaireMinTrimTab = new String[150][2];
        String[][] InstCoeffRevaloTab = new String[150][20];

        //imporation des données d'entrée
        IndTab = CsvFileHelper.getData(FILE_IND, IndTab);
        CsvFileHelper.getData(FILE_ANNUAL_DATA, AnnualDataTab);
        CsvFileHelper.getData(FILE_CUMUL_DROITS, CumulDroitsTab);
        CsvFileHelper.getData(FILE_DATE_RETRAITE, DateRetraiteTab);
        CsvFileHelper.getData(FILE_INST_AGE_TRIM, InstAgeTrimTab);
        CsvFileHelper.getData(FILE_INST_PARAM_REGIMES, InstParamRegimesTab);
        CsvFileHelper.getData(FILE_INST_PASS_POINTS_REGIMES, InstPassPointsRegimesTab);
        CsvFileHelper.getData(FILE_INST_SALAIRE_MIN_TRIM, InstSalaireMinTrimTab);
        CsvFileHelper.getData(FILE_INST_COEFF_REVALO, InstCoeffRevaloTab);

        Individu individu = new Individu(IndTab, InstAgeTrimTab);
        
        //création d'un tableau avec le nom des régimes déjà paramétrés et programmés MAJ dès qu'on programme un nouveau régime + MAJ de CreateRegimesTab dans Tools
        String [] InitialRegimesTab = {"agirc_arrco", "rci"};
        


        //on crée un tableau d'objet de classe DateDepart avec toutes les dates de retraite à calculer à partir de dateRetraiteTab
        int nbDate = 0;
        for (int i = 1; i < DateRetraiteTab.length; i++) {
            if (DateRetraiteTab[i][0] == null) break;
            if (DateRetraiteTab[i][0] != null) {                
                nbDate ++; 
            } 
        }
        DateDepart[] DateDepartTab = new DateDepart[nbDate];
        for (int i = 0; i < nbDate; i++) {
            LocalDate dateDep = Tools.dateFromString(DateRetraiteTab[i+1][1]);
            int trimRachat = Integer.parseInt(DateRetraiteTab[i+1][3]);
            Boolean retraiteProg = Integer.parseInt(DateRetraiteTab[i+1][4]) == 1;
            DateDepartTab[i] = new DateDepart(dateDep, trimRachat, retraiteProg, individu, AnnualDataTab);
        }
        
        //on crée un tableau d'objet de classe Regime avec tous les régimes à calculer à partir de CumulDroitsTab
            //détermination du nombre de régimes
        int nbReg = 0;
        for (int i =0; i < CumulDroitsTab[0].length; i++) {
            if (CumulDroitsTab[0][i] == null) break;
            nbReg = i;
        }
        System.out.println(" verif nb reg " + nbReg);
            //initialisation du tableau des regimes à calculer

        Regime[] RegimesTab = Tools.CreateRegimesTab(nbReg, InitialRegimesTab, CumulDroitsTab, InstParamRegimesTab );

        //pour chaque date de DateDepartTab : parcourir le tableau des régimes pour calcul des montants/nbPts/taux/surcote pour chaque regime
        String [][] Resultat = new String[nbReg * nbDate][15];
        int k = 0; //indice ligne
        for (int i = 0; i < nbDate; i++) {
            //TODO rajouter un if valeur nulle break
            for (int j = 0; j < nbReg; j++) {
                Resultat[k][0] = String.valueOf(DateDepartTab[i].GetDateDep());
                Resultat[k][1] = RegimesTab[j].nom;
                Resultat[k][2] = String.valueOf(RegimesTab[j].calculCumulPointsTrim(individu, CumulDroitsTab, DateDepartTab[i]));
                Resultat[k][3] = String.valueOf(RegimesTab[j].calculTaux(DateDepartTab[i]));
                Resultat[k][4] = String.valueOf(RegimesTab[j].calculSurcote(DateDepartTab[i]));
                Resultat[k][5] = String.valueOf(RegimesTab[j].calculSam(DateDepartTab[i], InstPassPointsRegimesTab, AnnualDataTab, InstCoeffRevaloTab));
                Resultat[k][6] = String.valueOf(RegimesTab[j].TrouverValeurPtRegime(InstPassPointsRegimesTab, DateDepartTab[i]));
                Resultat[k][7] = String.valueOf(RegimesTab[j].calculMajoEnfants(individu));
                Resultat[k][8] = String.valueOf(RegimesTab[j].calculAnnuelBrut(individu, DateDepartTab[i], InstPassPointsRegimesTab, CumulDroitsTab, AnnualDataTab, InstCoeffRevaloTab));
                Resultat[k][9] = String.valueOf(RegimesTab[j].GetTx_plvt_sociaux());
                Resultat[k][10] = String.valueOf(RegimesTab[j].calculAnnuelNet(individu, DateDepartTab[i], InstPassPointsRegimesTab, CumulDroitsTab, AnnualDataTab, InstCoeffRevaloTab));
                System.out.println("date " + DateDepartTab[i].GetDateDep() + " | " + RegimesTab[j].nom + " | retour sam " + Resultat[k][5]);
                k++;
            }
        }
        
        //tableau avec les en-tetes de colonne du tableau de résultat
        String[] headTabResult = {"Date Départ", "Régime", "Trim/Points", "Taux", "Surcote", "SAM", "Valeur Point", "Majoration familiale",  "Annuel Brut", "Prélèvements", "Annuel Net"}; 
        CsvFileHelper.writeData("C:\\Users\\audre\\Desktop\\Retraite\\Projet_Outil_Calcul_Retraite\\Output\\resultatsTest.csv", headTabResult, Resultat);
        
        //TODO sortir un fichier CSV avec les salaires revalorisés 
        //TODO sortir le nom d'affichage du régime dans le fichier resultat et non le nom du régime
        
       
    }

}