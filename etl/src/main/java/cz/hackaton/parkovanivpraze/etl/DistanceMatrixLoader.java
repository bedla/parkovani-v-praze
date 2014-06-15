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

    public DistanceMatrixLoader(File file, URI neo4jServerUri) {
        this.file = file;
        this.neo4jServerUri = neo4jServerUri;

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

    }

    private static DistanceResponse parseResponse(List<String> resp) {
        return new Gson().fromJson(Joiner.on("\n").join(resp), DistanceResponse.class);
    }

    public static class ReqMap {
        public final Map<String, String> originMap;
        public final Map<String, Integer> originIdx;
        public final Map<String, String> destinationMap;
        public final Map<String, Integer> destinationIdx;
        public final String reqUrl;

        public ReqMap(Map<String, String> originMap, Map<String, Integer> originIdx,
                      Map<String, String> destinationMap, Map<String, Integer> destinationIdx,
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

        Map<String, Integer> originIdx = indexReqIndexes(origin, originMap);
        Map<String, Integer> destinationIdx = indexReqIndexes(destination, destinationMap);

        return new ReqMap(originMap, originIdx, destinationMap, destinationIdx, reqUrl);
    }

    private static Map<String, Integer> indexReqIndexes(List<String> list, Map<String, String> map) {
        Map<String, Integer> idxMap = Maps.newHashMap();
        for (String key : map.keySet()) {
            idxMap.put(key, Utils.findLineIdx(list, key));
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
