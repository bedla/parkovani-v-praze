package cz.geodatahackaton.strategy;

import cz.geodatahackaton.entity.Coordination;
import cz.geodatahackaton.entity.Coords;

import java.util.List;

/**
 *
 * @author cubeek
 */
public class CoordsStrategyRectangular extends CoordsStrategy {

    private final CoordsStrategyRectangularCounter counter;

    private final Coords[][] coords;
    private final int requestLimit;

    /**
     * Instantiate the strategy
     *
     * @param data initial data
     */
    public CoordsStrategyRectangular(final List<Coordination> data, final int requestLimit) {
        super(data);
        this.requestLimit = requestLimit;

        counter = new CoordsStrategyRectangularCounter(data.size(), requestLimit);
        int sideSize = counter.getSideSize();
        if ((sideSize ^ 2) > requestLimit) {

        }
        coords = new Coords[counter.getSideSize()][counter.getSideSize()];
    }

    @Override
    public List<Coords> getCoordsList() {
        for (int i = 0; i < counter.getSideSize(); i++) {

        }

        return null;
    }


}
