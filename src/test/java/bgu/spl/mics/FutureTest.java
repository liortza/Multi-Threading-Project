package bgu.spl.mics;

import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void get() {
        Future<Integer> f = new Future<>();
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(1000);
                f.resolve(5);
            } catch (InterruptedException e) {}
        });
        t.start();
        long time1 = System.currentTimeMillis(); // time before sleep

        int result = f.get();
        long time2 = System.currentTimeMillis(); // time after sleep
        assertTrue(time2 - time1 >= 1000); // at least 1000 milliseconds passed
        assertEquals(5, result); // assert correct value
    }

    @Test
    public void resolve() {
        Future<Integer> f = new Future<>();
        assertFalse(f.isDone());
        f.resolve(5);
        assertTrue(f.isDone());
        int result = f.get();
        assertEquals(5, result);
    }

//    @Test
//    public void isDone() { // inside resolve
//
//    }

    @Test
    public void testGet() {

        // first test - more time than necessary
        Future<Integer> f1 = new Future<>();
        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(200);
                f1.resolve(5);
            } catch (InterruptedException e) {}
        });
        t1.start();
        long time1 = System.currentTimeMillis(); // time before sleep
        int result1 = f1.get(500, TimeUnit.MILLISECONDS);
        long time2 = System.currentTimeMillis(); // time after sleep
        assertTrue(time2 - time1 < 250); // no more than 250 milliseconds passed
        assertEquals(5, result1); // assert correct value

        // second test - less time than necessary
        Future<Integer> f2 = new Future<>();
        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(700);
                f1.resolve(5);
            } catch (InterruptedException e) {}
        });
        t2.start();
        long time3 = System.currentTimeMillis(); // time before sleep
        int result2 = f1.get(500, TimeUnit.MILLISECONDS);
        long time4 = System.currentTimeMillis(); // time after sleep
        assertTrue(time4 - time3 < 550); // no more than 550 milliseconds passed
        assertFalse(f2.isDone());
    }
}