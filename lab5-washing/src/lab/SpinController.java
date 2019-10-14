package lab;


import wash.WashingIO;

public class SpinController extends MessagingThread<WashingMessage> {

	// TODO: add attributes
	private WashingIO io;

	public SpinController(WashingIO io) {
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
					case WashingMessage.SPIN_SLOW:
						if(t != null && t.isAlive())
							t.interrupt();
						t = new Thread(new Runnable() {
							@Override
							public void run() {
								while (true) {
									io.setSpinMode(WashingIO.SPIN_LEFT);

									try {
										Thread.sleep(60000 / Wash.SPEEDUP);
									} catch (InterruptedException e) {
										io.setSpinMode(WashingIO.SPIN_IDLE);
										break;
									}
									io.setSpinMode(WashingIO.SPIN_RIGHT);

									try {
										Thread.sleep(60000 / Wash.SPEEDUP);
									} catch (InterruptedException e) {
										io.setSpinMode(WashingIO.SPIN_IDLE);
										break;
									}
								}
							}

						});
						t.start();
						break;
					case WashingMessage.SPIN_OFF:
						if(t != null && t.isAlive()) {
							t.interrupt();
						}
						io.setSpinMode(WashingIO.SPIN_IDLE);

						break;
					case WashingMessage.SPIN_FAST:
						if(t != null && t.isAlive())
							t.interrupt();
						t = new Thread(new Runnable() {
							@Override
							public void run() {
								io.setSpinMode(WashingIO.SPIN_FAST);
							}
						});
						t.start();
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
