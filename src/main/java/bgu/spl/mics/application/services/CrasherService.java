package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;

import java.util.ArrayList;
import java.util.List;

public class CrasherService extends MicroService {
    private boolean crashed;
    private int flag;// ==0 if not crashed, ==1 if camera, ==-1 if lidar
    private String Error;
    private MicroService faultySensor;
    private TrackedObject lastFrameLidar;
    private DetectedObject lastFrameCamera;
    private List<Pose> posesUntilCrash;

    public CrasherService(){
        super("Crasher");
        flag = 0;
        posesUntilCrash = new ArrayList<>();
        crashed = false;
    }

    protected void initialize() {
        super.messageBus.register(this);
        subscribeBroadcast(CrashedBroadcast.class, crashedBroadcast ->
        {
            crashed = true;
            Error = crashedBroadcast.getDescription();
            faultySensor = crashedBroadcast.getFaultySensor();
            if(faultySensor.getClass() == CameraService.class){
                flag = 1;
                lastFrameCamera = ((CameraService)faultySensor).getLastFrame();
                lastFrameLidar = null ;
            }else{
                if(faultySensor.getClass() == LiDarService.class){
                    flag = -1;
                    lastFrameLidar = ((LiDarService)faultySensor).getLastFrame();
                    lastFrameCamera = null;
                }
            }
            terminate();
        });

        subscribeBroadcast(TerminatedBroadcast.class, terminatedBroadcast ->
        {
            terminate();
        });

        subscribeEvent(PoseEvent.class,poseEvent ->
        {
           posesUntilCrash.add(poseEvent.getCurrentPose());
        });
    }

    public boolean isCrashed() {
        return crashed;
    }

    public String getError() {
        return Error;
    }

    public MicroService getFaultySensor() {
        return faultySensor;
    }

    public TrackedObject getLastFrameLidar() {
        return lastFrameLidar;
    }

    public DetectedObject getLastFrameCamera() {
        return lastFrameCamera;
    }

    public List<Pose> getPosesUntilCrash() {
        return posesUntilCrash;
    }

    public int getFlag() {
        return flag;
    }
}
