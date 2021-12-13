package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class PublishResultsEvent implements Event<String> {
    String name, status;

    public PublishResultsEvent(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }
}
