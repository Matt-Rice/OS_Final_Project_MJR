/**
 * @author mjric
 * @version 11-27-23
 * Client.java
 * class that allows the client to enter and read things that can communicate to the server and other classes
 */
 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
 
class Client { 
	
	//instance variables
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private String username;
	private int ID = 0;
	
	/**
	 * constructor for the client
	 * @param socket the socket to be associated with the client
	 * @param username the client's username
	 */
	public Client(Socket socket, String username) {
		
		try {
			this.socket = socket;
			this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.username = username;
			this.ID = ID++;
		}//try
		
		catch(IOException e) {
			closeEverything(socket,writer,reader);
		}//catch
	}//end Client
	
	/**
	 * writes the message that the scanner scans from the line
	 */
	public void sendMessage() {
		try {
			writer.write(username);
			writer.newLine();
			writer.flush();
			
			Scanner scan = new Scanner(System.in);
			while(socket.isConnected()){
				
				String message = scan.nextLine();
				writer.write(message);
				writer.newLine();
				writer.flush();
			}//while
		}//try
		
		catch(IOException e) {
			closeEverything(socket,writer,reader);
			
		}//catch
	}//sendMessage
	
	/**
	 * creates a thread that constantly listens for messages
	 */
	public void listenForMessage() {
		new Thread() {
			
			public void run() {
				String msgFromGroupChat;
				
				while (socket.isConnected()) {
					try {
						msgFromGroupChat = reader.readLine();
						System.out.println(msgFromGroupChat);
					}
					catch(IOException e) {
						closeEverything(socket,writer,reader);
					}
				}
			}
			
		}.start();
	}//end listenForMessage
	
	/**
	 * Closes the socket, writer, and reader for the client
	 * @param socket the client's socket
	 * @param writer the client's writer
	 * @param reader the client's reader
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
	
	public static void main(String[] args) throws IOException { 
		Scanner scan = new Scanner(System.in);
		System.out.println("Server: Please enter your username for the group chat: ");
		System.out.println("Server: Send \"^\" in order to exit the chat");
		String username = scan.nextLine();
		Socket socket = new Socket("localhost", MultiThreadedBlackjackServer.port);
		Client client = new Client(socket, username);
		client.listenForMessage();
		client.sendMessage();
		
	}//end main
}//end Client
