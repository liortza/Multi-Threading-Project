package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CPUService;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    private static int id = 0;
    private int currentTick = 0;

    private final int cores;
    private Queue<DataBatch> incoming = new LinkedList<>();
    private DataBatch current = null;
    private int ticksRemaining = 0;
    private final int capacity;
    private final Cluster cluster = Cluster.getInstance();
    private int ticksUsed = 0;
    private int processedBatches = 0;
    private final int myId;

    public CPU(int cores) {
        this.cores = cores;
        capacity = cores / 2; // TODO: see how to define capacity
        myId = id;
        id++;
    }

    public int getCapacity() {
        return capacity;
    }

    /**
     * @pre none
     * @post currentTick == @pre(currentTick) + 1
     * @post if @pre(current != null & ticksRemaining == 0) -> processedBatches = @pre(processedBatches) + 1
     * @post if @pre(current != null & ticksRemaining > 0) -> ticksRemaining = @pre(ticksRemaining) + 1
     * & ticksUsed = @pre(ticksUsed) + 1
     * @post if @pre(current == null & !incoming.isEmpty()) -> current != null
     */
    public void updateTick() {
        currentTick++;
        if (current != null & ticksRemaining == 0) { // finished processing batch
            current.process();
            processedBatches++;
            cluster.incomingBatchFromCPU(current);
            prepareNext();
        } else if (current != null & ticksRemaining > 0) { // during process
            ticksRemaining--;
            ticksUsed++;
        } else prepareNext();
    }

    private void prepareNext() {
        if (incoming.isEmpty()) {
            current = null;
            incoming = cluster.fetchUnprocessedDataCPU(capacity);
        }
        if (!incoming.isEmpty()) { // fetch successful
            current = incoming.remove();
            ticksRemaining = (32 / cores) * (current.getTickFactor()) - 1; // use current tick for process
        }
    }

    public int getTicksUsed() { // used for tests
        return ticksUsed;
    }

    public int getProcessedBatches() {
        return processedBatches;
    }

    public String getName() {
        return "CPU" + myId;
    }

    // region FOR TESTS
    public int getCurrentTick() {
        return currentTick;
    }

    public void setCurrent(DataBatch batch) {
        current = batch;
    }

    public DataBatch getCurrent() {
        return current;
    }

    public int getTicksRemaining() {
        return ticksRemaining;
    }

    public void setTicksRemaining(int remaining) {
        ticksRemaining = remaining;
    }

    public void addToIncoming(DataBatch batch) {
        incoming.add(batch);
    }

    public int getIncomingSize() {
        return incoming.size();
    }
    // endregion
}