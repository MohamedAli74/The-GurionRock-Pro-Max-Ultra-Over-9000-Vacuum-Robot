package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.StampedDetectedObjects;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {

    private final Camera camera;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super("Camera"+camera.getId());
        this.camera=camera;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        super.subscribeBroadcast(TerminatedBroadcast.class,terminatedBroadcast ->
        {
           this.camera.setStatus(STATUS.DOWN);
           this.terminate();
        });

        super.subscribeBroadcast(CrashedBroadcast.class, crashedBroadcast ->
        {
            this.camera.setStatus(STATUS.ERROR);
            this.terminate();
        });

        super.subscribeBroadcast(TickBroadcast.class, tickBroadcast ->
        {
            StampedDetectedObjects detectedObjects = this.camera.detect(tickBroadcast.getCurrentTick()- camera.getFrequency());
            if(detectedObjects!=null){
                this.sendEvent( new DetectObjectsEvent(detectedObjects));
            }
        });

    }
}
