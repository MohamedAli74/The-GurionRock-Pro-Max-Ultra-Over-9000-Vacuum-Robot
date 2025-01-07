package Tests;

import bgu.spl.mics.application.objects.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

public class FusoinSlamTest {
    @Test
    public void converLocaltoGlopalTest() {
        FusionSlam fusionSlam = FusionSlam.getInstance();
        CloudPoint point = new CloudPoint(0.5, 0.9);
        float x = 3.5f;
        float y = 1.0f;
        float yaw = 77.0f;
        Pose pose = new Pose(x, y, yaw, 1);
        CloudPoint result;
        double x1 = (Math.cos(Math.toRadians(pose.getYaw())) * point.getX()) - (Math.sin(Math.toRadians(pose.getYaw())) * point.getY()) + pose.getX();
        double y1 = (Math.sin(Math.toRadians(pose.getYaw())) * point.getX()) - (Math.cos(Math.toRadians(pose.getYaw())) * point.getY()) + pose.getY();
        result = new CloudPoint(x1, y1);
        assertEquals("the x coordinate must be equals ", fusionSlam.convert(point, pose).getX(), result.getX());
        assertEquals("the x coordinate must be equals ", fusionSlam.convert(point, pose).getY(), result.getY());
        assertNotNull(point);
    }
    @Test
    public void addTrackedTest()
    {
        FusionSlam fusionSlam = FusionSlam.getInstance();
        List<CloudPoint> coordinates = new ArrayList<>();
        CloudPoint point = new CloudPoint(0.5,0.9);
        TrackedObject object = new TrackedObject("1",1,"a",coordinates);
        float x = 3.5f;
        float y = 1.0f;
        float yaw = 77.0f;
        Pose pose = new Pose(x,y,yaw,1);
        fusionSlam.getPoseslist().add(pose);
        LandMark landMark = new LandMark("1","a",fusionSlam.convertLocalPointsToGlobalPoints(coordinates,pose));
        fusionSlam.getLandMarks().add(landMark);
        assertNotNull("poses list must not be null ",fusionSlam.getPoseslist());
        assertNotNull("LandMarks list must not be null ",fusionSlam.getLandMarks());
        assertTrue("pose must be in the list ",fusionSlam.getPoseslist().contains(pose));
        assertTrue("landMark must be in the list ",fusionSlam.getLandMarks().contains(landMark));
        assertEquals("the id must be the same ",landMark.getId(),object.getId());
        assertEquals("the description must be the same ",landMark.getDescription(),object.getDescription());

    }
}
