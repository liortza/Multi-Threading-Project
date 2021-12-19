package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    Future<Integer> f;

    @Before
    public void setUp() throws Exception {
        f = new Future<>();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void get() {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(1000);
                f.resolve(5);
            } catch (InterruptedException e) {
            }
        });
        t.start();
        long time1 = System.currentTimeMillis(); // time before sleep
        int result = 0;
        try {
            result = f.get();
        } catch (InterruptedException e) {
        }
        long time2 = System.currentTimeMillis(); // time after sleep
        assertTrue(time2 - time1 >= 1000); // at least 1000 milliseconds passed
        assertEquals(5, result);
    }

    @Test
    public void resolve() {
        assertFalse(f.isDone());
        f.resolve(5);
        assertTrue(f.isDone());
        int result = 0;
        try {
            result = f.get();
        } catch (InterruptedException e) {
        }
        assertEquals(5, result);
        assertThrows("cannot resolve null value", IllegalArgumentException.class, () -> f.resolve(null));
    }

    @Test
    public void testGetMoreTime() {
        // first test - more time than necessary
        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(200);
                f.resolve(5);
            } catch (InterruptedException e) {
            }
        });
        t1.start();
        long time1 = System.currentTimeMillis(); // time before sleep
        int result1 = f.get(500, TimeUnit.MILLISECONDS);
        long time2 = System.currentTimeMillis(); // time after sleep
        assertTrue(time2 - time1 < 250); // no more than 250 milliseconds passed
        assertEquals(5, result1); // assert correct value
    }

    @Test
    public void testGetLessTime() {
        // second test - less time than necessary
        Future<Integer> f1 = new Future<>();
        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(700);
                f1.resolve(5);
            } catch (InterruptedException e) {
            }
        });
        t2.start();
        long time3 = System.currentTimeMillis(); // time before sleep
        f1.get(500, TimeUnit.MILLISECONDS);
        long time4 = System.currentTimeMillis(); // time after sleep
        assertTrue(time4 - time3 < 550); // no more than 550 milliseconds passed
        assertFalse(f1.isDone());
    }
}