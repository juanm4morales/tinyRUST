package Frontend;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
}
