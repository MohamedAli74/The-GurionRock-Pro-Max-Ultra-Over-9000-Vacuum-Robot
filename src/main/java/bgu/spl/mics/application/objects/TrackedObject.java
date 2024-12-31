package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    private int id;
    private int time;
    private String description;
    private List<CloudPoint> coordintaes;

    public TrackedObject(int id, int time, String description, List<CloudPoint> coordintaes){
        this.id = id;
        this.time = time;
        this.description = description;
        this.coordintaes = coordintaes;
    }

    public int getId() {
        return id;
    }

    public int getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public List<CloudPoint> getCoordintaes() {
        return coordintaes;
    }
}
