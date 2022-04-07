package LABA_2;

import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Catalog for metrics
 * Every metric corresponds some CPU model
 */
@Slf4j
public class Catalog {
    // read catalog
    public static ArrayList<String> read(String path) {
        log.debug("Create Catalog");
        // init dynamic array
        ArrayList<String> catalog = new ArrayList<>();

        try {
            File file = new File(path);
            // read file class
            FileReader fr = new FileReader(file);
            // every line read
            BufferedReader reader = new BufferedReader(fr);
            // read all lines
            String line = reader.readLine();
            while (line != null) {
                catalog.add(line);
                line = reader.readLine();
            }
            // close fd
            fr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return catalog;
    }
}
