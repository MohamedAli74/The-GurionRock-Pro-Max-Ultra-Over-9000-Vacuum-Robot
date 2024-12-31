package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.Vector;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class _FusionSlam {
    private List<LandMark> landMarks;
    private List<Pose> poses;

    private _FusionSlam(){
        this.landMarks = new Vector<LandMark>();//TO EDIT!!!
        this.poses = new Vector<Pose>();//TO EDIT!!!
    }

    // Singleton instance holder
    private static class FusionSlamHolder {
        private static final _FusionSlam INSTANCE = new _FusionSlam();

        public static _FusionSlam getInstance(){
            return FusionSlamHolder.INSTANCE;
        }

        public List<LandMark> getLandMarks(){
            return INSTANCE.landMarks;
        }

        public List<Pose> getPoses(){
            return INSTANCE.poses;
        }
    }
}
