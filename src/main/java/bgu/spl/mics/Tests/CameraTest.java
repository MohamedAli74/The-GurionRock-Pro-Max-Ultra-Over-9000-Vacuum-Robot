package bgu.spl.mics.Tests;
import bgu.spl.mics.application.objects.*;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class CameraTest
{
    @Test
    public void statusErrorTest()
    {
        Camera camera = new Camera(1,1);
        camera.setStatus(STATUS.ERROR);
        int time = 1;
        StampedDetectedObjects stampedObjects;
        stampedObjects = camera.detect(time);
        assertNull("when Status is Error ,output must be null ",stampedObjects);
    }
    @Test
    public void statusDownTest()
    {
        Camera camera = new Camera(1,1);
        camera.setStatus(STATUS.DOWN);
        int time = 1;
        StampedDetectedObjects stampedObjects;
        stampedObjects = camera.detect(time);
        assertNull("when Status is Down ,output must be null ",stampedObjects);
    }
    @Test
    public void cameraDataAndStatisticTest()
    {
        Camera camera = new Camera(1,1);
        int time = 3;
        List<DetectedObject> list = new ArrayList<>();
        DetectedObject detectedObject = new DetectedObject("2","b");
        DetectedObject detectedObject1 = new DetectedObject("3","c");
        list.add(detectedObject);
        list.add(detectedObject1);
        StampedDetectedObjects stampedDetectedObjects = new StampedDetectedObjects(3,list);
        camera.getCameraData().add(stampedDetectedObjects);
        int numBefore = camera.getStatisticalFolder().getNumDetectedObjects();
        StampedDetectedObjects stObject = camera.detect(time);
        int numAfter = camera.getStatisticalFolder().getNumDetectedObjects();
        assertNotNull("detect function must have not null output ",stObject);
        assertEquals("output's time must be equal to time in the input ",time,stObject.getTime());
        assertTrue("this data have to be contained in the camera data list " ,camera.getCameraData().contains(stObject));
        assertEquals("num of detected objects in statistical folder must be increased by output's list size ",numAfter-numBefore,stObject.getDetectedObjects().size());
    }
}