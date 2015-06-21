package cz.pragueparking.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
public class IndexController {

    @Value("${geoServerUrl}")
    private String geoServerUrl;

    @RequestMapping("/")
    public ModelAndView index() {
        final Map<String, Object> model = new HashMap<>();
        model.put("geoServerUrl", this.geoServerUrl);
        return new ModelAndView("index", model);
    }
}