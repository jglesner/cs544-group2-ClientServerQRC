package server.card_game;

import java.util.Random;

import common.card_game.Card;

/*
 * This code was obtained with help from http://www.cs.uga.edu/~gtb/1302/Project1/Deck.java
 */

public class Deck {
	private static Random gen = new Random();
	private Card[] deck = null;
	private int curIndex;
	
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
