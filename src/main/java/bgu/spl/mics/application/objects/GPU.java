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
    private Model currentModel = null;
    private int remainingModelBatches;
    private int batchesToCluster, vRamCapacity, tickFactor, myId;
    private final Cluster cluster = Cluster.getInstance();
    private DataBatch currentBatch = null;
    private final Deque<Message> messageDeque = new ArrayDeque<>();
    private final Queue<DataBatch> disc = new LinkedList<>(); // unprocessed DataBatches, before being sent to CPU's
    private Queue<DataBatch> vRam; // incoming from cluster, processed
    private int ticksUsed = 0, ticksRemaining = 0;
    private TrainModelEvent trainEvent = null;

    public GPU(String type) {
        if (type.equals("RTX3090")) {
            this.type = Type.RTX3090;
            vRamCapacity = 32;
            tickFactor = 1;
        } else if (type.equals("RTX2080")) {
            this.type = Type.RTX2080;
            vRamCapacity = 16;
            tickFactor = 2;
        } else {
            this.type = Type.GTX1080;
            vRamCapacity = 8;
            tickFactor = 4;
        }
        batchesToCluster = vRamCapacity / 2;
        vRam = new LinkedBlockingQueue<>(vRamCapacity);
        myId = id;
        id++;
    }

    public void setMyService(GPUService service) {
        myService = service;
    }

    /**
     * @pre none
     * @post currentTick == @pre(currentTick) + 1
     * @post if @pre(currentModel != null & currentBatch != null & ticksRemaining == 0 & remainingModelBatches == 1)
     * -> currentModel.isTrained() == true
     * @post if @pre(currentModel != null & currentBatch != null & ticksRemaining == 0 & remainingModelBatches > 1)
     * -> remainingModelBatches = @pre(remainingModelBatches) - 1
     * @post if @pre(currentModel != null & currentBatch != null & ticksRemaining > 0)
     * -> ticksRemaining = @pre(ticksRemaining) - 1 & ticksUsed = @pre(ticksUsed) + 1
     * @post if @pre(currentModel != null & currentBatch == null & !vRam.isEmpty())
     * -> currentBatch != null & vRam.size() = @pre(vRam.size()) - 1
     * @post if @pre(currentModel == null & !messageDequeue.isEmpty() & messageDequeue.getFirst() is TrainEvent)
     * -> messageDequeue.size() = @pre(messageDequeue.size()) - 1 & disc.size() = model.data.size() / 1000
     * @post if @pre(currentModel == null & !messageDequeue.isEmpty() & messageDequeue.getFirst() is TestEvent)
     * -> messageDequeue.size() < @pre(messageDequeue.size())
     */
    public void updateTick() {
        currentTick++;
        if (currentModel != null) { // currently training a model
            System.out.println(getName() + " is in the middle of training: " + currentModel.getName());
            if (currentBatch != null & ticksRemaining == 1) { // finished training a batch
                System.out.println(getName() + " trained batch");
                prepareNext();
                remainingModelBatches--;
                if (remainingModelBatches == 0) { // finished training model
                    System.out.println(getName() + " trained model: " + currentModel.getName());
                    currentModel.train();
                    myService.completeEvent(trainEvent, currentModel);
                    cluster.addTrained(currentModel.getName());
                    currentModel = null;
                }
            } else if (currentBatch != null & ticksRemaining > 1) { // during training
                ticksRemaining--;
                ticksUsed++;
            } else prepareNext();
        } else { // test all models and start training next model
            System.out.println(getName() + " has " + messageDeque.size() + " messages in queue");
            Message m;
            while (!messageDeque.isEmpty() & currentModel == null) {
                m = messageDeque.removeFirst();
                if (m instanceof TestModelEvent) {
                    System.out.println(getName() + " testing model: " + ((TestModelEvent) m).getModel().getName());
                    testModel((TestModelEvent) m);
                } else {
                    prepareModelForTraining((TrainModelEvent) m);
                }
            }
        }
        if (!disc.isEmpty()) offerBatchesToCluster();
    }

    private void prepareNext() {
        if (vRam.isEmpty()) {
            currentBatch = null;
            vRam = cluster.fetchProcessedDataGPU(vRamCapacity, this);
        }
        if (!vRam.isEmpty()) { // fetch successful
            currentBatch = vRam.remove();
            ticksRemaining = tickFactor;
        }
    }

    /**
     * @pre none
     * @post messageDeque.size() = @pre(messageDeque.size()) + 1
     */
    public void handleTrainEvent(TrainModelEvent e) {
        System.out.println(getName() + " received train event for: " + e.getModel().getName());
        messageDeque.addLast(e);
    }

    /**
     * @pre none
     * @post messageDeque.size() = @pre(messageDeque.size()) + 1
     */
    public void handleTestEvent(TestModelEvent e) {
        System.out.println(getName() + " received test event for: " + e.getModel().getName());
        messageDeque.addFirst(e);
    }

    private <T> void prepareModelForTraining(TrainModelEvent e) {
        trainEvent = e;
        currentModel = e.getModel();
        prepareBatches(currentModel.getData());
    }

    private void prepareBatches(Data data) {
        if (data == null) throw new IllegalArgumentException("cannot prepare batches from null data");
        remainingModelBatches = data.getSize() / 1000;
        for (int i = 0; i < data.getSize(); i += 1000) {
            disc.add(new DataBatch(data, i, this));
        }
    }

    private void offerBatchesToCluster() {
        for (int i = 0; i < batchesToCluster & !disc.isEmpty(); i++) {
            DataBatch batch = disc.peek();
            if (cluster.incomingBatchFromGPU(batch)) disc.remove(); // offer successful
        }
    }

    private void testModel(TestModelEvent e) {
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
        System.out.println(getName() + " completed testing " + e.getModel().getName() + ". result: " + e.getModel().getStatus().toString());
        myService.completeEvent(e, e.getModel().getStatus());
    }

    public String getName() {
        return "GPU" + myId;
    }

    // region FOR TESTS
    public int getCurrentTick() {
        return currentTick;
    }

    public int getTicksRemaining() {
        return ticksRemaining;
    }

    public void setTicksRemaining(int i) {
        ticksRemaining = i;
    }

    public int getMessageSize() {
        return messageDeque.size();
    }

    public void addToMessageDequeue(Message m) {
        messageDeque.addFirst(m);
    }

    public Model getCurrentModel() {
        return currentModel;
    }

    public void setCurrentModel(Model m) {
        currentModel = m;
    }

    public void setCurrentBatch(DataBatch batch) {
        currentBatch = batch;
    }

    public DataBatch getCurrentBatch() {
        return currentBatch;
    }

    public int getRemainingModelBatches() {
        return remainingModelBatches;
    }

    public void setRemainingModelBatches(int i) {
        remainingModelBatches = i;
    }

    public int getTicksUsed() {
        return ticksUsed;
    }

    public int getDiscSize() {
        return disc.size();
    }

    public void addToVRam(DataBatch dataBatch) { // used for tests
        vRam.add(dataBatch);
    }

    public int getVRamSize() {
        return vRam.size();
    }

    // endregion
}