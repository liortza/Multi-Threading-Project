package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.CPUService;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    private static int id = 0;
    private int currentTick = 0;

    private int cores;
    private Queue<DataBatch> incoming;
    private Queue<DataBatch> outgoing;
    private int capacity;
    private Cluster cluster;
    private int ticksUsed;
    private int myId;

    public CPU(int cores, Cluster cluster) {
        this.cores = cores;
        this.incoming = new LinkedList<>();
        this.outgoing = new LinkedList<>();
        capacity = cores; // TODO: see how to define capacity
        this.cluster = cluster;
        ticksUsed = 0;
        myId = ++id;
        new CPUService(String.valueOf(myId), this);
    }

    /**
     * @pre none
     * @post currentTick == @pre(currentTick) + 1
     */

    public void updateTick() {
//        currentTick++;
//        if ()
    }

//    public DataBatch getData() {
//        if (incoming.isEmpty()) {
//            try {
//                cluster.fetchData(capacity);
//            } catch (InterruptedException e) {
//            } // TODO: make sure works correctly
//        }
//        return incoming.remove();
//    }

    /**
     * @pre !incoming.isEmpty()
     * @post outgoing.size() = @pre(outgoing.size) + @pre(incoming.size())
     * @post ticksUsed > @pre(ticksUsed)
     */
    public void processData() {
        if (incoming.isEmpty()) throw new IllegalStateException("Cannot process Data with empty incoming queue");
        while (!incoming.isEmpty()) {

        }
        // incoming is empty
        // cpu service should bring more data
    }

    public void addToIncoming(DataBatch dataBatch) { // used for tests
        incoming.add(dataBatch);
    }

    public int getIncomingSize() { // used for tests
        return incoming.size();
    }

    public int getOutgoingSize() { // used for tests
        return outgoing.size();
    }

    public int getCurrentTick() { // used for tests
        return currentTick;
    }

    public int getTicksUsed() { // used for tests
        return ticksUsed;
    }
}
