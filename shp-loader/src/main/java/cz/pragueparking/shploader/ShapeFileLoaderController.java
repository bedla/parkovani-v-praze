package cz.pragueparking.shploader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileSystemUtils;

import java.io.File;

@Controller
public class ShapeFileLoaderController implements CommandLineRunner {
    private static final Logger LOG = LoggerFactory.getLogger(ShapeFileLoaderController.class);

    @Value("${dbDataDir}")
    private String dbDataDir;

    @Value("${rootDataDir}")
    private String rootDataDir;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {

        FileSystemUtils.deleteRecursively(new File(dbDataDir));

        LOG.info("Creating Spatial alias");
        jdbcTemplate.execute("CREATE ALIAS IF NOT EXISTS SPATIAL_INIT FOR \"org.h2gis.h2spatialext.CreateSpatialExtension.initSpatialExtension\";");

        LOG.info("Initializing spatial extension");
        jdbcTemplate.execute("CALL SPATIAL_INIT();");

        loadData("DOP_ZPS_Stani_l_mercator.shp", "DOP_ZPS_Stani_l");
        createIndex("DOP_ZPS_Stani_l", "IDX_UID", "UID");
        createIndex("DOP_ZPS_Stani_l", "IDX_ZONA_L", "ZONA_L");

        loadData("DOP_ZachParkoviste_b_mercator.shp", "DOP_ZachParkoviste_b");
        createIndex("DOP_ZachParkoviste_b", "IDX_UID", "UID");

        loadData("DOP_ZachParkoviste_b_mercator-buffer20.shp", "DOP_ZachParkoviste_b_buffer20");
        createIndex("DOP_ZachParkoviste_b_buffer20", "IDX_UID", "UID");

        loadData("DOP_ZPS_Automaty_b_mercator.shp", "DOP_ZPS_Automaty_b");
        createIndex("DOP_ZPS_Automaty_b_mercator", "IDX_UID", "UID");
        createIndex("DOP_ZPS_Automaty_b_mercator", "IDX_TYP", "TYP");

        loadData("DOP_ZPS_Automaty_b_mercator-buffer20.shp", "DOP_ZPS_Automaty_b_buffer20");
        createIndex("DOP_ZPS_Automaty_b_buffer20", "IDX_UID", "UID");
        createIndex("DOP_ZPS_Automaty_b_buffer20", "IDX_TYP", "TYP");

    }

    private void loadData(String file, String tableName) {

        LOG.info(String.format("Loading spatial data for %s into table %s", file, tableName));
        jdbcTemplate.execute(String.format("CALL FILE_TABLE('%s', '%s');", rootDataDir + "/" + file, tableName));
    }

    private void createIndex(String table, String indexName, String... columns) {

        // TODO Spatial tabulka nemuze mit indexy. Podporovany jsou pouze Spatial indexy. Viz org.h2gis.drivers.file_table.H2Table.addIndex()
//        jdbcTemplate.execute("CREATE INDEX " + indexName + " ON " + table + "(" + Joiner.on(", ").join(columns) + ");");

    }

}