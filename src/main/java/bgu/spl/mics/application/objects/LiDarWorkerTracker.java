package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.Vector;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {

    private int id;
    private int frequency;
    private STATUS status;
    private List<TrackedObject> lastTrackedObjects;
    LiDarDataBase dataBase;

    public LiDarWorkerTracker(int id, int frequency){
        this.id = id ;
        this.frequency = frequency ;
        status = STATUS.UP;
        lastTrackedObjects = new Vector<TrackedObject>();
        String DATA_BASE_FILE_PATH = "example input/lidar_data.json";
        LiDarDataBase dataBase =LiDarDataBase.getInstance(DATA_BASE_FILE_PATH);
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

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public List<TrackedObject> track(StampedDetectedObjects stampedDetectedObjects){
        int currentTick = stampedDetectedObjects.getTime();
        List<TrackedObject> output = null;
        if(getStatus() == STATUS.UP){
            output = new Vector<TrackedObject>();
            List<DetectedObject> detectedObjects = stampedDetectedObjects.getDetectedObjectList();
            for(DetectedObject detectedObject : detectedObjects){
                TrackedObject trackedObject = searchInLiDarDataBase(detectedObject.getId());
                output.add(trackedObject);
            }
        }
        return output;
    }

    private TrackedObject searchInLiDarDataBase(int id) {
        int time = -1;
        List<CloudPoint> coordinates = null;
        List<StampedCloudPoints> dataBaseList = dataBase.getCloudPoints();
        for(StampedCloudPoints stampedCloudPoints : dataBaseList){
            if(stampedCloudPoints.getId() == id){
                time = stampedCloudPoints.getTime();
                coordinates = stampedCloudPoints.getCloudPoints();
            }
        }
        return new TrackedObject(id,time,"",coordinates);
    }
}
