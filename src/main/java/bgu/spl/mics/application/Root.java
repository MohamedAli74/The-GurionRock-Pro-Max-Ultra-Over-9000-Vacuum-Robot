package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;

import java.util.List;

public class Root {
    public Cameras Cameras;
    public LidarWorkers LidarWorkers;
    public String poseJsonFile;
    public int TickTime;
    public int Duration;

    public class Cameras{
        public List<Camera> CamerasConfigurations;
        public String camera_datas_path;

        public List<Camera> getCamerasConfigurations() {
            return CamerasConfigurations;
        }

        public String getCamera_datas_path() {
            return camera_datas_path;
        }
    }

    public class LidarWorkers {
        private List<LiDarWorkerTracker> LidarConfigurations;
        private String lidars_data_path;

        public String getLidars_data_path() {
            return lidars_data_path;
        }

        public List<LiDarWorkerTracker> getLidarConfigurations() {
            return LidarConfigurations;
        }
    }
}
