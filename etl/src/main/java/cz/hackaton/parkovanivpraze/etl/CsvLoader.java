package cz.hackaton.parkovanivpraze.etl;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Doubles;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;
import cz.hackaton.parkovanivpraze.Utils;
import cz.hackaton.parkovanivpraze.json.Neo4JResponse;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author ivo.smid
 * @version $Revision:$
 */
public class CsvLoader {

    private final File file;
    private final URI neo4jServerUri;
    private final String[] types;
    private final String splitStr;

    public CsvLoader(File file, URI neo4jServerUri, String splitStr, String... types) {
        this.file = file;
        this.neo4jServerUri = neo4jServerUri;
        this.splitStr = splitStr;
        this.types = Preconditions.checkNotNull(types);
        Preconditions.checkArgument(types.length > 0);
    }

    public void clearDb() {
        Utils.send(neo4jServerUri, "MATCH (n) OPTIONAL MATCH (n)-[r]-() DELETE n,r");
    }

    public void load() {

        String typesStr = Joiner.on(":").join(types);
        String query = "CREATE (:" + typesStr + " { kid: \"%s\", lat : %s, lng : %s, typ : \"%s\", hodiny : \"%s\", tarif : \"%s\"})";

        List<String> requestLines = Lists.newArrayList();

        Map<Integer, String> map = Maps.newHashMap();

        boolean firstLine = true;
        for (String line : Utils.readLines(file)) {
            if (firstLine) {
                firstLine = false;

                int i = 0;
                for (String col : Splitter.on(splitStr).splitToList(line)) {
                    Preconditions.checkArgument(!map.containsValue(col), "Map %s already contains column name %s", map, col);
                    map.put(i, col);
                    i++;
                }
            } else {
                List<String> values = Splitter.on(splitStr).splitToList(line);

                Double lat = null;
                Double lng = null;
                String kid = null;
                String typ = null;
                String hodiny = null;
                String tarif = null;
                for (int i = 0, n = values.size(); i < n; i++) {
                    String val = values.get(i);
                    String col = map.get(i);
                    switch (col.toLowerCase()) {
                        case "kid":
                            kid = val;
                            break;
                        case "lat":
                            lat = Doubles.tryParse(val);
                            break;
                        case "lng":
                            lng = Doubles.tryParse(val);
                            break;
                        case "typ":
                            typ = val;
                            break;
                        case "hodiny":
                            hodiny = val;
                            break;
                        case "tarif_kc_h":
                            tarif = val;
                            break;
                    }
                }

                Preconditions.checkNotNull(kid, "KID if NULL for [%s]", line);
                Preconditions.checkNotNull(lat, "Lat if NULL for [%s]", line);
                Preconditions.checkNotNull(lng, "Lng if NULL for [%s]", line);
                Preconditions.checkNotNull(typ, "TYP if NULL for [%s]", line);
                Preconditions.checkNotNull(hodiny, "HODINY if NULL for [%s]", line);
                Preconditions.checkNotNull(tarif, "TARIF if NULL for [%s]", line);

                requestLines.add(String.format(query, kid, lat, lng, typ, hodiny, tarif));
            }


            if (requestLines.size() >= 100) {
                createNodes(requestLines);
                requestLines.clear();
            }
        }

        createNodes(requestLines);
    }

    public int countNodes() {
        System.out.println(Utils.send(neo4jServerUri, "MATCH (a:" + Joiner.on(":").join(types) + ") RETURN count(a)"));
        return 0;
    }

    private void createNodes(List<String> requestLines) {
        Neo4JResponse response = new Gson().fromJson(Utils.send(neo4jServerUri, Joiner.on("\n").join(requestLines)), Neo4JResponse.class);
        System.out.println(response);
    }
}
