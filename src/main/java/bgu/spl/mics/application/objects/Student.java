package bgu.spl.mics.application.objects;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.PublishConfrenceBroadcast;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree degree;
    private int publications;
    private int papersRead;
    private Model[] models;
    private int index;
    private Future future; // Future<??>

    public Student(String name, String department, Degree degree, Model[] models) {
        this.name = name;
        this.department = department;
        this.degree = degree;
        this.models = models;
        publications = 0;
        papersRead = 0;
        index = 0;
    }

    public Model[] getModels() {
        return models;
    }

    public Model getNextModel() {
        Model next = null;
        if (index < models.length) {
            next = models[index];
            index++;
        }
        return next;
    }

    public void incrementPub() {
        publications++;
    }

    public void incrementRead() {
        papersRead++;
    }
}
