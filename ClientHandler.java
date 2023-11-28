/**
 * @author mjric
 * @version 11-27-23
 * ClientHandler.java
 * class that handles the interactions between the client and the server class
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;


public class ClientHandler implements Runnable {
       
	public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
        public Hand clientHand = new Hand();
		private Socket clientSocket;
        private BufferedWriter writer;
        private BufferedReader reader;
        private String clientUsername;
        private int clientID;
        private boolean hit;
        private boolean stay;
        private boolean again;
        private boolean done;
        private String sentMessage;
        private static int lastID = 100;
        
        /**
         * Constructor for the clienthandler that broadcasts their username once they connect and contains its variables
         * @param clientSocket its client's associated socket
         */
        public ClientHandler(Socket clientSocket) {
            
        	clientHand = new Hand();
          
            
            try {
            	this.clientSocket = clientSocket;
            	this.hit = false;
            	this.stay = false;
            	this.again = false;
            	this.done = false;
                this.writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                this.clientUsername = reader.readLine();
                this.clientID = lastID++;
                
                broadcastMessage("Server: " + clientUsername + " has connected to the server", MultiThreadedBlackjackServer.clients); 
                
            }//try
            
            catch (IOException e) {
            	closeEverything(clientSocket,writer,reader);
            }//catch
        }//end ClientHandler
        
        /**
         * constantly reads the line for messages and processes them if they are not null
         */
        @Override
        public void run() {
           String clientMessage;
        	while(clientSocket.isConnected()) {
        	   try {
        		  clientMessage = reader.readLine();
        		  if (clientMessage != null) {
        			  processMessage(clientMessage);       			 
        		  }//if
        	   }//try
        	   catch(IOException e) {
        		   closeEverything(clientSocket,writer,reader);
        		   break;
        	   }//catch
           }//while
        }//end run
        
        /**
         * returns the client ID
         * @return the client ID
         */
        public Integer getClientID() {
        	return clientID;
        }//end getClientID
        
        /**
         * returns the username
         * @return the username
         */
        public String getUsername() {
        	return clientUsername;
        }//end getUsername
        
        /**
         * returns the hand of the client
         * @return the hand of the client
         */
        public Hand getHand() {
        	return clientHand;
        }//end getHand
        
        /**
         * gets the writer of the client
         * @return the writer of the client
         */
        public BufferedWriter getWriter() {
        	return writer;
        }//end getWriter
        
        /**
         * gets the reader
         * @return the reader of the client
         */
        public BufferedReader getreader() {
        	return reader;
        }//end getreader
        
        /**
         * gets the client socket
         * @return the client's socket
         */
        public Socket getClientSocket() {
        	return clientSocket;
        }//end getClientSocket
        
        /**
         * returns the hit
         * @return hit
         */
        public boolean getHit() {
        	return hit;
        }//end getHit
        
        /**
         * makes hit false
         */
        public void makeHitFalse() {
        	hit = false;
        }//end makeHitFalse
        
        /**
         * makes stay false
         */
        public void makeStayFalse() {
        	stay = false;
        }//end makeStayFalse
        
        /**
         * makes again false
         */
        public void makeAgainFalse() {
        	again = false;
        }//end makeAgainFalse
        
        /**
         * makes done false
         */
        public void makeDoneFalse() {
        	done = false;
        }//end makeDoneFalse
        
        /**
         * returns stay
         * @return stay
         */
        public boolean getStay() {
        	return stay;
        }//end getStay
        
        /**
         * returns again
         * @return again
         */
        public boolean getAgain() {
        	return again;
        }//end getAgain
        
        /**
         * returns done
         * @return done
         */
        public boolean getDone() {
        	return done;
        }//end getDone
        
        /**
         * sets the blackjack related variables back to their default values
         */
        public void endClientGame() {
        	hit = false;
        	stay = false;
        	done = false;
        	again = false;
        	getHand().clearHand();
        }//end endClientGame
        
        /**
         * gets the sent message
         * @return the sent message
         */
        public String getSentMessage() {
        	return sentMessage;
        }//end getSentMessage
        
        /**
         * processes the message and sets the flags for blackjack to change given that they enter the corresponding message
         * @param message the message the user sends
         */
        public void processMessage(String message) {
        	
        	sentMessage = message.replaceAll(" ", "");
        	
        	if ("hit".equalsIgnoreCase(message)) {
        		hit = true;
        	}//if
        	
        	else if("stay".equalsIgnoreCase(message)) {
        		stay = true;
        	}//elif
        	
        	else if("y".equalsIgnoreCase(message)) {
        		again = true;
        	}//elif
        	
        	else if("n".equalsIgnoreCase(message)) {
        		done = true;
        	}//elif
        	
        	else {
        		sendMessage(message);
        	}//else
        }//end processMessage
       
       /**
        * sends the message to the clienthandler
        * @param message the message to be sent
        */
       public void sendMessage(String message) {
    	   
    	   try {
    		  writer.write(message);
    		  writer.newLine();
    		  writer.flush();
    	   }//try
    	   catch (IOException e) {
			// TODO Auto-generated catch block
			closeEverything(clientSocket,writer,reader);
		}//catch
       }//end sendMessage
        
       /**
        * sends message to every person in the specified queue
        * @param message to be send
        * @param clientHandlers queue that clients will be receive the message
        */
        public void broadcastMessage(String message, ArrayBlockingQueue<ClientHandler> clientHandlers) {
            for (ClientHandler clientHandler : clientHandlers) {
               
            	try {
            		   if(message.contains("^")) {
            			   clientHandler.removeClientHandler(clientHandlers);
            		   }//if
            		   
            		   else {
            			   clientHandler.writer.write(message);
            			   clientHandler.writer.newLine();
            			   clientHandler.writer.flush();
            		   }//else
            	 
               }//try
               catch(IOException e) {
            	   closeEverything(clientSocket,writer,reader);
               }//catch
            	
           }//for
       }//end broadcastMessage
       
        /**
         * removes a clientHandler from the queue
         * @param clienthandler the clientHandler to be removed
         */
       public void removeClientHandler(ArrayBlockingQueue<ClientHandler> clienthandler) {
    	   
    	   broadcastMessage("Server: " + clientUsername + " has left the chat.", clienthandler);
    	   
    	   try {   		
    		   MultiThreadedBlackjackServer.queueLock.lock();
    		   clienthandler.remove(this);
    		   
    	   }//tru
    	   
    	   finally {
    		   MultiThreadedBlackjackServer.queueLock.unlock();
    	   }//finally
    	   
       }//end removeClientHandler
       
       /**
        * closes the socket, reader, and writer for a client
        * @param socket the clienthandler's socket
        * @param writer the writer
        * @param reader the reader
        */
       public void closeEverything(Socket socket, BufferedWriter writer, BufferedReader reader) {
   		try {
   			if(writer != null) {
   				writer.close();
   			}//if
   			if(reader != null) {
   				reader.close();
   			}//if
   			if(socket != null) {
   				socket.close();
   			}//if
   		}//try
   		catch(IOException e) {
   			e.printStackTrace();
   		}//catch
   	}//end closeEverything
        
}//end ClientHandler
        

       
        
    
