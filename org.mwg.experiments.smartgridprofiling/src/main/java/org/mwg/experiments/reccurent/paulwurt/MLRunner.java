package org.mwg.experiments.reccurent.paulwurt;

import java.util.Random;

/**
 * Created by assaad on 05/12/2016.
 */
public class MLRunner {
    public static void main(String[] arg) {
        String path = "/Users/assaad/work/github/data/paulwurt/casts.csv";
        Random random = new Random(1234);
        try {

            PaulWurtImporter pw = new PaulWurtImporter(path, 60, 30, random);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
