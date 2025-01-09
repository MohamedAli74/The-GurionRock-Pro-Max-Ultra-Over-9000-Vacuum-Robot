package bgu.spl.mics.application;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
//import com.google.gson.reflect.TypeToken;
//import jdk.internal.foreign.CABI;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 * <p>
 * This class initializes the system and starts the simulation by setting up
 * services, objects, and configurations.
 * </p>
 */
public class GurionRockRunner {

    /**
     * The main method of the simulation.
     * This method sets up the necessary components, parses configuration files,
     * initializes services, and starts the simulation.
     *
     * @param args Command-line arguments. The first argument is expected to be the path to the configuration file.
     */
    public static void main(String[] args) {
        Gson gson = new Gson();
        MessageBusImpl messageBus = MessageBusImpl.getInstance();
        try {
            FileReader configReader = new FileReader(args[0]);
            Root root = gson.fromJson(configReader, Root.class);
            FileReader cameraDataReader = new FileReader("example_input/" +root.Cameras.camera_datas_path.substring(0));
                                            //TO EDIT!!!
            Type listType = new TypeToken<Map<String, List<StampedDetectedObjects>>>() {}.getType();
            Map<String, List<StampedDetectedObjects>> cameraDatas = gson.fromJson(cameraDataReader, listType);

            List<CameraService> CameraServiceList = new ArrayList<CameraService>();
            for (Camera c : root.Cameras.getCamerasConfigurations()) {
                c.setCameraData(cameraDatas.get("camera"+c.getId()));
                c.setStatus(STATUS.UP);
                c.setDetectedObjectsList(new ArrayList<StampedDetectedObjects>());
                c.StatisticalFolder();
                CameraServiceList.add(new CameraService(c));
            }

            List<LiDarService> LidarServices = new ArrayList<LiDarService>();
            LiDarDataBase dataBase = LiDarDataBase.getInstance("example_input/" +root.LidarWorkers.getLidars_data_path().substring(0));
                                                //TO EDIT!!!
            for (LiDarWorkerTracker lidar : root.LidarWorkers.getLidarConfigurations()) {
                lidar.setStatus(STATUS.UP);
                lidar.setLastTrackedObjects(new ArrayList<>());
                lidar.DataBase();
                lidar.StatisticalFolder();
                LidarServices.add(new LiDarService(lidar));
            }


            GPSIMU gpsimu = new GPSIMU("example_input/" +root.poseJsonFile.substring(0));
                                            //TO EDIT!!!
            PoseService poseService = new PoseService(gpsimu);

            FusionSlamService fusionSlamService = new FusionSlamService(FusionSlam.getInstance());
            fusionSlamService.setWaitingFor(
                    root.Cameras.getCamerasConfigurations().size()+root.LidarWorkers.getLidarConfigurations().size());

            List<Thread> cameraThreads = new ArrayList<>();
            List<Thread> lidarThreads = new ArrayList<>();
            for (int i = 0; i < CameraServiceList.size(); i++){
                cameraThreads.add(new Thread(CameraServiceList.get(i)));
                cameraThreads.get(i).start();
            }
            for (int i = 0; i < LidarServices.size(); i++) {
                lidarThreads.add(new Thread(LidarServices.get(i)));
                lidarThreads.get(i).start();
            }
            Thread poseServiceThread = new Thread(poseService);
            Thread fusionSlamServiceThread = new Thread(fusionSlamService);
            poseServiceThread.start();
            fusionSlamServiceThread.start();


            CrasherService crasherService = new CrasherService();
            Thread crasherThread = new Thread(crasherService);
            crasherThread.start();

            TimeService timeService = new TimeService(root.TickTime, root.Duration);
            Thread timeServiceThread = new Thread(timeService);
            timeServiceThread.start();
            try {
                timeServiceThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            File configFile = new File(args[0]);
            if(crasherService.isCrashed()){
                errorOutput(configFile.getParent(),crasherService);
            }
            else{
                regularOutput(configFile.getParent(), StatisticalFolder.getInstance(), fusionSlamService.getFusionSlam().getLandMarks());
            }


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void regularOutput(String path, StatisticalFolder statisticalFolder, List<LandMark> World_Map){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File output = new File(path + File.separator + "output_file.json");
        RegularOutput regularOutput = new RegularOutput(statisticalFolder, World_Map);
        try {
            FileWriter writer = new FileWriter(output);
            gson.toJson(regularOutput,writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void errorOutput(String path, CrasherService crasherService){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File output = new File(path + File.separator + "output_file.json");
        String faultySensor= crasherService.getFaultySensor().getName();
        String Error = crasherService.getError();
        List<Pose> poses = crasherService.getPosesUntilCrash();

        ErrorOutput errorOutput;
        if(crasherService.getFlag()==1){
            errorOutput = new ErrorOutputCamera(
                    Error,
                    faultySensor,
                    poses,
                    StatisticalFolder.getInstance(),
                    ((CameraService)crasherService.getFaultySensor()).getLastFrame());

        }else{
            errorOutput = new ErrorOutputLidar(
                    Error,
                    faultySensor,
                    poses,
                    StatisticalFolder.getInstance(),
                    ((LiDarService)crasherService.getFaultySensor()).getLastFrame());
        }

        try {
            FileWriter writer = new FileWriter(output);
            gson.toJson(errorOutput,writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ErrorOutput{
        private String Error;
        private String faultySensor;
        private List<Pose> poses;
        private StatisticalFolder statistics;

        public ErrorOutput(String Error, String faultySensor, List<Pose> poses, StatisticalFolder statistics) {
            this.Error = Error;
            this.faultySensor = faultySensor;
            this.poses = poses;
            this.statistics = statistics;
        }

        public String getError() {
            return Error;
        }

        public void setError(String Error) {
            this.Error = Error;
        }

        public String getFaultySensor() {
            return faultySensor;
        }

        public void setFaultySensor(String faultySensor) {
            this.faultySensor = faultySensor;
        }

        public List<Pose> getPoses() {
            return poses;
        }

        public void setPoses(List<Pose> poses) {
            this.poses = poses;
        }

        public StatisticalFolder getStatistics() {
            return statistics;
        }

        public void setStatistics(StatisticalFolder statistics) {
            this.statistics = statistics;
        }

        protected ErrorOutput(){
        }
    }

    private static class ErrorOutputCamera extends ErrorOutput{
        private DetectedObject lastFrame;
        public ErrorOutputCamera(String Error, String faultySensor, List<Pose> poses, StatisticalFolder statistics, DetectedObject lastFrame){
            super(Error,faultySensor,poses,statistics);
            this.lastFrame =lastFrame;
        }

        public DetectedObject getLastFrame() {
            return lastFrame;
        }

        public void setLastFrame(DetectedObject lastFrame) {
            this.lastFrame = lastFrame;
        }
    }

    private static class ErrorOutputLidar extends ErrorOutput{
        private TrackedObject lastFrame;
        public ErrorOutputLidar(String Error, String faultySensor, List<Pose> poses, StatisticalFolder statistics, TrackedObject lastFrame){
            super(Error,faultySensor,poses,statistics);
            this.lastFrame =lastFrame;
        }

        public TrackedObject getLastFrame() {
            return lastFrame;
        }

        public void setLastFrame(TrackedObject lastFrame) {
            this.lastFrame = lastFrame;
        }
    }

    private static class RegularOutput{
            private StatisticalFolder Statistics;
            private List<LandMark> World_Map;

        public RegularOutput(StatisticalFolder Statistics, List<LandMark> World_Map){
            this.Statistics = Statistics;
            this.World_Map = World_Map;
        }

            public StatisticalFolder getStatistics() {
            return Statistics;
        }

            public void setStatistics(StatisticalFolder statistics) {
            Statistics = statistics;
        }

            public List<LandMark> getWorld_Map() {
            return World_Map;
        }

            public void setWorld_Map(List<LandMark> world_Map) {
            World_Map = world_Map;
        }
    }

}
