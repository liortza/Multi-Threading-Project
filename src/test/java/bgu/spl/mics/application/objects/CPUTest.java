package bgu.spl.mics.application.objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CPUTest {

    CPU cpu;
    GPU gpu;
    Data data;
    DataBatch batch;

    @Before
    public void setUp() throws Exception {
        cpu = new CPU(32);
        gpu = new GPU("3090");
        data = new Data(Data.Type.Images, 1000);
        batch = new DataBatch(data, 0, gpu);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void updateTick() {
        // case 1: finished processing batch
        cpu.setCurrent(batch);
        cpu.setTicksRemaining(0);
        int batchedProcessed = cpu.getProcessedBatches();
        cpu.updateTick();
        assertEquals(batchedProcessed + 1, cpu.getProcessedBatches());

        // case 2: during process
        cpu.setCurrent(batch);
        cpu.setTicksRemaining(0);
        int ticksUsed = cpu.getTicksUsed();
        int ticksRemaining = cpu.getTicksRemaining();
        cpu.updateTick();
        assertEquals(ticksUsed + 1, cpu.getTicksUsed());
        assertEquals(ticksRemaining - 1, cpu.getTicksRemaining());

        // case 3: starting process of new batch
        cpu.setCurrent(null);
        cpu.addToIncoming(batch);
        cpu.updateTick();
        assertNotNull(cpu.getCurrent());
    }

}