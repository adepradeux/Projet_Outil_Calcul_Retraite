
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
            Boolean retraiteProg = Integer.parseInt(DateRetraiteTab[i+1][2]) == 1;
            String nomDate = DateRetraiteTab[i+1][0];
            DateDepartTab[i] = new DateDepart(nomDate, dateDep, trimRachat, retraiteProg, individu, AnnualDataTab);
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

        //REMPLISSAGE TABLEAUX POUR OUTPUT
        //pour chaque date de DateDepartTab : parcourir le tableau des régimes pour calcul des montants/nbPts/taux/surcote pour chaque regime
        String [][] Resultat = new String[(nbReg + 3) * nbDate][11];  //initialisation du tableau de résultat
        String [][] ResultatCumulGains = new String[12][nbDate + 1];    //initialisation du tableau des gains
        int k = 0; //indice ligne
        int indCol = 1;  //indice colonne pour tableau des gains
        for (int i = 0; i < nbDate; i++) {
            int totalAnnuelNet = 0;
            int totalMensuelNet = 0;
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
                    Resultat[k][2] = String.valueOf(RegimesTab[j].calculCumulPointsTrim(individu, data, DateDepartCalcul));
                    Resultat[k][3] = String.valueOf(RegimesTab[j].calculTaux(DateDepartCalcul));
                    Resultat[k][4] = String.valueOf(RegimesTab[j].calculSurcote(DateDepartCalcul));
                    Resultat[k][5] = String.valueOf(RegimesTab[j].calculSam(DateDepartTab[i], data));
                    Resultat[k][6] = String.valueOf(RegimesTab[j].TrouverValeurPtRegime(InstPassPointsRegimesTab, DateDepartCalcul));
                    Resultat[k][7] = String.valueOf(RegimesTab[j].calculMajoEnfants(individu));
                    Resultat[k][8] = "";
                    Resultat[k][9] = "";
                    Resultat[k][10] = affichageVersementUnique.concat(String.valueOf(RegimesTab[j].calculAnnuelNet(individu, DateDepartCalcul, data))).concat(" €");
                }
                else {
                    Resultat[k][0] = String.valueOf(DateDepartCalcul.GetDateDep()); 
                    Resultat[k][1] = RegimesTab[j].GetNomOutput();
                    Resultat[k][2] = String.valueOf(RegimesTab[j].calculCumulPointsTrim(individu, data, DateDepartCalcul));
                    Resultat[k][3] = String.valueOf(RegimesTab[j].calculTaux(DateDepartCalcul));
                    Resultat[k][4] = String.valueOf(RegimesTab[j].calculSurcote(DateDepartCalcul));
                    Resultat[k][5] = String.valueOf(RegimesTab[j].calculSam(DateDepartTab[i], data));
                    Resultat[k][6] = String.valueOf(RegimesTab[j].TrouverValeurPtRegime(InstPassPointsRegimesTab, DateDepartCalcul));
                    Resultat[k][7] = String.valueOf(RegimesTab[j].calculMajoEnfants(individu));
                    Resultat[k][8] = String.valueOf(RegimesTab[j].calculAnnuelBrut(individu, DateDepartCalcul, data));
                    Resultat[k][9] = String.valueOf(RegimesTab[j].GetTx_plvt_sociaux());
                    Resultat[k][10] = String.valueOf(RegimesTab[j].calculAnnuelNet(individu, DateDepartCalcul, data));
                    totalAnnuelNet = totalAnnuelNet + RegimesTab[j].calculAnnuelNet(individu, DateDepartCalcul, data);
                }
                
                totalMensuelNet = totalAnnuelNet / (int)12;
                k++;
            }
            //LIGNE avec le total annuel
            Resultat[k][0] = "";
            Resultat[k][1] = "";
            Resultat[k][2] = "";
            Resultat[k][3] = "";
            Resultat[k][4] = "";
            Resultat[k][5] ="";
            Resultat[k][6] = "";
            Resultat[k][7] = "";
            Resultat[k][8] = "";
            Resultat[k][9] = "TOTAL ";
            Resultat[k][10] = String.valueOf(totalAnnuelNet);
            k++;
            //LIGNE avec le total mensuel
            Resultat[k][0] = "";
            Resultat[k][1] = "";
            Resultat[k][2] = "";
            Resultat[k][3] = "";
            Resultat[k][4] = "";
            Resultat[k][5] ="";
            Resultat[k][6] = "";
            Resultat[k][7] = "";
            Resultat[k][8] = "";
            Resultat[k][9] = "Mensuel Net ";
            Resultat[k][10] = String.valueOf(totalMensuelNet);
            k++;
            //LIGNE VIDE
            Resultat[k][0] = "";
            Resultat[k][1] = "";
            Resultat[k][2] = "";
            Resultat[k][3] = "";
            Resultat[k][4] = "";
            Resultat[k][5] ="";
            Resultat[k][6] = "";
            Resultat[k][7] = "";
            Resultat[k][8] = "";
            Resultat[k][9] = "";
            Resultat[k][10] = "";
            k++;

        
            //REMPLISSAGE TABLEAU Cumuls des gains (en-tete dans la 1ere colonne)
            
                //détermination date avec rachat ou non
            String affichageRachat;
            if (DateDepartTab[i].GetTrimRachat() > 0) {
                affichageRachat = "Avec rachat";
            }
            else {
                affichageRachat = "Sans rachat";
            }

                //détermination date taux plein ou taux minoré
            String affichageTaux;
            if (DateDepartTab[i].GetTrimManquant() > 0) {
                affichageTaux = "Taux minoré - cumul limité";
            }
            else {
                affichageTaux = "TAUX PLEIN - cumul libre";
            }


                //calcul gain mensuel
            int gainMensuel;
            int gainMensuelCumul;
            if (indCol == 1) {
                gainMensuel = 0;
            }
            else {
                gainMensuel = Math.round(totalMensuelNet - Float.parseFloat(ResultatCumulGains[4][indCol - 1]));
            }
                //calcul cumul gain mensuel
            if (indCol == 1) {
                gainMensuelCumul = 0;
            }
            else {
                gainMensuelCumul = Math.round(totalMensuelNet - Float.parseFloat(ResultatCumulGains[4][1]));
            }
                //calcul gains cumulés
            Age age65 = new Age(65, 0);
            Age age70 = new Age(70, 0);
            Age age75 = new Age(75, 0);
            Age age80 = new Age(80, 0);
            Age age85 = new Age(85, 0);
            int gain65 = Math.max(0, age65.duree - DateDepartTab[i].GetAgeDep().duree) * totalMensuelNet ;
            int gain70 = Math.max(0, age70.duree - DateDepartTab[i].GetAgeDep().duree) * totalMensuelNet ;
            int gain75 = Math.max(0, age75.duree - DateDepartTab[i].GetAgeDep().duree) * totalMensuelNet ;
            int gain80 = Math.max(0, age80.duree - DateDepartTab[i].GetAgeDep().duree) * totalMensuelNet ;
            int gain85 = Math.max(0, age85.duree - DateDepartTab[i].GetAgeDep().duree) * totalMensuelNet ;

            ResultatCumulGains[0][indCol] = affichageRachat;
            ResultatCumulGains[1][indCol] = affichageTaux;
            ResultatCumulGains[2][indCol] = String.valueOf(DateDepartTab[i].GetDateDep()); //ligne avec les dates de départ
            ResultatCumulGains[3][indCol] = Tools.getStringOfAge(DateDepartTab[i].GetAgeDep()); //ligne avec l'age
            ResultatCumulGains[4][indCol] = String.valueOf(totalMensuelNet);
            ResultatCumulGains[5][indCol] = "'+".concat(String.valueOf(gainMensuel)).concat(" €");
            ResultatCumulGains[6][indCol] = "'+".concat(String.valueOf(gainMensuelCumul)).concat(" €");
            ResultatCumulGains[7][indCol] = String.valueOf(gain65).concat(" €");
            ResultatCumulGains[8][indCol] = String.valueOf(gain70).concat(" €");
            ResultatCumulGains[9][indCol] = String.valueOf(gain75).concat(" €");
            ResultatCumulGains[10][indCol] = String.valueOf(gain80).concat(" €");
            ResultatCumulGains[11][indCol] = String.valueOf(gain85).concat(" €");
            indCol++;
            
        }
        
        //OUTPUT Résultats avec les montants de retraite
        String[] headTabResult = {"Date Départ", "Régime", "Trim/Points", "Taux", "Surcote", "SAM", "Valeur Point", "Majoration familiale",  "Annuel Brut", "Prélèvements", "Annuel Net"}; 
        CsvFileHelper.writeData("C:\\Users\\audre\\Desktop\\Retraite\\Projet_Outil_Calcul_Retraite\\Output\\resultatsTest.csv", headTabResult, Resultat);
        
               
        //OUTPUT salaires et salaires revalorisés année par année
        //création du tableau pour la première date de départ
        String [][] ResultatSalaires = Tools.CreerTabSalaireOuput(DateDepartTab[0], data, RegimesTab);
        String[] headTabResultSalaire = {"Année", "Salaire €", "Coefficient de revalorisation","Salaire revalorisé pour SAM"}; 
        CsvFileHelper.writeData("C:\\Users\\audre\\Desktop\\Retraite\\Projet_Outil_Calcul_Retraite\\Output\\resultatsSalaires.csv", headTabResultSalaire, ResultatSalaires);

        //OUTPUT Recap montants et cumul des gains
            //en-tete des lignes
        ResultatCumulGains[2][0] = "Date Retraite";
        ResultatCumulGains[3][0] = "Age";
        ResultatCumulGains[4][0] = "Mensuel net";
        ResultatCumulGains[5][0] = "Gain mensuel";
        ResultatCumulGains[6][0] = "Cumul";
        ResultatCumulGains[7][0] = "Cumul gains à 65 ans";
        ResultatCumulGains[8][0] = "Cumul gains à 70 ans";
        ResultatCumulGains[9][0] = "Cumul gains à 75 ans";
        ResultatCumulGains[10][0] = "Cumul gains à 80 ans";
        ResultatCumulGains[11][0] = "Cumul gains à 85 ans";
        String[] headTabResultGains = {""}; 
        CsvFileHelper.writeData("C:\\Users\\audre\\Desktop\\Retraite\\Projet_Outil_Calcul_Retraite\\Output\\resultatsTableauGains.csv", headTabResultGains, ResultatCumulGains);



        //TODO retraite anticipée pour carrière longue
        
       
    }

}