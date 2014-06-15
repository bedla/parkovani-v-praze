package cz.hackaton.parkovanivpraze;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.io.CharStreams;

import java.io.*;
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

}
