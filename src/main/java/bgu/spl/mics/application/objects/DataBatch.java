package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private final Data data;
    private final int startIndex;
    private final GPU myGPU;
    private boolean processed;

    public DataBatch(Data data, int startIndex, GPU gpu) {
        this.data = data;
        this.startIndex = startIndex;
        myGPU = gpu;
        processed = false;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void process() {
        processed = true;
    }

    public int getTickFactor() { return data.getTickFactor(); }

    public GPU getMyGPU() { return myGPU; }
}
