package cz.geodatahackaton;

import cz.geodatahackaton.dao.CoordinatesDaoMock;
import cz.geodatahackaton.entity.Coordinates;
import cz.geodatahackaton.strategy.CoordinatesStrategy;
import cz.geodatahackaton.strategy.CoordinatesStrategyRectangular;
import cz.geodatahackaton.util.DistanceMatrixConfigKeys;
import cz.geodatahackaton.util.DistanceMatrixConstants;
import cz.geodatahackaton.util.DistanceMatrixUrlUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 * Runner for the matrix request(s).
 *
 * @author cubeek
 */
public class DistanceMatrixRunner {

    private static final Properties props = new Properties();

    public static void main(String[] args) throws Exception {
        props.load(DistanceMatrixRunner.class.getResourceAsStream(DistanceMatrixConstants.CONFIG_FILE));
        new DistanceMatrixRunner(
                new CoordinatesStrategyRectangular(
                        (new CoordinatesDaoMock()).getData(),
                        Integer.parseInt(props.getProperty(DistanceMatrixConfigKeys.REQUEST_LIMIT)))
        );
    }

    /**
     * Create new instance of the runner. Go through a list of coords, based on the specific strategy and process the results
     *
     * @param strategy filtering strategy
     */
    public DistanceMatrixRunner(final CoordinatesStrategy strategy) {
        List<Coordinates> coordinates = strategy.getCoordsList();

        for (Coordinates c : coordinates) {
            downloadDistanceMatrix(c);

            try {
                Thread.sleep(Long.parseLong(props.getProperty(DistanceMatrixConfigKeys.REQUEST_DELAY)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Download the distance matrix for the specific {@link cz.geodatahackaton.entity.Coordinates}
     *
     * @param coordinates coordinates
     */
    private void downloadDistanceMatrix(Coordinates coordinates) {
        try {
            final URL url = new URL(DistanceMatrixUrlUtils.getMatrixUrl(props.getProperty(DistanceMatrixConfigKeys.URL_PTR), coordinates, null));
            if (!Boolean.parseBoolean(props.getProperty(DistanceMatrixConfigKeys.EXECUTE))) {
                System.out.println(url.toString());
                return;
            }
            final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            final StringBuilder result = new StringBuilder(generateHeader(coordinates, url.toString()));

            String buff;
            while (null != (buff = reader.readLine())) {
                result.append(buff).append("\n");
            }

            flush(result);
        } catch (MalformedURLException e) {
            System.err.println(String.format("Error creating URL: %s", e.getMessage()));
        } catch (IOException e) {
            System.err.println(String.format("IO Exception occurred! %s", e.getMessage()));
        }
    }

    /**
     * Save a buffer to a file
     *
     * @param buff buffer to be saved
     * @throws IOException
     */
    private void flush(final StringBuilder buff) throws IOException {
        final File file = new File(
                props.getProperty(DistanceMatrixConfigKeys.FOLDER_URL) +
                        props.getProperty(DistanceMatrixConfigKeys.OUTPUT_FILE_PREFIX) +
                        System.currentTimeMillis() +
                        props.getProperty(DistanceMatrixConfigKeys.OUTPUT_FILE_SUFFIX)
        );

        final BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(buff.toString());
        out.flush();
        out.close();
        System.out.println(buff.toString());
    }

    private String generateHeader(final Coordinates coordinates, String url) {
        return "== Request ==\n" + coordinates.toString() + "URL:\n" + url + "\n\n" + "== Response ==\n";
    }

}
