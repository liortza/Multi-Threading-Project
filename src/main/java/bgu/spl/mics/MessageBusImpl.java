package bgu.spl.mics;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 *
 * @INV: number of subscribed is non-negative?
 */
public class MessageBusImpl implements MessageBus {

	private HashMap<Class<? extends Event>, Pair<ArrayList<MicroService>, Integer>> eventList; // TODO: Event<T> ?
	private HashMap<Broadcast, ArrayList<MicroService>> broadcastList;
	private HashMap<MicroService, Queue<Message>> queues;
	private HashMap<Event, Future> futuresList;

	/**
	 * @param type != null
	 * @param m != null
	 * @pre isRegistered(m) == true
	 * @pre isSubscribedToEvent(type, m) == false
	 * @pre isSubscribedToEvent(type, m) == true
	 */
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub
	}

	private <T> boolean isSubscribedToEvent(Class<? extends Event<T>> type, MicroService m) {
		Pair<ArrayList<MicroService>, Integer> pair = eventList.get(type);
		return pair.getKey().contains(m);
	}

	/**
	 * @param type != null
	 * @param m != null
	 * @pre isRegistered(m) == true
	 * @pre isSubscribedToBroadcast(type, m) == false
	 * @post isSubscribedToBroadcast(type, m) == true
	 */

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub
	}

	private <T> boolean isSubscribedToBroadcast(Class<? extends Event<T>> type, MicroService m) {
		return broadcastList.get(type).contains(m);
	}

	/**
	 * @param e =! null
	 * @param result =! null
	 * @pre getFutureValue(e) == null
	 * @post getFutureValue(e) != null
	 */

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub
	}

	private <T> T getFutureValue(Event<T> e) { return (T) futuresList.get(e).get(); }

	/**
	 * @param b =! null
	 * @pre none
	 * @post TODO: do we need to get list of microservices subscribed and check each one's queue??
	 */

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub
		// need to check that broadcasts were sent?? how?
	}

	/**
	 *
	 * @param e != null
	 * @pre eventList.get(e) != null // some microservice is subscribed
	 * @pre futuresList.get(e) == null
	 * @post futuresList.get(e) != null
	 * @return futuresList.get(e)
	 */
	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;

		// param - e != null
		// pre - num of subscribers to Event<t> > 0 (list is not empty..) ??
		// if list is empty -> return null. how to check in unit tests?
		// need to check event was added to microservice?
	}

	/**
	 *
	 * @param m != null
	 * @pre isRegistered(m) == false
	 * @post isRegistered(m) == true
	 */
	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub
	}

	private boolean isRegistered(MicroService m) {
		return queues.containsKey(m);
	}

	/**
	 *
	 * @param m != null
	 * @pre isRegistered(m) == true
	 * @post isRegistered(m) == false
	 * @post TODO: check if removed from all Event's list and Broadcast's list
	 */

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

		// param - m != null
		// pre - m is registered (has a queue)
		// pre - m is not registered (doesn't have a queue)
	}

	/**
	 *
	 * @param m != null
	 * @pre isRegistered(m) == true
	 * @pre queues.get(m).isEmpty == false TODO: correct?? if isEmpty() should wait..
	 * @post queues.get(m).size() == @pre(queues.get(m).size() - 1)
	 * @throws InterruptedException if interrupted while waiting for a message
	 *                              to became available.
	 */

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;

		// param - m != null
		// pre - queue not empty
		// else throws exception??
		// post - queue.size()--;
	}
}
