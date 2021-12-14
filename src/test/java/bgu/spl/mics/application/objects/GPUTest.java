package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GPUTest {
    Cluster cluster;
    GPU gpu;
    Data data;
    Model model;
    Event<Model> trainEvent;
    Event<Model.Status> testEvent;

    @Before
    public void setUp() throws Exception {
        cluster = new Cluster();
        gpu = new GPU(GPU.Type.RTX3090, cluster);
        data = new Data(Data.Type.Images, 1000);
        model = new Model("test", data);
        trainEvent = new TrainModelEvent("gpuTrain", model);
        testEvent = new TestModelEvent("gpuTest", model);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void updateTick() {
        int currentTick = gpu.getCurrentTick();
        gpu.updateTick();
        assertEquals(currentTick + 1, gpu.getCurrentTick());
    }

    @Test
    public void prepareBatches() {
        assertEquals(0, gpu.getRemainingModelBatches());
        int discSize = gpu.getDiscSize();
        gpu.prepareBatches(data); // should create 20 Batches
        assertEquals(discSize + 20, gpu.getDiscSize());
        assertEquals(20, gpu.getRemainingModelBatches());
        assertThrows("Cannot prepare batches from null data", IllegalArgumentException.class, () -> gpu.prepareBatches(null));
    }

    @Test
    public void sendBatchesToCluster() {
        gpu.prepareBatches(data);
        int discSize = gpu.getDiscSize();
        gpu.sendBatchesToCluster(discSize);
        assertEquals(0, gpu.getDiscSize());
        assertThrows("numOfBatches must be positive", IllegalArgumentException.class, () -> gpu.sendBatchesToCluster(-1));
    }

    @Test
    public void trainProcessed() {
        gpu.prepareBatches(data);
        DataBatch processed = gpu.getNextBatch();
        processed.setProcessed(true);
        gpu.addToVRam(processed);
        gpu.trainProcessed(); // should take 1 tick
        assertFalse(processed.isTrained());
        gpu.updateTick();
        assertTrue(processed.isTrained());
        assertTrue(gpu.vRamIsEmpty());
    }

    @Test
    public void testModel() {
        model.setTrained(true);
        assertEquals("None", model.getStatus());
        gpu.testModel();
        assertNotEquals("None", model.getStatus()); // can be "Good" or "Bad"
    }
}