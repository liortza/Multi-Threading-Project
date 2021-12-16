package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	int tickCounter = 1;
	int speed;
	int duration;
	Timer timer;

	public TimeService(int speed, int duration) {
		super("TimeService");
		this.speed = speed;
		this.duration = duration;
	}

	@Override
	protected void initialize() {
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
		sendBroadcast(new TickBroadcast());
		System.out.println("TimeService is sending TickBroadcast #" + tickCounter);
		tickCounter++;
	}

	private void sendTerminateBroadcast() {
		System.out.println(getName() + " is sending termination broadcast");
		timer.cancel();
		terminate();
	}
}
