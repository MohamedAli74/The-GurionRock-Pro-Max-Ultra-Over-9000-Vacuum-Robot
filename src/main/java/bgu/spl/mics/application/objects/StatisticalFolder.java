package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private static StatisticalFolder instance = null;
    private volatile AtomicInteger systemRuntime;
    private volatile AtomicInteger numDetectedObjects;
    private volatile AtomicInteger numTrackedObjects;
    private volatile AtomicInteger numLandmarks;

    private StatisticalFolder() {
        this.systemRuntime = new AtomicInteger(0);
        this.numDetectedObjects = new AtomicInteger(0);
        this.numTrackedObjects = new AtomicInteger(0);
        this.numLandmarks = new AtomicInteger(0);
    }

    public static StatisticalFolder getInstance() {
        if (instance == null) {
            instance = new StatisticalFolder();
        }
        return instance;
    }

    public int getSystemRuntime() {
        return systemRuntime.get();
    }

    public int getNumDetectedObjects() {
        return numDetectedObjects.get();
    }

    public int getNumTrackedObjects() {
        return numTrackedObjects.get();
    }

    public int getNumLandmarks() {
        return numLandmarks.get();
    }

    public void inceaseSystemRuntime(int time) {
        int oldVal;
        int newval;
        do {
            oldVal = getSystemRuntime();
            newval = oldVal + time;
            System.out.println("System Runtime increased to: " + systemRuntime.get());
        } while (!systemRuntime.compareAndSet(oldVal, newval));
    }

    public void inceaseNumDetectedObjects(int num) {
        int oldVal;
        int newval;
        do {
            oldVal = getNumDetectedObjects();
            newval = oldVal + num;
            System.out.println("Number of Detected Objects increased to: " + numDetectedObjects.get());
        } while (!numDetectedObjects.compareAndSet(oldVal, newval));
    }

    public void inceaseNumTrackedObjects(int num) {
        int oldVal;
        int newval;
        do {
            oldVal = getNumTrackedObjects();
            newval = oldVal + num;
            System.out.println("Number of Tracked Objects increased to: " + numTrackedObjects.get());
        } while (!numTrackedObjects.compareAndSet(oldVal, newval));
    }

    public void inceaseNumLandmarks(int num) {
        int oldVal;
        int newval;
        do {
            oldVal = getNumLandmarks();
            newval = oldVal + num;
            System.out.println("Number of Landmarks increased to: " + numLandmarks.get());
        } while (!numLandmarks.compareAndSet(oldVal, newval));
    }
}
