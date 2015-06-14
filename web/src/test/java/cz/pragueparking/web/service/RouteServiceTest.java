package cz.pragueparking.web.service;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;
import cz.pragueparking.web.ParkingApplication;
import cz.pragueparking.web.dto.Route;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.StringReader;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ParkingApplication.class)
@WebIntegrationTest
public class RouteServiceTest {

    @Autowired
    RouteService routeService;

    @Test
    public void testFindMore() throws Exception {

        List<Route> routes = routeService.findRoutes(50.09097, 14.41839, 10, true);

        System.out.println(routes);
    }

    @Test
    public void testWKT() throws Exception {

        WKTReader wktReader = new WKTReader(new GeometryFactory());
        Geometry geometry = wktReader.read(new StringReader("POINT(50.0540058 14.2894902)"));
        System.out.println(geometry);

    }
}