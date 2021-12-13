package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConfrenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

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

    public StudentService(String name, Student myStudent) {
        super(name);
        this.myStudent = myStudent;
        // TODO Implement this
    }

    @Override
    protected void initialize() {
        // TODO Implement this
        super.subscribeBroadcast(PublishConfrenceBroadcast.class, this::handlePublishBroadcast); // todo: callback
    }

    private void sendTrainEvent() {
        Model next = myStudent.getNextModel();
        if (next != null) {
            TrainModelEvent trainEvent = new TrainModelEvent(super.getName(), next);
            trainFuture = super.sendEvent(trainEvent);
        } else terminate(); // no more models to train
    }

    private void sendTestEvent() {
        TestModelEvent testEvent = new TestModelEvent(super.getName(), trainFuture.get()); // TODO: blocking method ??
        // TODO: use isDone()?
        testFuture = super.sendEvent(testEvent);
        // ......
    }

    private void publishEvent() {

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
