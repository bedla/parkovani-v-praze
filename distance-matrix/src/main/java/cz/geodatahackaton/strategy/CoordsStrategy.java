package cz.geodatahackaton.strategy;

import com.google.common.base.Preconditions;
import cz.geodatahackaton.entity.Coordination;
import cz.geodatahackaton.entity.Coords;

import java.util.List;

/**
 * Strategy for evaluating the data list.
 *
 * @author cubeek
 */
public abstract class CoordsStrategy {

    /** Data instance */
    protected final List<Coordination> data;

    /**
     * Instantiate the strategy
     *
     * @param data initial data
     */
    public CoordsStrategy(final List<Coordination> data) {
        Preconditions.checkNotNull(data);
        this.data = data;
    }

    public abstract List<Coords> getCoordsList();

}
