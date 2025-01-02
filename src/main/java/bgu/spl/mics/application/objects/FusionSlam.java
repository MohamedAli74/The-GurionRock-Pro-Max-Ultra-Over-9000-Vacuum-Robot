package bgu.spl.mics.application.objects;

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

    // Singleton instance holder
    private static class FusionSlamHolder {
        private static final FusionSlam INSTANCE = new FusionSlam();

        public static FusionSlam getInstance(){
            return FusionSlamHolder.INSTANCE;
        }

    }
}
