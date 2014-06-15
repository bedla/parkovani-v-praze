package cz.geodatahackaton.strategy;

import com.google.common.base.Preconditions;
import cz.geodatahackaton.entity.Coordinate;
import cz.geodatahackaton.entity.Coordinates;

import java.util.List;

/**
 * Strategy for evaluating the data list.
 *
 * @author cubeek
 */
public abstract class CoordinatesStrategy {

    /** Data instance */
    protected final List<Coordinate> data;

    /**
     * Instantiate the strategy
     *
     * @param data initial data
     */
    public CoordinatesStrategy(final List<Coordinate> data) {
        Preconditions.checkNotNull(data);
        this.data = data;
    }

    public abstract List<Coordinates> getCoordsList();

}
