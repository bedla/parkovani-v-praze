package cz.pragueparking.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WelcomeController {

    @Value("${application.message:Hello World}")
    private String message;


    @RequestMapping("/")
    public ModelAndView welcome() {
        Map<String, Object> model = new HashMap<>();
        model.put("time", new Date());
        model.put("message", this.message);
        return new ModelAndView("index", "messages", model);
    }

    @RequestMapping("/foo")
    public String foo(Map<String, Object> model) {
        throw new RuntimeException("Foo");
    }

}