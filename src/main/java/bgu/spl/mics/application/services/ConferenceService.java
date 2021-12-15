package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.PublishConfrenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {
    private ConfrenceInformation myConference;
    private int currentTick;
    private ArrayList<PublishResultsEvent> publishEvents = new ArrayList<>();

    public ConferenceService(String name, ConfrenceInformation myConference) {
        super(name);
        this.myConference = myConference;
        currentTick = 0;
    }

    @Override
    protected void initialize() {
        // PublishResultsEvent
        Callback<PublishResultsEvent> publishCallback = (PublishResultsEvent e) -> handlePublishEvent(e);
        subscribeEvent(PublishResultsEvent.class, publishCallback);

        // TickBroadcast
        Callback<TickBroadcast> tickCallback = (TickBroadcast b) -> updateTick();
        subscribeBroadcast(TickBroadcast.class, tickCallback);

        // TerminateBroadcast
        Callback<TerminateBroadcast> terminateCallback = (TerminateBroadcast b) -> terminateConference();
        subscribeBroadcast(TerminateBroadcast.class, terminateCallback);
    }

    public void handlePublishEvent(PublishResultsEvent event) {
        myConference.addModel(event.getModel()); // only "Good" models are sent
        publishEvents.add(event);
    }

    private void updateTick() {
        currentTick++;
        if (currentTick == myConference.getDate()) {
            publishConferenceBroadcast();
            // super.unregister(this);
        }
    }

    private void publishConferenceBroadcast() {
        PublishConfrenceBroadcast broadcast = new PublishConfrenceBroadcast(myConference.getModels());
        for (Model m: myConference.getModels()) m.publish();
        for (PublishResultsEvent e: publishEvents) {
            publishEvents.remove(e);
            complete(e, true);
        }
        sendBroadcast(broadcast);
    }

    private void terminateConference() {
        for (PublishResultsEvent e: publishEvents) complete(e, false); // didn't publish
        terminate();
    }
}
