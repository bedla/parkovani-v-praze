package cz.geodatahackaton.entity;

import com.google.common.base.Preconditions;

/**
 * @author cubeek
 */
public class Coordinate {

    private String id;

    private String value;

    public Coordinate(String id, String value) {
        Preconditions.checkNotNull(value);

        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return id + ": " + value;
    }
}
