package cz.pragueparking.dataloader;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.osgeo.proj4j.*;

import java.util.Set;

/**
 * Created by ivo.smid on 13.6.2015.
 */
public class Utils {

    private static final CRSFactory CRS_FACTORY = new CRSFactory();
    private static final CoordinateReferenceSystem CRS_MERCATOR = CRS_FACTORY.createFromName("epsg:3857");
    private static final CoordinateReferenceSystem CRS_WGS84 = CRS_FACTORY.createFromName("epsg:4326");
    private static final CoordinateTransformFactory COORDINATE_TRANSFORM_FACTORY = new CoordinateTransformFactory();
    private static final CoordinateTransform TRANSFORM_MERCATOR_TO_WGS84 = COORDINATE_TRANSFORM_FACTORY.createTransform(CRS_MERCATOR, CRS_WGS84);

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

}
