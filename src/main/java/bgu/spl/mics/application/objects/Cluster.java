package bgu.spl.mics.application.objects;


import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {

	private Queue<DataBatch> queue;

	/**
     * Retrieves the single instance of this class.
     */

	public Cluster() {
		queue = new ConcurrentLinkedQueue<>();
	}

	public static Cluster getInstance() {
		//TODO: Implement this
		return null;
	}

	public Queue<DataBatch> fetchData(int dataBatches) throws InterruptedException{
		return null; // TODO: wait if queue is empty
	}

}
