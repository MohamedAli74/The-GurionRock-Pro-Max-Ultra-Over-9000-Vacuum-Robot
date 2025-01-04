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
    private StatisticalFolder statisticalFolder = StatisticalFolder.getInstance();


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

    public void track(StampedDetectedObjects stampedDetectedObjects){
        if(getStatus() == STATUS.UP){
            List<DetectedObject> detectedObjects = stampedDetectedObjects.getDetectedObjectList();
            int count = 0;
            for(DetectedObject detectedObject : detectedObjects){
                TrackedObject trackedObject = searchInLiDarDataBase(detectedObject.getId(),stampedDetectedObjects.getTime());
                trackedObject.setDescription(detectedObject.getDescription());
                lastTrackedObjects.add(trackedObject);
                count++;
            }
            statisticalFolder.inceaseNumTrackedObjects(count);
        }
    }

    private TrackedObject searchInLiDarDataBase(String id,int time) {
        List<CloudPoint> coordinates = null;
        List<StampedCloudPoints> dataBaseList = dataBase.getCloudPoints();
        for(StampedCloudPoints stampedCloudPoints : dataBaseList){
            if(stampedCloudPoints.getId() == id && stampedCloudPoints.getTime() == time){
                coordinates = stampedCloudPoints.getCloudPoints();
            }
        }
        return new TrackedObject(id.toString(),time,"",coordinates);
    }

    public boolean checkERROR(int time) {
        for (int i = 0; i < dataBase.getCloudPoints().size(); i++) {
            if (dataBase.getCloudPoints().get(i).getTime() == time) {
                if(dataBase.getCloudPoints().get(i).getId()=="ERROR"){
                    return true;
                }
            }
        }
        return false;
    }

}
