package cz.geodatahackaton;

import cz.geodatahackaton.dao.CoordsDao;
import cz.geodatahackaton.dao.CoordsDaoMock;
import cz.geodatahackaton.entity.Coords;
import cz.geodatahackaton.strategy.CoordsStrategy;
import cz.geodatahackaton.strategy.CoordsStrategyBasic;
import cz.geodatahackaton.util.DistanceMatrixConfigKeys;
import cz.geodatahackaton.util.DistanceMatrixConstants;
import cz.geodatahackaton.util.DistanceMatrixUrlUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * @author cubeek
 */
public class DistanceMatrixRunner {

    private static final Properties props = new Properties();

    private final boolean execute;

    public static void main(String[] args) throws Exception {
        props.load(DistanceMatrixRunner.class.getResourceAsStream(DistanceMatrixConstants.CONFIG_FILE));
        new DistanceMatrixRunner(new CoordsStrategyBasic((new CoordsDaoMock()).getData()));
    }

    /**
     * Create new instance of the runner. Go through a list of coords, based on the specific strategy and process the results
     *
     * @param strategy filtering strategy
     */
    public DistanceMatrixRunner(final CoordsStrategy strategy) {
        execute = Boolean.parseBoolean(props.getProperty(DistanceMatrixConfigKeys.EXECUTE));

        if (execute) {
            for (Coords c : strategy.getCoordsList()) {
                downloadDistanceMatrix(c);
            }
        }
    }

    /**
     * Download the distance matrix for the specific {@link cz.geodatahackaton.entity.Coords}
     *
     * @param coords coords
     */
    private void downloadDistanceMatrix(Coords coords) {
        try {
            final URL url = new URL(DistanceMatrixUrlUtils.getMatrixUrl(props.getProperty(DistanceMatrixConfigKeys.URL_PTR), coords, null));
            final BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            final StringBuilder result = new StringBuilder(generateHeader(coords, url.toString()));

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
                        props.getProperty(DistanceMatrixConfigKeys.OUTPUT_FILE_SUFFIX));

        final BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(buff.toString());
        out.flush();
        out.close();
        System.out.println(buff.toString());
    }

    private String generateHeader(final Coords coords, String url) {
        return "Request\n=======\n" + coords.toString() + "URL:\n" + url + "\n\n" + "Response\n========\n";
    }

}
