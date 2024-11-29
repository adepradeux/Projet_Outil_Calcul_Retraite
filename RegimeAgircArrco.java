


public class RegimeAgircArrco extends RegimePoints {
   


    //CONSTRUCTEUR
    public RegimeAgircArrco(String nom, String nomOutput, Data data) throws Exception {
        super(nom, nomOutput, data);
    }


    //METHODES

    //calcul taux de calcul avec decote : 1% pour les trim manquants de 1 à 12, puis 1,25% pour trim manquant à partir de 13 - decote 22% max
    @Override
    public float calculTaux (DateDepart dateDep) {
        float decoteP1 = Math.min(dateDep.GetTrimManquant(), (float)12)  * this.GetTx_decote_1();
        float decoteP2 = Math.max((float)0, dateDep.GetTrimManquant() - (float)12) * this.GetTx_decote_2();
        float decote = Math.min((float)0.22, decoteP1 + decoteP2);
        float result = 1 - Math.round(decote * 10000) / (float)10000;
        return result;
    }



     //Méthode déterminer le versement se fait en capital unique (si montant inf à un certain seuil)
     @Override
     public Boolean estVersementUnique (Individu individu, Data data, DateDepart dateDep) {
         Boolean result = false;
         float cumulPoints = 0;
         float seuilMontant = 0;
         float montant = 0;
         try {
             cumulPoints = calculCumulPointsTrim(individu, data, dateDep);
             float ValPt = TrouverValeurPtRegime(data.GetInstPassPointsRegimesTab(), dateDep);
             seuilMontant = this.GetSeuilPointCapUnique() * ValPt;  //montant de la pension pour 100 points
             montant = ValPt * cumulPoints * this.calculTaux(dateDep) * (1 + this.calculSurcote(dateDep)) + montantMajoEnfants(individu, dateDep, data);  //montant de la pension
         } catch (Exception ex) {
         }
         if (montant < seuilMontant) {
             result = true;
         }
         return result;
     }


    //Méthode pour calcul du montant annuel brut
    @Override
    public int calculAnnuelBrut (Individu individu, DateDepart dateDep, Data data) throws Exception {
        int result = 0;
        float ValPt = TrouverValeurPtRegime(data.GetInstPassPointsRegimesTab(), dateDep);
        float montant = ValPt * calculCumulPointsTrim(individu, data, dateDep) * this.calculTaux(dateDep) * (1 + this.calculSurcote(dateDep)) + montantMajoEnfants(individu, dateDep, data);
        if (estVersementUnique(individu, data, dateDep)) {
            // versement unique = montant calculé * coeff spécifique Versement Unique
            String ageDepAnnee = String.valueOf(dateDep.GetAgeDep().ageAnnee); //ex : si age de 63 ans et 5 mois, ageDepAnnee = 63
            int indLigne = Tools.TrouverIndiceLigne(data.GetInstCoeffArrcoVu(), ageDepAnnee);
            float coeffVersementUnique = Float.parseFloat(data.GetInstCoeffArrcoVu()[indLigne][1]);
            float montantVersement = montant * coeffVersementUnique;
            result = Math.round(montantVersement);
        }
        else {
            result = Math.round(montant);
        }
        return result;
    }

    //méthode pour calculer le montant de la majoration pour enfants
    public float montantMajoEnfants (Individu individu, DateDepart dateDep, Data data) throws Exception {
        //calcul des points après 2012 pour calcul majo
        int indCol = Tools.TrouverIndiceColonne(data.GetCumulDroitsTab(), this.GetNom());
        float pointsAvant2012 = Float.parseFloat(data.GetCumulDroitsTab()[3][indCol]);
        float pointsTotal = this.calculCumulPointsTrim(individu, data, dateDep);
        float pointsMajoEnfant = pointsTotal - pointsAvant2012;
        //calcul du montant de la majo sur les points après 2012
        float ValPt = TrouverValeurPtRegime(data.GetInstPassPointsRegimesTab(), dateDep);
        float montantMajoEnfant = ValPt * pointsMajoEnfant * this.calculTaux(dateDep) * (1 + this.calculSurcote(dateDep)) * this.calculMajoEnfants(individu);
        float plafondMajo = Tools.TrouverDonneeRegime(this.GetNom(), data.GetInstParamRegimesTab(), "plafond_majo_enfant");
        float result = Math.min(montantMajoEnfant, plafondMajo);
        return result;

    }



   
}