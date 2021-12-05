package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageBusImplTest {

    private static MessageBusImpl bus;
    private static MicroService m1, m2, m3, m4;
    private static Broadcast broadcast;
    private static Event<String> event;

    @Before
    public void setUp() throws Exception {
        bus = new MessageBusImpl();
        m1 = new ExampleMessageSenderService("m1", new String[]{"broadcast"});
        m2 = new ExampleMessageSenderService("m2", new String[]{"event"});
        m3 = new ExampleBroadcastListenerService("m3", new String[]{"5"});
        m4 = new ExampleEventHandlerService("m4", new String[]{"5"});
        broadcast = new ExampleBroadcast("m1");
        event = new ExampleEvent("m2");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void subscribeEvent() {
        bus.register(m4);
        bus.subscribeEvent(ExampleEvent.class, m4); // m4 should get ExampleEvent when sent
        Future<String> f1 = m2.sendEvent(event);
        try {
            Message received = bus.awaitMessage(m4);
            assertEquals(event, received);
        } catch (InterruptedException e) {
            // TODO ??
        }
    }

    @Test
    public void subscribeBroadcast() {
        bus.register(m3);
        bus.subscribeBroadcast(ExampleBroadcast.class, m3);
        m1.sendBroadcast(broadcast);
        try {
            Message received = bus.awaitMessage(m3);
            assertEquals(broadcast, received);
        } catch (InterruptedException e) {
            // TODO ??
        }
    }

    @Test
    public void complete() {
        bus.register(m4);
        bus.subscribeEvent(ExampleEvent.class, m4);
        Future<String> f1 = m2.sendEvent(event);
        try {
            Message received = bus.awaitMessage(m4);
            assertEquals(event, received);
        } catch (InterruptedException e) {
            // TODO ??
        }
        bus.complete(event, "result");
        assert f1 != null;
        assertTrue(f1.isDone());
        assertEquals(f1.get(), "result");
    }

//    @Test
//    public void sendBroadcast() {
//        bus.register(m3);
//        bus.subscribeBroadcast(ExampleBroadcast.class, m3);
//        m1.sendBroadcast(broadcast);
//        try {
//            Message received = bus.awaitMessage(m3);
//            assertEquals(broadcast, received);
//        } catch (InterruptedException e) {
//            // TODO ??
//        }
//    }
//
//    @Test
//    public void sendEvent() {
//        bus.register(m4);
//        bus.subscribeEvent(ExampleEvent.class, m4);
//        Future<String> f1 = m2.sendEvent(event);
//        try {
//            Message received = bus.awaitMessage(m4);
//            assertEquals(event, received);
//        } catch (InterruptedException e) {
//            // TODO ??
//        }
//    }
//
//    @Test
//    public void register() {
//        // TODO: try to subscribe before register, catch some exception from HashMap? legal test?
//    }

    @Test
    public void unregister() {
        bus.register(m3);
        bus.unregister(m3); // TODO: try to subscribe before register, catch some exception from HashMap? legal test?
        try {
            bus.awaitMessage(m3);
        } catch (InterruptedException e) {
            // TODO ??
        }
    }

//    @Test
//    public void awaitMessage() {
//        bus.register(m3);
//        bus.subscribeBroadcast(ExampleBroadcast.class, m3);
//        m1.sendBroadcast(broadcast);
//        try {
//            Message received = bus.awaitMessage(m3);
//            assertEquals(broadcast, received);
//        } catch (InterruptedException e) {
//            // TODO ??
//        }
//    }
}