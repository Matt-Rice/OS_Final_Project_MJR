
/**
 * @author mjric
 * @version 11-27-23
 * BlackjackGame.java
 * Contains all of the blackjack logic necessary to simulate a game
 */

//Import statements
import java.io.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.Collections;

public class BlackjackGame {

	//Instance variables
	private final String[] RANKS = {"2", "3","4","5","6","7","8","9","10","J","Q","K","A"};
	private final String[] SUITS = {"CLUBS","DIAMONDS","HEARTS","SPADES"};
	private ArrayList<Card> deck;
	private Hand dealerHand;
	private ArrayBlockingQueue<ClientHandler> gameQueue;
	private ReentrantLock gameLock;
	private ConnectionChecker checker;
	private final int PLAYERS;
	
	/**
	 * The constructor for a blackjack game which contains an array list for the deck of cards, a blocking queue for the players, locks and instantiates a thread to check for connections and remove clients when disconnected
	 * @param players the max number of players in the game 
	 */
	public BlackjackGame(int players) {
		
		this.PLAYERS = players;
		deck = new ArrayList<Card>(52);
		gameLock = new ReentrantLock();
		gameQueue = new ArrayBlockingQueue<ClientHandler>(players);
		checker = new ConnectionChecker(gameQueue,gameLock);
		Thread connectionCheckerThread = new Thread(checker);
		connectionCheckerThread.start();
		dealerHand = new Hand();
	}//end BlackjackGame
	
	/**
	 * Creates the deck and then shuffles it using the Collections.shuffle method
	 */
	public void shuffle() {
		
		for(String suit : SUITS) {
			for(String rank : RANKS) {
				Card card = new Card(suit,rank);
				deck.add(card);
			}//for
		}//for
		
		Collections.shuffle(deck);
		}//end shuffle
	
	/**
	 * returns the deck in the form of a String
	 * @return the deck in the form of a string
	 */
	public String toString() {
		
		return deck.toString();
	}//end toString
	
	/**
	 * returns and removes the first card of the deck
	 * @return the first card of the deck
	 */
	public Card deal() {
		
		return deck.remove(0);
	}//end deal
	
	/**
	 * returns the deck
	 * @return the deck
	 */
	public ArrayList<Card> getDeck() {
		
		return deck;
	}// end getDeck
	
	/**
	 * Method that shuffles the deck, removes all entries in clientQueue then adds them to the game queue. 
	 * It then deals two cards to the dealer and two to each player then prints everyone's hands
	 * @param clientQueue the souce queue for the clients that will play
	 */
	public void StartGame(ArrayBlockingQueue<ClientHandler> clientQueue) {
		
		this.shuffle();
		
		try { 
			gameLock.lock();
			
			while(!clientQueue.isEmpty() || gameQueue.size() == PLAYERS) {
				gameQueue.offer(clientQueue.remove());
				System.out.println(gameQueue.toString());
			}//while
		}//try
		
		finally{
			gameLock.unlock();
		}//finally
		
		this.dealerHand.clearHand();	
		this.dealerHand.addCard(this.deal());
		this.dealerHand.addCard(this.deal());
		this.dealerHand.countCardSum();
		gameQueue.peek().broadcastMessage("Dealer Hand: " + dealerHand.toString(), gameQueue);
	 
		
		try {
			gameLock.lock();

	        // Deal cards to all clients
	        for (ClientHandler client : gameQueue) {
	        	client.endClientGame();
	            client.getHand().addCard(this.deal());
	            client.getHand().addCard(this.deal());
	            client.getHand().countCardSum();
	            client.broadcastMessage(client.getUsername() + "'s hand: " + client.getHand().toString(), gameQueue);
	            
	        }//for
    	}//try
    	
		finally {
  			
    		gameLock.unlock();
			}//finally
    }//end startGame
	
	
	/**
	 * Goes through each client in queue order asking if they want to hit or stay until they bust or stay and 
	 * then deals cards to the dealer until they reach 17
	 * @throws IOException if the user enters an invalid input
	 * @throws InterruptedException if the thread gets interrupted
	 */
	 public void takeTurns() throws IOException, InterruptedException {	
		
		 try {
	    		gameLock.lock();
	    		
	    		for(ClientHandler client: gameQueue) {
	    			if(!client.getClientSocket().isConnected() || client.getClientSocket().isClosed()) {
	    				continue;
	    			}//if
	    		
	    			boolean doneWithTurn = false;
	    			boolean disconnected = false;
	    			
	    			while(!doneWithTurn) {
	    				
	    				client.sendMessage(client.getUsername() + " would you like to hit or stay? \nCurrent Hand Value: " + client.getHand().toString());
	    				
	    				//making sure no other client can change the hit or stay of this client
	    				synchronized (client){
	        				
	    					while (!client.getHit() && !client.getStay()) {
	                            // Wait until the client enters hit or stay
	                           
	    						try {
	                                Thread.sleep(100);
	                                
	                                if(!client.getClientSocket().isConnected() || client.getClientSocket().isClosed()) {
	                                	gameQueue.peek().broadcastMessage(client.getUsername() + "has been disconnected", gameQueue);
	                                	disconnected = true;
	                                	break;
	                                }//if
	                            }//try 
	                            
	    						catch (InterruptedException e) {
	                                System.out.println("This thread has been interrupted");
	                                client.closeEverything(client.getClientSocket(), client.getWriter(), client.getreader());
	                            }//catch
	                        }//while
	    				}//synchronized
	    				
	    				//to prevent a deadlock if client gets disconnected while waiting for a response
	    				if(disconnected)
	    					break;
	    				
	    				//if they hit
	    				if(client.getHit()) {
	    					client.getHand().addCard(this.deal());
	    					client.getHand().countCardSum();
	    					client.makeHitFalse();
	    					client.sendMessage("New hand: " + client.getHand().toString());
	    				}//if
	    				
	    				//if they stay
	    				else if(client.getStay()) {
	    					doneWithTurn = true;
	    				}//elif
	    				
	    				else {
	    					client.sendMessage("Please enter hit or stay");
	    					
	    				}//else	
	    				
	    				//if they bust or have blackjack
	    				if(client.getHand().hasBlackjack() || client.getHand().hasBust()) {
	    					break;
	    				}//if
	    					    				
	    			}//end while
	    			
	    			client.makeStayFalse();
	    			client.makeHitFalse();
	    			
	    		}//end for
	    	}//end try
	    		    	
	    	finally {
	    		gameLock.unlock();
	    	}//end finally
	    	
	    	//dealer has to get to at least 17
			while(dealerHand.getCardSum() < 17) {
				
				dealerHand.addCard(this.deal());
				dealerHand.countCardSum();
				gameQueue.peek().broadcastMessage("New Dealer's Hand: " + dealerHand.toString(), gameQueue);
			}//while
	    }//end takeTurns
	 
	 /**
	  * goes through and individually compares each client's hand with the dealer's hand and prints an appropriate response
	  */
	 public void calculateScore() {
		 
		 try {
 			gameLock.lock();
 			for(ClientHandler client:gameQueue) {
 				
 				if(dealerHand.compareHands(client.getHand()) == 1) {
 					client.sendMessage("You won");
 				}//if
 				
 				else if(dealerHand.compareHands(client.getHand()) == -1) {
 					client.sendMessage("You lose");
 				}//elif
 				
 				else {
 					client.sendMessage("Push");
 				}//else
 					
 			}//for	
 		
 		}//try
 		finally {
 			gameLock.unlock();
 		}//finally
 		
 	}//end calculateScore
	
	/**
	 * clears the game queue and clears the dealer hand then shuffles the deck
	 */
	public void resetGame() {
		
		try {
			gameLock.lock();
			gameQueue.clear();
			dealerHand.clearHand();
			shuffle();
		}//try
		
		finally {
			gameLock.unlock();
		}//finally
	}//end resetGame
	
	/**
	 * prompts the users to continue playing with y or n and is similar to takeTurns except if user types 
	 * y, they get pushed to the clients queue of the Server class, n goes to the next client in the loop, then clears it
	 */
	 public void playAgain() {
		
		try {
			gameLock.lock();
			
			
			for(ClientHandler client: gameQueue) {
				client.sendMessage("Would you like to continue playing?\ny or n?: ");
				
				boolean disconnected = false;
				
				synchronized (client){
    				
					while (!client.getDone() && !client.getAgain()) {
                        // Wait until the client sets done or again
                        try {
                            Thread.sleep(100);
                            if(!client.getClientSocket().isConnected() || client.getClientSocket().isClosed()) {
                            	
                            	gameQueue.peek().broadcastMessage(client.getUsername() + " has been disconnected", gameQueue);
                            	disconnected = true;
                            	break;
                            }
                        }//try 
                        catch (InterruptedException e) {
                        	
                        	System.out.println("This thread has been interrupted");
                             client.closeEverything(client.getClientSocket(), client.getWriter(), client.getreader());
                        }//catch
                    }//while
				}//synchronized
				
				if(disconnected) {
					continue;
				}//if
				
				if(client.getDone()) {
					continue;
					
				}//if
				
				else if(client.getAgain()) {
					MultiThreadedBlackjackServer.clients.offer(client);
				}//elif
				else {
					client.sendMessage("Please enter y or n");
				}//else
				
			}//for
			
			gameQueue.clear();
					
		}//try
		
		finally {
			gameLock.unlock();
		}//finally
	}//playAgain
	
	

	
}//end BlackjackGame
