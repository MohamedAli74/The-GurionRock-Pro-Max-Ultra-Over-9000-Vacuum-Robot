package bgu.spl.mics.application.objects;

import com.google.gson.Gson;

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
    private StatisticalFolder statisticalFolder = StatisticalFolder.getInstance();

    private FusionSlam(){
        this.landMarks = new Vector<LandMark>();
        this.poses = new Vector<Pose>();
    }
    public List<LandMark> getLandMarks(){
        return landMarks;
    }

    public List<Pose> getPoseslist(){
        return poses;
    }
    public Pose getPose(int time)
    {
        for (Pose pose : poses)
        {
            if (pose.getTime() == time)
            {
                return  pose;
            }
        }
        return null;
    }

    public static FusionSlam getInstance(){
        return FusionSlamHolder.INSTANCE;
    }

    public List<CloudPoint> convertLocalPointsToGlobalPoints(List<CloudPoint> coordinates, Pose pose)
    {
        List<CloudPoint> output = new ArrayList<>();
        for (CloudPoint point : coordinates)
        {
            output.add(convert(point,pose));
        }
        return output;
    }

    public CloudPoint convert(CloudPoint point, Pose pose) {
        Gson gson = new Gson();
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
    public LandMark CheckLandMark(TrackedObject trackedObject)
    {
        for (LandMark landMark : landMarks)
        {
            if (landMark.getId().equals(trackedObject.getId()))
            {
                landMarks.remove(landMark);
                return landMark;
            }

        }
        return null;
    }
    public List<CloudPoint> newCoordinates(List<CloudPoint> alreadyCalculated,List<CloudPoint> newScanned)
    {
        List<CloudPoint> output = new ArrayList<>();
        int i;
        for(i=0;i<alreadyCalculated.size()&i<newScanned.size();i++)
        {
            output.add(getAverage(alreadyCalculated.get(i),newScanned.get(i)));
        }
        if(i<newScanned.size())
            for(;i<newScanned.size();i++)
                output.add(newScanned.get(i));
        return output;
    }
    public CloudPoint getAverage(CloudPoint cloudPoint1,CloudPoint cloudPoint2)
    {
        return new CloudPoint((cloudPoint1.getX()+cloudPoint2.getX())/2.0,(cloudPoint1.getY()+cloudPoint2.getY())/2.0);
    }

    public StatisticalFolder getStatisticalFolder() {
        return statisticalFolder;
    }

    // Singleton instance holder
    public static class FusionSlamHolder {
        private static final FusionSlam INSTANCE = new FusionSlam();

        public static FusionSlam getInstance(){
            return FusionSlamHolder.INSTANCE;
        }
    }
}