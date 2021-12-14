package bgu.spl.mics.application.objects;


import sun.misc.Cleaner;

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

	private BlockingQueue<DataBatch> unprocessed;
	private HashMap<GPU, Queue<DataBatch>> gpuQueues;

	/**
     * Retrieves the single instance of this class.
     */

	public Cluster() {
		unprocessed = new LinkedBlockingQueue<>();
		gpuQueues = new HashMap<>();
	}

	public static Cluster getInstance() {
		return ClusterHolder.instance;
	}

	public void addGPUQueue(GPU gpu) {
		gpuQueues.put(gpu, new LinkedBlockingQueue<>());
	}

	public void incomingDataFromGPU(Queue<DataBatch> batches) {

	}

	public Queue<DataBatch> fetchProcessedDataGPU (int dataBatches, GPU gpu) {
		Queue<DataBatch> toGPU = new LinkedList<>();
		Queue<DataBatch> gpuQueue = gpuQueues.get(gpu);
		for (int i = 0; i < dataBatches & !gpuQueue.isEmpty(); i++) {
			toGPU.add(gpuQueue.remove());
		}
		return toGPU;
	}

	public Queue<DataBatch> fetchUnprocessedDataCPU(int dataBatches) {
		return null;
	}

	public void incomingBatchFromCPU(DataBatch batch) {
		// put in correct GPU's queue
	}

}
