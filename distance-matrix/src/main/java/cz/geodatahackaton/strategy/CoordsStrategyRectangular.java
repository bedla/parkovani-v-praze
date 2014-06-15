package cz.geodatahackaton.strategy;

import cz.geodatahackaton.entity.Coordination;
import cz.geodatahackaton.entity.Coords;

import java.util.List;

/**
 *
 * @author cubeek
 */
public class CoordsStrategyRectangular extends CoordsStrategy {

    /**
     * Instantiate the strategy
     *
     * @param data initial data
     */
    public CoordsStrategyRectangular(List<Coordination> data) {
        super(data);
    }

    @Override
    public List<Coords> getCoordsList() {

        return null;
    }


}
