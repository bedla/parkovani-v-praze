package cz.hackaton.parkovanivpraze.etl;

import cz.hackaton.parkovanivpraze.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

public class CsvLoaderTest {

    private CsvLoader csvLoader;

    @Before
    public void prepareTestDatabase() {
        csvLoader = new CsvLoader(Utils.file("shp-load.csv", CsvLoaderTest.class.getClassLoader()),
                URI.create("http://localhost:7474/db/data/"), ",", "Automat");
        csvLoader.clearDb();
    }

    @Test
    public void testName() throws Exception {
        csvLoader.load();
        int countNodes = csvLoader.countNodes();
        System.out.println(countNodes);
    }

    @After
    public void destroyTestDatabase() {
        csvLoader.clearDb();
    }

}