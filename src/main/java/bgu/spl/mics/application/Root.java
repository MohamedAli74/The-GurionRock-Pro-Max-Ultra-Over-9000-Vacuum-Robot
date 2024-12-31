package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;

import java.util.List;

public class Root {
    public List<Camera> Cameras;
    public List<LiDarWorkerTracker> LidarWorkers;
    public String PoseJsonFile;
    public int TickTime;
    public int Duration;
}
