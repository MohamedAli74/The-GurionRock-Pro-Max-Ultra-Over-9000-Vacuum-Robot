package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.TrackedObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * 
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private FusionSlam fusionSlam;
    private ArrayList<Pose> poseForEachTick;
    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam) {
        super("FusionSlam");
        this.fusionSlam = fusionSlam;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        super.subscribeBroadcast(TerminatedBroadcast.class, terminatedBroadcast ->
        {
            this.terminate();
        });

        super.subscribeBroadcast(CrashedBroadcast.class, crashedBroadcast ->
        {
            this.terminate();
        });
        

        super.subscribeEvent(TrackedObjectsEvent.class, trackedObjectsEvent->
        {
            for(TrackedObject trackedObject : trackedObjectsEvent.getTrackedObjectList()){

            }
        });

        super.subscribeEvent(PoseEvent.class,poseEvent ->
        {
            poseForEachTick.add(poseEvent.getCurrentPose());
        });

    }
}
