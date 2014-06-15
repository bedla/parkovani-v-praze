package cz.geodatahackaton.strategy;

import com.google.common.collect.ImmutableList;
import cz.geodatahackaton.entity.Coordination;
import cz.geodatahackaton.entity.Coords;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author cubeek
 */
public class CoordsStrategyRectangular extends CoordsStrategy {

    private final CoordsStrategyRectangularCounter counter;

    private final Coordination[][] coords;
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
        coords = new Coordination[counter.getSideSize()][counter.getSideSize()];
    }

    @Override
    public List<Coords> getCoordsList() {
        int i = 0;
        int j = 0;

        for (Coordination coordination : data) {
            if (j == counter.getSideSize()) {
                i++;
                j = 0;
            }
            coords[i][j] = coordination;
            j++;
        }

        return null;

//        int i = 0;
//        final List<Coords> results = new LinkedList<>();
//        List<Coordination> origins = new LinkedList<>();
//        List<Coordination> destinations = new LinkedList<>();
//
//        for (Coordination coordination : data) {
//            if (i < requestLimit) {
//                if (i < counter.getSideSize()) {
//                    origins.add(coordination);
//                } else {
//                    destinations.add(coordination);
//                }
//            } else {
//                results.add(new Coords(ImmutableList.copyOf(origins), ImmutableList.copyOf(destinations)));
//                origins.clear();
//                destinations.clear();
//            }
//
//            if (origins.size() > 0 && destinations.size() > 0) {
//                results.add(new Coords(ImmutableList.copyOf(origins), ImmutableList.copyOf(destinations)));
//            }
//
//            // increment counter
//            i += 1;
//        }
//        return results;
    }


}
