package cz.geodatahackaton.dao;

import cz.geodatahackaton.entity.Coordinate;
import cz.geodatahackaton.util.DistanceMatrixTestData;

import java.util.List;

/**
 * Mock data provider.
 *
 * @author cubeek
 */
public class CoordinatesDaoMock implements CoordinatesDao {

    public List<Coordinate> getData() {
        return DistanceMatrixTestData.DATA;
    }

}
