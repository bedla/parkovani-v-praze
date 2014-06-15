package cz.geodatahackaton.dao;

import cz.geodatahackaton.util.DistanceMatrixTestData;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Dummy test for coordinates DAO
 *
 * @author cubeek
 */
public class CoordinatesDaoTest {

    private CoordinatesDao dao;

    @Before
    public void setUp() throws Exception {
        this.dao = new CoordinatesDaoMock();
    }

    @Test
    public void testGetData() throws Exception {
        assertEquals(
                "DAO must return the whole set.",
                DistanceMatrixTestData.DATA.size(),
                dao.getData().size());
    }

}
