package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.StatisticalFolder;

import java.util.concurrent.TimeUnit;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {

    private int count;
    private final int tickTime;
    private final int duration;
    private StatisticalFolder statisticalFolder = StatisticalFolder.getInstance();


    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration) {
        super("TimeService");
        count = 0;
        tickTime = TickTime;
        duration = Duration;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {
       super.messageBus.register(this);
    }
    public void overrided()
    {
        while(count<duration){
            try {
                TimeUnit.SECONDS.sleep(this.tickTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count += 1;
            statisticalFolder.inceaseSystemRuntime(1);


            TickBroadcast tickBroadcast = new TickBroadcast(count);
            super.sendBroadcast(tickBroadcast);
        }
        super.sendBroadcast(new TerminatedBroadcast());
        super.terminate();
    }
}
