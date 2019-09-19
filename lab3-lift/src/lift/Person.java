package lift;

import java.util.Random;

public class Person implements Runnable{
	private int startFloor;
	private int destFloor;
	private LiftView lv;
	private Passenger passenger;
	
	private Monitor monitor;
	
	public Person(Monitor monitor, LiftView lv) {
		this.monitor = monitor;
		this.lv = lv;
		passenger = lv.createPassenger();
		startFloor = passenger.getStartFloor();
		destFloor = passenger.getDestinationFloor();
	}

	@Override
	public synchronized void run() {
			try {
				Thread.sleep(new Random().nextInt(10000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			passenger.begin();
			try {
				monitor.transport(startFloor, destFloor, passenger);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			passenger.end();
		
		
	}

}
