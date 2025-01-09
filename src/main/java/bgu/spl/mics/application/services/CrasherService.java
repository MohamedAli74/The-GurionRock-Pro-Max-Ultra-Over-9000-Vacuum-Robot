package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.ArrayList;
import java.util.List;

public class CrasherService extends MicroService {
    private boolean crashed;
    private String Error;
    private MicroService faultySensor;
    private List<TrackedObject> lastFrameLidar;
    private StampedDetectedObjects lastFrameCamera;
    private List<Pose> posesUntilCrash;

    public CrasherService(){
        super("Crasher");
        posesUntilCrash = new ArrayList<>();
        crashed = false;
    }

    protected void initialize() {
        super.messageBus.register(this);
        subscribeBroadcast(CrashedBroadcast.class, crashedBroadcast ->
        {
            synchronized (messageBus) {
                crashed = true;
                Error = crashedBroadcast.getDescription();
                faultySensor = crashedBroadcast.getFaultySensor();
                terminate();
            }
        });

        subscribeBroadcast(TerminatedBroadcast.class, terminatedBroadcast ->
        {
            terminate();
        });

        subscribeEvent(FusionSlamToCrasherEvent.class, poseEvent ->
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

    public List<TrackedObject> getLastFrameLidar() {
        return lastFrameLidar;
    }

    public StampedDetectedObjects getLastFrameCamera() {
        return lastFrameCamera;
    }

    public List<Pose> getPosesUntilCrash() {
        return posesUntilCrash;
    }
}
