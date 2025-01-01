package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;

import java.util.List;

public class TrackedObjectsEvent implements Event {
    private List<TrackedObject> trackedObjectList;

    public TrackedObjectsEvent(List<TrackedObject> trackedObjectList){
        this.trackedObjectList = trackedObjectList;
    }

    public List<TrackedObject> getTrackedObjectList(){
        return trackedObjectList;
    }


}
