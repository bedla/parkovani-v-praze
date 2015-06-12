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
            new Coordinate("vz8", "50.09124,14.41621"),
            new Coordinate("vz9", "50.09049,14.41545"),
            new Coordinate("vz10", "50.09014,14.41635"),
            new Coordinate("vz11", "50.09003,14.41596"),
            new Coordinate("vz12", "50.08929,14.41563"),
            new Coordinate("vz13", "50.09250,14.42635"),
            new Coordinate("vz14", "50.09237,14.42597"),
            new Coordinate("vz15", "50.09066,14.42586"),
            new Coordinate("vz16", "50.09075,14.42691"),
            new Coordinate("vz18", "50.09249,14.42532"),
            new Coordinate("vz19", "50.09226,14.42012"),
            new Coordinate("vz20", "50.09106,14.41578"),
            new Coordinate("vz21", "50.09187,14.41714"),
            new Coordinate("vz22", "50.09063,14.42646"),
            new Coordinate("vz1", "50.08724,14.41749"),
            new Coordinate("vz2", "50.08686,14.41723"),
            new Coordinate("vz3", "50.08680,14.41776"),
            new Coordinate("vz4", "50.08737,14.41821"),
            new Coordinate("vz5", "50.08756,14.41862"),
            new Coordinate("vz6", "50.09123,14.41834"),
            new Coordinate("vz7", "50.09109,14.41819"),
            new Coordinate("c1", "50.083638,14.434301"),
            new Coordinate("c2", "50.080754,14.431580"),
            new Coordinate("c3", "50.108938,14.441199"),
            new Coordinate("vz17", "50.09007,14.42781")
        ));

        COORDINATES = new Coordinates(
                Arrays.asList(
                        new Coordinate("vz4,", "50.08737,14.41821"),
                        new Coordinate("vz5,", "50.08756,14.41862"),
                        new Coordinate("vz6,", "50.09123,14.41834"),
                        new Coordinate("vz7,", "50.09109,14.41819"),
                        new Coordinate("vz17", "50.09007,14.42781")
                ),
                Arrays.asList(
                        new Coordinate("vz22", "50.09063,14.42646"),
                        new Coordinate("vz1,", "50.08724,14.41749"),
                        new Coordinate("vz2,", "50.08686,14.41723"),
                        new Coordinate("vz3,", "50.08680,14.41776")
                )
        );
    }

    public static final String URL_EXPECTED_TEST = "http://maps.googleapis.com/maps/api/distancematrix/json" +
            "?origins=50.08737,14.41821|50.08756,14.41862|50.09123,14.41834|50.09109,14.41819|50.09007,14.42781" +
            "&destinations=50.09063,14.42646|50.08724,14.41749|50.08686,14.41723|50.08680,14.41776" +
            "&mode=driving&units=metric&language=cs-CZ"; // + "&key=null"; // TODO ADD KEY

    public static final String URL_EXPECTED_REAL = "http://maps.googleapis.com/maps/api/distancematrix/json" +
            "?origins=50.08737,14.41821|50.08756,14.41862|50.09123,14.41834|50.09109,14.41819|50.09007,14.42781" +
            "&destinations=50.09063,14.42646|50.08724,14.41749|50.08686,14.41723|50.08680,14.41776" +
            "&mode=driving&units=metric&language=cs-CZ"; // + "&key=" + TEST_KEY; // TODO ADD KEY

}
