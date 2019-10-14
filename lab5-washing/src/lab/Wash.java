package lab;
import simulator.WashingSimulator;
import wash.WashingIO;

public class Wash {

    // simulation speed-up factor:
    // 50 means the simulation is 50 times faster than real time
    public static final int SPEEDUP = 50;

    public static void main(String[] args) throws InterruptedException {
        WashingSimulator sim = new WashingSimulator(SPEEDUP);
        
        WashingIO io = sim.startSimulation();

        TemperatureController temp = new TemperatureController(io);
        WaterController water = new WaterController(io);
        SpinController spin = new SpinController(io);

        temp.start();
        water.start();
        spin.start();
        
        Thread t = null;
        while (true) {
        	System.out.println("Awaiting button");
            int n = io.awaitButton();
            System.out.println("User selected program " + n);
            
            switch(n) {
            	case 0:
            		if(t != null && t.isAlive()) {
            			t.interrupt();
            		}
            		break;
           		case 1:
           			t = new Thread(new Runnable() {
						@Override
						public void run() {							
							WashingProgram1 wp1 = new WashingProgram1(io, temp, water, spin);
							wp1.run();
						}
           			});
           			t.start();
           			break;
	            case 2:
	            	t = new Thread(new Runnable() {
						@Override
						public void run() {							
							WashingProgram2 wp2 = new WashingProgram2(io, temp, water, spin);
							wp2.run();
						}
           			});
           			t.start();
	            	break;
	            case 3:
	            	t = new Thread(new Runnable() {
						@Override
						public void run() {							
							WashingProgram3 wp3 = new WashingProgram3(io, temp, water, spin);
							wp3.run();
						}
           			});
           			t.start();
	            	break;
	            default:
	            	break;
            	
            }
        }
    }
};
