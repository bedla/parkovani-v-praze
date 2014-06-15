package cz.hackaton.parkovanivpraze;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.LoggingFilter;

import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * @author ivo.smid
 * @version $Revision:$
 */
public class Utils {

    public static List<String> readLines(File file) {
        try {
            return CharStreams.readLines(new BufferedReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8)));
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }


    public static int findLineIdx(List<String> lines, final String startsWith) {
        return Iterables.indexOf(lines, new Predicate<String>() {
            @Override
            public boolean apply(String line) {
                return line.startsWith(startsWith);
            }
        });
    }


    public static File file(String fileName, ClassLoader classLoader) {
        try {
            return new File(classLoader.getResource(fileName).toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static String send(URI neo4jServerUri, String query) {
        Client client = Client.create();
        client.addFilter(new LoggingFilter(System.out));
        WebResource resource = client.resource(neo4jServerUri + "transaction/commit");

        ClientResponse response = resource
                .accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .entity("{\"statements\":[{\"statement\":" + new Gson().toJson(query) + ",\"resultDataContents\":[\"row\",\"graph\"],\"includeStats\":true}]}")
                .post(ClientResponse.class);

        String responseStr = response.getEntity(String.class);

        System.out.println(String.format("POST status code [%d], returned data: %s\n%s", response.getStatus(), response, responseStr));

        response.close();

        return responseStr;
    }
}
