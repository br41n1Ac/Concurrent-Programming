package lab;

import wash.WashingIO;

public class TemperatureController extends MessagingThread<WashingMessage> {

    // TODO: add attributes
	private WashingIO io;
	private int dt = 10000;
    public TemperatureController(WashingIO io) {
    	this.io = io;
    }

    @Override
    public void run() {
    	Thread t = null;
		try {

			while (true) {
				// wait for up to a (simulated) minute for a WashingMessage
				WashingMessage m = receiveWithTimeout(60000 / Wash.SPEEDUP);

				// if m is null, it means a minute passed and no message was received
				if (m != null) {
					System.out.println("got " + m);
					System.out.println(m.getCommand());
					switch (m.getCommand()) {
					case WashingMessage.TEMP_SET:
						t = new Thread(new Runnable() {
							@Override
							public void run() {
								io.heat(true);
								while(true) {
									if(io.getTemperature() >= m.getValue()) {
										io.heat(false);
										break;
									}
									try {
										Thread.sleep(dt);
									} catch (InterruptedException e) {
										io.heat(false);
									}
								}
							}

						});
						t.start();
						break;

					case WashingMessage.TEMP_IDLE:
						if(t != null && t.isAlive()) {
							t.interrupt();
							io.heat(false);
						}
						break;
					}
				}
			}
		} catch (

		InterruptedException unexpected) {
			// we don't expect this thread to be interrupted,
			// so throw an error if it happens anyway
			throw new Error(unexpected);
		}
	}
}
