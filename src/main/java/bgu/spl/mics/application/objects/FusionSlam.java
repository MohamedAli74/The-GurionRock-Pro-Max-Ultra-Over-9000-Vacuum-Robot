package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    private List<LandMark> landMarks;
    private List<Pose> poses;

    public List<LandMark> getLandMarks(){
        return landMarks;
    }

    public List<Pose> getPoses(){
        return poses;
    }

    public void updatePos(TrackedObject toUpdate,Pose pose){
        LandMark landMark = searchLandMark(toUpdate.getId());
        List<CloudPoint> globalCoordinates = fetchGlobalCoordinates(toUpdate,pose);
        if(landMark == null){
            landMark = new LandMark(toUpdate.getId()+"",toUpdate.getDescription(),globalCoordinates);
            landMarks.add(landMark);
        }else{
            updateLandMarkCoordinates(landMark,globalCoordinates);
        }
    }

    private LandMark searchLandMark(int id) {
        for(LandMark l : landMarks){
            if(Integer.parseInt(l.getId()) == id)
                return l;
        }
        return null;
    }

    private List<CloudPoint> fetchGlobalCoordinates(TrackedObject trackedObject, Pose pose) {
        List<CloudPoint> preCoordinates = trackedObject.getcoordinates();
        List<CloudPoint> output = new ArrayList<CloudPoint>();
        for(CloudPoint cloudPoint : preCoordinates){
            CloudPoint newGlobalPoint = convertLocalPointToGlobalPoint(cloudPoint,pose);
            output.add(newGlobalPoint);
        }
        return output;
    }

    private void updateLandMarkCoordinates(LandMark landMark, List<CloudPoint> globalCoordinates) {
        //TO EDIT!!!
    }

    private CloudPoint convertLocalPointToGlobalPoint(CloudPoint point, Pose pose) {
        double xLocal = point.getX();
        double yLocal = point.getY();
        double xRobot = pose.getX();
        double yRobot = pose.getY();

        double yawInRadian = Math.toRadians(pose.getYaw());
        double cosinYaw = Math.cos(yawInRadian);
        double sinYaw = Math.sin(yawInRadian);
        double xGlobal = (cosinYaw * xLocal) - (sinYaw * yLocal) + xRobot;
        double yGlobal = (sinYaw * xLocal) + (cosinYaw * yLocal) + yRobot;

        return new CloudPoint(xGlobal, yGlobal);
    }

    private FusionSlam(){
        this.landMarks = new Vector<LandMark>();
        this.poses = new Vector<Pose>();
    }

    // Singleton instance holder
    private static class FusionSlamHolder {
        private static final FusionSlam INSTANCE = new FusionSlam();

        public static FusionSlam getInstance(){
            return FusionSlamHolder.INSTANCE;
        }

    }
}
