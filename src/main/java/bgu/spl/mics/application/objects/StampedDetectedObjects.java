package bgu.spl.mics.application.objects;
import java.util.List;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    private int time;
    private List<DetectedObject> detectedObjectList;

    public StampedDetectedObjects(int time , List<DetectedObject> list ){
        this.time = time;
        detectedObjectList = list;
    }

    public List<DetectedObject> getDetectedObjectList() {
        return detectedObjectList;
    }

    public int getTime() {
        return time;
    }
}
