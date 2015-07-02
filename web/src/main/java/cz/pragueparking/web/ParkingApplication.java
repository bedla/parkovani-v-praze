package cz.pragueparking.web;

import cz.pragueparking.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@SpringBootApplication
public class ParkingApplication extends SpringBootServletInitializer {

    public static void main(String[] args) throws Exception {
        System.setProperty("h2.implicitRelativePath", "true");
        System.setProperty("spring.profiles.active", Utils.activateSpringProfilesAsString());
        SpringApplication.run(ParkingApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        System.setProperty("h2.implicitRelativePath", "true");
        return application
                .profiles(Utils.activateSpringProfilesAsArray())
                .sources(Config.class);
    }

    @EnableWebMvc
    @ComponentScan
    public static class Config extends WebMvcAutoConfiguration {

        private static final Logger LOG = LoggerFactory.getLogger(Config.class);

        @Autowired
        private DataSource dataSource;

        @PreDestroy
        public void close() {

            dbShutdown();
            if (dataSource instanceof org.apache.tomcat.jdbc.pool.DataSource) {
                ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).close(true);
            }
        }

        /**
         * Shutdown database to prevent opened .db files
         */
        private void dbShutdown() {
            try (Connection connection = dataSource.getConnection()) {
                try (Statement statement = connection.createStatement()) {
                    statement.execute("SHUTDOWN");
                }
            } catch (SQLException e) {
                LOG.error("Unable to shutdown H2 database", e);
            }
        }
    }

}