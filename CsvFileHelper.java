import java.awt.Frame;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvFileHelper {
    
    //méthode pour obtenir le chemin d'accès au fichier d'après son nom
    public static String getResourcePath(String fileName) {
       final File f = new File("");
       final String dossierPath = f.getAbsolutePath() + File.separator + fileName;
       return dossierPath;
    }

   //méthode pour retourner le fichier d'après son nom
   public static File getResource(String fileName) {
       final String completeFileName = getResourcePath(fileName);
       File file = new File(completeFileName);
       return file;
   }

   //méthode pour importer chaque ligne du CSV dans une liste
   public static List<String> readFile(File file) {
        List<String> result = new ArrayList<String>();
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
    
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                result.add(line);
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

   //méthode pour importer les données du csv dans un tableau 
   public static String[][] getData(String FILE_NAME, String[][] tab){
        final File file = CsvFileHelper.getResource(FILE_NAME);
        List<String> fileDataList = CsvFileHelper.readFile(file); //on importe les données ligne à ligne dans une liste
        
        int longList = fileDataList.size();
        for (int i = 0; i < longList ; i ++) {
            String[] TabTmp = fileDataList.get(i).split(";");
            tab[i] = TabTmp;
            //System.out.println(tab[i][1]);
        }
    return tab;
   } 

   //méthode pour écrire dans un csv
   public static void writeData(String FILE_NAME, String[] columnHeader, String [][] tab) {
       try {
			// Création d'un fileWriter pour écrire dans un fichier
			FileOutputStream file = new FileOutputStream(FILE_NAME);
            OutputStreamWriter fileWriter = new OutputStreamWriter(file, StandardCharsets.UTF_8);
            //FileWriter fileWriter = new FileWriter(FILE_NAME, false);

			// Création d'un bufferedWriter qui utilise le fileWriter
			BufferedWriter writer = new BufferedWriter(fileWriter);

           // ajout des en-tetes de colonnes
           for (String columnHeader1 : columnHeader) {
               writer.write(columnHeader1 + ";");
           }
            writer.newLine();

            for (int i = 0; i < tab.length; i++) {
                for (int j = 0; j < tab[i].length; j++) {
                    writer.write(tab[i][j] + ";");
                }
                writer.newLine();
            }

            

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
       
   }
   

}
