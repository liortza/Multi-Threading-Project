package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;

public class PublishConfrenceBroadcast implements Broadcast {

    private LinkedList<Model> successfulModels;

    public LinkedList<Model> getModels() {
        return successfulModels;
    }

    public PublishConfrenceBroadcast(LinkedList<Model> successfulModels) {
        this.successfulModels = successfulModels;
    }
}
