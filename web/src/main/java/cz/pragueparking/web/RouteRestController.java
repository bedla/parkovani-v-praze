package cz.pragueparking.web;

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
    public List<Route> getRoute(@RequestParam double lat, @RequestParam double lng) {
        return routeService.findRoutes(lat, lng);
    }

}
