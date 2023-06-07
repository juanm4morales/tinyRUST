package Frontend;

import java.io.*;

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
