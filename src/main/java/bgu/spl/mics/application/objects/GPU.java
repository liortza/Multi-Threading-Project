package bgu.spl.mics.application.objects;

import bgu.spl.mics.Message;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.services.GPUService;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
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

    private static int id = 0;

    private int currentTick;

    private Type type;
    private Model model;
    private int remainingModelBatches, batchesToCluster;
    private Cluster cluster;
    private int vRamCapacity;
    private Deque<Message> messageDeque;
    private Queue<DataBatch> disc; // unprocessed DataBatches, before being sent to CPU's
    private Queue<DataBatch> vRam; // incoming from cluster, processed
    private int ticksUsed;
    private int myId;

    public GPU(Type type, Cluster cluster) {
        currentTick = 0;
        this.type = type;
        if (type == Type.RTX3090) batchesToCluster = 8;
        else if (type == Type.RTX2080) batchesToCluster = 4;
        else batchesToCluster = 2;
        // TODO: trainingTicks, vRamCapacity, availableVRam
        model = null;
        this.cluster = cluster;
        ticksUsed = 0;
        messageDeque = new ArrayDeque<>();
        disc = new LinkedList<>();
        vRam = new LinkedBlockingQueue<>(vRamCapacity);
        myId = ++id;
        new GPUService(String.valueOf(myId), this);
    }

    /**
     * @pre none
     * @post currentTick == @pre(currentTick) + 1
     */
    public void updateTick() {
        currentTick++;
        if (model != null) { // currently training a model
            sendBatchesToCluster();

            // get batches from cluster
            // clear vram if possible
            // check if finished
        } else { // test all models and start training next model
            Message m;
            while (!messageDeque.isEmpty()) {
                m = messageDeque.getFirst();
                if (m instanceof TestModelEvent) testModel((TestModelEvent) m);
                else prepareModelForTraining((TrainModelEvent) m);
            }
        }
    }

    /**
     *
     */
    public void handleTrainEvent(TrainModelEvent e) {
        messageDeque.addLast(e);
    }

    public void handleTestEvent(TestModelEvent e) {
        messageDeque.addFirst(e);
    }

    /**
     * @pre !trainEvent.model.isTrained()
     * @post trainEvent.model.isTrained()
     */
    public <T> void prepareModelForTraining(TrainModelEvent e) {
        model = e.getModel();
        prepareBatches(model.getData());
        sendBatchesToCluster();
//        sendBatchesToCluster(); // limited amount at a time
//        try {
//            DataBatch toTrain = vRam.remove();
//        } catch (InterruptedException e) {}
//        once model is fully trained - bus.complete()
//        model = null;
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
    public void sendBatchesToCluster() {

    }

    private void fetchProcessedFromCluster() {

    }

    /**
     * @pre !vRam.isEmpty()
     * @pre vRam.peek().isProcessed == true
     * @pre vRam.peek().isTrained == false
     * @post vRam.isEmpty() || currentTick == duration
     * @post vRam.peek().isTrained == true
     * @post currentTick > @pre(currentTick)
     */
    public void trainProcessed() {
        if (vRam.isEmpty()) throw new IllegalStateException("has no processed batches to train");
    }

    /**
     * @pre trainEvent.model.isTrained() && trainEvent.model.status == "None"
     * @post trainEvent.model.status == "Good" || trainEvent.model.status == "Bad"
     */
    public void testModel(TestModelEvent e) {

    }

    public int getCurrentTick() {
        return currentTick;
    }

    public Model getModel() {
        return model;
    }

    public int getRemainingModelBatches() {
        return remainingModelBatches;
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
