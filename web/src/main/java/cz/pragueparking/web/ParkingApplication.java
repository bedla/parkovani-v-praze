package cz.pragueparking.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
public class ParkingApplication extends SpringBootServletInitializer {

    public static void main(String[] args) throws Exception {
        System.setProperty("spring.profiles.active", "local");
        SpringApplication.run(ParkingApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application
                .profiles("server")
                .sources(Config.class);
    }

    @EnableWebMvc
    @ComponentScan
    public static class Config extends WebMvcAutoConfiguration {

    }

}