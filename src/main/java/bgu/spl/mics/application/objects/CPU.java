package bgu.spl.mics.application.objects;

import java.util.LinkedList;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int cores;
    private LinkedList<DataBatch> data;
    private Cluster cluster;

    public CPU(int cores, Cluster cluster) {
        this.cores = cores;
        this.data = new LinkedList<>();
        this.cluster = cluster;
    }

}
