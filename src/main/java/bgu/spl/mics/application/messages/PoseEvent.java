package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Pose;

public class PoseEvent implements Event {
    private Pose currentPose;

    public PoseEvent(Pose p){
        currentPose = p;
    }

    public Pose getCurrentPose(){
        return currentPose;
    }

}
