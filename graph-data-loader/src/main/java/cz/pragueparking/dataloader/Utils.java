package cz.pragueparking.dataloader;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import java.util.Set;

/**
 * Created by ivo.smid on 13.6.2015.
 */
public class Utils {

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

}
