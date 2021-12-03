package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    static int currentTick = 0;

    private int cores;
    private Queue<DataBatch> data;
    private int capacity;
    private Cluster cluster;
    private DataBatch currentBatch;

    public CPU(int cores, Cluster cluster) {
        this.cores = cores;
        this.data = new LinkedList<>();
        capacity = cores; // TODO: see how to define capacity
        this.cluster = cluster;
    }

    private void updateTick() {

    }

    private boolean hasNextBatch() {
        return false;
    }

    private void getDataFromCluster() {

    }

    private void processData() {

    }

    public boolean doneProcessing() {
        return false;
    }

}
