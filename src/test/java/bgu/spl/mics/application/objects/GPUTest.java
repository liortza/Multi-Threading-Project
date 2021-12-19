package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GPUTest {
    Cluster cluster = Cluster.getInstance();
    GPU gpu;
    Data data;
    DataBatch batch;
    Model model;
    TrainModelEvent trainEvent;
    TestModelEvent testEvent;

    @Before
    public void setUp() throws Exception {
        gpu = new GPU("3090");
        data = new Data(Data.Type.Images, 1000);
        batch = new DataBatch(data, 0, gpu);
        model = new Model("gpuTest", data);
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
    public void updateTickFinishModel() {
        gpu.setCurrentModel(model);
        gpu.setCurrentBatch(batch);
        gpu.setTicksRemaining(0);
        gpu.setRemainingModelBatches(1);
        assertFalse(model.isTrained());
        gpu.updateTick();
        assertTrue(model.isTrained());
    }

    @Test
    public void updateTickFinishBatch() {
        gpu.setCurrentModel(model);
        gpu.setCurrentBatch(batch);
        gpu.setTicksRemaining(0);
        gpu.setRemainingModelBatches(3);
        int remainingBatches = gpu.getRemainingModelBatches();
        gpu.updateTick();
        assertEquals(remainingBatches - 1, gpu.getRemainingModelBatches());
    }

    @Test
    public void updateTickDuringBatch() {
        gpu.setCurrentModel(model);
        gpu.setCurrentBatch(batch);
        gpu.setTicksRemaining(3);
        int ticksRemaining = gpu.getTicksRemaining();
        int ticksUsed = gpu.getTicksUsed();
        gpu.updateTick();
        assertEquals(ticksRemaining - 1, gpu.getTicksRemaining());
        assertEquals(ticksUsed + 1, gpu.getTicksUsed());
    }

    @Test
    public void updateTickFetchBatch() {
        gpu.setCurrentModel(model);
        gpu.setCurrentBatch(null);
        gpu.addToVRam(batch);
        int vRamSize = gpu.getVRamSize();
        assertNotNull(gpu.getCurrentBatch());
        assertEquals(vRamSize - 1, gpu.getVRamSize());
    }

    @Test
    public void updateTickNextTrain() {
        gpu.setCurrentModel(null);
        gpu.addToMessageDequeue(trainEvent);
        gpu.updateTick();
        assertNotNull(gpu.getCurrentModel());
        assertEquals(data.getSize() / 1000, gpu.getDiscSize());
    }

    @Test
    public void updateTickTest() {
        gpu.setCurrentModel(null);
        gpu.addToMessageDequeue(testEvent);
        int messages = gpu.getMessageSize();
        gpu.updateTick();
        assertEquals(messages - 1, gpu.getMessageSize());
    }

    @Test
    public void handleTrainEvent() {
        int messageSize = gpu.getMessageSize();
        trainEvent = new TrainModelEvent("gpuTrain", model);
        gpu.handleTrainEvent(trainEvent);
        assertEquals(messageSize + 1, gpu.getMessageSize());
    }

    @Test
    public void handleTestEvent() {
        int messageSize = gpu.getMessageSize();
        testEvent = new TestModelEvent("gpuTest", model, Student.Degree.PhD);
        gpu.handleTestEvent(testEvent);
        assertEquals(messageSize + 1, gpu.getMessageSize());
    }

}