package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class TrainModelEvent implements Event<Boolean> {
    private Model model;
    private String name;

    public TrainModelEvent(String name, Model model) {
        this.name = name;
        this.model = model;
    }
}
