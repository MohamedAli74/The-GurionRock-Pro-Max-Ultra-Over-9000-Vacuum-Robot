package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.reflect.TypeToken;
import jdk.internal.foreign.CABI;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


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
        try {
            FileReader configReader = new FileReader(args[1]);
            Root root = gson.fromJson(configReader,Root.class);
            FileReader cameraDataReader = new FileReader(root.Cameras.cameraDatasPath);

            Type listType = new TypeToken<List<List<StampedDetectedObjects>>>() {}.getType();
            List<List< StampedDetectedObjects >> cameraDatas = gson.fromJson(cameraDataReader,listType);;

            List<CameraService> CameraServiceList = new ArrayList<CameraService>();
            for(Camera c: root.Cameras.getCamerasConfigurations()){
                c.setCameraData(cameraDatas.get(c.getId()));
                CameraServiceList.add(new CameraService(c));
            }

            List<LiDarService> LidarServices = new ArrayList<LiDarService>();
            for(LiDarWorkerTracker lidar : root.LidarWorkers.getLidarConfigurations()){
                LidarServices.add(new LiDarService(lidar));
            }

            TimeService timeService = new TimeService(root.TickTime, root.Duration);

            GPSIMU gpsimu = new GPSIMU(root.PoseJsonFile);
            PoseService poseService = new PoseService(gpsimu);

            FusionSlam fusionSlam = FusionSlam.getInstance();
            FusionSlamService fusionSlamService = new FusionSlamService(fusionSlam);

            //TO EDIT!!! (TO DO: RUN THE SIMULATION)

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        // TODO: Parse configuration file.
        // TODO: Initialize system components and services.
        // TODO: Start the simulation.
    }
}
