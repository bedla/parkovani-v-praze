package cz.geodatahackaton.util;

/**
 * Constants for the distance matrix shooter.
 *
 * <ul>Limits for the free API:
 * <li>100 elements per query.</li>
 * <li>100 elements per 10 seconds.</li>
 * <li>2 500 elements per 24 hour period.</li>
 * </ul>
 *
 * @author cubeek
 */
public final class DistanceMatrixConstants {

    public static final String CONFIG_FILE = "config.properties";

    private DistanceMatrixConstants() {
        // util class
    }

}
