package cz.pragueparking.dataloader;

import com.vividsolutions.jts.geom.GeometryFactory;
import cz.pragueparking.utils.Utils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
public class GraphDataLoader {
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", Utils.activateSpringProfilesAsString());
        SpringApplication.run(GraphDataLoader.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public GeometryFactory geometryFactory() {
        return new GeometryFactory();
    }
}
