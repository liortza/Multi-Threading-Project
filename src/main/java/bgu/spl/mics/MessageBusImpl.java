package bgu.spl.mics;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 *
 * @INV: number of subscribed is non-negative?
 */
public class MessageBusImpl implements MessageBus {

    private final HashMap<Class<? extends Event>, BlockingQueue<MicroService>> eventList; // TODO: Event<T> ?
    private final HashMap<Class<? extends Broadcast>, BlockingQueue<MicroService>> broadcastList;
    private final HashMap<MicroService, BlockingQueue<Message>> queues;
    private final HashMap<Event, Future> futuresList;

    private static class MessageBusHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    public MessageBusImpl() {
        eventList = new HashMap<>();
        broadcastList = new HashMap<>();
        queues = new HashMap<>();
        futuresList = new HashMap<>();
    }

    /**
     * @param type != null
     * @param m    != null
     * @pre isRegistered(m) == true
     * @pre isSubscribedToEvent(type, m) == false
     * @post isSubscribedToEvent(type, m) == true
     */
    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        if (!isRegistered(m)) throw new IllegalStateException("MicroService must be registered before subscribe");
        synchronized (eventList) {
            if (eventList.get(type) == null) // add event to map if doesn't exist
                eventList.put(type, new LinkedBlockingQueue<>());
            eventList.notifyAll();
        }
        synchronized (eventList.get(type)) {
            if (!isSubscribedToEvent(type, m)) // add m to eventList if not subscribed
                eventList.get(type).add(m);
            eventList.get(type).notifyAll();
        }
    }

    public <T> boolean isSubscribedToEvent(Class<? extends Event<T>> type, MicroService m) {
        return eventList.get(type).contains(m);
    }

    /**
     * @param type != null
     * @param m    != null
     * @pre isRegistered(m) == true
     * @pre isSubscribedToBroadcast(type, m) == false
     * @post isSubscribedToBroadcast(type, m) == true
     */

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        if (!isRegistered(m)) throw new IllegalStateException("MicroService must be registered before subscribe");
        synchronized (broadcastList) {
            if (broadcastList.get(type) == null) // add broadcast to map if doesn't exist
                broadcastList.put(type, new LinkedBlockingQueue<>());
            broadcastList.notifyAll();
        }
        synchronized (broadcastList.get(type)) {
            if (!isSubscribedToBroadcast(type, m)) // add m to broadcastList if not subscribed
                broadcastList.get(type).add(m);
            broadcastList.get(type).notifyAll();
        }
    }

    public <T> boolean isSubscribedToBroadcast(Class<? extends Broadcast> type, MicroService m) {
        return broadcastList.get(type).contains(m);
    }

    /**
     * @param e      =! null
     * @param result =! null
     * @pre getFutureValue(e) == null
     * @post getFutureValue(e) != null
     */

    @Override
    public <T> void complete(Event<T> e, T result) {
        if (result == null) throw new IllegalArgumentException("result cannot be null");
        futuresList.get(e).resolve(result);
    }

    /**
     * @param b =! null
     * @pre none
     * @post TODO: do we need to get list of microservices subscribed and check each one's queue??
     */

    @Override
    public void sendBroadcast(Broadcast b) { // TODO: throwsInterruptedException??
        System.out.println("broadcast received (bus)");
        if (broadcastList.get(b.getClass()) != null) { // at least one microservice subscribed to broadcast
            BlockingQueue<MicroService> subscribers = broadcastList.get(b.getClass());
            for (MicroService ms : subscribers) {
                queues.get(ms).add(b);
                System.out.println(ms.getName() + " got broadcast");
            }
        }
    }

    /**
     * @param e != null
     * @return futuresList.get(e)
     * @pre eventList.get(e) != null // some microservice is subscribed
     * @pre futuresList.get(e) == null
     * @post futuresList.get(e) != null
     */

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        Future<T> future = null;
        if (eventList.get(e.getClass()) != null) { // at least one microservice subscribed to event
            synchronized (eventList.get(e.getClass())) {
                MicroService recipient = eventList.get(e.getClass()).remove(); // get next in line by round robin
                queues.get(recipient).add(e);
                //queues.get(recipient).notifyAll();
                eventList.get(e.getClass()).add(recipient); // add to end of queue
                eventList.get(e.getClass()).notifyAll();
            }
            future = new Future<>(); // TODO: check thread safe
            futuresList.put(e, future);
        }
        return future;
    }

    /**
     * @param m != null
     * @pre isRegistered(m) == false
     * @post isRegistered(m) == true
     */
    @Override
    public void register(MicroService m) {
        if (m == null) throw new IllegalArgumentException("MicroService is null");
        synchronized (queues) {
            if (isRegistered(m)) throw new IllegalStateException("MicroService already registered");
            queues.put(m, new LinkedBlockingQueue<>());
            queues.notifyAll();
        }
    }

    public boolean isRegistered(MicroService m) {
        return queues.containsKey(m);
    }

    /**
     * @param m != null
     * @pre isRegistered(m) == true
     * @post isRegistered(m) == false
     * @post TODO: check if removed from all Event's list and Broadcast's list
     */

    @Override
    public void unregister(MicroService m) {
        if (!isRegistered(m)) throw new IllegalStateException("MicroService is not registered");
        queues.remove(m);
    }

    /**
     * @param m != null
     * @throws InterruptedException if interrupted while waiting for a message
     *                              to became available.
     * @pre isRegistered(m) == true
     * @pre queues.get(m).isEmpty == false TODO: correct?? if isEmpty() should wait..
     * @post queues.get(m).size() == @pre(queues.get(m).size() - 1)
     */

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        return queues.get(m).take(); // blocks m if queue is empty
    }

    public static MessageBus getInstance() {
        return MessageBusHolder.instance;
    }

}
