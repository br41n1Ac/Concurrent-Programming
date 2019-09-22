package lift;

public class Monitor {
	public int here = 0;
	public int next = 1;
	public int[] waitEntry = new int[7];
	public int[] waitExit = new int[7];
	private int load = 0;
	private LiftView lv;
	boolean moving = true;
	boolean direction = true;

	public Monitor(LiftView lv) {
		this.lv = lv;
	}

	public synchronized void addToQueue(int start, Passenger passenger) {
		waitEntry[start]++;
		lv.showDebugInfo(waitEntry, waitExit);
		notifyAll();
	}

	public synchronized void moveLift() {
		System.out.println(here + " " + next);
		lv.moveLift(here, next);
	}
	
	public synchronized void enterLift(int start, int end, Passenger passenger) {

		while (start != here || load >=4 || moving) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		load++;
		waitEntry[here]--;
		waitExit[end]++;
		passenger.enterLift();
		lv.showDebugInfo(waitEntry, waitExit);
		notifyAll();
	}

	public synchronized void waitingExit() {
		moving = false;
		notifyAll();
		while(waitExit[here]!=0) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		moving = true;
		notifyAll();
	}
	
	public synchronized void waitingEntry() {
		moving = false;
		notifyAll();
		while(waitEntry[here]!= 0 && load < 4) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		moving = true;
		notifyAll();
		
	}
	public synchronized void exitLift(int end, Passenger passenger) {
		while(here != end || moving) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		load--;
		waitExit[here]--;
		passenger.exitLift();
		lv.showDebugInfo(waitEntry, waitExit);
		notifyAll();
	}
	
	
	protected synchronized void calculateNext(){
		if(next > here){
			here = next;
			if(next == 6){
				next--;
				direction = false;
			}else{
				next++;
				direction = true;
			}
		}else{
			here = next;
			if(next == 0){
				next++;
				direction = true;
			}else{
				next--;
				direction = false;
			}
		}
		notifyAll();
	}
	

	
	
}
