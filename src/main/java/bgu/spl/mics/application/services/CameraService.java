package bgu.spl.mics.application.services;

import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.DetectedObject;
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
    private DetectedObject lastFrame;
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
        super.messageBus.register(this);
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
            if(camera.checkERROR(tickBroadcast.getCurrentTick())){
                sendBroadcast(new CrashedBroadcast());
            }
            StampedDetectedObjects detectedObjects = this.camera.detect(tickBroadcast.getCurrentTick()-camera.getFrequency());
            if(detectedObjects!=null) {
                if (detectedObjects.getDetectedObjects() != null) {
                    setLastFrame(detectedObjects.getDetectedObjects().get(detectedObjects.getDetectedObjects().size()-1));
                    this.sendEvent(new DetectObjectsEvent(detectedObjects));
                }
            }
        });

    }

    public void setLastFrame(DetectedObject lastFrame) {
        this.lastFrame = lastFrame;
    }

    public DetectedObject getLastFrame() {
        return lastFrame;
    }

    //for debugging:
    public Camera getCamera() {
        return camera;
    }
    //delete this
}
