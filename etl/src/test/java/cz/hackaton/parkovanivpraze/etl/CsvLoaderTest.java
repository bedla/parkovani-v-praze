package cz.hackaton.parkovanivpraze.etl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class CsvLoaderTest {

    private GraphDatabaseService graphDb;

    @Before
    public void prepareTestDatabase()
    {
        graphDb = new TestGraphDatabaseFactory().newImpermanentDatabase();
    }

    public void testName() throws Exception {

        CsvLoader csvLoader = new CsvLoader(file("shp-load.csv"), URI.create("http://localhost:7474/db/data/"), ";");
        csvLoader.load();

    }

    private static File file(String fileName) {
        try {
            return new File(CsvLoaderTest.class.getClassLoader().getResource(fileName).toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @After
    public void destroyTestDatabase()
    {
        graphDb.shutdown();
    }
}