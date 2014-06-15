package cz.geodatahackaton.strategy;

import com.google.common.collect.ImmutableList;
import cz.geodatahackaton.entity.Coordinate;
import cz.geodatahackaton.entity.Coordinates;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Split the group in half and use the results as the directions.
 *
 * @author cubeek
 */
@SuppressWarnings("unused") // It just is ...
public class CoordinatesStrategyBasic extends CoordinatesStrategy {

    /**
     * Instantiate the strategy
     *
     * @param data initial data
     */
    public CoordinatesStrategyBasic(List<Coordinate> data) {
        super(data);
    }

    @Override
    public List<Coordinates> getCoordsList() {
        if (data != null) {
            List<Coordinate> origin = new LinkedList<>();
            List<Coordinate> destination = new LinkedList<>();
            for (Coordinate c : data) {
                if (origin.size() < data.size() / 2)
                    origin.add(c);
                else
                    destination.add(c);
            }
            return ImmutableList.copyOf(Arrays.asList(new Coordinates(origin, destination)));
        }
        return null;
    }

}
