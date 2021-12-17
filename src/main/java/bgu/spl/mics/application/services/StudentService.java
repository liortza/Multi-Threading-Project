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
    private Student myStudent;
    private Model current = null;
    private Future<Model> trainFuture;
    private Future<Model.Status> testFuture;
    private HashMap<Model, Future<Boolean>> publishFutures;

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
            System.out.println(getName() + " is sending TrainEvent: " + current.getName());
            trainFuture = sendEvent(trainEvent);
        }
    }

    private void sendTestEvent() {
        TestModelEvent testEvent = null;
        try { // TODO: fix try/catch
            testEvent = new TestModelEvent(getName(), trainFuture.get(), myStudent.getDegree());
            System.out.println(getName() + " is sending TestEvent: " + current.getName());
        } catch (InterruptedException e) {
        }
        testFuture = sendEvent(testEvent);
    }

    private void sendPublishEvent() {
        PublishResultsEvent publishEvent = null;
        Model.Status status;
        try {
            status = testFuture.get(); // TODO: try/catch ok??
        } catch (InterruptedException e) {
            status = testFuture.get(1, TimeUnit.MILLISECONDS);
        }
        if (status == Model.Status.Good) {
            System.out.println(getName() + " is sending PublishEvent: " + current.getName());
            publishEvent = new PublishResultsEvent(current, status);
            publishFutures.put(current, sendEvent(publishEvent));
        }
        current = null;
        trainFuture = null;
        testFuture = null;
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