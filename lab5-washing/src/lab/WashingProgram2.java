package lab;

import wash.WashingIO;


class WashingProgram2 extends MessagingThread<WashingMessage> {

    private WashingIO io;
    private MessagingThread<WashingMessage> temp;
    private MessagingThread<WashingMessage> water;
    private MessagingThread<WashingMessage> spin;
    
    public WashingProgram2(WashingIO io,
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
            System.out.println("washing program 2 started");
            
            initialFillAndHeat();
            

            // Switch off spin
            spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));
            Thread.sleep(15*60000 / Wash.SPEEDUP);
            spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
            temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
           
            temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 60));
            Thread.sleep(15*60000 / Wash.SPEEDUP);

            // Switch off spin
            spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));
            Thread.sleep(30*60000 / Wash.SPEEDUP);
            spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
//             Switch off heating
            temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
//             Drain barrel (may take some time)
            water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
            Thread.sleep(2*60000 / Wash.SPEEDUP);
    		water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));

        	
        	for(int i = 0 ; i < 5 ; i++) {
        		rinse();
        	}

           
            spin.send(new WashingMessage(this, WashingMessage.SPIN_FAST));
            Thread.sleep(5*60000 / Wash.SPEEDUP);
            spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
            // Unlock hatch
            io.lock(false);
            
            interrupt();
            System.out.println("washing program 2 finished");
            WashingMessage ack = receive();  // wait for acknowledgment
            System.out.println("got " + ack);
            
        } catch (InterruptedException e) {
            
            // if we end up here, it means the program was interrupt()'ed
            // set all controllers to idle

            temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
            water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
            spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
            System.out.println("washing program terminated");
        }
        
       
    }
    private void initialFillAndHeat() throws InterruptedException {
    	io.lock(true);
        water.send(new WashingMessage(this, WashingMessage.WATER_FILL,10));
        Thread.sleep(60000 / Wash.SPEEDUP);
        temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 40));
        Thread.sleep(15*60000 / Wash.SPEEDUP);
    }
    
    private void rinse() throws InterruptedException {
    	water.send(new WashingMessage(this, WashingMessage.WATER_FILL,10));
		Thread.sleep(2*60000 / Wash.SPEEDUP);
		water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
		Thread.sleep(60000 / Wash.SPEEDUP);
		water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
		Thread.sleep(60000 / Wash.SPEEDUP);
    }
}
