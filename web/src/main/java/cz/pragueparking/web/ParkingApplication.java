package cz.pragueparking.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
@ComponentScan
public class ParkingApplication extends WebMvcAutoConfiguration {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ParkingApplication.class, args);
    }

}