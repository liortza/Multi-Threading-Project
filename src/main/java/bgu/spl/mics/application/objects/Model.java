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

    private String name, type;
    private int size;
    private Status status;
    private Data data;
    private Student student;
    private boolean trained = false, published = false;

    public Model(String name, Data data) {
        this.name = name;
        status = Status.None;
        this.data = data;
    }

//    public Model(String name, String type, int size) {
//        this.name = name;
//        status = Status.None;
//        Data.Type dataType = Data.Type.Images;
//        if (type.equals("Tabular")) dataType = Data.Type.Tabular;
//        else if (type.equals("Text")) dataType = Data.Type.Text;
//        this.data = new Data(dataType, size);
//    }

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

}
