package bgu.spl.mics.application.objects;

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
    private String status;
    private Degree degree;
    private int publications = 0, papersRead = 0, index = 0;
    private Model[] models;

    public Student(String name, String department, String status, Model[] models) {
        this.name = name;
        this.department = department;
        degree = Degree.MSc;
        if (status.equals("PhD")) degree = Degree.PhD;
        this.models = models;
    }

    public void init() {
        degree = Degree.MSc;
        if (status.equals("PhD")) degree = Degree.PhD;
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

    public Student.Degree getDegree() {
        return degree;
    }

    public String getName() {
        return name;
    }

    public void incrementPub() {
        publications++;
    }

    public void incrementRead() {
        papersRead++;
    }

    public String getDepartment() {
        return department;
    }

    public String getStatus() { //for output file
        if (degree == Degree.MSc) return "Msc";
        else return "PhD";
    }

    public int getNumOfPublications() {
        return publications;
    }

    public int getNumOfPapers() {
        return papersRead;
    }

}