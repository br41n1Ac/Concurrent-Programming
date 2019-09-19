package lift;

import java.util.Random;

public class Lift implements Runnable {

	private Monitor monitor;
	private LiftView lv;
	private int floor = 0;

	public Lift(Monitor monitor, LiftView lv) {
		this.monitor = monitor;
		this.lv = lv;

	}

	@Override
	public void run() {
		while (true) {
			monitor.moveLift(monitor.next);
			floor = monitor.next;
			try {
				Thread.sleep(1550);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
