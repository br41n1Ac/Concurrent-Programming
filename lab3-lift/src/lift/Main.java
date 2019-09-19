package lift;

public class Main {
	public static void main(String[] args) {
		// Initialize system
		LiftView lv = new LiftView();
		
		Monitor monitor = new Monitor(lv);
		
		Lift lift = new Lift(monitor, lv);
		int people = 2;
		Person persons[] = new Person[people];
		Person person = new Person(monitor, lv);
//		new Thread(person).start();
		
		for(int i  = 0; i < people; i++) {
		persons[i] = new Person(monitor, lv);
		new Thread(persons[i]).start();
	}
		new Thread(lift).start();

	}
}
