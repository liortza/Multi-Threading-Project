package bgu.spl.mics.application.services;
import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.GPU;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    private GPU gpu;

    public GPUService(String name, GPU myGpu) {
        super(name);
        gpu = myGpu;
    }

    @Override
    protected void initialize() {
        // TrainModelEvent
        Callback<TrainModelEvent> trainCallback = (TrainModelEvent e) -> gpu.handleTrainEvent(e);
        System.out.println(getName() + " is subscribing to trainEvent");
        subscribeEvent(TrainModelEvent.class, trainCallback);

        // TestModelEvent
        Callback<TestModelEvent> testCallback = (TestModelEvent e) -> gpu.handleTestEvent(e);
        System.out.println(getName() + " is subscribing to testEvent");
        subscribeEvent(TestModelEvent.class, testCallback);

        // TickBroadcast
        Callback<TickBroadcast> tickCallback = (TickBroadcast b) -> gpu.updateTick();
        System.out.println(getName() + " is subscribing to tickBroad");
        subscribeBroadcast(TickBroadcast.class, tickCallback);

        // TerminateBroadcast
        Callback<TerminateBroadcast> terminateCallback = (TerminateBroadcast b) -> terminate();
        System.out.println(getName() + " is subscribing to terminateBroad");
        subscribeBroadcast(TerminateBroadcast.class, terminateCallback);
    }

    public <T> void completeEvent(Event<T> e, T result) {
        complete(e, result);
    }
}
