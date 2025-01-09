package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Pose;

public class FusionSlamToCrasherEvent implements Event {
    private Pose currentPose;

    public FusionSlamToCrasherEvent(Pose p){
        currentPose = p;
    }

    public Pose getCurrentPose(){
        return currentPose;
    }

}
