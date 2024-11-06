
public class CalculRetraite {
    public static void main(String[] args) {

        //initialisation des fichiers avec les données d'entrée
        String FILE_IND = "/Input_client/InputIndividu.csv";
        String FILE_ANNUAL_DATA = "/Input_client/InputAnnualData.csv";
        String FILE_CUMUL_POINTS = "/Input_client/InputCumulPoints.csv";
        String FILE_DATE_RETRAITE = "/Input_client/InputDateRetraite.csv";
        
        //initialisation des tableaux pour importer les données d'entrée
        String[][] IndTab = new String[6][2];           //TODO voir si on peut faire autrement que prévoir la taille du tableau
        String[][] AnnualDataTab = new String[300][7];
        String[][] CumulPointsTab = new String[50][4];
        String[][] DateRetraiteTab = new String[20][5];

        //imporation des données d'entrée
        IndTab = CsvFileHelper.getData(FILE_IND, IndTab);
        AnnualDataTab = CsvFileHelper.getData(FILE_ANNUAL_DATA, AnnualDataTab);
        CumulPointsTab = CsvFileHelper.getData(FILE_CUMUL_POINTS, CumulPointsTab);
        DateRetraiteTab = CsvFileHelper.getData(FILE_DATE_RETRAITE, DateRetraiteTab);
        // TEST System.out.println(" test valeur " + AnnualDataTab[18][0]);

        Individu individu = new Individu();
        individu.dateNaissance = Tools.dateFromString(IndTab[0][1]);
        
        System.out.println(" test ddn  " + individu.dateNaissance);
        
       
    }

}