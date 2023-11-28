/**
 * @author mjric
 * @version 11-27-23
 * MultiThreadedBlackjackServer.java
 * class that starts the server and runs the blackjack logic
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public class MultiThreadedBlackjackServer {
	
	//instance variables
	static BlackjackGame game = new BlackjackGame(6);
	
    static final int port = 8080;
    private ServerSocket serverSocket;
    public static ArrayBlockingQueue<ClientHandler> clients = new ArrayBlockingQueue<>(200);
    public static ArrayBlockingQueue<ClientHandler> gameQueue = new ArrayBlockingQueue<ClientHandler>(200);
    public static ReentrantLock queueLock = new ReentrantLock();
 
    
    /**
     * constructor for the server
     * @param serverSocket the serverSocket
     */
    public MultiThreadedBlackjackServer(ServerSocket serverSocket){
    	this.serverSocket = serverSocket;
    }//end MultiThreadedBlackjackServer
    
    /**
     * starts the server
     * @throws InterruptedException if the thread is interrrupted while waiting 
     */
    public void startServer() throws InterruptedException {
    	try {
    		
    	ConnectionChecker checker = new ConnectionChecker(clients, queueLock);
    	Thread connectionCheckerThread =  new Thread(checker);
    	connectionCheckerThread.start();
    	while(true) {
    		
    		System.out.println("Server listening on port "+ port);
    		Socket socket = serverSocket.accept();
    		System.out.println("A new client has connected");
    		ClientHandler clientHandler = new ClientHandler(socket);
    		Thread thread = new Thread(clientHandler);
    		System.out.println("User ID: " + clientHandler.getClientID());
    		thread.start();
    		
    		
    		
    		try {
    			queueLock.lock();
    			clients.offer(clientHandler);
    			clients.peek().broadcastMessage("Waiting for New Game to begin...", clients);
    		}//try
    		finally {
    			queueLock.unlock();
    		}//finally
    		
    		
	    	if (clients.size() > 3) {
	    		game.StartGame(clients);
	    		game.takeTurns();
	    		game.calculateScore();
	    		game.playAgain();
	    			    			
	    	}//if
    	}//while
  
    	 
    		}//try
    	catch(IOException e) {
    		e.printStackTrace();
    	}//catch
    }//end startServer
    
    /**
     * closes the server socket
     */
    public void closeServerSocket() {
    	try {
    		if(serverSocket != null) {
    			serverSocket.close();
    		}//if
    	}//try
    	catch(IOException e) {
    		e.printStackTrace();
    	}//catch
    }//closeServerSocket
    
    public static void main(String[]args) throws InterruptedException {
    	try {
    		ServerSocket serverSocket = new ServerSocket(port);
    		MultiThreadedBlackjackServer server = new MultiThreadedBlackjackServer(serverSocket); 
    		server.startServer();
    		
    		
    	}//try
    	catch(IOException e) {
    		
    		e.printStackTrace();
    	}//catch
    }//end main

}//end MultiThreadedBlackjackServer
