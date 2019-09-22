import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Semaphore;

public class Time {
	private int time;
	private int alarm;
	private Semaphore mutex = new Semaphore(1);

	public Time() {
		time = currentTime();
	}

	private int currentTime() {
		LocalTime curr = LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
		String time = curr.format(DateTimeFormatter.ofPattern("HHmmss"));
		int t = Integer.parseInt(time);
		return t;
	}
	
	public void setAlarm(int hhmmss) {
		try {
			mutex.acquire();
			alarm = hhmmss;
			mutex.release();
		} catch (InterruptedException e) {
		}
	
		
	}

	public void setTime(int hhmmss) {
		time = hhmmss;
	}
	public boolean triggerAlarm() {
		boolean trig = false;
		try {
			mutex.acquire();
			if(alarm == time) {
				trig = true;
			}else {
				trig = false;
			}
			mutex.release();
		} catch (InterruptedException e) {
		}
		return trig;
	}

	public int getTime() {
		incrementTime();
		return time;
	}

	public void incrementTime() {
		time++;
		String t = String.valueOf(time);
		if (t.length() < 6) {
			for(int i = t.length(); i < 6 ; i ++) {
				t = "" + 0 + t;
			}
		}
		String seconds = t.substring(4);
		String minutes = t.substring(2, 4);
		String hours = t.substring(0, 2);
		int ss = time % 100;
		int mm = Integer.parseInt(minutes);
		int hh = Integer.parseInt(hours);
		if (ss >= 60) {
			mm = Integer.parseInt(minutes) + 1;
			seconds = "00";
		}
		if (mm >= 60) {
			hh = Integer.parseInt(hours) + 1;
			minutes = "00";
		} else {
			minutes = String.valueOf(mm);
		}
		if (hh >= 24) {
			hours = "00";
		} else {
			hours = String.valueOf(hh);
		}
		if (hours == "00" && minutes == "00" && seconds == "00") {
			time = 0;
		} else {
			time = Integer.parseInt("" + verifyLength(hours) + verifyLength(minutes) + verifyLength(seconds));
		}
	}

	private String verifyLength(String s) {
		if (s.length() == 1) {
			return "" + 0 + s;
		} else {
			return s;
		}
	}
}
