package lift;

public class Monitor {
	public int here = 0;
	public int next = 0;
	private int[] waitEntry = new int[7];
	private int[] waitExit = new int[7];
	private int load = 0;
	private LiftView lv;

	public Monitor(LiftView lv) {
		this.lv = lv;
	}

	public boolean canEnter(int currentFloor) {
		return currentFloor == here && load < 4 && here == next;
	}

	public synchronized void moveLift(int dest) {
		next = dest;
		System.out.println(here + " " + next);
		if(here != next) {
			lv.moveLift(here, next);
		}
		here = next;
	}

	public synchronized void enterLift(int entryFloor, int exitFloor) {
		load++;
		waitEntry[entryFloor]--;
		waitExit[exitFloor]++;
	}

	public void exitLift() {
		load--;
		waitExit[here]--;
	}

	public synchronized void setDestination(int going) {
		next = going;
		notifyAll();
	}

	public void currentFloor(int current) {
		here = current;

	}
	public synchronized void transport(int start, int destination, Passenger passenger) throws InterruptedException {
		waitEntry[start]++;
		if(start != here) {
			setDestination(start);
			moveLift(start);
		}
		while(!canEnter(start)) {
			wait();
		}
		passenger.enterLift();
		notifyAll();
		moveLift(destination);
		waitExit[destination]++;
		waitEntry[start]--;
		while(destination != here || here != next) {
			wait();
		}
		passenger.exitLift();
		waitExit[destination]--;
		notifyAll();
	}

}
