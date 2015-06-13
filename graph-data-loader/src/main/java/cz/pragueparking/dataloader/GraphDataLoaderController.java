package cz.pragueparking.dataloader;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Controller
public class GraphDataLoaderController implements CommandLineRunner, InitializingBean {

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

        final Integer size = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM DOP_ZPS_Automaty_b", Integer.class);
        LOG.info("Size of complete graph will be " + size);

        final Graph<AutomatVertex, DefaultEdge> completeGraph = generateCompleteGraph(size);

        loadGraphWithDataFromDb(size, completeGraph);

        final Set<AutomatEdge> set = Sets.newHashSet();
        final Iterator<AutomatVertex> graphIterator = new DepthFirstIterator<>(completeGraph);
        while (graphIterator.hasNext()) {
            final AutomatVertex vertex = graphIterator.next();
            Set<DefaultEdge> edges = completeGraph.edgesOf(vertex);

            for (DefaultEdge edge : edges) {
                AutomatVertex source = completeGraph.getEdgeSource(edge);
                AutomatVertex target = completeGraph.getEdgeTarget(edge);
                set.add(new AutomatEdge(source, target));
            }
        }

        final Point point1 = new Point(new CoordinateArraySequence(new Coordinate[]{new Coordinate(50.127622, 14.315186)}), geometryFactory);
        final Point point2 = new Point(new CoordinateArraySequence(new Coordinate[]{new Coordinate(50.165463, 14.026794)}), geometryFactory);

        final Path computedPath = graphHopperGetPath(point1, point2);

        System.out.println(computedPath);

    }

    private Path graphHopperGetPath(Point point1, Point point2) {
        final String point1Str = String.format("%s,%s", point1.getX(), point1.getY());
        final String point2Str = String.format("%s,%s", point2.getX(), point2.getY());

        final ResponseEntity<Map> entity = restTemplate.getForEntity(routeUrl + "?point={point1}&point={point2}&type=json&instructions=false", Map.class, point1Str, point2Str);

        Preconditions.checkState(entity.getStatusCode().is2xxSuccessful(), entity);

        final Map<?, ?> body = entity.getBody();
        final Object pathsObj = body.get("paths");
        Preconditions.checkState(pathsObj instanceof List, pathsObj);
        Preconditions.checkState(!((List) pathsObj).isEmpty(), pathsObj);
        final Object pathObj = ((List<?>) pathsObj).get(0);
        Preconditions.checkState(pathObj instanceof Map, pathObj);
        final Map<?, ?> path = ((Map<?, ?>) pathObj);

        final Object distanceObj = path.get("distance");
        final Object bboxObj = path.get("bbox");
        final Object weightObj = path.get("weight");
        final Object timeObj = path.get("time");
        final Object pointsObj = path.get("points");

        Preconditions.checkState(distanceObj instanceof Double, distanceObj);
        Preconditions.checkState(bboxObj instanceof List, bboxObj);
        Preconditions.checkState(weightObj instanceof Double, weightObj);
        Preconditions.checkState(timeObj instanceof Integer, timeObj);
        Preconditions.checkState(pointsObj instanceof String, pointsObj);

        final double distance = (double) distanceObj;
        final List<?> bbox = (List<?>) bboxObj;
        final double weight = (double) weightObj;
        final int time = (int) timeObj;
        final String points = (String) pointsObj;

        return new Path(null, null, distance, bbox, weight, time, points);
    }


    private void loadGraphWithDataFromDb(Integer size, Graph<AutomatVertex, DefaultEdge> completeGraph) {
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