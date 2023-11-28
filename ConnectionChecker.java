/**
 * @author mjric
 * @version 11-27-23
 * ConnectionChecker.java
 * class that checks queues for disconnected sockets and removes them
 */
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionChecker implements Runnable{
	
	private ArrayBlockingQueue<ClientHandler> arrayQueue;
	private ReentrantLock lock;
	
	/**
	 * connection checker object
	 * @param arrayQueue the queue that the checker will watch over
	 * @param lock the lock for the queue
	 */
	public ConnectionChecker(ArrayBlockingQueue<ClientHandler> arrayQueue, ReentrantLock lock){
		this.arrayQueue = arrayQueue;
		this.lock = lock;
	}//end ConnectionChecker
	
	/**
	 * the method that removes and closes all of the disconnected sockets
	 */
	@Override
	public void run() {
		
		    try {
		        lock.lock();

		        // Use an iterator to safely remove disconnected clients
		        Iterator<ClientHandler> iterator = arrayQueue.iterator();
		        while (iterator.hasNext()) {
		            ClientHandler client = iterator.next();
		            if (!client.getClientSocket().isConnected() || client.getClientSocket().isClosed()) {
		            	client.closeEverything(client.getClientSocket(), client.getWriter(), client.getreader());
		                iterator.remove();
		                client.broadcastMessage("Removed disconnected client: " + client.getUsername(), arrayQueue);
		            }//if
		        }//while
		    }//try
		    finally {
		        lock.unlock();
		    }//finally
		}//end run
	}//end ConnectionChecker

