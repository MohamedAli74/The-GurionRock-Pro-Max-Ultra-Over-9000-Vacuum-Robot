package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.STATUS;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects._Camera;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * 
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {

    private final _Camera camera;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(_Camera camera) {
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

        //TO EDIT!!! (subscribe broadcast Tick)

    }
}
