package cz.geodatahackaton.strategy;

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

    /**
     * Instantiate the strategy
     *
     * @param data initial data
     */
    public CoordsStrategyRectangular(final List<Coordination> data, final int requestLimit) {
        super(data);

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

        final List<Coords> results = new LinkedList<>();
        final List<Coordination> origins = new LinkedList<>();
        final List<Coordination> destinations = new LinkedList<>();

        for (int pos = 0; pos < counter.getSideSize(); pos++) {
            for (int w = pos; w < counter.getSideSize(); w++) {
                for (int h = 0; h < counter.getSideSize(); h++) {
                    final Coordination coord = coords[h][w];

                    if (coord == null) continue;

                    if (w == pos) {
                        origins.add(coord);
                    } else {
                        destinations.add(coord);
                    }
                }
                if (w > pos) {
                    results.add(new Coords(new LinkedList<>(origins), new LinkedList<>(destinations)));
                    destinations.clear();
                }
            }
            origins.clear();
        }

        return results;
    }
}
