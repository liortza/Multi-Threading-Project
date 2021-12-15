//package bgu.spl.mics.application.objects;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//public class CPUTest {
//
//    Cluster cluster;
//    CPU cpu;
//    GPU gpu;
//
//    @Before
//    public void setUp() throws Exception {
//        cluster = new Cluster();
//        cpu = new CPU(32, cluster);
//        gpu = new GPU(GPU.Type.RTX3090, cluster);
//    }
//
//    @After
//    public void tearDown() throws Exception {
//    }
//
//    @Test
//    public void updateTick() {
//        int currentTick = cpu.getCurrentTick();
//        cpu.updateTick();
//        assertEquals(currentTick + 1, cpu.getCurrentTick());
//    }
//
//    @Test
//    public void processData() {
//        Data data = new Data(Data.Type.Images, 1000);
//        DataBatch imageBatch = new DataBatch(data, 0, gpu);
//        cpu.addToIncoming(imageBatch); // should take 1 tick
//        int incomingSize = cpu.getIncomingSize();
//        int outgoingSize = cpu.getOutgoingSize();
//        int ticksUsed = cpu.getTicksUsed();
//        cpu.processData(); // should take 1 tick
//        assertFalse(imageBatch.isProcessed());
//        cpu.updateTick();
//        assertTrue(imageBatch.isProcessed());
//        assertEquals(incomingSize + outgoingSize, cpu.getOutgoingSize());
//        assertEquals(ticksUsed + 1, cpu.getTicksUsed());
//        assertThrows("cannot process data with empty incoming queue", IllegalStateException.class, () -> cpu.processData());
//    }
//}