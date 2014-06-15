package cz.geodatahackaton.strategy;

import cz.geodatahackaton.entity.Coordinate;
import cz.geodatahackaton.entity.Coordinates;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author cubeek
 */
public class CoordinatesStrategyRectangular extends CoordinatesStrategy {

    private final CoordinatesStrategyRectangularCounter counter;

    /**
     * Instantiate the strategy
     *
     * @param data initial data
     */
    public CoordinatesStrategyRectangular(final List<Coordinate> data, final int requestLimit) {
        super(data);

        counter = new CoordinatesStrategyRectangularCounter(data.size(), requestLimit);
    }

    @Override
    public List<Coordinates> getCoordsList() {
        int i = 0;
        int j = 0;

        int sideSize = counter.getSideSize();

        final Coordinate[][] coords = new Coordinate[sideSize][sideSize];

        for (Coordinate coordinate : data) {
            if (i == sideSize) {
                j++;
                i = 0;
            }
            coords[i][j] = coordinate;
            i++;
        }

        final List<Coordinates> results = getCoords(sideSize, coords);

        return results;
    }

    private List<Coordinates> getCoords(int sideSize, Coordinate[][] coords) {
        final List<Coordinates> results = new LinkedList<>();
        final List<Coordinate> origins = new LinkedList<>();
        final List<Coordinate> destinations = new LinkedList<>();

        for (int pos = 0; pos < sideSize; pos++) {
            for (int w = pos; w < sideSize; w++) {
                for (int h = 0; h < sideSize; h++) {
                    final Coordinate c = coords[h][w];

                    if (c == null) continue;

                    if (w == pos) {
                        origins.add(c);
                    } else {
                        destinations.add(c);
                    }
                }
                if (w > pos) {
                    results.add(new Coordinates(new LinkedList<>(origins), new LinkedList<>(destinations)));
                    destinations.clear();
                }
            }
            origins.clear();
        }
        return results;
    }
}
