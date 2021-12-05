package bgu.spl.mics.application.objects;

import bgu.spl.mics.Message;

import java.util.Queue;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    static int currentTick = 0;

    private Type type;
    private Model model;
    private Cluster cluster;
    private int trainingTicks;
    private int vRamCapacity;
    private int availableVRam;
    private Queue<Message> messageQueue;
    private Queue<DataBatch> disc; // incoming from MessageBus, unprocessed
    private Queue<DataBatch> vRam; // incoming from cluster, processed
    private int ticksUsed;

    public GPU(Type type, Cluster cluster) {
        this.type = type;
        // TODO: trainingTicks, vRamCapacity, availableVRam
        model = null;
        this.cluster = cluster;
        ticksUsed = 0;
    }

    private void updateTick() {

    }

    private void retrieveData() { // retrieve data from model

    }

    private void prepareBatches() {

    }

    private void sendBatchesToCluster() {

    }

    public boolean hasAvailableVRam() {
        return false;
    }

    public void insertBatchToVRam() {

    }

    private void removeBatchFromVRam() {

    }

    private boolean doneProcessing() {
        return false;
    }
}
