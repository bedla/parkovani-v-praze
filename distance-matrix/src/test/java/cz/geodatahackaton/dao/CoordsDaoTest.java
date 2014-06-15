package cz.geodatahackaton.dao;

import cz.geodatahackaton.entity.Coordination;
import cz.geodatahackaton.util.DistanceMatrixTestData;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Dummy test for coords DAO
 *
 * @author cubeek
 */
public class CoordsDaoTest {

    private CoordsDao dao;

    @Before
    public void setUp() throws Exception {
        this.dao = new CoordsDaoMock();
    }

    @Test
    public void testGetData() throws Exception {
        assertEquals(
                "DAO must return the whole set.",
                DistanceMatrixTestData.DATA.size(),
                dao.getData().size());
    }

}
