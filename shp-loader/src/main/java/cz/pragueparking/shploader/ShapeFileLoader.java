package cz.pragueparking.shploader;

import cz.pragueparking.utils.Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
public class ShapeFileLoader {
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", Utils.activateSpringProfilesAsString());
        SpringApplication.run(ShapeFileLoader.class, args);
    }
}
