package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.services.ConferenceService;

import java.util.LinkedList;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private String name;
    private int date;
    private LinkedList<Model> models = new LinkedList<>();

    public ConfrenceInformation(String name, int date) {
        this.name = name;
        this.date = date;
        // new ConferenceService(name, this);
    }

    public String getName() { return name; }

    public int getDate() {
        return date;
    }

    public void addModel(Model m) {
        models.add(m);
    }

    public LinkedList<Model> getModels() {
        return models;
    }
}
