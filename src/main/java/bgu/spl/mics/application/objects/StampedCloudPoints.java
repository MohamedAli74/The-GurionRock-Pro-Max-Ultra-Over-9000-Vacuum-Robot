package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    private int id;
    private int time;
    private List<CloudPoint> cloudPoints;

    public StampedCloudPoints(int id, int time, List<CloudPoint> cloudPoints){
        this.id = id;
        this.time = time;
        this.cloudPoints=cloudPoints;
    }

    public int getTime() {
        return time;
    }

    public int getId() {
        return id;
    }

    public List<CloudPoint> getCloudPoints() {
        return cloudPoints;
    }
}
