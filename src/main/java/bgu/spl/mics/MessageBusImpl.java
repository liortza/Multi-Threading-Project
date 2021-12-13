package bgu.spl.mics;

import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 *
 * @INV: number of subscribed is non-negative?
 */
public class MessageBusImpl implements MessageBus {
    int trainIndex = 0, testIndex = 0, publishIndex = 0;

    private ConcurrentHashMap<Class<? extends Event>, ArrayList<MicroService>> eventList; // TODO: Event<T> ?
    private ConcurrentHashMap<Broadcast, ArrayList<MicroService>> broadcastList;
    private ConcurrentHashMap<MicroService, Queue<Message>> queues;
    private ConcurrentHashMap<Event, Future> futuresList;
    private static MessageBus INSTANCE = null;
    private static final Object trainLock = new Object(), testLock = new Object(), publishLock = new Object(),
            subscribeEvent = new Object(), subscribeBroadcast = new Object();

    public MessageBusImpl() {
        eventList = new ConcurrentHashMap<>();
        broadcastList = new ConcurrentHashMap<>();
        queues = new ConcurrentHashMap<>();
        futuresList = new ConcurrentHashMap<>();
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
        // TODO: synchronize exception??
        if (!isRegistered(m)) throw new IllegalStateException("MicroService must be registered before subscribe");
        synchronized (subscribeEvent) {
            if (!eventList.get(type).contains(m)) eventList.get(type).add(m);
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
        synchronized (subscribeBroadcast) {
            if (!broadcastList.get(type).contains(m)) broadcastList.get(type).add(m);
        }
    }

    public <T> boolean isSubscribedToBroadcast(Class<? extends Event<T>> type, MicroService m) {
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

    private <T> T getFutureValue(Event<T> e) {
        return (T) futuresList.get(e).get();
    }

    /**
     * @param b =! null
     * @pre none
     * @post TODO: do we need to get list of microservices subscribed and check each one's queue??
     */

    @Override
    public void sendBroadcast(Broadcast b) {
        ArrayList<MicroService> subscribed = broadcastList.get(b);
        for (MicroService ms : subscribed) {
            queues.get(ms).add(b);
            ms.notify(); // TODO: ok??
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
        if (eventList.get(e) != null) { // at least one microservice subscribed to event type
            if (e.getClass() == TrainModelEvent.class) {
                synchronized (trainLock) {
                    MicroService recipient = eventList.get(e).get(trainIndex);
                    queues.get(recipient).add(e);
                    recipient.notify(); // TODO: ok??
                    trainIndex = (trainIndex++) % eventList.get(e).size();
                }
            } else if (e.getClass() == TestModelEvent.class) {
                synchronized (testLock) {
                    MicroService recipient = eventList.get(e).get(testIndex);
                    queues.get(recipient).add(e);
                    recipient.notify();
                    testIndex = (testIndex++) % eventList.get(e).size();
                }
            } else {
                synchronized (publishLock) {
                    MicroService recipient = eventList.get(e).get(publishIndex);
                    queues.get(recipient).add(e);
                    recipient.notify();
                    publishIndex = (publishIndex++) % eventList.get(e).size();
                }
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
        if (isRegistered(m)) throw new IllegalStateException("MicroService already registered");
        queues.put(m, new LinkedList<>());
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
        if (queues.get(m).isEmpty()) m.wait();
        return queues.get(m).remove();
    }

    public static MessageBus getInstance() { // TODO make sure thread safe
        if (INSTANCE == null) {
            INSTANCE = new MessageBusImpl();
        }
        return INSTANCE;
    }
}
