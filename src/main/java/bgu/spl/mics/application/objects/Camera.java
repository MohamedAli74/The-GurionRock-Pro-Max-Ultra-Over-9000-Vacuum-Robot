package bgu.spl.mics.application.objects;

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
                    statisticalFolder.inceaseNumDetectedObjects(cameraData.get(i).getDetectedObjectList().size());
                    return cameraData.get(i);
                }
            }

        }
        return null;
    }

    public boolean checkERROR(int time) {
        for (int i = 0; i < cameraData.size(); i++) {
            if (cameraData.get(i).getTime() == time) {
                for(DetectedObject d : cameraData.get(i).getDetectedObjectList()){
                    if(d.getId()=="ERROR")
                        return true;
                }
            }
        }
        return false;
    }
    ///////////////////////////getters for test://////////////////////////////
    public List<StampedDetectedObjects> getCameraData() {
        return cameraData;
    }

    public StatisticalFolder getStatisticalFolder() {
        return statisticalFolder;
    }

    public void setDetectedObjectsList(ArrayList<StampedDetectedObjects> stampedDetectedObjects) {

    }

    public void StatisticalFolder() {
    }
}
