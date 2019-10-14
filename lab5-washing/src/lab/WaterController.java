package lab;

import wash.WashingIO;

public class WaterController extends MessagingThread<WashingMessage> {

	// TODO: add attributes
	private WashingIO io;

	public WaterController(WashingIO io) {
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
					switch (m.getCommand()) {
					case WashingMessage.WATER_FILL:
						t = new Thread(new Runnable() {
							@Override
							public void run() {
								io.fill(true);
								while(true) {
									if(io.getWaterLevel() >= m.getValue()) {
										io.fill(false);
										
									}
									try {
										Thread.sleep(5000 / Wash.SPEEDUP);
									} catch (InterruptedException e) {
										io.fill(false);
										break;
									}
								}
							}

						});
						t.start();
						break;
					case WashingMessage.WATER_DRAIN:
						if(t != null && t.isAlive()) {
							t.interrupt();
						}
						io.fill(false);
						io.drain(true);

						break;
					case WashingMessage.WATER_IDLE:
						if(t != null && t.isAlive()) {
							t.interrupt();
						}
						io.drain(false);
						break;
					}
				}
				// ... TODO ...
			}
		} catch (

		InterruptedException unexpected) {
			// we don't expect this thread to be interrupted,
			// so throw an error if it happens anyway
			throw new Error(unexpected);
		}
	}
}
