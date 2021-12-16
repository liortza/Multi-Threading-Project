package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.ReadyBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {

    private int tickCounter = 1;
    private final int speed;
    private final int duration;
    private int notReadyServices;
    private Timer timer;

    public TimeService(int speed, int duration, int notReadyServices) {
        super("TimeService");
        this.speed = speed;
        this.duration = duration;
        this.notReadyServices = notReadyServices;
    }

    @Override
    protected void initialize() {
        Callback<ReadyBroadcast> readyCallback = ((ReadyBroadcast b) -> handleReadyBroadcast());
        subscribeBroadcast(ReadyBroadcast.class, readyCallback);
    }

    private void handleReadyBroadcast() { // wait for all services to register and subscribe
        notReadyServices--;
        if (notReadyServices == 0) sendTicks();
    }

    private void sendTicks() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (tickCounter != duration) sendTickBroadcast();
                else sendTerminateBroadcast();
            }
        }, 0, speed);
    }

    private void sendTickBroadcast() {
        System.out.println("TimeService is sending TickBroadcast #" + tickCounter);
        sendBroadcast(new TickBroadcast());
        tickCounter++;
    }

    private void sendTerminateBroadcast() {
        System.out.println(getName() + " is sending termination broadcast");
        timer.cancel();
        terminate();
    }
}
