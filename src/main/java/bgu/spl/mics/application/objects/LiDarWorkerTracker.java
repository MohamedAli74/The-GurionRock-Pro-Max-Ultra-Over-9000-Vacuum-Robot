package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.services.LiDarService;

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

    private int maxTick;


    public LiDarWorkerTracker(int id, int frequency){
        this.id = id ;
        this.frequency = frequency ;
        status = STATUS.UP;
        lastTrackedObjects = new Vector<TrackedObject>();
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

    public void setLastTrackedObjects(List<TrackedObject> lastTrackedObjects) {
        this.lastTrackedObjects = lastTrackedObjects;
    }

    public void DataBase() {
        this.dataBase = LiDarDataBase.getInstance("DATABASE INSTANCE INITIALIZED");
    }

    public void track(StampedDetectedObjects stampedDetectedObjects){
        if(getStatus() == STATUS.UP) {
            if (stampedDetectedObjects.getDetectedObjects().size() != 0) {
                List<DetectedObject> detectedObjects = stampedDetectedObjects.getDetectedObjects();
                int count = 0;
                for (DetectedObject detectedObject : detectedObjects) {
                    TrackedObject trackedObject = searchInLiDarDataBase(detectedObject.getId(), stampedDetectedObjects.getTime());
                    trackedObject.setDescription(detectedObject.getDescription());
                    lastTrackedObjects.add(trackedObject);
                    count++;
                }
                statisticalFolder.inceaseNumTrackedObjects(count);
            }
        }
    }

    private TrackedObject searchInLiDarDataBase(String id,int time) {
        List<CloudPoint> coordinates = null;
        List<StampedCloudPoints> dataBaseList = dataBase.getCloudPoints();
        for(StampedCloudPoints stampedCloudPoints : dataBaseList){
            if(stampedCloudPoints.getId().equals(id) && stampedCloudPoints.getTime() == time){
                coordinates = stampedCloudPoints.getCloudPoints();
            }
        }
        return new TrackedObject(id.toString(),time,"",coordinates);
    }

    public boolean checkERROR(int time) {
        for (int i = 0; i < dataBase.getCloudPoints().size(); i++) {
            if (dataBase.getCloudPoints().get(i).getTime() == time) {
                if(dataBase.getCloudPoints().get(i).getId().equals("ERROR")){
                    return true;
                }
            }
        }
        return false;
    }

    public CrashedBroadcast getCrashedbroadcast(int time, LiDarService faultySensor){
        CrashedBroadcast output = null;
        for (int i = 0; i < dataBase.getCloudPoints().size(); i++) {
            if (dataBase.getCloudPoints().get(i).getTime() == time) {
                if(dataBase.getCloudPoints().get(i).getId().equals("ERROR")){
                    output = new CrashedBroadcast("TO EDIT",faultySensor);
                }
            }
        }
        return output;
    }

    public TrackedObject getLastFrame() {
        return lastTrackedObjects.get(lastTrackedObjects.size());
    }

    public void StatisticalFolder() {
       this.statisticalFolder = StatisticalFolder.getInstance();
    }

    public void setMaxTick(int maxTick){
        this.maxTick= maxTick;
    }

    public int getMaxTick() {
        return maxTick;
    }
}
