package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageBusImplTest {

    private MessageBusImpl bus;

    @Before
    public void setUp() throws Exception {
        bus = new MessageBusImpl();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void subscribeEvent() {
        //assertTrue(bus);
    }

    @Test
    public void subscribeBroadcast() {
    }

    @Test
    public void complete() {
    }

    @Test
    public void sendBroadcast() {
    }

    @Test
    public void sendEvent() {
    }

    @Test
    public void register() {
    }

    @Test
    public void unregister() {
    }

    @Test
    public void awaitMessage() {
    }
}