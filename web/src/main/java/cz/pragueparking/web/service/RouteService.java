package cz.pragueparking.web.service;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import cz.pragueparking.utils.Utils;
import cz.pragueparking.web.dto.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class RouteService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Route> findRoutes(double lat, double lng, int count) {

        final double[] doubles = Utils.transformWgs84ToMercator(lng, lat);
        final String sql = String.format("SELECT uid FROM DOP_ZPS_Automaty_b_buffer20 as t WHERE ST_Contains(t.the_geom, 'POINT(%s %s)') = TRUE ", doubles[0], doubles[1]);

        final List<Integer> listStartAutomaty = jdbcTemplate.queryForList(sql, Integer.class);

        final int uid;
        if (listStartAutomaty.size() == 0) {
            final String point = String.format("%s %s", doubles[0], doubles[1]);
            final String sqlClosest = String.format("SELECT uid  FROM DOP_ZPS_Automaty_b_buffer20 as t where ST_Distance(t.the_geom, 'POINT(%s)') > 0 order by ST_Distance(t.the_geom, 'POINT(%s)') limit 1", point, point);

            final Integer id = jdbcTemplate.queryForObject(sqlClosest, Integer.class);
            if (id != null) {
                uid = id;
            } else {
                return Lists.newArrayList();
            }
        } else {
            uid = listStartAutomaty.get(0);
        }

        final List<Route> routes = jdbcTemplate.query("select p.*, a.the_geom as source_point, b.the_geom as target_point\n" +
                "from PATHS as p \n" +
                "inner join DOP_ZPS_AUTOMATY_B as a on p.source_uid = a.uid \n" +
                "inner join DOP_ZPS_AUTOMATY_B as b on p.target_uid = b.uid \n" +
                "where p.source_uid = ?\n" +
                "order by distance\n" +
                "limit ?", new RowMapper<Route>() {
            @Override
            public Route mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Route(rs.getInt(2),
                        rs.getInt(3),
                        rs.getDouble(4),
                        Splitter.on(',').splitToList(rs.getString(5)).toArray(new String[0]),
                        rs.getInt(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getString(10));
            }
        }, uid, count);

        return routes;
    }
}
