package factory.controller;

import java.util.concurrent.atomic.AtomicLong;

import factory.model.DigitalSignal;
import factory.model.SignalValue;
import factory.model.WidgetKind;
import factory.swingview.Factory;

public class ToolController {
	private final DigitalSignal conveyor, press, paint;
	private final long pressingMillis, paintingMillis;
	private AtomicLong state = new AtomicLong();

	public ToolController(DigitalSignal conveyor, DigitalSignal press, DigitalSignal paint, long pressingMillis,
			long paintingMillis) {
		this.conveyor = conveyor;
		this.press = press;
		this.paint = paint;
		this.pressingMillis = pressingMillis;
		this.paintingMillis = paintingMillis;
	}

	public synchronized void onPressSensorHigh(WidgetKind widgetKind) throws InterruptedException {
		//
		// TODO: you will need to modify this method
		//
		if (widgetKind == WidgetKind.BLUE_RECTANGULAR_WIDGET) {
			conveyor.off();
			press.on();
			state.addAndGet(1);
//			Thread.sleep(pressingMillis);
			waitOutside(pressingMillis);
			press.off();
//			wait(pressingMillis);
			waitOutside(pressingMillis);
			actionDone();
//			conveyor.on();
		}
	}

	public synchronized void onPaintSensorHigh(WidgetKind widgetKind) throws InterruptedException {
		//
		// TODO: you will need to modify this method
		//
		if (widgetKind == WidgetKind.ORANGE_ROUND_WIDGET) {
			conveyor.off();
			paint.on();
			state.addAndGet(1);
//			Thread.sleep(paintingMillis);
			waitOutside(paintingMillis);
			paint.off();
			actionDone();
//			conveyor.on();

			// TODO
		}
	}

	private void actionDone() throws InterruptedException {
		state.decrementAndGet();
		notifyAll();
		activateConveyor();
	}

	private synchronized void activateConveyor() throws InterruptedException {
		while (state.get() != 0) {
			wait();
		}
		conveyor.on();
	}

	private void waitOutside(long millis) throws InterruptedException {
		long timeToWakeUp = System.currentTimeMillis() + millis;
		// ...
		while (timeToWakeUp - System.currentTimeMillis() > 0) {
			long dt = timeToWakeUp - System.currentTimeMillis();
			wait(dt);
		}
	}
	

	// -----------------------------------------------------------------------

	public static void main(String[] args) {
		Factory factory = new Factory();
		factory.startSimulation();
	}
}
