package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    private String id;
    private int time;
    private String description;
    private List<CloudPoint> coordinates;
    private boolean isSentBefore;

    public TrackedObject(String id, int time, String description, List<CloudPoint> coordinates

){
        this.id = id;
        this.time = time;
        this.description = description;
        this.coordinates = coordinates;
        isSentBefore = false;
    }

    public String getId() {
        return id;
    }

    public int getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSentBefore() {
        return isSentBefore;
    }

    public void setSentBefore(boolean sentBefore)
    {
        isSentBefore = sentBefore;
    }

    public List<CloudPoint> getcoordinates() {
        return coordinates;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
