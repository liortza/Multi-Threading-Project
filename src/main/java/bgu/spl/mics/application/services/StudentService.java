package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private final Student myStudent;
    private Model current = null;
    private Future<Model> trainFuture;
    private Future<Model.Status> testFuture;
    private final HashMap<Model, Future<Boolean>> publishFutures = new HashMap<>();

    public StudentService(String name, Student myStudent) {
        super(name);
        this.myStudent = myStudent;
    }

    @Override
    protected void initialize() {
        // PublishConferenceBroadcast
        Callback<PublishConfrenceBroadcast> publishCallback = (PublishConfrenceBroadcast b) -> handlePublishBroadcast(b);
        subscribeBroadcast(PublishConfrenceBroadcast.class, publishCallback);

        // TickEvent
        Callback<TickBroadcast> tickCallback = (TickBroadcast b) -> updateTick();
        subscribeBroadcast(TickBroadcast.class, tickCallback);

        // TerminateBroadcast
        Callback<TerminateBroadcast> terminateCallback = (TerminateBroadcast b) -> terminate();
        subscribeBroadcast(TerminateBroadcast.class, terminateCallback);

        sendBroadcast(new ReadyBroadcast(this));
    }

    private void sendTrainEvent() {
        current = myStudent.getNextModel();
        if (current != null) {
            TrainModelEvent trainEvent = new TrainModelEvent(getName(), current);
            trainFuture = sendEvent(trainEvent);
        }
    }

    private void sendTestEvent() {
        if (trainFuture.isDone()) { // trainFuture.get() doesn't block
            TestModelEvent testEvent = null;
            try {
                testEvent = new TestModelEvent(getName(), trainFuture.get(), myStudent.getDegree());
            } catch (InterruptedException e) {
            }
            testFuture = sendEvent(testEvent);
        }
    }

    private void sendPublishEvent() {
        PublishResultsEvent publishEvent;
        Model.Status status = null;
        if (testFuture.isDone()) { // testFuture.get() doesn't block
            try {
                status = testFuture.get();
            } catch (InterruptedException e) {}
        } if (status != null) {
            if (status == Model.Status.Good) {
                publishEvent = new PublishResultsEvent(current, status);
                publishFutures.put(current, sendEvent(publishEvent));
            }
            // reset futures for next model loop
            current = null;
            trainFuture = null;
            testFuture = null;
        }
    }

    private void updateTick() {
        if (current == null) sendTrainEvent();
        else if (trainFuture != null && trainFuture.isDone() & testFuture == null) sendTestEvent();
        else if (testFuture != null && testFuture.isDone()) sendPublishEvent();
    }

    private void handlePublishBroadcast(PublishConfrenceBroadcast broadcast) {
        for (Model model : broadcast.getModels()) {
            if (model.getStudent().equals(myStudent)) myStudent.incrementPub();
            else myStudent.incrementRead();
        }
    }
}