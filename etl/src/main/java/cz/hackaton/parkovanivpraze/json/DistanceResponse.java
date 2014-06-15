package cz.hackaton.parkovanivpraze.json;

import com.google.common.collect.ObjectArrays;

import java.util.Arrays;

/**
 * @author ivo.smid
 * @version $Revision:$
 */
public class DistanceResponse {

    public final String[] destination_addresses = ObjectArrays.newArray(String.class, 0);
    public final String[] origin_addresses = ObjectArrays.newArray(String.class, 0);
    public final String status = null;
    public final Row[] rows = ObjectArrays.newArray(Row.class, 0);

    @Override
    public String toString() {
        return "DistanceResponse{" +
                "destination_addresses=" + Arrays.toString(destination_addresses) +
                ", origin_addresses=" + Arrays.toString(origin_addresses) +
                ", status='" + status + '\'' +
                ", rows=" + Arrays.toString(rows) +
                '}';
    }

    public static class Row {
        public final Element[] elements = ObjectArrays.newArray(Element.class, 0);

        @Override
        public String toString() {
            return "Row{" +
                    "elements=" + Arrays.toString(elements) +
                    '}';
        }

        public static class Element {

            public final Value distance = null;
            public final Value duration = null;
            public final String status = null;

            @Override
            public String toString() {
                return "Element{" +
                        "distance=" + distance +
                        ", duration=" + duration +
                        '}';
            }

            public static class Value {
                public final String text = null;
                public final Integer value = null;

                @Override
                public String toString() {
                    return "Value{" +
                            "text='" + text + '\'' +
                            ", value=" + value +
                            '}';
                }
            }
        }
    }
}
