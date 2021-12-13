package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.LinkedList;

public class PublishConfrenceBroadcast implements Broadcast {
    private LinkedList<String> successfulModels;

    public LinkedList<String > getModelsNames() {
        return successfulModels;
    }

    public PublishConfrenceBroadcast(LinkedList<String> successfulModels) {
        this.successfulModels = successfulModels;
    }
}
