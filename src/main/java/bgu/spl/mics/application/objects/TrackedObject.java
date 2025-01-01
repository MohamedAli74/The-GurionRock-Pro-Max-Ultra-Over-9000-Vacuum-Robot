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
    private boolean isSentBefore;
    private List<CloudPoint> coordinates;

    public TrackedObject(int id, int time, String description, List<CloudPoint> coordinates

){
        this.id = id;
        this.time = time;
        this.description = description;
        isSentBefore = false;
        this.coordinates = coordinates

;
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
