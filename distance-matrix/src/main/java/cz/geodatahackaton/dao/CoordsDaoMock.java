package cz.geodatahackaton.dao;

import cz.geodatahackaton.entity.Coordination;
import cz.geodatahackaton.util.DistanceMatrixTestData;

import java.util.List;

/**
 * Mock data provider.
 *
 * @author cubeek
 */
public class CoordsDaoMock implements CoordsDao {

    public List<Coordination> getData() {
        return DistanceMatrixTestData.DATA;
    }

}
