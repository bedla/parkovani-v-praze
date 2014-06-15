package cz.geodatahackaton.strategy;

import com.google.common.collect.ImmutableList;
import cz.geodatahackaton.entity.Coordination;
import cz.geodatahackaton.entity.Coords;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Split the group in half and use the results as the directions.
 *
 * @author cubeek
 */
public class CoordsStrategyBasic extends CoordsStrategy {

    /**
     * Instantiate the strategy
     *
     * @param data initial data
     */
    public CoordsStrategyBasic(List<Coordination> data) {
        super(data);
    }

    @Override
    public List<Coords> getCoordsList() {
        if (data != null) {
            List<Coordination> origin = new LinkedList<>();
            List<Coordination> destination = new LinkedList<>();
            for (Coordination c : data) {
                if (origin.size() < data.size() / 2)
                    origin.add(c);
                else
                    destination.add(c);
            }
            return ImmutableList.copyOf(Arrays.asList(new Coords(origin, destination)));
        }
        return null;
    }

}
