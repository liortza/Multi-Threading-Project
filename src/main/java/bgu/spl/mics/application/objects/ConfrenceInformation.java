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
    private LinkedList<String> models;

    public ConfrenceInformation(String name, int date) {
        this.name = name;
        this.date = date;
        models = new LinkedList<>();
        new ConferenceService(name, this);
    }

    public int getDate() {
        return date;
    }

    public void addModel(String name) {
        models.add(name);
    }

    public LinkedList<String> getModels() {
        return models;
    }
}
