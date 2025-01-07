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

    public class StampedCloudPointsJSON{
        private int time;
        private String id;
        private List<List<Double>> cloudPoints;

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<List<Double>> getCloudPoints() {
            return cloudPoints;
        }

        public void setCloudPoints(List<List<Double>> cloudPoints) {
            this.cloudPoints = cloudPoints;
        }
    }

}
