package cz.hackaton.parkovanivpraze.etl;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

@Ignore
public class CsvLoaderTest {

    private CsvLoader csvLoader;

    @Before
    public void prepareTestDatabase() {
        csvLoader = new CsvLoader(file("shp-load.csv"), URI.create("http://localhost:7474/db/data/"), ";", "Automat");
        csvLoader.clearDb();
    }

    public void testName() throws Exception {
        csvLoader.load();
        int countNodes = csvLoader.countNodes();
        System.out.println(countNodes);
    }

    @After
    public void destroyTestDatabase() {
        csvLoader.clearDb();
    }

    private static File file(String fileName) {
        try {
            return new File(CsvLoaderTest.class.getClassLoader().getResource(fileName).toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}