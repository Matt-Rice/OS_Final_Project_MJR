/**
 * @author mjric
 * @version 11-27-23
 * Card.java
 * class that creates the card object
 */
public class Card {
	
	private String suit;
	private String rank;
	private int value;
	
	/**
	 * Constructor for the card
	 * @param suit the suit of the card
	 * @param rank the rank of the card
	 */
	public Card(String suit,String rank) {
		this.suit = suit;
		this.rank = rank;
		value = 0;
	}//end Card
	
	/**
	 * returns the suit
	 * @return the suit
	 */
	public String getSuit() {
		return suit;
	}//end getSuit
	
	/**
	 * gets the rank
	 * @return the rank of the card
	 */
	public String getRank() {
		return rank;
	}//end getRank
	
	/**
	 * changes the value of the card number
	 * @param num the value the card will be changed to 
	 */
	public void changeValue(int num) {
		value = num;
	}//end changeValue
	
	/**
	 * return the card with rank and suit
	 * @return the rank and suit of card in the form of a String
	 */
	public String toString() {
		return rank + " of " + suit;
	}//end to String
	
	/**
	 * returns the value of the given card
	 * @return the value of the card in card games
	 */
	public int cardValue() {
		switch(rank) {
		case "2": value = 2;
			break;
		case "3": value = 3;
			break;
		case "4": value = 4;
			break;
		case "5": value = 5;
			break;
		case "6": value = 6;
			break;
		case "7": value = 7;
			break;
		case "8": value = 8;
			break;
		case "9": value = 9;
			break;
		case "10": value = 10;
			break;
		case "J": value = 10;
			break;
		case "Q": value = 10;
			break;
		case "K": value = 10;
			break;
		case "A": value = 11;
		}//switch
		return value;
	}//end cardValue
}//end Card
