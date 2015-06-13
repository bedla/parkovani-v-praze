package cz.pragueparking.dataloader;

import java.util.List;

public class Path {
    public AutomatVertex source;
    public AutomatVertex target;

    public double distance;
    public List<?> bbox;
    public double weight;
    public int time;
    public String points;

    public Path(AutomatVertex source, AutomatVertex target, double distance, List<?> bbox, double weight, int time, String points) {
        this.source = source;
        this.target = target;
        this.distance = distance;
        this.bbox = bbox;
        this.weight = weight;
        this.time = time;
        this.points = points;
    }

    @Override
    public String toString() {
        return "Path{" +
                "source=" + source +
                ", target=" + target +
                ", distance=" + distance +
                ", bbox=" + bbox +
                ", weight=" + weight +
                ", time=" + time +
                ", points='" + points + '\'' +
                '}';
    }
}
