package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class TestModelEvent implements Event<Model.Status> {
    private Model model;
    private String name;

    public TestModelEvent(String name, Model model) {
        this.name = name;
        this.model = model;
    }
}
