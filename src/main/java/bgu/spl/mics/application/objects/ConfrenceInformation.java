package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.ConferenceService;

import java.util.LinkedList;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private final String name;
    private final int date;
    private LinkedList<Model> models;

    public ConfrenceInformation(String name, int date) {
        this.name = name;
        this.date = date;
    }

    public void init() {
        models = new LinkedList<>();
    }

    public int getDate() {
        return date;
    }

    public void addModel(Model m) {
        if (m == null) throw new IllegalArgumentException("Model is null");
        models.add(m);
    }

    public LinkedList<Model> getModels() {
        return models;
    }

    public String getName() {
        return name;
    }

}
