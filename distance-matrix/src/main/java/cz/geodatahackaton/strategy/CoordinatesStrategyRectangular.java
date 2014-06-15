package cz.geodatahackaton.strategy;

import com.google.common.collect.Lists;
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
        final Coordinate[][] coords1 = prepareCoordidatesVertical();
        final Coordinate[][] coords2 = prepareCoordidatesVerticalReverse();
        final Coordinate[][] coords3 = prepareCoordinatesHorizontal();
        final Coordinate[][] coords4 = prepareCoordinatesHorizontalReverse();

        final List<Coordinates> results = new LinkedList<>();
        results.addAll(getCoords(coords1));
        results.addAll(getCoords(coords2));
        results.addAll(getCoords(coords3));
        results.addAll(getCoords(coords4));
        return results;
    }

    private Coordinate[][] prepareCoordidatesVertical() {
        return prepareCoordinates(Direction.VERTICAL, false);
    }

    private Coordinate[][] prepareCoordidatesVerticalReverse() {
        return prepareCoordinates(Direction.VERTICAL, true);
    }

    private Coordinate[][] prepareCoordinatesHorizontal() {
        return prepareCoordinates(Direction.HORIZONTAL, false);
    }

    private Coordinate[][] prepareCoordinatesHorizontalReverse() {
        return prepareCoordinates(Direction.HORIZONTAL, true);
    }

    private Coordinate[][] prepareCoordinates(Direction dir, boolean reverse) {
        int sideSize = counter.getSideSize();

        int i = 0;
        int j = 0;

        final Coordinate[][] coordinates = new Coordinate[sideSize][sideSize];
        List<Coordinate> feed = reverse ? Lists.reverse(data) : data;
        switch (dir) {
            case HORIZONTAL:
                for (Coordinate coordinate : feed) {
                    if (j == sideSize) {
                        i++;
                        j = 0;
                    }
                    coordinates[i][j] = coordinate;
                    j++;
                }
                break;
            case VERTICAL:
                for (Coordinate coordinate : feed) {
                    if (i == sideSize) {
                        j++;
                        i = 0;
                    }
                    coordinates[i][j] = coordinate;
                    i++;
                }
                break;
        }
        return coordinates;
    }

    private List<Coordinates> getCoords(Coordinate[][] coords) {
        int sideSize = counter.getSideSize();

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
