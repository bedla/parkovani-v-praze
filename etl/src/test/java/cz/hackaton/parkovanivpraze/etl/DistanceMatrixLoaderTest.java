package cz.hackaton.parkovanivpraze.etl;

import cz.hackaton.parkovanivpraze.Utils;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.*;

public class DistanceMatrixLoaderTest {

    private DistanceMatrixLoader loader;

    @Before
    public void setUp() throws Exception {
        loader = new DistanceMatrixLoader(Utils.file("response-dump.log",
                DistanceMatrixLoaderTest.class.getClassLoader()),
                URI.create("http://localhost:7474/db/data/"));

    }

    @Test
    public void testName() throws Exception {
        loader.load();
    }
}