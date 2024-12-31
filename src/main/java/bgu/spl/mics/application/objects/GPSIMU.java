package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.Vector;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick;
    private STATUS status;
    private List<Pose> PoseList;

    public GPSIMU(){
        currentTick = 0; //NOT SURE
        status = STATUS.UP;
        PoseList = new Vector<Pose>();//TO EDIT!!!
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public List<Pose> getPoseList() {
        return PoseList;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }
}
