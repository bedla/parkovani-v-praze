package cz.hackaton.parkovanivpraze.etl;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import cz.hackaton.parkovanivpraze.Utils;
import cz.hackaton.parkovanivpraze.json.DistanceResponse;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author ivo.smid
 * @version $Revision:$
 */
public class DistanceMatrixLoader {

    private final File file;
    private final URI neo4jServerUri;
    private final String[] types;

    public DistanceMatrixLoader(File file, URI neo4jServerUri, String... types) {
        this.file = file;
        this.neo4jServerUri = neo4jServerUri;
        this.types = Preconditions.checkNotNull(types);
        Preconditions.checkArgument(types.length > 0);
    }

    public void load() {
        final List<String> lines = Utils.readLines(file);

        int idxReq = Utils.findLineIdx(lines, "== Request ==");
        int idxResp = Utils.findLineIdx(lines, "== Response ==");

        Preconditions.checkState(idxReq >= 0);
        Preconditions.checkState(idxResp >= 0);

        List<String> req = lines.subList(idxReq + 1, idxResp);
        List<String> resp = lines.subList(idxResp + 1, lines.size());

        ReqMap reqMap = parseRequest(req);
        DistanceResponse respDistance = parseResponse(resp);

        createRelations(reqMap, respDistance);
    }

    private void createRelations(ReqMap reqMap, DistanceResponse respDistance) {

        DistanceResponse.Row[] rows = respDistance.rows;
        for (int srcIdx = 0, rowsLength = rows.length; srcIdx < rowsLength; srcIdx++) {
            DistanceResponse.Row row = rows[srcIdx];

            DistanceResponse.Row.Element[] elements = row.elements;
            for (int dstIdx = 0, elementsLength = elements.length; dstIdx < elementsLength; dstIdx++) {
                DistanceResponse.Row.Element element = elements[dstIdx];

                if (!"OK".equals(element.status)) {
                    continue;
                }

                String originAddress = respDistance.origin_addresses[srcIdx];
                String destinationAddress = respDistance.destination_addresses[dstIdx];

                String srcKid = reqMap.originIdx.get(srcIdx);
                String dstKid = reqMap.destinationIdx.get(dstIdx);

                String typesStr = Joiner.on(":").join(types);
                String cypher = String.format("MATCH (a:%s),(b:%s)\n" +
                        "WHERE a.kid = '%s' AND b.kid = '%s'\n" +
                        "CREATE (a)-[:DISTANCE {m: %s}]->(b)\n" +
                        "CREATE (a)-[:DURATION {sec: %s}]->(b)\n" +
                        "SET a.address = '%s'\n" +
                        "SET b.address = '%s'",
                        typesStr, typesStr,
                        srcKid, dstKid,
                        element.distance.value, element.duration.value,
                        originAddress, destinationAddress);

                System.out.println("------------------------------------");
                System.out.println(cypher);
                Utils.send(neo4jServerUri, cypher);
            }

        }


    }

    private static DistanceResponse parseResponse(List<String> resp) {
        return new Gson().fromJson(Joiner.on("\n").join(resp), DistanceResponse.class);
    }

    public static class ReqMap {
        public final Map<String, String> originMap;
        public final Map<Integer, String> originIdx;
        public final Map<String, String> destinationMap;
        public final Map<Integer, String> destinationIdx;
        public final String reqUrl;

        public ReqMap(Map<String, String> originMap, Map<Integer, String> originIdx,
                      Map<String, String> destinationMap, Map<Integer, String> destinationIdx,
                      String reqUrl) {
            this.originMap = originMap;
            this.originIdx = originIdx;
            this.destinationMap = destinationMap;
            this.destinationIdx = destinationIdx;
            this.reqUrl = reqUrl;
        }
    }

    private static ReqMap parseRequest(List<String> req) {
        int idxOrigin = Utils.findLineIdx(req, "Origin:");
        int idxDestination = Utils.findLineIdx(req, "Destination:");
        int idxUrl = Utils.findLineIdx(req, "URL:");

        Preconditions.checkState(idxOrigin >= 0);
        Preconditions.checkState(idxDestination >= 0);
        Preconditions.checkState(idxUrl >= 0);

        List<String> origin = req.subList(idxOrigin + 1, idxDestination);
        List<String> destination = req.subList(idxDestination + 1, idxUrl);
        String reqUrl = req.get(idxUrl);

        Map<String, String> originMap = indexReqMap(origin);
        Map<String, String> destinationMap = indexReqMap(destination);

        Map<Integer, String> originIdx = indexReqIndexes(origin, originMap);
        Map<Integer, String> destinationIdx = indexReqIndexes(destination, destinationMap);

        return new ReqMap(originMap, originIdx, destinationMap, destinationIdx, reqUrl);
    }

    private static Map<Integer, String> indexReqIndexes(List<String> list, Map<String, String> map) {
        Map<Integer, String> idxMap = Maps.newHashMap();
        for (String key : map.keySet()) {
            idxMap.put(Utils.findLineIdx(list, key), key);
        }
        return idxMap;
    }

    private static Map<String, String> indexReqMap(List<String> origin) {
        return Maps.transformValues(
                Maps.uniqueIndex(origin, new Function<String, String>() {
                    @Override
                    public String apply(String input) {
                        return Splitter.on(": ").splitToList(input).get(0);
                    }
                }), new Function<String, String>() {
                    @Override
                    public String apply(String input) {
                        return Splitter.on(": ").splitToList(input).get(1);
                    }
                });
    }
}
