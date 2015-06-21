package cz.pragueparking.geocoding;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.vividsolutions.jts.geom.Point;
import cz.pragueparking.utils.Utils;
import org.h2.api.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class GeoCodingController implements CommandLineRunner {
    public static final Map<String, String> AUTOMAT_DESC = new HashMap<>();
    public static final String URL = "http://open.mapquestapi.com/nominatim/v1/reverse.php?lat={point1}&lon={point2}&format=json&zoom=16";
    private static final Logger LOG = LoggerFactory.getLogger(GeoCodingController.class);
    private static final String NAMES_TABLE = "NAMES";

    static {
        // Fake data
        AUTOMAT_DESC.put("O1", "40,- Kč#Po - Pá, 8 - 18 h#15 min.#2 hod.");
        AUTOMAT_DESC.put("O2", "40,- Kč#Po - So, 8 - 18 h#30 min.#2 hod.");
        AUTOMAT_DESC.put("O3", "40,- Kč#Po - So, 8 - 20 h#40 min.#2 hod.");
        AUTOMAT_DESC.put("O4", "40,- Kč#Po - Pá, 8 - 18 h#30 min.#2 hod.");
        AUTOMAT_DESC.put("O5", "40,- Kč#Po - Ne, 8 - 18 h#30 min.#2 hod.");
        AUTOMAT_DESC.put("O6", "40,- Kč#Po - Ne, 8 - 20 h#40 min.#2 hod.");
        AUTOMAT_DESC.put("O7", "40,- Kč#Po - Pá, 8 - 18 h#30 min.#2 hod.");
        AUTOMAT_DESC.put("O8", "40,- Kč#Po - So, 8 - 18 h#30 min.#2 hod.");
        AUTOMAT_DESC.put("O9", "40,- Kč#Po - So, 8 - 20 h#40 min.#2 hod.");
        AUTOMAT_DESC.put("Z1", "15,- Kč#Po - Pá, 8 - 18 h#15 min. #6 hod.");
        AUTOMAT_DESC.put("Z2", "30,- Kč#Po - So, 8 - 18 h#15 min. #6 hod.");
        AUTOMAT_DESC.put("Z3", "30,- Kč#Po - Pá, 8 - 18 h#15 min. #6 hod.");
        AUTOMAT_DESC.put("Z4", "15,- Kč#Po - Ne, 8 - 18 h#15 min. #6 hod.");
        AUTOMAT_DESC.put("Z5", "30,- Kč#Po - So, 8 - 18 h#15 min. #6 hod.");
        AUTOMAT_DESC.put("Z6", "15,- Kč#Po - Pá, 8 - 18 h#15 min. #6 hod.");
        AUTOMAT_DESC.put("Z7", "30,- Kč#Po - Pá, 8 - 18 h#15 min. #6 hod.");
        AUTOMAT_DESC.put("Z8", "30,- Kč#Po - So, 8 - 18 h#15 min. #6 hod.");
    }

    @Value("${dbDataDir}")
    private String dbDataDir;
    @Value("${rootDataDir}")
    private String rootDataDir;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void run(String... args) throws Exception {

        ddlOrDelete();

        final List<Object[]> names = geoCodeNames();

        final String insertSql = "INSERT INTO " + NAMES_TABLE + " (id, name, automat) VALUES (?, ?, ?)";
        jdbcTemplate.batchUpdate(insertSql, names, names.size(), (ps, argument) -> {
            ps.setInt(1, (Integer) argument[0]);
            ps.setString(2, (String) argument[1]);
            ps.setString(3, (String) argument[2]);
        });

    }

    private List<Object[]> geoCodeNames() {

        // TODO remove Object[]
        final List<Object[]> names = new ArrayList<>();

        final List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT UID, THE_GEOM, TYP FROM DOP_ZPS_AUTOMATY_B");

        LOG.info("Count {}", list.size());

        final int count = list.size();
        int idx = 0;
        final StopWatch stopWatch = new StopWatch("geocode");

        for (Map<String, Object> map : list) {
//            if (idx == 10) break;
            stopWatch.start();
            final Object uidObj = map.get("UID");
            final Object geomObj = map.get("THE_GEOM");
            final Object typObj = map.get("TYP");

            if (uidObj instanceof Number && geomObj instanceof Point && typObj instanceof String) {

                final double[] doubles = Utils.transformMercatorToWgs84(((Point) geomObj).getX(), ((Point) geomObj).getY());

                final String name = geoCodeName(doubles[1], doubles[0]);

                names.add(new Object[]{((Number) uidObj).intValue(), name, getAutomat(typObj.toString())});

            } else {
                throw new IllegalStateException("Invalid data row " + map);
            }

            stopWatch.stop();
            if (idx % 10 == 0) {
                LOG.info(String.format("%.2f %s", (float) idx / (float) count * 100.0f, "%"));
            }
            idx++;
        }
        LOG.info(String.format("%.2f %s", (float) idx / (float) count * 100.0f, "%"));
        LOG.info(stopWatch.shortSummary());

        return names;
    }

    private String getAutomat(String typ) {
        return AUTOMAT_DESC.containsKey(typ) ? AUTOMAT_DESC.get(typ) : "N/A";
    }

    private String geoCodeName(double lat, double lng) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }

        final ResponseEntity<Map> entity = restTemplate.getForEntity(URL, Map.class, lat, lng);

        if (entity.getStatusCode().is2xxSuccessful()) {

            final Map<?, ?> body = entity.getBody();
            final Object addressObj = body.get("address");
            if (addressObj instanceof Map) {
                final Object roadObj = ((Map) addressObj).get("road");
                final Object suburbObj = ((Map) addressObj).get("suburb");
                final Object cityObj = ((Map) addressObj).get("city");

                return Joiner.on(", ").skipNulls().join(roadObj, suburbObj, cityObj);
            }
        }

        throw new IllegalStateException(String.format("Unable to reverse geocode %s", new UriTemplate(URL).expand(lat, lng)));
    }


    private void ddlOrDelete() {
        try {

            final int pathsCount = JdbcTestUtils.countRowsInTable(jdbcTemplate, NAMES_TABLE);
            if (pathsCount > 0) {
                JdbcTestUtils.deleteFromTables(jdbcTemplate, NAMES_TABLE);
            }

        } catch (BadSqlGrammarException e) {
            switch (e.getSQLException().getErrorCode()) {
                case ErrorCode.TABLE_OR_VIEW_NOT_FOUND_1:

                    jdbcTemplate.execute(new StringBuilder()
                            .append("CREATE TABLE ").append(NAMES_TABLE).append(" (\n")
                            .append(" id INT PRIMARY KEY,\n")
                            .append(" name VARCHAR,\n")
                            .append(" automat VARCHAR\n")
                            .append(");").toString());

                    break;
                default:
                    throw e;
            }
        }
    }

}