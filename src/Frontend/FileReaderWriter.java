package Frontend;

import java.io.*;

/**
 * Esta clase facilita el proceso de serialización y deserialización de los
 * achivos de tinyRust+. Tiene funcionalidades adicionales para extraer el
 * nombre del archivo.
 *
 * @author Juan Martín Morales
 */
public class FileReaderWriter {
    public static BufferedReader read(String path) throws FileNotFoundException {;
        BufferedReader br;
        try{
            br = new BufferedReader(new FileReader(path));
            return br;
        }
        catch (FileNotFoundException e){
            throw e;
        }
        catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public static void write(String stringData, String fileName) {

        File file = new File(fileName);

        try (FileWriter fileWriter = new FileWriter(file);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            bufferedWriter.write(stringData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFileName(String path) {
        File file = new File(path);
        return file.getName();
    }

    public static String getFileNameWithoutExt(String path) {
        File file = new File(path);
        String fileName = file.getName();
        int extIndex = fileName.lastIndexOf('.');
        if (extIndex > 0) {
            fileName = fileName.substring(0, extIndex);
        }
        return fileName;
    }


}
