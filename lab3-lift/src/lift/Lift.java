package lift;

import java.util.Random;

public class Lift implements Runnable {

	private Monitor monitor;
	private LiftView lv;
	private int floor = 0;
	boolean first = true;

	public Lift(Monitor monitor, LiftView lv) {
		this.monitor = monitor;
		this.lv = lv;

	}

	@Override
	public void run() {
		while (true) {
			monitor.waitPassengers();
			monitor.waitingExit();
			monitor.waitingEntry();
			lv.moveLift(monitor.here, monitor.next);
			monitor.updateNextFloor();
		}

	}

}
