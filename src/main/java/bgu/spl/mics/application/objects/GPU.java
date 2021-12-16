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

    private int currentTick = 0;
    private GPUService myService;

    private String gpuType;
    private Type type;
    private Model model = null;
    private int remainingModelBatches;
    private int batchesToCluster, vRamCapacity, tickFactor, myId;
    private final Cluster cluster = Cluster.getInstance();
    private DataBatch current = null;
    private final Deque<Message> messageDeque = new ArrayDeque<>();
    private final Queue<DataBatch> disc = new LinkedList<>(); // unprocessed DataBatches, before being sent to CPU's
    private Queue<DataBatch> vRam; // incoming from cluster, processed
    private int ticksUsed = 0, ticksRemaining = 0;
    private TrainModelEvent trainEvent = null;

    public GPU(String type) {
        if (type.equals("RTX3090")) {
            this.type = Type.RTX3090;
            vRamCapacity = 32;
            batchesToCluster = 8;
            tickFactor = 1;
        } else if (type.equals("RTX2080")) {
            this.type = Type.RTX2080;
            vRamCapacity = 16;
            batchesToCluster = 4;
            tickFactor = 2;
        } else {
            this.type = Type.GTX1080;
            vRamCapacity = 8;
            batchesToCluster = 2;
            tickFactor = 4;
        }
        vRam = new LinkedBlockingQueue<>(vRamCapacity);
        myId = ++id;
        myService = new GPUService(String.valueOf(myId), this);
        cluster.registerGPU(this);
    }

//    public void init() {
//        if (gpuType.equals("RTX3090")) {
//            this.type = Type.RTX3090;
//            vRamCapacity = 32;
//            batchesToCluster = 8;
//            tickFactor = 1;
//        } else if (gpuType.equals("RTX2080")) {
//            this.type = Type.RTX2080;
//            vRamCapacity = 16;
//            batchesToCluster = 4;
//            tickFactor = 2;
//        } else {
//            this.type = Type.GTX1080;
//            vRamCapacity = 8;
//            batchesToCluster = 2;
//            tickFactor = 4;
//        }
//        vRam = new LinkedBlockingQueue<>(vRamCapacity);
//        myId = ++id;
//        // myService = new GPUService(String.valueOf(myId), this);
//        cluster.registerGPU(this);
//    }

    /**
     * @pre none
     * @post currentTick == @pre(currentTick) + 1
     */
    public void updateTick() {
        currentTick++;
        if (model != null) { // currently training a model
            sendBatchesToCluster();
            if (current != null & ticksRemaining == 1) { // finished training a batch
                System.out.println(getName() + " finished training a batch");
                prepareNext();
                remainingModelBatches--;
                if (remainingModelBatches == 0) { // finished training model
                    System.out.println(getName() + " finished training model: " + model.getName());
                    model.train();
                    myService.completeEvent(trainEvent, model);
                    cluster.addTrained(model.getName());
                }
            } else if (current != null & ticksRemaining > 1) { // during training
                System.out.println(getName() + " during training, " + ticksRemaining + " ticks remaining");
                ticksRemaining--;
                ticksUsed++;
            } else prepareNext();
        } else { // test all models and start training next model
            Message m;
            while (!messageDeque.isEmpty()) {
                m = messageDeque.getFirst();
                if (m instanceof TestModelEvent) {
                    System.out.println(getName() + " testing model: " + model.getName());
                    testModel((TestModelEvent) m);
                }
                else {
                    prepareModelForTraining((TrainModelEvent) m);
                    sendBatchesToCluster();
                }
            }
        }
    }

    public void prepareNext() {
        if (vRam.isEmpty()) {
            current = null;
            cluster.fetchProcessedDataGPU(vRamCapacity, this);
        }
        if (!vRam.isEmpty()) { // fetch successful
            current = vRam.remove();
            ticksRemaining = tickFactor;
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
        trainEvent = e;
        model = e.getModel();
        prepareBatches(model.getData());
    }

    /**
     * @param data != null
     * @pre numOfBatches == 0
     * @post disc.size() = @pre(disc.size()) + model.data.size() / 1000
     * @post numOfBatches == model.data.size() / 1000
     */
    public void prepareBatches(Data data) {
        if (data == null) throw new IllegalArgumentException("cannot prepare batches from null data");
        remainingModelBatches = data.getSize() / 1000;
        for (int i = 0; i < data.getSize() / 1000; i += 1000) {
            disc.add(new DataBatch(data, i, this));
        }
    }

    /**
     * @param numOfBatches > 0
     * @pre none
     * @post disc.size() == max{0, @pre(disc.size()) - numOfBatches}
     */
    public void sendBatchesToCluster() {
        Queue<DataBatch> toCluster = new LinkedList<>();
        for (int i = 0; i < batchesToCluster & !disc.isEmpty(); i++) {
            toCluster.add(disc.remove());
        }
        cluster.incomingDataFromGPU(toCluster);
    }

    private void fetchProcessedFromCluster() {
        vRam = cluster.fetchProcessedDataGPU(vRamCapacity, this); // vRam is empty before this method
    }

//    /**
//     * @pre !vRam.isEmpty()
//     * @pre vRam.peek().isProcessed == true
//     * @pre vRam.peek().isTrained == false
//     * @post vRam.isEmpty() || currentTick == duration
//     * @post vRam.peek().isTrained == true
//     * @post currentTick > @pre(currentTick)
//     */
//    public void trainProcessed() {
//        if (vRam.isEmpty()) throw new IllegalStateException("has no processed batches to train");
//    }

    /**
     * @pre trainEvent.model.isTrained() && trainEvent.model.status == "None"
     * @post trainEvent.model.status == "Good" || trainEvent.model.status == "Bad"
     */
    public void testModel(TestModelEvent e) {
        double rdm = Math.random();
        switch (e.getDegree()) {
            case MSc:
                if (rdm < 0.6) e.getModel().setStatus(Model.Status.Good);
                else e.getModel().setStatus(Model.Status.Bad);
                break;
            case PhD:
                if (rdm < 0.8) e.getModel().setStatus(Model.Status.Good);
                else e.getModel().setStatus(Model.Status.Bad);
                break;
        }
        e.getModel().tested();
        myService.completeEvent(e, e.getModel().getStatus());
    }

//    public int getCurrentTick() {
//        return currentTick;
//    }

    public String getName() { return "GPU" + id; }

    public Model getModel() {
        return model;
    }

    public int getRemainingModelBatches() {
        return remainingModelBatches;
    }

    public int getTicksUsed() {
        return ticksUsed;
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
