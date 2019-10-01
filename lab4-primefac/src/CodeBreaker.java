
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import rsa.Factorizer;
import rsa.ProgressTracker;
import client.view.ProgressItem;
import client.view.StatusWindow;
import client.view.WorklistItem;
import network.Sniffer;
import network.SnifferCallback;

public class CodeBreaker implements SnifferCallback {

	private final JPanel workList;
	private final JPanel progressList;
	private final JProgressBar mainProgressBar;
	private final ExecutorService pool;
	private ConcurrentHashMap<String, Integer> active = new ConcurrentHashMap<String, Integer>();
	private ConcurrentHashMap<ProgressItem, String> threads = new ConcurrentHashMap<ProgressItem, String>();

	// -----------------------------------------------------------------------

	private CodeBreaker() {
		StatusWindow w = new StatusWindow();

		workList = w.getWorkList();
		progressList = w.getProgressList();
		mainProgressBar = w.getProgressBar();
		pool = Executors.newFixedThreadPool(2);
		w.enableErrorChecks();
		new Sniffer(this).start();
	}

	// -----------------------------------------------------------------------

	public static void main(String[] args) throws Exception {

		/*
		 * Most Swing operations (such as creating view elements) must be performed in
		 * the Swing EDT (Event Dispatch Thread).
		 * 
		 * That's what SwingUtilities.invokeLater is for.
		 */

		SwingUtilities.invokeLater(() -> new CodeBreaker());
	}

	// -----------------------------------------------------------------------

	/** Called by a Sniffer thread when an encrypted message is obtained. */
	@Override
	public void onMessageIntercepted(String message, BigInteger n) {
		System.out.println("message intercepted (N=" + n + ")...");
		SwingUtilities.invokeLater(() -> {
			WorklistItem temp = new WorklistItem(n, message);
			JButton b1 = new JButton("Break");
			b1.addActionListener(e -> {
				ProgressItem temp1 = new ProgressItem(n, message);
				JButton b2 = new JButton("Remove");
				b2.addActionListener(e1 -> {
					progressList.remove(temp1);
					active.remove(threads.get(temp1));
					mainProgressBar.setValue(mainProgressBar.getValue() - 1000000);
					mainProgressBar.setMaximum(mainProgressBar.getMaximum() - 1000000);
				});
				moveMessage(temp, temp1, n, b1, b2, message);
			});
			createMessage(temp, b1);
		});

	}

	private synchronized void updateProgress(Tracker tracker, String name) {
		active.put(name, tracker.totalProgress);
		int value = active.values().stream().reduce(0, Integer::sum);
		mainProgressBar.setValue(value);
		mainProgressBar.repaint();
	}

	private void moveMessage(WorklistItem wlt, ProgressItem temp, BigInteger n, JButton b1, JButton b2,
			String message) {
		progressList.add(temp);
		mainProgressBar.setMaximum(mainProgressBar.getMaximum() + 1000000);
		temp.getProgressBar().setMaximum(100);
		pool.execute(new Runnable() {
			@Override
			public void run() {
				Tracker tracker = new Tracker();
				ExecutorService exec = Executors.newSingleThreadExecutor();
				exec.execute(new Runnable() {

					@Override
					public void run() {
						String threadName = Thread.currentThread().getName();
						threads.put(temp, threadName);
						while (temp.getProgressBar().getValue() != 100) {
							try {
								SwingUtilities.invokeAndWait(() -> {
									temp.getProgressBar().setValue(tracker.prevPercent);
									temp.repaint();
									updateProgress(tracker, threadName);
								});
							} catch (InvocationTargetException | InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				});
				exec.shutdown();
				String plaintext = Factorizer.crack(message, n, tracker);
				SwingUtilities.invokeLater(() -> {
					temp.add(b2);
					temp.getTextArea().setText(plaintext);
				});
				System.out.println("Decryption complete. The message is \"" + plaintext + "\"");
			}
		});
		workList.remove(wlt);
	}

	private void createMessage(WorklistItem wlt, JButton b1) {
		wlt.add((b1));
		workList.add(wlt);
	}

	private static class Tracker implements ProgressTracker {
		private int totalProgress = 0;
		private int prevPercent = -1;

		public void onProgress(int ppmDelta) {
			totalProgress += ppmDelta;
			int percent = totalProgress / 10000;
			if (percent != prevPercent) {
				System.out.println(percent + "%");
				prevPercent = percent;

			}
		}
	}
}
