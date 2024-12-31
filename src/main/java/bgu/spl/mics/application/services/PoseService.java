package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {
    private GPSIMU gpsimu;
    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("GPSIMU");
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        super.subscribeBroadcast(TerminatedBroadcast.class, terminatedBroadcast ->
        {
            this.gpsimu.setStatus(STATUS.DOWN);
            this.terminate();
        });

        super.subscribeBroadcast(CrashedBroadcast.class, crashedBroadcast ->
        {
            this.gpsimu.setStatus(STATUS.ERROR);
            this.terminate();
        });

        super.subscribeBroadcast(TickBroadcast.class, tickBroadcast ->
        {
            Pose currentPose = null;
            for(Pose p : gpsimu.getPoseList()){
                if(p.getTime() == tickBroadcast.getCurrentTick())
                    currentPose=p;
            }
            super.sendEvent(new PoseEvent(currentPose));
        });

    }
}
