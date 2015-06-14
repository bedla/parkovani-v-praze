package cz.pragueparking.web;

import com.google.common.collect.Range;
import cz.pragueparking.web.dto.Route;
import cz.pragueparking.web.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/route")
public class RouteRestController {

    @Autowired
    private RouteService routeService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Route> getRoute(@RequestParam double lat, @RequestParam double lng, @RequestParam int count) {
        return routeService.findRoutes(lat, lng, Range.closed(10, 20).contains(count) ? count : 10);
    }

}
