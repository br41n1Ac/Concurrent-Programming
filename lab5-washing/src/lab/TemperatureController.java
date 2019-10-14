package lab;

import wash.WashingIO;

public class TemperatureController extends MessagingThread<WashingMessage> {

	// TODO: add attributes
	private WashingIO io;
	private int dt = 10000 / Wash.SPEEDUP;

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
				double mu = 2000*10/(io.getWaterLevel()*4184);
				// if m is null, it means a minute passed and no message was received
				if (m != null) {
					System.out.println("got " + m);
					switch (m.getCommand()) {
					case WashingMessage.TEMP_SET:
						t = new Thread(new Runnable() {
							@Override
							public void run() {
								if (io.getWaterLevel() != 0)
									io.heat(true);
								double valueTemperature = m.getValue();

								while (true) {
									if (io.getTemperature() >= valueTemperature - 2 + 0.2 + 10 * 0.000238*(valueTemperature-20)) {
										io.heat(false);
									} else if (io.getTemperature() <= valueTemperature-mu-0.2 && io.getWaterLevel() != 0) {
										io.heat(true);
									}
										
									try {
										Thread.sleep(dt);
									} catch (InterruptedException e) {

										io.heat(false);
										break;
									}
								}
							}

						});
						t.start();
						break;

					case WashingMessage.TEMP_IDLE:
						if (t != null) {
							t.interrupt();
						}
						io.heat(false);
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
