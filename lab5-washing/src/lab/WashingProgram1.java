package lab;

import wash.WashingIO;

/**
 * Program 3 for washing machine.
 * Serves as an example of how washing programs are structured.
 * 
 * This short program stops all regulation of temperature and water
 * levels, stops the barrel from spinning, and drains the machine
 * of water.
 * 
 * It is can be used after an emergency stop (program 0) or a
 * power failure.
 */
class WashingProgram1 extends MessagingThread<WashingMessage> {

    private WashingIO io;
    private MessagingThread<WashingMessage> temp;
    private MessagingThread<WashingMessage> water;
    private MessagingThread<WashingMessage> spin;
    
    public WashingProgram1(WashingIO io,
                           MessagingThread<WashingMessage> temp,
                           MessagingThread<WashingMessage> water,
                           MessagingThread<WashingMessage> spin) {
        this.io = io;
        this.temp = temp;
        this.water = water;
        this.spin = spin;
    }
    
    @Override
    public void run() {
        try {
        	 io.lock(true);
            System.out.println("washing program 1 started");
            water.send(new WashingMessage(this, WashingMessage.WATER_FILL,10));
            temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 40));

            // Switch off spin
            spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));
            WashingMessage ack = receive();  // wait for acknowledgment
            // Switch off heating
            temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
            // Drain barrel (may take some time)
            water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
            spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
            System.out.println("got " + ack);
            WashingMessage ack1 = receive();
            water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
            Thread.sleep(5*10000 / Wash.SPEEDUP);
            System.out.println("got " + ack1);
            spin.send(new WashingMessage(this, WashingMessage.SPIN_FAST));
            // Unlock hatch
            io.lock(false);
            
            System.out.println("washing program 1 finished");
        } catch (InterruptedException e) {
            
            // if we end up here, it means the program was interrupt()'ed
            // set all controllers to idle

            temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
            water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
            spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
            System.out.println("washing program terminated");
        }
    }
}
