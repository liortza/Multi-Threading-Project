package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private Data data;
    private int startIndex;
    private GPU myGPU;
    private boolean processed, trained;

    public DataBatch(Data data, int startIndex, GPU gpu) {
        this.data = data;
        this.startIndex = startIndex;
        myGPU = gpu;
        processed = false;
        trained = false;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public boolean isTrained() {
        return trained;
    }

    public void setTrained () {
        trained = true;
    }
}
