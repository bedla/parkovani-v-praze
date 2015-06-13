package cz.pragueparking.web.dto;


import java.util.Arrays;

public class Route {

    public int sourceUid;
    public int targetUid;
    public double distance;
    public String[] bbox;
    public int time;
    public String points;
    public String sourcePoint;
    public String targetPoint;

    public Route(int sourceUid, int targetUid, double distance, String[] bbox, int time, String points, String sourcePoint, String targetPoint) {
        this.sourceUid = sourceUid;
        this.targetUid = targetUid;
        this.distance = distance;
        this.bbox = bbox;
        this.time = time;
        this.points = points;
        this.sourcePoint = sourcePoint;
        this.targetPoint = targetPoint;
    }

    @Override
    public String toString() {
        return "Route{" +
                "sourceUid=" + sourceUid +
                ", targetUid=" + targetUid +
                ", distance=" + distance +
                ", bbox=" + Arrays.toString(bbox) +
                ", time=" + time +
                ", points='" + points + '\'' +
                ", sourcePoint='" + sourcePoint + '\'' +
                ", targetPoint='" + targetPoint + '\'' +
                '}';
    }
}
