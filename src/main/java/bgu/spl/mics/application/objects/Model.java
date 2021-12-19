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

    private final String name;
    private String type;
    private int size;
    private Status status;
    private Data data;
    private Student student;
    private boolean trained = false, published = false;
    private String tested = "NotTested";

    public Model(String name, Data data) {
        this.name = name;
        status = Status.None;
        this.data = data;
    }

    public void init(Student student) {
        Data.Type type = Data.Type.Images;
        if (this.type.equals("Tabular")) type = Data.Type.Tabular;
        else if (this.type.equals("Text")) type = Data.Type.Text;
        data = new Data(type, size);
        status = Status.None;
        this.student = student;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public Data getData() {
        return data;
    }

    public Student getStudent() {
        return student;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isTrained() {
        return trained;
    }

    public void train() {
        trained = true;
    }

    public void publish() {
        published = true;
    }

    public boolean isPublished() {
        return published;
    }

    public String getResults() {
        if (status == Status.Good)
            return "Good";
        else if (status == Status.Bad)
            return "Bad";
        else return "None";
    }

    public void tested() {
        tested = "Tested";
    }

    public String getTested() {
        return tested;
    }
}
