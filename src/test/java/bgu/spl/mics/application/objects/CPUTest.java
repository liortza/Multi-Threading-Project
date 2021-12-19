package bgu.spl.mics.application.objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CPUTest {

    Cluster cluster = Cluster.getInstance();
    CPU cpu;
    GPU gpu;
    Data data;
    DataBatch batch;

    @Before
    public void setUp() throws Exception {
        cpu = new CPU(32);
        gpu = new GPU("3090");
        cluster.registerGPU(gpu);
        cluster.init(10);
        data = new Data(Data.Type.Images, 1000);
        batch = new DataBatch(data, 0, gpu);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void updateTickFinishBatch() {
        cpu.setCurrent(batch);
        cpu.setTicksRemaining(0);
        int batchesProcessed = cpu.getProcessedBatches();
        cpu.updateTick();
        assertEquals(batchesProcessed + 1, cpu.getProcessedBatches());
    }

    @Test
    public void updateTickDuringProcess() {
        cpu.setCurrent(batch);
        cpu.setTicksRemaining(4);
        int ticksUsed = cpu.getTicksUsed();
        int ticksRemaining = cpu.getTicksRemaining();
        cpu.updateTick();
        assertEquals(ticksUsed + 1, cpu.getTicksUsed());
        assertEquals(ticksRemaining - 1, cpu.getTicksRemaining());
    }

    @Test
    public void updateTickStartProcess() {
        cpu.setCurrent(null);
        cpu.addToIncoming(batch);
        cpu.updateTick();
        assertNotNull(cpu.getCurrent());
    }

}