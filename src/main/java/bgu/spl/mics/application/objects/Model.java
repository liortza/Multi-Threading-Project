package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {
    public String name, status;
    private Data data;
    public boolean isTrained; // public for tests

    public Model(String name, Data data) {
        status = "None";
    }
}
