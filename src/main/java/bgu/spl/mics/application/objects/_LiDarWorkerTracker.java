package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.Vector;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class _LiDarWorkerTracker {

    private int id;
    private int frequency;
    private STATUS status;
    private List<TrackedObject> lastTrackedObjects;
    //TO EDIT!!!!

    public _LiDarWorkerTracker(int id, int frequency){
        this.id = id ;
        this.frequency = frequency ;
        status = STATUS.UP;
        lastTrackedObjects = new Vector<TrackedObject>();
        //TO EDIT!!!
    }

    public int getId() {
        return id;
    }

    public int getFrequency() {
        return frequency;
    }

    public List<TrackedObject> getLastTrackedObjects() {
        return lastTrackedObjects;
    }

    public STATUS getStatus() {
        return status;
    }

}
