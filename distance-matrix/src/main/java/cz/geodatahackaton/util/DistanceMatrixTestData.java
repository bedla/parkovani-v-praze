package cz.geodatahackaton.util;

import cz.geodatahackaton.entity.Coordinate;
import cz.geodatahackaton.entity.Coordinates;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Test data.
 *
 * @author cubeek
 */
public final class DistanceMatrixTestData {

    public static final Coordinates COORDINATES;

    public static final List<Coordinate> DATA;

    public static final String TEST_KEY = "TEST_KEY";

    static {
        DATA = new LinkedList<>(Arrays.asList(
                new Coordinate("1", "50.069903,14.439266"),
                new Coordinate("2", "50.063435,14.447101"),
                new Coordinate("3", "50.063242,14.391440"),
                new Coordinate("4", "50.088003,14.463452"),
                new Coordinate("5", "50.112612,14.473194"),
                new Coordinate("6", "51.394399,-62.044549"),
                new Coordinate("7", "50.071011,14.456526"),
                new Coordinate("8", "50.082292,14.462841"),
                new Coordinate("9", "50.069903,14.439266"),
                new Coordinate("10", "50.063435,14.447101"),
                new Coordinate("11", "50.063242,14.391440"),
                new Coordinate("12", "50.088003,14.463452"),
                new Coordinate("13", "50.112612,14.473194"),
                new Coordinate("14", "51.394399,-62.044549"),
                new Coordinate("15", "50.071011,14.456526"),
                new Coordinate("16", "50.082292,14.462841"),
                new Coordinate("17", "50.069903,14.439266"),
                new Coordinate("18", "50.063435,14.447101"),
                new Coordinate("19", "50.063242,14.391440"),
                new Coordinate("20", "50.088003,14.463452"),
                new Coordinate("21", "50.112612,14.473194"),
                new Coordinate("22", "51.394399,62.044549")
        ));
        COORDINATES = new Coordinates(
                Arrays.asList(
                        new Coordinate("1", "50.069903,14.439266"),
                        new Coordinate("2", "50.063435,14.447101"),
                        new Coordinate("3", "50.063242,14.391440")),
                Arrays.asList(
                        new Coordinate("4", "50.088003,14.463452"),
                        new Coordinate("5", "50.112612,14.473194"),
                        new Coordinate("6", "50.071011,14.456526"))
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
