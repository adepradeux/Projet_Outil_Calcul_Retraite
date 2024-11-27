public class Data {
    private final String[][] IndTab;          
    private final String[][] AnnualDataTab;
    private final String[][] CumulDroitsTab; 
    private final String[][] DateRetraiteTab;
    
    //données institutionnelles
    private final String[][] InstAgeTrimTab;           
    private final String[][] InstParamRegimesTab;
    private final String[][] InstPassPointsRegimesTab;
    private final String[][] InstSalaireMinTrimTab;
    private final String[][] InstCoeffRevaloTab;
    private final String[][] InstCoeffArrcoVu;

    //CONSTRUCTEUR
    public Data (String[][] IndTab, String[][] AnnualDataTab, String[][] CumulDroitsTab, String[][] DateRetraiteTab, String[][] InstAgeTrimTab, 
    String[][] InstParamRegimesTab, String[][] InstPassPointsRegimesTab, String[][] InstSalaireMinTrimTab, String[][] InstCoeffRevaloTab, String[][] InstCoeffArrcoVu) throws Exception {
        this.IndTab = IndTab;
        this.AnnualDataTab = AnnualDataTab;
        this.CumulDroitsTab = CumulDroitsTab;
        this.DateRetraiteTab = DateRetraiteTab;
        this.InstAgeTrimTab = InstAgeTrimTab;
        this.InstParamRegimesTab = InstParamRegimesTab;
        this.InstPassPointsRegimesTab = InstPassPointsRegimesTab;
        this.InstSalaireMinTrimTab = InstSalaireMinTrimTab;
        this.InstCoeffRevaloTab = InstCoeffRevaloTab;
        this.InstCoeffArrcoVu = InstCoeffArrcoVu;
    }

    //GETTER

    public String[][] GetIndTab () {
        return IndTab;
    }

    public String[][] GetAnnualDataTab () {
        return AnnualDataTab;
    }

    public String[][] GetCumulDroitsTab () {
        return CumulDroitsTab;
    }

    public String[][] GetDateRetraiteTab () {
        return DateRetraiteTab;
    }

    public String[][] GetInstAgeTrimTab () {
        return InstAgeTrimTab;
    }

    public String[][] GetInstParamRegimesTab () {
        return InstParamRegimesTab;
    }

    public String[][] GetInstPassPointsRegimesTab() {
        return InstPassPointsRegimesTab;
    }

    public String[][] GetInstInstSalaireMinTrimTab() {
        return InstSalaireMinTrimTab;
    }

    public String[][] GetInstCoeffRevaloTab() {
        return InstCoeffRevaloTab;
    }

    public String[][] GetInstCoeffArrcoVu() {
        return InstCoeffArrcoVu;
    }
}
