package cz.geodatahackaton.util;

import com.google.common.base.Preconditions;
import cz.geodatahackaton.entity.Coordination;
import cz.geodatahackaton.entity.Coords;

import java.util.Iterator;
import java.util.List;

/**
 * @author cubeek
 */
public class DistanceMatrixUrlUtils {

    /**
     * Get URL for the matrix service.
     *
     * @param urlPattern url pattern
     * @param data origins & destinations wrapper
     * @param key current key
     * @return URL
     */
    public static String getMatrixUrl(final String urlPattern, final Coords data, final String key) {
        Preconditions.checkNotNull(urlPattern);
        Preconditions.checkNotNull(data);
        return getMatrixUrl(
                urlPattern,
                getUrlParams(data.getOrigins()),
                getUrlParams(data.getDestinations()),
                key);
    }

    /**
     * Get URL for the matrix service.
     *
     * <p>Use {@link #getUrlParams(java.util.List)}</p> to prepare the params for you.
     *
     * @param urlPattern url pattern
     * @param origins origins list
     * @param destinations destinations list
     * @param key current key
     * @return URL
     */
    private static String getMatrixUrl(final String urlPattern, final String origins, final String destinations, final String key) {
        return String.format(urlPattern, origins, destinations); // , key);
    }

    /**
     * Get params in <tt>41.43206,-81.38992|41.43206,-81.38992</tt> format.
     *
     * @param params a list of coordination values
     * @return params in <tt>41.43206,-81.38992|41.43206,-81.38992|...</tt> format
     */
    private static String getUrlParams(final List<Coordination> params) {
        final StringBuilder sb = new StringBuilder();
        final Iterator<Coordination> it = params.iterator();
        while (it.hasNext()) {
            sb.append(it.next().getValue());
            if (it.hasNext())
                sb.append('|');
        }
        return sb.toString();
    }

}
