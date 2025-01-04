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
            if (landMark.getId() == trackedObject.getId())
            {
                landMarks.remove(landMark);
                return landMark;
            }

        }
        return null;
    }
    public List<CloudPoint> newCoordinates(List<CloudPoint> coordinates1,List<CloudPoint> coordinates2)
    {
        List<CloudPoint> output = new ArrayList<>();
        for(int i=0;i<coordinates1.size();i++)
        {
            output.add(getAverage(coordinates1.get(i),coordinates2.get(i)));
        }
        return output;
    }
    public CloudPoint getAverage(CloudPoint cloudPoint1,CloudPoint cloudPoint2)
    {
        return new CloudPoint((cloudPoint1.getX()+cloudPoint2.getX())/2.0,(cloudPoint1.getY()+cloudPoint2.getY())/2.0);
    }

    // Singleton instance holder
    public static class FusionSlamHolder {
        private static final FusionSlam INSTANCE = new FusionSlam();

        public static FusionSlam getInstance(){
            return FusionSlamHolder.INSTANCE;
        }

    }
}
