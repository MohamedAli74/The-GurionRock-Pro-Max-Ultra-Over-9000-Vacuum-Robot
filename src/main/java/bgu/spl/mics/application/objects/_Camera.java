package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.Vector;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class _Camera {
    private int id;
    private int frequency;
    private STATUS status;
    private List<StampedDetectedObjects> detectedObjectsList;
    //TO EDIT!!!

    public _Camera(int id, int frequency){
        this.id = id;
        this.frequency = frequency;
        status = STATUS.UP;
        detectedObjectsList = new Vector<StampedDetectedObjects>();
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public List<StampedDetectedObjects> getDetectedObjectsList() {
        return detectedObjectsList;
    }

    public STATUS getStatus() {
        return status;
    }

}
