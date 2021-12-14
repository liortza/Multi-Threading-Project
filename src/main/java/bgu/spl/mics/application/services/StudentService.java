package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.HashMap;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
    private Student myStudent;
    private Future<Model> trainFuture;
    private Future<Model.Status> testFuture;
    private Future<Boolean> publishFuture;
    private int currentTick = 0;

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
    }

    private void sendTrainEvent() {
        Model next = myStudent.getNextModel();
        if (next != null) {
            TrainModelEvent trainEvent = new TrainModelEvent(getName(), next);
            trainFuture = sendEvent(trainEvent);
        } else terminate(); // no more models to train
    }

    private void sendTestEvent() {
        TestModelEvent testEvent = new TestModelEvent(getName(), trainFuture.get()); // TODO: blocking method ??
        // TODO: use isDone()?
        testFuture = sendEvent(testEvent);
        // ......
    }

    private void publishEvent() {

    }

    private void updateTick() {
        currentTick++;
        // call train, test, publish
    }

    private void handlePublishBroadcast(PublishConfrenceBroadcast broadcast) {
        for (String modelName: broadcast.getModelsNames()) {
            for (Model M: myStudent.getModels()) {
                if (M.getName().equals(modelName)) {
                    myStudent.incrementPub();
                    M.publish(); // TODO: publish models here? in conference it's a little problematic because of strings
                }
                else myStudent.incrementRead();
            }
        }
    }
}
