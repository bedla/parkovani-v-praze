package cz.geodatahackaton.dao;

import cz.geodatahackaton.entity.Coordinate;

import java.util.List;

/**
 * @author cubeek
 */
public interface CoordinatesDao {

    /**
     * Return coords data
     *
     * @return coords data
     */
    List<Coordinate> getData();

}
