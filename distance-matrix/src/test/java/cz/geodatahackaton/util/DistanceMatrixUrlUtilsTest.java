package cz.geodatahackaton.util;

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Test the whole util class.
 *
 * @author cubeek
 */
public class DistanceMatrixUrlUtilsTest {

    private Properties props = new Properties();

    @Before
    public void setUp() throws Exception {
        props.load(DistanceMatrixUrlUtils.class.getResourceAsStream("config-util.properties"));
    }

    @Test(expected = NullPointerException.class)
    public void testGetMatrixUrl_null_coords() throws Exception {
        DistanceMatrixUrlUtils.getMatrixUrl(props.getProperty(DistanceMatrixConfigKeys.URL_PTR), null, null);
    }

    @Test
    public void testGetMatrixUrl_test_call_url() throws Exception {
        final String url = DistanceMatrixUrlUtils.getMatrixUrl(props.getProperty(DistanceMatrixConfigKeys.URL_PTR), DistanceMatrixTestData.COORDS, null);
        assertEquals("The URL should match the default test URL.", DistanceMatrixTestData.URL_EXPECTED_TEST, url);
    }

    @Test
    public void testGetMatrixUrl_real_url() throws Exception {
        final String url = DistanceMatrixUrlUtils.getMatrixUrl(props.getProperty(DistanceMatrixConfigKeys.URL_PTR), DistanceMatrixTestData.COORDS, DistanceMatrixTestData.TEST_KEY);
        assertEquals("The URL should match the default test URL.", DistanceMatrixTestData.URL_EXPECTED_REAL, url);
    }

}
