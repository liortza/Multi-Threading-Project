package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class TestModelEvent implements Event<Model.Status> {
    private Model model;
    private String name;
    private Student.Degree degree;

    public TestModelEvent(String name, Model model, Student.Degree degree) {
        this.name = name;
        this.model = model;
        this.degree = degree;
    }

    public Student.Degree getDegree() { return degree; }

    public Model getModel() { return model; }
}
