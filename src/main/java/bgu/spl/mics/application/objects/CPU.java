package bgu.spl.mics.application.objects;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {

    public int currentTick = 0; // public for tests

    private int cores;
    public Queue<DataBatch> incoming; // public for tests
    public Queue<DataBatch> outgoing; // public for tests
    private int capacity;
    private Cluster cluster;
    private int ticksUsed;

    public CPU(int cores, Cluster cluster) {
        this.cores = cores;
        this.incoming = new LinkedList<>();
        this.outgoing = new LinkedList<>();
        capacity = cores; // TODO: see how to define capacity
        this.cluster = cluster;
        ticksUsed = 0;
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
     */
    public void processData() {
        if (incoming.isEmpty()) throw new IllegalStateException("Cannot process Data with empty incoming queue");
        while (!incoming.isEmpty()) {

        }
        // incoming is empty
        // cpu service should bring more data
    }
}
