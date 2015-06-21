package cz.pragueparking.utils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.osgeo.proj4j.*;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class Utils {

    private static final CRSFactory CRS_FACTORY = new CRSFactory();
    private static final CoordinateReferenceSystem CRS_MERCATOR = CRS_FACTORY.createFromName("epsg:3857");
    private static final CoordinateReferenceSystem CRS_WGS84 = CRS_FACTORY.createFromName("epsg:4326");
    private static final CoordinateTransformFactory COORDINATE_TRANSFORM_FACTORY = new CoordinateTransformFactory();
    private static final CoordinateTransform TRANSFORM_MERCATOR_TO_WGS84 = COORDINATE_TRANSFORM_FACTORY.createTransform(CRS_MERCATOR, CRS_WGS84);
    private static final CoordinateTransform TRANSFORM_WGS84_TO_MERCATOR = COORDINATE_TRANSFORM_FACTORY.createTransform(CRS_WGS84, CRS_MERCATOR);

    private Utils() {
    }

    public static <T extends Object> boolean replaceVertex(Graph<T, DefaultEdge> completeGraph, T oldVertex, T newVertex) {
        if ((oldVertex == null) || (newVertex == null)) {
            return false;
        }
        final Set<DefaultEdge> relatedEdges = completeGraph.edgesOf(oldVertex);
        completeGraph.addVertex(newVertex);

        T sourceVertex;
        T targetVertex;
        for (DefaultEdge e : relatedEdges) {
            sourceVertex = completeGraph.getEdgeSource(e);
            targetVertex = completeGraph.getEdgeTarget(e);
            if (sourceVertex.equals(oldVertex)
                    && targetVertex.equals(oldVertex)) {
                completeGraph.addEdge(newVertex, newVertex);
            } else {
                if (sourceVertex.equals(oldVertex)) {
                    completeGraph.addEdge(newVertex, targetVertex);
                } else {
                    completeGraph.addEdge(sourceVertex, newVertex);
                }
            }
        }
        completeGraph.removeVertex(oldVertex);
        return true;
    }

    public static double[] transformMercatorToWgs84(double x, double y) {
        final ProjCoordinate projCoordinate = TRANSFORM_MERCATOR_TO_WGS84.transform(new ProjCoordinate(x, y), new ProjCoordinate());
        return new double[]{projCoordinate.x, projCoordinate.y};
    }


    public static double[] transformWgs84ToMercator(double x, double y) {
        final ProjCoordinate projCoordinate = TRANSFORM_WGS84_TO_MERCATOR.transform(new ProjCoordinate(x, y), new ProjCoordinate());
        return new double[]{projCoordinate.x, projCoordinate.y};
    }

    public static double[] roundTo(double[] array, int places) {

        array = array.clone();
        for (int i = 0; i < array.length; i++) {
            array[i] = roundTo(array[i], places);
        }
        return array;
    }


    public static double roundTo(double value, int places) {
        final double c = (places < 0 ? 1.0 : Math.pow(10.0, places));
        return Math.round(value * c) / c;
    }

    public static String activateSpringProfilesAsString() {
        return Joiner.on(',').join(activateSpringProfilesAsArray());
    }

    public static String[] activateSpringProfilesAsArray() {
        final Properties properties = new Properties();
        try {
            properties.load(Utils.class.getClassLoader().getResourceAsStream("activate.properties"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return Splitter.on(',')
                .omitEmptyStrings()
                .trimResults()
                .splitToList(properties.getProperty("springProfileActivate"))
                .toArray(new String[0]);
    }
}
