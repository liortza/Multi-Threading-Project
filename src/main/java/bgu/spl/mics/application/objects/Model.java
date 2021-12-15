package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    public enum Status {
        None, Good, Bad;
    }

    private String name;
    private Status status;
    private Data data;
    private Student student;
    private boolean trained = false, published = false;

    public Model(String name, Data data, Student student) {
        this.name = name;
        status = Status.None;
        this.data = data;
        this.student = student;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public Data getData() { return data; }

    public Student getStudent() { return student; }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isTrained() {
        return trained;
    }

    public void train() {
        trained = true;
    }

    public void publish() { published = true; }

    public String getTested(){
        if(status==status.None) return "NotTested";
        else return "Tested";
    }
    public String getResults() {
        if(status==status.Good)
            return "Good";
        else if(status==status.Bad)
            return "Bad";
        else return "None";
    }

}
