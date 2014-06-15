package cz.geodatahackaton.dao;

import cz.geodatahackaton.entity.Coordination;

import java.util.List;

/**
 * @author cubeek
 */
public interface CoordsDao {

    /**
     * Return coords data
     *
     * @return coords data
     */
    List<Coordination> getData();

}
