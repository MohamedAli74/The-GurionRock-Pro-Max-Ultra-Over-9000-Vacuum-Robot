package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.services.CameraService;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    private int id;
    private int frequency;
    private STATUS status;
    private List<StampedDetectedObjects> detectedObjectsList;
    private List<StampedDetectedObjects> cameraData;
    private StatisticalFolder statisticalFolder = StatisticalFolder.getInstance();

    private int maxTick;

    public Camera(int id, int frequency) {
        this.id = id;
        this.frequency = frequency;
        status = STATUS.UP;
        detectedObjectsList = new Vector<StampedDetectedObjects>();

        this.cameraData = new Vector<StampedDetectedObjects>();//will be edited in the main function;
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

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public void setCameraData(List<StampedDetectedObjects> cameraData) {
        this.cameraData = cameraData;
    }

    public StampedDetectedObjects detect(int time) {
        if (status == STATUS.UP) {
            for (int i = 0; i < cameraData.size(); i++) {
                if (cameraData.get(i).getTime() == time) {
                    detectedObjectsList.add(cameraData.get(i));
                    if(cameraData.get(i).getDetectedObjects() != null)
                        statisticalFolder.inceaseNumDetectedObjects(cameraData.get(i).getDetectedObjects().size());
                    return cameraData.get(i);
                }
            }
            if(time > maxTick)
                return null;
            else{//we are not done with the data but in the given tick there is no data
                StampedDetectedObjects empty = new StampedDetectedObjects(time , new ArrayList<DetectedObject>());
            }
        }
        return null;
    }

    public boolean checkERROR(int time) {
        for (int i = 0; i < cameraData.size(); i++) {
            if (cameraData.get(i).getTime() == time) {
                if (cameraData.get(i).getDetectedObjects() != null) {
                    for (DetectedObject d : cameraData.get(i).getDetectedObjects()) {
                        if (d.getId().equals("ERROR"))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public CrashedBroadcast getCrashedBroadcast(int time, CameraService faultySensor){
        CrashedBroadcast output=null;
        for (int i = 0; i < cameraData.size(); i++) {
            if (cameraData.get(i).getTime() == time) {
                if (cameraData.get(i).getDetectedObjects() != null) {
                    for (DetectedObject d : cameraData.get(i).getDetectedObjects()) {
                        if (d.getId().equals("ERROR"))
                            output = new CrashedBroadcast(d.description,faultySensor);
                    }
                }
            }
        }
        return output;
    }

    ///////////////////////////getters for test://////////////////////////////
    public List<StampedDetectedObjects> getCameraData() {
        return cameraData;
    }

    public StatisticalFolder getStatisticalFolder() {
        return statisticalFolder;
    }

    public void StatisticalFolder() {
        this.statisticalFolder = StatisticalFolder.getInstance();
    }
    public void setDetectedObjectsList(ArrayList<StampedDetectedObjects> detctedObjectsList) {
        this.detectedObjectsList=detctedObjectsList;
    }

    public void setMaxTick(int maxTick) {
        this.maxTick = maxTick;
    }

    public int getMaxTick() {
        return maxTick;
    }
}
