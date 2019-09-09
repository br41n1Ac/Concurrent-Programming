import java.util.concurrent.Semaphore;

import clock.ClockInput;
import clock.ClockInput.UserInput;
import clock.ClockOutput;
import emulator.AlarmClockEmulator;

public class ClockMain {
	private static boolean alarm;
	private static boolean stopAlarm;

	public static void inputHandler(ClockInput in, ClockOutput out, Semaphore sem, Time time)
			throws InterruptedException {
		sem.acquire(); // wait for user input
		UserInput userInput = in.getUserInput();
		int choice = userInput.getChoice();
		int value = userInput.getValue();
		if (choice == 1)
			time.setTime(value);
		if (choice == 2) {
			time.setAlarm(value);
			alarm = true;
			out.setAlarmIndicator(alarm);
		}
		if (choice == 3 || choice == 4 && alarm) {
			stopAlarm = true;
		}

		System.out.println("choice = " + choice + "  value=" + value);
	}

	public static void main(String[] args) throws InterruptedException {
		AlarmClockEmulator emulator = new AlarmClockEmulator();
		ClockInput in = emulator.getInput();
		ClockOutput out = emulator.getOutput();
		Semaphore sem = in.getSemaphore();
		Time time = new Time();

		Thread t1 = new Thread(new Runnable() {
			public void run() {
				long t0 = System.currentTimeMillis();
				while (true) {
					long now = System.currentTimeMillis();
					out.displayTime(time.getTime());
					System.out.println(now - t0);
					if (time.triggerAlarm()) {
						new Thread(new Runnable() {
							public void run() {
								int n = 0;
								while (true) {
									out.alarm();
									if (stopAlarm) {
										alarm = false;
										stopAlarm = false;
										out.setAlarmIndicator(alarm);
										Thread.currentThread().interrupt();
										return;
									}
									n++;
									if (n == 20) {
										out.setAlarmIndicator(false);
										Thread.currentThread().interrupt();
										return;
									}
									try {
										Thread.sleep(510);
									} catch (InterruptedException e) {
										return;
									}
								}
							}
						}).start();
					}
					try {
						Thread.sleep(998);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		Thread t2 = new Thread(() -> {
			while (true) {
				try {
					inputHandler(in, out, sem, time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		out.displayTime(time.getTime());
		t1.start();
		t2.start();

	}
}
