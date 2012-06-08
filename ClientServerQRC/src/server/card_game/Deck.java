package server.card_game;

import java.util.Random;
import common.card_game.Card;

/**
 *  The Deck Class
 *  
 *  This class is used by the server to represent a deck in 
 *	card games. It contains 52 card objects and the server can
 *	shuffle and retrieve cards
 *
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  Members: Jeremy Glesner, Dustin Overmiller, Yiqi Ju, Lei Yuan
 *  Project: Advanced Game Message Protocol Implementation
 *  
 */
public class Deck {
	/* private member variables */
	private static Random gen = new Random();
	private Card[] deck = null;
	private int curIndex;
	
	/**
	* Constructor - create the 52 card deck
	*
	*/
	public Deck() {
		deck = new Card[52];
		int kK = 0;
		for (int iI = 0; iI < Card.NUM_SUITS; iI++)
		{
			for (int jJ = 1; jJ <= Card.NUM_RANKS; jJ++)
			{
				int suite = (int)Math.pow(2.0, (double)iI);
				deck[kK] = new Card(suite, jJ);
            kK++;
			}
		}
		curIndex = 0;
	}
	
	/**
      * shuffle - method to randomize the cards in an effort
	  * to "shuffle" the deck
      * @param version
      * @return none
      */
	public void shuffle()
	{
		int randIndex;
		Card cardTemp;
		for (int iI = 0; iI < deck.length; iI++)
		{
			randIndex = gen.nextInt(deck.length-iI) + iI;
			cardTemp = deck[randIndex];
			deck[randIndex] = deck[iI];
			deck[iI] = cardTemp;			
		}
		curIndex = 0;
	}
	/**
      * getCards - get a number of cards from the deck
      * @param nNumCards
      * @return Card[]
      */
	public Card[] getCards(int nNumCards)
	{
		Card[] cards = null;
		if (nNumCards > 0)
		{
			cards = new Card[nNumCards];
			for (int iI = 0; iI < nNumCards; iI++)
			{
				if (curIndex < deck.length)
				{
					cards[iI] = deck[curIndex];
					curIndex++;
				}
				else
				{
					return null;
				}
			}
		}
		return cards;
	}

}
