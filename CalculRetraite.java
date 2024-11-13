
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

        //imporation des données d'entrée
        IndTab = CsvFileHelper.getData(FILE_IND, IndTab);
        CsvFileHelper.getData(FILE_ANNUAL_DATA, AnnualDataTab);
        CsvFileHelper.getData(FILE_CUMUL_DROITS, CumulDroitsTab);
        CsvFileHelper.getData(FILE_DATE_RETRAITE, DateRetraiteTab);
        CsvFileHelper.getData(FILE_INST_AGE_TRIM, InstAgeTrimTab);
        CsvFileHelper.getData(FILE_INST_PARAM_REGIMES, InstParamRegimesTab);
        CsvFileHelper.getData(FILE_INST_PASS_POINTS_REGIMES, InstPassPointsRegimesTab);
        CsvFileHelper.getData(FILE_INST_SALAIRE_MIN_TRIM, InstSalaireMinTrimTab);

        Individu individu = new Individu(IndTab, InstAgeTrimTab);
        
        //création d'un tableau avec le nom des régimes déjà paramétrés et programmés MAJ dès qu'on programme un nouveau régime + MAJ de CreateRegimesTab dans Tools
        String [] InitialRegimesTab = {"agirc_arrco", "rci"};
        


        //on crée un tableau d'objet de classe DateDepart avec toutes les dates de retraite à calculer à partir de dateRetraiteTab
        int nbDate = 0;
        for (int i = 1; i < DateRetraiteTab.length; i++) {
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
        Regime[] RegimesTab = new Regime[nbReg];
        Tools.CreateRegimesTab(RegimesTab, InitialRegimesTab, CumulDroitsTab, InstParamRegimesTab );

        //pour chaque date de DateDepartTab : parcourir le tableau des régimes pour calcul des montants/nbPts/taux/surcote pour chaque regime
        String [][] Resultat = new String[nbReg * nbDate][10];
        int k = 0; //indice ligne
        for (int i = 0; i < nbDate; i++) {
            for (int j = 0; j < nbReg; j++) {
                Resultat[k][0] = String.valueOf(DateDepartTab[i].GetDateDep());
                Resultat[k][1] = RegimesTab[j].nom;
                Resultat[k][2] = String.valueOf(RegimesTab[j].calculCumulPointsTrim(CumulDroitsTab, DateDepartTab[i]));
                Resultat[k][3] = String.valueOf(RegimesTab[j].calculTaux(DateDepartTab[i]));
                Resultat[k][4] = String.valueOf(RegimesTab[j].calculSurcote(DateDepartTab[i]));
                Resultat[k][5] = String.valueOf(RegimesTab[j].calculMajoEnfants(individu));
                Resultat[k][6] = String.valueOf(RegimesTab[j].calculAnnuelBrut(individu, DateDepartTab[i], InstPassPointsRegimesTab, CumulDroitsTab));
                Resultat[k][7] = String.valueOf(RegimesTab[j].GetTx_plvt_sociaux());
                Resultat[k][8] = String.valueOf(RegimesTab[j].calculAnnuelNet(individu, DateDepartTab[i], InstPassPointsRegimesTab, CumulDroitsTab));
                k++;
            }
        }

        //TODO écrire les résultats dans un fichier CSV + programmer les autres régimes dont RG

        
        System.out.println(" verif resultat " + Resultat[0][0] + "  " + Resultat[0][1] + "  " + Resultat[0][2] + "  " + Resultat[0][3] + "  " + Resultat[0][4] + "  " + Resultat[0][5] + "  " + Resultat[0][6] + "  " + Resultat[0][7] + "  " + Resultat[0][8]);
        System.out.println(" verif resultat " + Resultat[1][0] + "  " + Resultat[1][1] + "  " + Resultat[1][2] + "  " + Resultat[1][3] + "  " + Resultat[1][4] + "  " + Resultat[1][5] + "  " + Resultat[1][6] + "  " + Resultat[1][7] + "  " + Resultat[1][8]);

        //RegimeRCI regTest = new RegimeRCI("rci", InstParamRegimesTab);
        /*System.out.println(" date depart testée " + DateDepartTab[0].GetDateDep());
        System.out.println(" verif cumul trim " + DateDepartTab[0].GetCumulTrim());
        System.out.println(" nb trim manquant " + DateDepartTab[0].GetTrimManquant());
        System.out.println(" annuel brut " + RegimesTab[2].calculAnnuelBrut(individu, DateDepartTab[0], InstPassPointsRegimesTab, CumulDroitsTab));*/
        
       
    }

}