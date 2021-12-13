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
    private boolean isTrained;
    private boolean published;

    public Model(String name, Data data) {
        this.name = name;
        status = Status.None;
        this.data = data;
        isTrained = false;
        published = false;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isTrained() {
        return isTrained;
    }

    public void setTrained(boolean trained) {
        isTrained = trained;
    }

    public void publish() { published = true; }

}
