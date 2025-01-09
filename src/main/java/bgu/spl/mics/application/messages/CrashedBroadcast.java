package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;

public class CrashedBroadcast implements Broadcast {
    private String description;
    private MicroService faultySensor;

    public CrashedBroadcast(String description,MicroService faultySensor){
        this.description=description;
        this.faultySensor=faultySensor;
    }

    public String getDescription() {
        return description;
    }

    public MicroService getFaultySensor() {
        return faultySensor;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFaultySensor(MicroService faultySencor) {
        this.faultySensor = faultySencor;
    }
}
