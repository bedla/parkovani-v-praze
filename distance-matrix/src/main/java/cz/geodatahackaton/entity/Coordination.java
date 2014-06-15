package cz.geodatahackaton.entity;

import com.google.common.base.Preconditions;

/**
 * @author cubeek
 */
public class Coordination {

    private int id;

    private String value;

    public Coordination(int id, String value) {
        Preconditions.checkNotNull(value);

        this.id = id;
        this.value = value;
    }

    public int getId() {
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
