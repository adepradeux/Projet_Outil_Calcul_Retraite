
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
        String FILE_INST_COEFF_ARRCO_VU = "/Input_data_institutionnelles/Input_coeff_arrco_vu.csv";

        
        //initialisation des tableaux pour importer les données d'entrée client
        String[][] IndTab = new String[10][2];           //TODO voir si on peut faire autrement que prévoir la taille du tableau
        String[][] AnnualDataTab = new String[300][7];
        String[][] CumulDroitsTab = new String[300][20]; 
        String[][] DateRetraiteTab = new String[20][5];
        
        //initialisation des tableaux pour importer les données institutionnelles
        String[][] InstAgeTrimTab = new String[150][6];           
        String[][] InstParamRegimesTab = new String[50][8];
        String[][] InstPassPointsRegimesTab = new String[150][50];
        String[][] InstSalaireMinTrimTab = new String[150][2];
        String[][] InstCoeffRevaloTab = new String[150][20];
        String[][] InstCoeffArrcoVu = new String[150][2];

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
        CsvFileHelper.getData(FILE_INST_COEFF_ARRCO_VU, InstCoeffArrcoVu);

        //initialisation des instances Data et Individu
        Data data = new Data(IndTab, AnnualDataTab, CumulDroitsTab, DateRetraiteTab, InstAgeTrimTab, InstParamRegimesTab, InstPassPointsRegimesTab,
         InstSalaireMinTrimTab, InstCoeffRevaloTab, InstCoeffArrcoVu);
        Individu individu = new Individu(data);
        
        //SI CREATION NOUVEAU REGIME : MAJ de CreateRegimesTab dans Tools
             

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

        Regime[] RegimesTab = Tools.CreateRegimesTab(nbReg, data);

        //pour chaque date de DateDepartTab : parcourir le tableau des régimes pour calcul des montants/nbPts/taux/surcote pour chaque regime
        String [][] Resultat = new String[nbReg * nbDate][15];
        int k = 0; //indice ligne
        for (int i = 0; i < nbDate; i++) {
            for (int j = 0; j < nbReg; j++) {
                //on décale la date de depart au 1er jour trim civil suivant si le régime impose un départ au 1er jour du trimestre civil
                DateDepart DateDepartCalcul;
                if (RegimesTab[j].GetDepTrimCivil() == 0) {
                    DateDepartCalcul = DateDepartTab[i];
                }
                else {
                    DateDepartCalcul = Tools.DecalerTrimCivil(DateDepartTab[i], individu, AnnualDataTab);
                }
                //on adapte l'affichage si le versement est un capital unique (et non un montant mensuel)
                if (RegimesTab[j].estVersementUnique(individu, data, DateDepartCalcul)) {
                    String affichageVersementUnique = "Versement net unique de ";
                    Resultat[k][0] = String.valueOf(DateDepartCalcul.GetDateDep()); 
                    Resultat[k][1] = RegimesTab[j].GetNomOutput();
                    Resultat[k][2] = String.valueOf(RegimesTab[j].calculCumulPointsTrim(individu, CumulDroitsTab, DateDepartCalcul));
                    Resultat[k][3] = String.valueOf(RegimesTab[j].calculTaux(DateDepartCalcul));
                    Resultat[k][4] = String.valueOf(RegimesTab[j].calculSurcote(DateDepartCalcul));
                    Resultat[k][5] = String.valueOf(RegimesTab[j].calculSam(DateDepartTab[i], data));
                    //Resultat[k][5] = String.valueOf(RegimesTab[j].calculSam(DateDepartTab[i], data));
                    Resultat[k][6] = String.valueOf(RegimesTab[j].TrouverValeurPtRegime(InstPassPointsRegimesTab, DateDepartCalcul));
                    Resultat[k][7] = String.valueOf(RegimesTab[j].calculMajoEnfants(individu));
                    Resultat[k][8] = "";
                    Resultat[k][9] = "";
                    Resultat[k][10] = affichageVersementUnique.concat(String.valueOf(RegimesTab[j].calculAnnuelNet(individu, DateDepartCalcul, data))).concat(" €");
                }
                else {
                    Resultat[k][0] = String.valueOf(DateDepartCalcul.GetDateDep()); 
                    Resultat[k][1] = RegimesTab[j].GetNomOutput();
                    Resultat[k][2] = String.valueOf(RegimesTab[j].calculCumulPointsTrim(individu, CumulDroitsTab, DateDepartCalcul));
                    Resultat[k][3] = String.valueOf(RegimesTab[j].calculTaux(DateDepartCalcul));
                    Resultat[k][4] = String.valueOf(RegimesTab[j].calculSurcote(DateDepartCalcul));
                    Resultat[k][5] = String.valueOf(RegimesTab[j].calculSam(DateDepartTab[i], data));
                    Resultat[k][6] = String.valueOf(RegimesTab[j].TrouverValeurPtRegime(InstPassPointsRegimesTab, DateDepartCalcul));
                    Resultat[k][7] = String.valueOf(RegimesTab[j].calculMajoEnfants(individu));
                    Resultat[k][8] = String.valueOf(RegimesTab[j].calculAnnuelBrut(individu, DateDepartCalcul, data));
                    Resultat[k][9] = String.valueOf(RegimesTab[j].GetTx_plvt_sociaux());
                    Resultat[k][10] = String.valueOf(RegimesTab[j].calculAnnuelNet(individu, DateDepartCalcul, data));
                }
                // TEST System.out.println("date " + DateDepartTab[i].GetDateDep() + " | " + RegimesTab[j].nom + " | retour sam " + Resultat[k][5]);
                k++;
            }
        }
        
        //tableau avec les en-tetes de colonne du tableau de résultat
        String[] headTabResult = {"Date Départ", "Régime", "Trim/Points", "Taux", "Surcote", "SAM", "Valeur Point", "Majoration familiale",  "Annuel Brut", "Prélèvements", "Annuel Net"}; 
        CsvFileHelper.writeData("C:\\Users\\audre\\Desktop\\Retraite\\Projet_Outil_Calcul_Retraite\\Output\\resultatsTest.csv", headTabResult, Resultat);
        
        //TODO sortir un fichier CSV avec les salaires revalorisés 
        //TODO finir test 2
        
       
    }

}