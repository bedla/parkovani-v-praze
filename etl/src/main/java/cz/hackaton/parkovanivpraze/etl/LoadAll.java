package cz.hackaton.parkovanivpraze.etl;

import cz.hackaton.parkovanivpraze.Utils;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.nio.file.Files;

/**
 * @author ivo.smid
 * @version $Revision:$
 */
public class LoadAll {

    public static void main(String[] args) {
        URI neo4jServerUri = URI.create("http://localhost:7474/db/data/");
        CsvLoader csvLoader = new CsvLoader(new File(".", "distance-matrix/data/vz_7.csv"), neo4jServerUri, ",", "Automat");
        csvLoader.clearDb();
        csvLoader.load();
        System.out.println(csvLoader.countNodes());

        File[] files = new File(".", "distance-matrix/data/dump").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("response-dump-") && name.endsWith(".log");
            }
        });

        for (File file : files) {
            DistanceMatrixLoader distanceLoader = new DistanceMatrixLoader(file, neo4jServerUri, "Automat");
            distanceLoader.load();
        }


    }

}
