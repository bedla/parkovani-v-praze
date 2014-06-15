package cz.geodatahackaton.strategy;

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Test for the square counter.
 *
 * @author cubeek
 */
public class CoordsStrategyRectangularCounterTest {

    private Properties props = new Properties();

    private CoordsStrategyRectangularCounter counter;

    @Before
    public void setUp() throws Exception {
        props.load(CoordsStrategyRectangularCounterTest.class.getResourceAsStream("config-util.properties"));
        counter = new CoordsStrategyRectangularCounter(
                Integer.parseInt(props.getProperty("request.size")),
                Integer.parseInt(props.getProperty("max.request.size"))
        );
    }

    @Test
    public void testGetSideSize() throws Exception {
        assertEquals("Counter size should equal sqrt(size)!",
                Integer.parseInt(props.getProperty("expectations.a")), counter.getSideSize());
    }

    @Test
    public void testGetNumberOfRequestsSingleDirection() throws Exception {
        assertEquals("Number of requests should equal the expected value!",
                Integer.parseInt(props.getProperty("expectations.requests")),
                counter.getNumberOfRequestsSingleDirection());
    }

    @Test
    public void testGetTotalSingleDirection() throws Exception {
        assertEquals("Total number of elements processed in a single direction should equal expected!",
                Integer.parseInt(props.getProperty("expectations.total")), counter.getTotalSingleDirection());
    }

}
