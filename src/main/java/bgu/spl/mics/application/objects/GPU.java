package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;
import bgu.spl.mics.Message;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

    public int currentTick;

    private Type type;
    private Model model;
    private int numOfBatches;
    private Cluster cluster;
    private int vRamCapacity;
    private Queue<Message> messageQueue;
    private Queue<DataBatch> disc; // unprocessed DataBatches, before being sent to CPU's
    private Queue<DataBatch> vRam; // incoming from cluster, processed
    private int ticksUsed;

    public GPU(Type type, Cluster cluster) {
        currentTick = 0;
        this.type = type;
        // TODO: trainingTicks, vRamCapacity, availableVRam
        model = null;
        this.cluster = cluster;
        ticksUsed = 0;
        messageQueue = new LinkedList<>();
        disc = new LinkedList<>();
        vRam = new LinkedBlockingQueue<>(vRamCapacity);
    }

    /**
     * @pre none
     * @post currentTick == @pre(currentTick) + 1
     */
    public void updateTick() {
        currentTick++;
    }

    /**
     *
     */
    public void handleEvent() {

    }

    /**
     * @pre !trainEvent.model.isTrained()
     * @post trainEvent.model.isTrained()
     */
    public <T> void trainModel() {
//        prepareBatches();
//        sendBatchesToCluster(); // limited amount at a time
//        try {
//            DataBatch toTrain = vRam.remove();
//        } catch (InterruptedException e) {}
//        once model is fully trained - bus.complete()
    }

    /**
     * @param data != null
     * @pre numOfBatches == 0
     * @post disc.size() = @pre(disc.size()) + model.data.size() / 1000
     * @post numOfBatches == model.data.size() / 1000
     */
    public void prepareBatches(Data data) {
        if (data == null) throw new IllegalArgumentException("cannot prepare batches from null data");
        // numOfBatches = data.size() / 1000
        // push to disc
    }

    /**
     * @param numOfBatches > 0
     * @pre none
     * @post disc.size() == max{0, @pre(disc.size()) - numOfBatches}
     */
    public void sendBatchesToCluster(int numOfBatches) {

    }

    /**
     * @pre !vRam.isEmpty()
     * @pre vRam.peek().isProcessed == true
     * @pre vRam.peek().isTrained == false
     * @post vRam.isEmpty() || currentTick == duration
     * @post vRam.peek().isTrained == true
     */
    public void trainProcessed() {
        if (vRam.isEmpty()) throw new IllegalStateException("has no processed batches to train");
    }

    /**
     * @pre trainEvent.model.isTrained() && trainEvent.model.status == "None"
     * @post trainEvent.model.status == "Good" || trainEvent.model.status == "Bad"
     */
    public void testModel() {

    }

    public Model getModel() {
        return model;
    }

    public int getNumOfBatches() {
        return numOfBatches;
    }

    public int getDiscSize() {
        return disc.size();
    }

    public DataBatch getNextBatch() { // used for tests
        return disc.remove();
    }

    public void addToVRam(DataBatch dataBatch) { // used for tests
        vRam.add(dataBatch);
    }

    public boolean vRamIsEmpty() { // used for tests
        return vRam.isEmpty();
    }
}
