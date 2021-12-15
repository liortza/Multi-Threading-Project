package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class PublishResultsEvent implements Event<Boolean> {
    Model model;
    Model.Status status;

    public PublishResultsEvent(Model model, Model.Status status) {
        this.model = model;
        this.status = status;
    }

    public Model getModel() {
        return model;
    }

    public Model.Status getStatus() {
        return status;
    }
}
