package bgu.spl.mics;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	// inv - number of subscribed is non-negative?

	// private field (dynamic array/LL 2 dimensional) for microservices subscribed to event/broadcast?
	// private field pointer to next microservice (round robin)

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// TODO Auto-generated method stub

		// param - type != null, m != null
		// pre - m is not subscribed to type EVENT
		// post - m is subscribed to type EVENT. event.size() = pre.size() + 1;
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub

		// param - type != null, m != null
		// pre - m is not subscribed to type BROADCAST
		// post - m is subscribed to type BROADCAST. broadcast.size() = pre.size() + 1;
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

		// param != null
		// pre, post - ??
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub

		// param - b != null
		// need to check that broadcasts were sent?? how?
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;

		// param - e != null
		// pre - num of subscribers to Event<t> > 0 (list is not empty..) ??
		// if list is empty -> return null. how to check in unit tests?
		// need to check event was added to microservice?
	}

	@Override
	public void register(MicroService m) {
		// TODO Auto-generated method stub

		// param - m != null
		// pre - m is not registered (doesn't have a queue)
		// post - m is registered (has a queue)
	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

		// param - m != null
		// pre - m is registered (has a queue)
		// pre - m is not registered (doesn't have a queue)
	}

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
