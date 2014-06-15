package cz.hackaton.parkovanivpraze.json;

import com.google.common.collect.ObjectArrays;

import java.util.Arrays;

/**
 * @author ivo.smid
 * @version $Revision:$
 */
public class Neo4JResponse {

    public final Results[] results = ObjectArrays.newArray(Results.class, 0);
    public final Error[] errors = ObjectArrays.newArray(Error.class, 0);

    @Override
    public String toString() {
        return "Neo4JResponse{" +
                "results=" + Arrays.toString(results) +
                ", errors=" + Arrays.toString(errors) +
                '}';
    }

    public static class Results {
        public final String[] columns = ObjectArrays.newArray(String.class, 0);
        public final Stats stats = null;
        public final Data[] data = ObjectArrays.newArray(Data.class, 0);

        @Override
        public String toString() {
            return "Results{" +
                    "columns=" + Arrays.toString(columns) +
                    ", data=" + Arrays.toString(data) +
                    ", stats=" + stats +
                    '}';
        }

        public static class Data {
            public final String[] row = ObjectArrays.newArray(String.class, 0);
//            public final String graph = null;
//            public final String nodes = null;
//            public final String relationships = null;
//    graph: {nodes:[], relationships:[]}
//    nodes: []
//    relationships: []
//    row: [1]  0: 1

            @Override
            public String toString() {
                return "Data{" +
                        "row=" + Arrays.toString(row) +
                        '}';
            }
        }

        public static class Stats {
            public final int constraints_added = 0;
            public final int constraints_removed = 0;
            public final boolean contains_updates = false;
            public final int indexes_addedindexes = 0;
            public final int indexes_removedindexes = 0;
            public final int labels_addedlabels = 0;
            public final int labels_removedlabels = 0;
            public final int nodes_creatednodes = 0;
            public final int nodes_deletednodes = 0;
            public final int properties_setproperties = 0;
            public final int relationship_deletedrelationship = 0;
            public final int relationships_createdrelationships = 0;

            @Override
            public String toString() {
                return "Stats{" +
                        "constraints_added=" + constraints_added +
                        ", constraints_removed=" + constraints_removed +
                        ", contains_updates=" + contains_updates +
                        ", indexes_addedindexes=" + indexes_addedindexes +
                        ", indexes_removedindexes=" + indexes_removedindexes +
                        ", labels_addedlabels=" + labels_addedlabels +
                        ", labels_removedlabels=" + labels_removedlabels +
                        ", nodes_creatednodes=" + nodes_creatednodes +
                        ", nodes_deletednodes=" + nodes_deletednodes +
                        ", properties_setproperties=" + properties_setproperties +
                        ", relationship_deletedrelationship=" + relationship_deletedrelationship +
                        ", relationships_createdrelationships=" + relationships_createdrelationships +
                        '}';
            }
        }

    }

    public static class Error {
        private final String code = null;
        private final String message = null;

        @Override
        public String toString() {
            return "Error{" +
                    "code='" + code + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
