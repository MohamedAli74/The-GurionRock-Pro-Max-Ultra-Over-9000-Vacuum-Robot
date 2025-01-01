package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;

import java.util.List;

public class Root {
    public Cameras Cameras;
    public Lidars LidarWorkers;
    public String PoseJsonFile;
    public int TickTime;
    public int Duration;

    public class Cameras{
        public List<Camera> CamerasConfigurations;
        public String cameraDatasPath;

        public List<Camera> getCamerasConfigurations() {
            return CamerasConfigurations;
        }

        public String getCameraDatasPath() {
            return cameraDatasPath;
        }
    }

    public class Lidars {
        private List<LiDarWorkerTracker> lidarConfigurations;
        private String lidarsDataPath;

        public String getLidarsDataPath() {
            return lidarsDataPath;
        }

        public List<LiDarWorkerTracker> getLidarConfigurations() {
            return lidarConfigurations;
        }
    }

}
