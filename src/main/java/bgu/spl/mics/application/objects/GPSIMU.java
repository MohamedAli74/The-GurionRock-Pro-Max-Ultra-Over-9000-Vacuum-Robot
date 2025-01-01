package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    private int currentTick;
    private STATUS status;
    private List<Pose> PoseList;

    public GPSIMU(String PATH_TO_FILE){
        currentTick = 0; //NOT SURE
        status = STATUS.UP;
        PoseList = read(PATH_TO_FILE);//TO EDIT!!!
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public List<Pose> getPoseList() {
        return PoseList;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    private Vector<Pose> read(String PATH_TO_FILE){
        Gson gson=new Gson();
        Vector<Pose> output;
        try {
            FileReader reader = new FileReader(PATH_TO_FILE);
            Type listType = new TypeToken<Vector<Pose>>() {}.getType();
            output = gson.fromJson(reader,listType);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return output;
    }
}
