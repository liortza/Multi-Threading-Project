package bgu.spl.mics.application.objects;


import sun.misc.Cleaner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

    private static class ClusterHolder {
        private static Cluster instance = new Cluster();
    }

    private ArrayList<GPU> gpus = new ArrayList<>();
    private ArrayList<CPU> cpus = new ArrayList<>();
    private final ArrayList<String> trainedModels = new ArrayList<>();
    private final BlockingQueue<DataBatch> unprocessed = new LinkedBlockingQueue<>();
    private final HashMap<GPU, Queue<DataBatch>> gpuQueues = new HashMap<>();

    public Cluster() {}

    /**
     * Retrieves the single instance of this class.
     */
    public static Cluster getInstance() {
        return ClusterHolder.instance;
    }

    public void registerGPU(GPU gpu) {
        System.out.println(gpu.getName() + " is registering at cluster");
        gpus.add(gpu);
        gpuQueues.put(gpu, new LinkedBlockingQueue<>());
    }

    public void registerCPU(CPU cpu) {
        System.out.println(cpu.getName() + " is registering at cluster");
        cpus.add(cpu);
    }

    public void incomingDataFromGPU(Queue<DataBatch> batches) {
        unprocessed.addAll(batches); // TODO: check if need to sync? because of cpu pulling from same queue
    }

    public Queue<DataBatch> fetchProcessedDataGPU(int dataBatches, GPU gpu) {
        Queue<DataBatch> toGPU = new LinkedList<>();
        Queue<DataBatch> gpuQueue = gpuQueues.get(gpu);
        for (int i = 0; i < dataBatches & !gpuQueue.isEmpty(); i++) {
            toGPU.add(gpuQueue.remove());
        }
        return toGPU;
    }

    public Queue<DataBatch> fetchUnprocessedDataCPU(int dataBatches) {
        Queue<DataBatch> toCPU = new LinkedList<>();
        synchronized (unprocessed) {
            for (int i = 0; i < dataBatches & !unprocessed.isEmpty(); i++) {
                toCPU.add(unprocessed.remove());
            }
        }
        return toCPU;
    }

    public void incomingBatchFromCPU(DataBatch batch) {
        GPU recipient = batch.getMyGPU();
        gpuQueues.get(recipient).add(batch);
    }

    public int gpuTicksUsed() {
        int total = 0;
        for (GPU gpu : gpus) total += gpu.getTicksUsed();
        return total;
    }

    public int cpuTicksUsed() {
        int total = 0;
        for (CPU cpu : cpus) total += cpu.getTicksUsed();
        return total;
    }

    public int cpuTotalProcessedBatches() {
        int total = 0;
        for (CPU cpu : cpus) total += cpu.getProcessedBatches();
        return total;
    }

    public void addTrained(String mName) {
        trainedModels.add(mName);
    }

}
