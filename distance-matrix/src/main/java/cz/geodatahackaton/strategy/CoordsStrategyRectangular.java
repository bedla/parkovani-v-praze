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



    /**
     * Instantiate the strategy
     *
     * @param data initial data
     */
    public CoordsStrategyRectangular(final List<Coordination> data, final int requestLimit) {
        super(data);

        counter = new CoordsStrategyRectangularCounter(data.size(), requestLimit);
    }

    @Override
    public List<Coords> getCoordsList() {
        int i = 0;
        int j = 0;

        int sideSize = counter.getSideSize();

        final Coordination[][] coords = new Coordination[sideSize][sideSize];

        for (Coordination coordination : data) {
            if (i == sideSize) {
                j++;
                i = 0;
            }
            coords[i][j] = coordination;
            i++;
        }

        final List<Coords> results = getCoords(sideSize, coords);

        return results;
    }

    private List<Coords> getCoords(int sideSize, Coordination[][] coords) {
        final List<Coords> results = new LinkedList<>();
        final List<Coordination> origins = new LinkedList<>();
        final List<Coordination> destinations = new LinkedList<>();

        for (int pos = 0; pos < sideSize; pos++) {
            for (int w = pos; w < sideSize; w++) {
                for (int h = 0; h < sideSize; h++) {
                    final Coordination c = coords[h][w];

                    if (c == null) continue;

                    if (w == pos) {
                        origins.add(c);
                    } else {
                        destinations.add(c);
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
