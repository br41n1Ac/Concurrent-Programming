package lab;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessagingThread<M> extends Thread {
	
	private final BlockingQueue<M> queue = new LinkedBlockingQueue<M>();

    /** Called by another thread, to send a message to this thread. */
    public void send(M message) {
    	queue.offer(message);
    }
    
    /** Returns the first message in the queue, or blocks if none available. */
    protected M receive() throws InterruptedException {
        return queue.take();
    }
    
    /** Returns the first message in the queue, or blocks up to 'timeout'
        milliseconds if none available. Returns null if no message is obtained
        within 'timeout' milliseconds. */
    protected M receiveWithTimeout(long timeout) throws InterruptedException {
        return queue.poll(timeout, TimeUnit.MILLISECONDS );
    }
}