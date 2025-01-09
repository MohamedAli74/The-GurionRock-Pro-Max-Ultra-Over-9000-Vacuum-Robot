package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.DetectedObject;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.LiDarWorkerTracker;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.ArrayList;
import java.util.List;

/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {
    private LiDarWorkerTracker liDar;
    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker) {
        super("LiDar"+LiDarWorkerTracker.getId());
        liDar = LiDarWorkerTracker;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {

        super.messageBus.register(this);
        super.subscribeBroadcast(TerminatedBroadcast.class, terminatedBroadcast ->
        {
            this.liDar.setStatus(STATUS.DOWN);
            this.terminate();
        });

        super.subscribeBroadcast(CrashedBroadcast.class, crashedBroadcast ->
        {
            this.liDar.setStatus(STATUS.ERROR);
            this.terminate();
        });

        super.subscribeEvent(DetectObjectsEvent.class,detectedObjectEvent->
        {
            liDar.track(detectedObjectEvent.getStampedDetectedObjects());
            complete(detectedObjectEvent,true);
        });

        super.subscribeBroadcast(TickBroadcast.class, tickBroadcast ->
        {
            int currentTime = tickBroadcast.getCurrentTick();
            if(liDar.checkERROR(currentTime)){
                CrashedBroadcast crashedBroadcast = liDar.getCrashedbroadcast(tickBroadcast.getCurrentTick(),this);
                sendBroadcast(crashedBroadcast);
            }
            List<TrackedObject> willSend = new ArrayList<>();
            for (TrackedObject obj : liDar.getLastTrackedObjects())
            {
                if(obj.getTime() <= currentTime - liDar.getFrequency() && !obj.isSentBefore())
                {
                    obj.setSentBefore(true);
                    willSend.add(obj);
                }
            }
            TrackedObjectsEvent trackEvent = new TrackedObjectsEvent(willSend);
            Future<Boolean> future = super.sendEvent(trackEvent);

        });

    }

    public TrackedObject getLastFrame() {
        return liDar.getLastFrame();
    }
}
