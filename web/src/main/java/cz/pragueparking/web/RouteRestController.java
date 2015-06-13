package cz.pragueparking.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/route")
public class RouteRestController {

    @RequestMapping(method = RequestMethod.GET)
    public Object getRoute() {
        final Map<Object, Object> map = new HashMap<>();
        map.put("foo", "bar");
        return map;
    }

}
