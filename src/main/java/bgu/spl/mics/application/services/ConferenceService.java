package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

import java.util.ArrayList;

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
    private final ConfrenceInformation myConference;
    private int currentTick;
    private final ArrayList<PublishResultsEvent> publishEvents = new ArrayList<>();

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

        sendBroadcast(new ReadyBroadcast(this));
    }

    public void handlePublishEvent(PublishResultsEvent event) {
        myConference.addModel(event.getModel()); // only "Good" models are sent
        synchronized (publishEvents) {
            publishEvents.add(event);
            publishEvents.notifyAll();
        }
    }

    private void updateTick() {
        currentTick++;
        if (currentTick == myConference.getDate()) {
            publishConferenceBroadcast();
        }
    }

    private void publishConferenceBroadcast() {
        PublishConfrenceBroadcast broadcast = new PublishConfrenceBroadcast(myConference.getModels());
        for (Model m : myConference.getModels()) {
            m.publish();
        }
        for (PublishResultsEvent e : publishEvents) {
            complete(e, true);
        }
        System.out.println("");
        sendBroadcast(broadcast);
        terminate();
    }

    private void terminateConference() {
        for (PublishResultsEvent e : publishEvents) complete(e, false); // didn't publish
        terminate();
    }
}
