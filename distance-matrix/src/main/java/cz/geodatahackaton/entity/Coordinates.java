package cz.geodatahackaton.entity;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * @author cubeek
 */
public class Coordinates {

    final List<Coordinate> origins;

    final List<Coordinate> destinations;

    /**
     * Create new Coordinates instance.
     *
     * @param origins origins
     * @param destinations origins
     */
    public Coordinates(final List<Coordinate> origins, final List<Coordinate> destinations) {
        Preconditions.checkNotNull(origins);
        Preconditions.checkNotNull(destinations);

        this.origins = origins;
        this.destinations = destinations;
    }

    public List<Coordinate> getOrigins() {
        return origins;
    }

    public List<Coordinate> getDestinations() {
        return destinations;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Origin:\n");
        for (Coordinate o : origins) {
            sb.append(o.toString()).append("\n");
        }
        sb.append("Destination:\n");
        for (Coordinate d : destinations) {
            sb.append(d.toString()).append("\n");
        }
        return sb.toString();
    }
}
