package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;

public class ReadyBroadcast implements Broadcast {
    private MicroService microService;

    public ReadyBroadcast(MicroService ms) {
        microService = ms;
    }
}
