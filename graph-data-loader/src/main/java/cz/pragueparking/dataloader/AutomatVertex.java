package cz.pragueparking.dataloader;


import com.vividsolutions.jts.geom.Point;

public class AutomatVertex {
    public int uid;
    public String type;
    public Point point;

    // TODO do not make hashCode and equals for now

    @Override
    public boolean equals(Object o) {
        if (uid == 0 && type == null && point == null) {
            return super.equals(o);
        }

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AutomatVertex that = (AutomatVertex) o;

        if (uid != that.uid) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        return !(point != null ? !point.equals(that.point) : that.point != null);

    }

    @Override
    public int hashCode() {
        if (uid == 0 && type == null && point == null) {
            return super.hashCode();
        }

        int result = uid;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (point != null ? point.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AutomatVertex{" +
                "uid=" + uid +
                ", type='" + type + '\'' +
                '}';
    }
}
