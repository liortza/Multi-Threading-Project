package bgu.spl.mics.application.objects;


import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	// private static class

	private BlockingQueue<DataBatch> unprocessed;
	private HashMap<GPU, Queue<DataBatch>> gpuProcessedQueues;

	/**
     * Retrieves the single instance of this class.
     */

	public Cluster() {
		unprocessed = new LinkedBlockingQueue<>();
	}

	public static Cluster getInstance() {
		//TODO: Implement this
		return null;
	}

	public void incomingDataFromGPU(Queue<DataBatch> batches) {

	}

	public Queue<DataBatch> fetchProcessedDataGPU (int dataBatches, GPU gpu) {
		return null; // TODO: wait if queue is empty
	}

	public Queue<DataBatch> fetchUnprocessedDataCPU(int dataBatches) {
		return null;
	}

	public void incomingBatchFromCPU(DataBatch batch) {
		// put in correct GPU's queue
	}

}
