/**
 * @author mjric
 * @version 11-19-23
 * Hand.java
 * Creates a constructor and methods to represent a player's hand
 */

//import statements
import java.util.ArrayList;

public class Hand {
	//instance variables
	private ArrayList<Card> hand;
	private int handSum;
	private boolean hasBlackjack;
	private boolean bust;
	private int aceCount;
	
	/**
	 * Constructor for a player's hand
	 */
	public Hand() {
		hand = new ArrayList<Card>();
		handSum = 0;
		hasBlackjack = false;
		bust = false;
	}//end Hand
	
	/**
	 * Adds a card to the hand
	 * @param card the card to be added
	 */
	public void addCard(Card card) {
		hand.add(card);	
	}//end addCard
	
	/**
	 * Returns the hand in the form of a string along with the sum of the values of the hand
	 * @return the values of the cards in the hand along with the sum of the hands of the cards
	 */
	public String toString() {
		String handString = "";
		
		for(Card card:hand) {
			handString = handString + card.toString() + ", ";
		}//end for
		
		handString = handString + "\t Total: " + handSum;
		
		if(bust)
			handString = handString + " You Busted";
		if(hasBlackjack)
			handString = handString + " Blackjack!!!";
		
		return handString;
	}//end toString
	
	/**
	 * Clears the hand along with all of the instance variables associated with the hand
	 */
	public void clearHand() {
		hand.clear();
		aceCount= 0;
		bust = false;
		hasBlackjack = false;
	}//end clearHand
	
	/**
	 * sums the value of the cards in the hand along with handling changing aces to 21, checking if player busted, and checking for blackjack
	 * @return the sum of the cards in the hand
	 */
	public int countCardSum() {
		handSum = 0;
		for(Card card: hand) {
			handSum += card.cardValue();
			if (card.getRank() == "A") {
				aceCount++;
			}//end if
		}//end for
		
		//converts an ace to a 1 if over 21
		while (aceCount>0 && handSum>21) {
			handSum -= 10;
			aceCount--;
		}//end while
			
		
		//checks for bust
		if (handSum>21) {
			bust = true;
			System.out.println("You Busted");
		}//end if
		
		//checks for blackjack
		if(hand.size()==2 && handSum == 21) {
			hasBlackjack = true;
			System.out.println("Blackjack!!!");
		}//end if
		return handSum;
	}//end countCardSum
	
	/**
	 * returns the card sum
	 * @return the sum of the cards in the hand
	 */
	public int getCardSum() {
		return handSum;
	}//end getCardSum
	
	/**
	 * returns if the hand has blackjack
	 * @return true if blackjack
	 */
	public boolean hasBlackjack() {
		return hasBlackjack;
	}//end hasBlackjack
	
	/**
	 * Returns whether or not the player has busted
	 * @return bust
	 */
	public boolean hasBust() {
		return bust;
	}//end hasBust
	
	/**
	 * Compares two hands to see which handSum is larger or if either side busted
	 * @param playerHand the player's hand
	 * @return 1 if the player handSum is greater or dealer busted and they didn't, -1 if less or player busted, and 0 if equal or both busted
	 */
	public int compareHands(Hand playerHand) {
		if(!this.bust && !playerHand.bust) {
			if(playerHand.handSum > this.handSum) 
				return 1;
			
			if(playerHand.handSum < this.handSum) 
				return -1;
			
			else
				return 0;
		}//if
		
		else if(this.bust && !playerHand.bust) 
			return 1;
		
		else if(playerHand.bust && !this.bust) 
			return -1;
		
		else if(playerHand.hasBlackjack && !this.hasBlackjack)
			return 1;
		
		else if(!playerHand.hasBlackjack && this.hasBlackjack)
			return -1;
		
		else if(playerHand.hasBlackjack && this.hasBlackjack)
			return 0;
		
		else 
			return 0;	
	}//end compareHands
}//end Hand.java
