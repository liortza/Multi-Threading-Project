package bgu.spl.mics.application.objects;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.PublishConfrenceBroadcast;

import java.util.ArrayList;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    private String name;
    private String department;
    private Degree degree;
    private int publications = 0, papersRead = 0, index = 0;
    private Model[] models;
    private Future future; // Future<??>

    public Student(String name, String department, Degree degree) {
        this.name = name;
        this.department = department;
        this.degree = degree;
    }


    public Model getNextModel() {
        Model next = null;
        if (index < models.length) {
            next = models[index];
            index++;
        }
        return next;
    }

    public Student.Degree getDegree() { return degree; }

    public void incrementPub() {
        publications++;
    }

    public void incrementRead() {
        papersRead++;
    }

    public String getName() { return name;}

    public String getDepartment() { return department;}

    public String getStatus() { //for output file
        if(degree== Degree.MSc) return "Msc";
        else return "PhD";
    }
    public int getNumOfPublications() {return publications;}

    public int getNumOfPapers() {return papersRead;}

    public Model[] getModels() {return models;}
}
