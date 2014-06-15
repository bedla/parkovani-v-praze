package cz.geodatahackaton.entity;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 * @author cubeek
 */
public class Coords {

    final List<Coordination> origins;

    final List<Coordination> destinations;

    /**
     * Create new Coords instance.
     *
     * @param origins origins
     * @param destinations origins
     */
    public Coords(final List<Coordination> origins, final List<Coordination> destinations) {
        Preconditions.checkNotNull(origins);
        Preconditions.checkNotNull(destinations);

        this.origins = origins;
        this.destinations = destinations;
    }

    public List<Coordination> getOrigins() {
        return origins;
    }

    public List<Coordination> getDestinations() {
        return destinations;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Origin:\n");
        for (Coordination o : origins) {
            sb.append(o.toString()).append("\n");
        }
        sb.append("Destination:\n");
        for (Coordination d : destinations) {
            sb.append(d.toString()).append("\n");
        }
        return sb.toString();
    }
}
