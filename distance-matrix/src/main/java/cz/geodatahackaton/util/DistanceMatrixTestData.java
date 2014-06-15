package cz.geodatahackaton.util;

import cz.geodatahackaton.entity.Coordination;
import cz.geodatahackaton.entity.Coords;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Test data.
 *
 * @author cubeek
 */
public final class DistanceMatrixTestData {

    public static final Coords COORDS;

    public static final List<Coordination> DATA;

    public static final String TEST_KEY = "TEST_KEY";

    static {
        DATA = new LinkedList<>(Arrays.asList(
                new Coordination("1", "50.069903,14.439266"),
                new Coordination("2", "50.063435,14.447101"),
                new Coordination("3", "50.063242,14.391440"),
                new Coordination("4", "50.088003,14.463452"),
                new Coordination("5", "50.112612,14.473194"),
                new Coordination("6", "51.394399,-62.044549"),
                new Coordination("7", "50.071011,14.456526"),
                new Coordination("8", "50.082292,14.462841"),
                new Coordination("9", "50.069903,14.439266"),
                new Coordination("10", "50.063435,14.447101"),
                new Coordination("11", "50.063242,14.391440"),
                new Coordination("12", "50.088003,14.463452"),
                new Coordination("13", "50.112612,14.473194"),
                new Coordination("14", "51.394399,-62.044549"),
                new Coordination("15", "50.071011,14.456526"),
                new Coordination("16", "50.082292,14.462841"),
                new Coordination("17", "50.069903,14.439266"),
                new Coordination("18", "50.063435,14.447101"),
                new Coordination("19", "50.063242,14.391440"),
                new Coordination("20", "50.088003,14.463452"),
                new Coordination("21", "50.112612,14.473194"),
                new Coordination("22", "51.394399,62.044549")
        ));
        COORDS = new Coords(
                Arrays.asList(
                        new Coordination("1", "50.069903,14.439266"),
                        new Coordination("2", "50.063435,14.447101"),
                        new Coordination("3", "50.063242,14.391440")),
                Arrays.asList(
                        new Coordination("4", "50.088003,14.463452"),
                        new Coordination("5", "50.112612,14.473194"),
                        new Coordination("6", "50.071011,14.456526"))
        );
    }

    public static final String URL_EXPECTED_TEST = "http://maps.googleapis.com/maps/api/distancematrix/json" +
            "?origins=50.069903,14.439266|50.063435,14.447101|50.063242,14.391440" +
            "&destinations=50.088003,14.463452|50.112612,14.473194|50.071011,14.456526" +
            "&mode=driving" +
            "&units=metric" +
            "&language=cs-CZ"; // + "&key=null"; // TODO ADD KEY

    public static final String URL_EXPECTED_REAL = "http://maps.googleapis.com/maps/api/distancematrix/json" +
            "?origins=50.069903,14.439266|50.063435,14.447101|50.063242,14.391440" +
            "&destinations=50.088003,14.463452|50.112612,14.473194|50.071011,14.456526" +
            "&mode=driving" +
            "&units=metric" +
            "&language=cs-CZ"; // + "&key=" + TEST_KEY; // TODO ADD KEY

}
