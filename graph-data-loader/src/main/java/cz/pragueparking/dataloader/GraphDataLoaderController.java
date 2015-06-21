package cz.pragueparking.dataloader;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import cz.pragueparking.utils.Utils;
import org.h2.api.ErrorCode;
import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.ClassBasedVertexFactory;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

@Controller
public class GraphDataLoaderController implements CommandLineRunner, InitializingBean {

    public static final String PATHS_TABLE = "PATHS";
    private static final Logger LOG = LoggerFactory.getLogger(GraphDataLoaderController.class);
    @Value("${dbDataDir}")
    private String dbDataDir;

    @Value("${graphHopper.server}")
    private String graphHopperServer;

    @Value("${graphHopper.port}")
    private int graphHopperPort;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GeometryFactory geometryFactory;

    private String routeUrl;

    @Override
    public void run(String... args) throws Exception {

        LOG.info("=== Start ===");

        final Integer size = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM DOP_ZPS_Automaty_b", Integer.class);
        LOG.info("Size of complete graph will be " + size);

        final Graph<AutomatVertex, DefaultEdge> completeGraph = generateCompleteGraph(size);
        loadGraphWithDataFromDb(size, completeGraph);
        final List<Path> paths = getPathsFromGraphHopper(completeGraph);
        savePathsIntoDb(paths);

        LOG.info("=== End ===");
    }

    private void savePathsIntoDb(List<Path> paths) throws SQLException {
        LOG.info("--- savePathsIntoDb ---");

        try {

            final int pathsCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, PATHS_TABLE);
            if (pathsCount > 0) {
                JdbcTestUtils.deleteFromTables(jdbcTemplate, PATHS_TABLE);
            }

        } catch (BadSqlGrammarException e) {
            switch (e.getSQLException().getErrorCode()) {
                case ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1:

                    jdbcTemplate.execute(new StringBuilder()
                            .append("CREATE TABLE ").append(PATHS_TABLE).append(" (\n")
                            .append(" id INT PRIMARY KEY AUTO_INCREMENT,\n")
                            .append(" source_uid INT,\n")
                            .append(" target_uid INT,\n")
                            .append(" distance DOUBLE,\n")
                            .append(" bbox VARCHAR,\n")
                            .append(" weight DOUBLE,\n")
                            .append(" time INT,\n")
                            .append(" points CLOB\n")
                            .append(");").toString());

                    jdbcTemplate.execute(String.format("CREATE UNIQUE INDEX IDX_source_target ON %s (source_uid, target_uid);", PATHS_TABLE));
                    jdbcTemplate.execute(String.format("CREATE INDEX IDX_source_uid ON %s (source_uid);", PATHS_TABLE));
                    jdbcTemplate.execute(String.format("CREATE INDEX IDX_target_uid ON %s (target_uid);", PATHS_TABLE));
                    jdbcTemplate.execute(String.format("CREATE INDEX IDX_distance ON %s (distance);", PATHS_TABLE));
                    jdbcTemplate.execute(String.format("CREATE INDEX IDX_time ON %s (time);", PATHS_TABLE));

                    break;
                default:
                    throw e;
            }
        }

        final String insertSql = "INSERT INTO " + PATHS_TABLE + " (source_uid, target_uid, distance, bbox, weight, time, points) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(insertSql, paths, paths.size(), (ps, argument) -> {
            ps.setInt(1, argument.source.uid);
            ps.setInt(2, argument.target.uid);
            ps.setDouble(3, argument.distance);
            ps.setString(4, Joiner.on(',').join(argument.bbox));
            ps.setDouble(5, argument.weight);
            ps.setInt(6, argument.time);
            ps.setString(7, argument.points);
        });


//        System.out.println(JdbcTestUtils.countRowsInTable(jdbcTemplate, PATHS_TABLE));
//        List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT * FROM " + PATHS_TABLE);
//        for (Map<String, Object> map : list) {
//            System.out.println(map);
//        }
    }

    private List<Path> getPathsFromGraphHopper(Graph<AutomatVertex, DefaultEdge> completeGraph) {
        LOG.info("--- getPathsFromGraphHopper ---");

        final Set<AutomatEdge> automatEdges = Sets.newHashSet();
        final Iterator<AutomatVertex> graphIterator = new DepthFirstIterator<>(completeGraph);
        while (graphIterator.hasNext()) {
            final AutomatVertex vertex = graphIterator.next();
            Set<DefaultEdge> edges = completeGraph.edgesOf(vertex);

            for (DefaultEdge edge : edges) {
                AutomatVertex source = completeGraph.getEdgeSource(edge);
                AutomatVertex target = completeGraph.getEdgeTarget(edge);
                automatEdges.add(new AutomatEdge(source, target));
            }
        }

        final List<Path> paths = Lists.newArrayList();

        final int count = automatEdges.size();
        int idx = 0;
        int errors = 0;
        final StopWatch stopWatch = new StopWatch("route");
        for (AutomatEdge automatEdge : automatEdges) {
//            if (idx == 10) break;
            stopWatch.start();
            final Path computedPath = graphHopperGetPath(automatEdge.source, automatEdge.target);
            if (computedPath != null) {
                paths.add(computedPath);
            } else {
                errors++;
            }
            stopWatch.stop();
            if (idx % 1000 == 0) {
                LOG.info(String.format("%.2f %s", (float) idx / (float) count * 100.0f, "%"));
            }
            idx++;
        }
        LOG.info(String.format("%.2f %s", (float) idx / (float) count * 100.0f, "%"));
        LOG.info(stopWatch.shortSummary());
        if (errors > 0) {
            LOG.error("Errors count is {} which is {} from all data {}", errors, String.format("%.4f %s", (float) errors / (float) automatEdges.size() * 100.0f, "%"), automatEdges.size());
        }
        return paths;
    }

    private Path graphHopperGetPath(AutomatVertex vertex1, AutomatVertex vertex2) {

        double[] doubles1 = Utils.transformMercatorToWgs84(vertex1.point.getX(), vertex1.point.getY());
        double[] doubles2 = Utils.transformMercatorToWgs84(vertex2.point.getX(), vertex2.point.getY());

        doubles1 = Utils.roundTo(doubles1, 6);
        doubles2 = Utils.roundTo(doubles2, 6);

        final Point point1 = new Point(new CoordinateArraySequence(new Coordinate[]{new Coordinate(doubles1[1], doubles1[0])}), geometryFactory);
        final Point point2 = new Point(new CoordinateArraySequence(new Coordinate[]{new Coordinate(doubles2[1], doubles2[0])}), geometryFactory);

        final String url = routeUrl + "?point={point1}&point={point2}&type=json&instructions=false";
        final Map<String, Object> urlVariables = Maps.newHashMap();
        urlVariables.put("point1", String.format("%s,%s", point1.getX(), point1.getY()));
        urlVariables.put("point2", String.format("%s,%s", point2.getX(), point2.getY()));

        final ResponseEntity<Map> entity = restTemplate.getForEntity(url, Map.class, urlVariables);

        Preconditions.checkState(entity.getStatusCode().is2xxSuccessful(), entity);

        final Map<?, ?> body = entity.getBody();

        if (isNotFoundErrorInResponse(body)) {
            LOG.error("Unable to route for URL {}", new UriTemplate(url).expand(urlVariables));
            return null;
        } else {

            final Object pathsObj = body.get("paths");
            Preconditions.checkState(pathsObj instanceof List, pathsObj);
            Preconditions.checkState(!((List) pathsObj).isEmpty(), pathsObj);
            final Object pathObj = ((List<?>) pathsObj).get(0);
            Preconditions.checkState(pathObj instanceof Map, pathObj);
            final Map<?, ?> path = ((Map<?, ?>) pathObj);

            final Object distanceObj = path.get("distance");
            Preconditions.checkState(distanceObj instanceof Number, distanceObj);
            final double distance = ((Number) distanceObj).doubleValue();

            Preconditions.checkState(distance != 0, "Unable to route for URL %s", new UriTemplate(url).expand(urlVariables));

            final Object bboxObj = path.get("bbox");
            final Object weightObj = path.get("weight");
            final Object timeObj = path.get("time");
            final Object pointsObj = path.get("points");

            Preconditions.checkState(bboxObj instanceof List, bboxObj);
            Preconditions.checkState(weightObj instanceof Number, weightObj);
            Preconditions.checkState(timeObj instanceof Number, timeObj);
            Preconditions.checkState(pointsObj instanceof String, pointsObj);

            final List<?> bbox = (List<?>) bboxObj;
            final double weight = ((Number) weightObj).doubleValue();
            final int time = ((Number) timeObj).intValue();
            final String points = (String) pointsObj;

            return new Path(vertex1, vertex2, distance, bbox, weight, time, points);
        }
    }

    private boolean isNotFoundErrorInResponse(Map<?, ?> body) {
        Object infoObj = body.get("info");
        if (infoObj instanceof Map) {
            Object errorsObj = ((Map) infoObj).get("errors");
            if (errorsObj instanceof List && ((List) errorsObj).size() > 0 && ((List) errorsObj).get(0) instanceof Map) {
                Object message = ((Map) ((List) errorsObj).get(0)).get("message");
                if (message instanceof String && "Not found" .equals(message)) {
                    return true;
                }
            }
        }

        return false;
    }


    private void loadGraphWithDataFromDb(Integer size, Graph<AutomatVertex, DefaultEdge> completeGraph) {
        LOG.info("--- loadGraphWithDataFromDb ---");
        LOG.info("Loading data from db into complete graph");

        final List<Map<String, Object>> dbList = jdbcTemplate.queryForList("SELECT * FROM DOP_ZPS_Automaty_b");

        Preconditions.checkState(size == dbList.size(), "Graph size (%s) and data size (%s) not equal", size, dbList.size());


        final Set<AutomatVertex> vertices = new HashSet<>();
        vertices.addAll(completeGraph.vertexSet());

        final Iterator<Map<String, Object>> dbIterator = dbList.iterator();
        for (AutomatVertex vertex : vertices) {
            Preconditions.checkState(dbIterator.hasNext());

            Map<String, Object> map = dbIterator.next();

            AutomatVertex newVertex = new AutomatVertex();
            newVertex.uid = (int) map.get("UID");
            newVertex.type = (String) map.get("TYP");
            newVertex.point = (Point) map.get("THE_GEOM");
            Utils.replaceVertex(completeGraph, vertex, newVertex);

        }
    }

    private Graph<AutomatVertex, DefaultEdge> generateCompleteGraph(int size) {
        LOG.info("--- generateCompleteGraph ---");

        //Create the CompleteGraphGenerator object
        final Graph<AutomatVertex, DefaultEdge> completeGraph = new SimpleGraph<>(DefaultEdge.class);
        final CompleteGraphGenerator<AutomatVertex, DefaultEdge> completeGenerator = new CompleteGraphGenerator<>(size);

        //Create the VertexFactory so the generator can create vertices
        final VertexFactory<AutomatVertex> vFactory = new ClassBasedVertexFactory<>(AutomatVertex.class);

        LOG.info("Generating complete graph");

        //Use the CompleteGraphGenerator object to make completeGraph a
        //complete graph with [size] number of vertices
        completeGenerator.generateGraph(completeGraph, vFactory, null);
        return completeGraph;
    }

    private void dumpGraph(Graph<AutomatVertex, DefaultEdge> completeGraph) {
        final Iterator<AutomatVertex> graphIterator = new DepthFirstIterator<>(completeGraph);
        while (graphIterator.hasNext()) {
            final AutomatVertex vertex = graphIterator.next();
            System.out.println("Vertex " + vertex.toString() + " is connected to: " + completeGraph.edgesOf(vertex).toString());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.routeUrl = String.format("http://%s:%d/route", graphHopperServer, graphHopperPort);
    }
}