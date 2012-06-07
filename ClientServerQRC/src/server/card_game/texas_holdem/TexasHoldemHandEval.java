/**
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  
 *  This page contains game logic for playing Texas Holdem.
 *  
 *  This ranking function was downloaded and modified from
 *  http://www.jroller.com/JamesGoodwill/entry/texas_hold_em_hand_evaluator
 *  
 */

package server.card_game.texas_holdem;

import common.card_game.Card;

/*
 *  This ranking function was downloaded and modified from
 *  http://www.jroller.com/JamesGoodwill/entry/texas_hold_em_hand_evaluator
 */

public class TexasHoldemHandEval 
{
	
	// Constructor
	public TexasHoldemHandEval()
	{
		
	}
	
	/**
	 * * Compares two hands against each other.
	 * @param h1
	 *   The first hand
	    * @param h2
	    *           The second hand
	    * @return 1 = first hand is best, -1 = second hand is best, 0 = tie
	    */
	   public int compareHands(Card[] playerHand, Card[] dealerHand) {
	      int r1 = rankHand(playerHand);
	      int r2 = rankHand(dealerHand);

	      if (r1 > r2)
	    	  return 1;
	      if (r1 < r2)
	         return -1;
	      return 0;
	   }

	   /** ******************************************************************* */
	   // DENIS PAPP'S HAND RANK IDENTIFIER CODE:
	   /** ******************************************************************* */

	   private static final int POKER_HAND = 5;

	   public static final int HIGH = 0;
	   public static final int PAIR = 1;
	   public static final int TWOPAIR = 2;
	   public static final int THREEKIND = 3;
	   public static final int STRAIGHT = 4;
	   public static final int FLUSH = 5;
	   public static final int FULLHOUSE = 6;
	   public static final int FOURKIND = 7;
	   public static final int STRAIGHTFLUSH = 8;
	   public static final int FIVEKIND = 9;
	   public static final int NUM_HANDS = 10;

	   private static final int ID_GROUP_SIZE = (Card.NUM_RANKS * Card.NUM_RANKS
	         * Card.NUM_RANKS * Card.NUM_RANKS * Card.NUM_RANKS);

	   private final static byte ID_ExistsStraightFlush(Card[] h, byte major_suit)
	   {
		   boolean[] present = new boolean[Card.NUM_RANKS];
		   // for (i=0;i<Card.NUM_RANKS;i++) present[i]=false;

		   for (int i = 0; i < h.length; i++) 
		   {
			   if (h[i].getSuitValue() == major_suit) 
			   {
				   present[h[i].getRankValue()] = true;
	        
			   }	     
		   }
		   Card ace = new Card(Card.HEART, Card.CARD_ACE);
		   int straight = present[ace.getRankValue()] ? 1 : 0;
		   byte high = 0;
		   for (int i = 0; i < Card.NUM_RANKS; i++)
		   {
			   if (present[i]) 
			   {
				   if ((++straight) >= POKER_HAND) 
				   {
					   high = (byte) i;	           
				   }	        
			   } 
			   else 
			   {	           
				   straight = 0;	         
			   }	      
		   }	     
		   return high;	  
	   }

	   // suit: Card.NUM_SUITS means any
	   // not_allowed: Card.NUM_RANKS means any
	   // returns ident value
	   private final static int ID_KickerValue(byte[] paired, int kickers, byte[] not_allowed) 
	   {
		   Card ace = new Card(Card.HEART, Card.CARD_ACE);
		   int i = ace.getRankValue();
		   int value = 0;
		   while (kickers != 0) 
		   {
			   while (paired[i] == 0 || i == not_allowed[0] || i == not_allowed[1])
			   {
				   i--;
			   }
			   kickers--;
			   value += pow(Card.NUM_RANKS, kickers) * i;
			   i--;	     
		   }	     
		   return value;	  
	   }

	   private final static int ID_KickerValueSuited(Card[] h, int kickers, byte suit) 
	   {
		   int i;
		   int value = 0;
		   Card ace = new Card(Card.HEART, Card.CARD_ACE);

		   boolean[] present = new boolean[Card.NUM_RANKS];
		   // for (i=0;i<Card.NUM_RANKS;i++) present[i] = false;

		   for (i = 0; i < h.length; i++)
		   {
			   if (h[i].getSuitValue() == suit)
			   {
				   present[h[i].getRankValue()] = true;
			   }
		   }

		   i = ace.getRankValue();
		   while (kickers != 0) 
		   {
			   while (present[i] == false)
			   {
				   i--;
			   }
			   kickers--;
			   value += pow(Card.NUM_RANKS, kickers) * i;
			   i--;			   
		   }
		   return value;	  
	   }

	   /**
	    * Get a numerical ranking of this hand. Uses java based code, so may be
	    * slower than using the native methods, but is more compatible this way.
	    * 
	    * Based on Denis Papp's Loki Hand ID code (id.cpp) Given a 1-9 card hand,
	    * will return a unique rank such that any two hands will be ranked with the
	    * better hand having a higher rank.
	    * 
	    * @param h a 1-9 card hand
	    * @return a unique number representing the hand strength of the best 5-card
	    *         poker hand in the given 7 cards. The higher the number, the better
	    *         the hand is.
	    */
	   public final static int rankHand(Card[] h) 
	   {
		   boolean straight = false;
		   boolean flush = false;
		   byte max_hand = (byte) (h.length >= POKER_HAND ? POKER_HAND : h.length);
		   Card ace = new Card(Card.HEART, Card.CARD_ACE);
		   int r, c;
		   byte rank, suit;

		   // pair data
		   byte[] group_size = new byte[POKER_HAND + 1];   // array to track the groups or cards in your hand
		   byte[] paired = new byte[Card.NUM_RANKS];       // array to track paired carsd
		   byte[][] pair_rank = new byte[POKER_HAND + 1][2];  // array to track the rank of our pairs
		   // straight
		   byte straight_high = 0;                         // track the high card (rank) of our straight
		   byte straight_size;
		   // flush
		   byte[] suit_size = new byte[Card.NUM_SUITS];
		   byte major_suit = 0;

		   // determine pairs, dereference order data, check flush
		   // for (r=0;r<Card.NUM_RANKS;r++) paired[r] = 0;
		   // for (r=0;r<Card.NUM_SUITS;r++) suit_size[r] = 0;
		   // for (r=0;r<=POKER_HAND;r++) group_size[r] = 0;
		   for (r = 0; r < h.length; r++) 
		   {
			   rank = (byte)h[r].getRankValue();
			   suit = (byte)h[r].getSuitValue();
			   paired[rank]++;            // Add rank of card to paired array to track the pairs we have.	       
			   group_size[paired[rank]]++;   // keep track of the groups in our hand (1-pair, 2-pair, 1-trips, 1-trips 1-pair)
	        
			   if (paired[rank] != 0)     // To prevent looking at group_size[-1], which would be bad.
			   {
				   group_size[paired[rank] - 1]--;  // Decrese the previous group by one.  group_size[0] should end up at -5.
			   }
			   if ((++suit_size[suit]) >= POKER_HAND)
			   {  
				   // Add suit to suit array, then check for a flush.	            
				   flush = true;
				   major_suit = suit;				   
			   }	     
		   }
		   // Card.ACE low?  Add to straight_size if so.
		   straight_size = (byte) (paired[ace.getRankValue()] != 0 ? 1 : 0);

		   for (int i = 0; i < (POKER_HAND + 1); i++) 
		   {
			   pair_rank[i][0] = (byte) Card.NUM_RANKS;
			   pair_rank[i][1] = (byte) Card.NUM_RANKS;	     
		   }

		   // check for straight and pair data
		   // Start at the Deuce.  straight_size = 1 if we have an ace.
		   for (r = 0; r < Card.NUM_RANKS; r++) 
		   {
			   // check straight
			   if (paired[r] != 0) {
				   if ((++straight_size) >= POKER_HAND) 
				   {   
					   // Do we have five cards in a row (a straight!)
					   straight = true;        // We sure do.
					   straight_high = (byte) r;  // Keep track of that high card	          
				   }	         
			   } 
			   else
			   {             
				   // Missing a card for our straight.  start the count over.	            
				   straight_size = 0;	         
			   }
			   // get pair ranks, keep two highest of each
			   c = paired[r];
			   if (c != 0) 
			   {
				   pair_rank[c][1] = pair_rank[c][0];
				   pair_rank[c][0] = (byte) r;				   
			   }	    
		   }

		   // now id type
		   int ident;

		   if (group_size[POKER_HAND] != 0)
		   {    
			   // we have five cards of the same rank in our hand.
			   ident = FIVEKIND * ID_GROUP_SIZE;   // must have five of a kind !!
			   ident += pair_rank[POKER_HAND][0];
			   return ident;	   
		   }

		   if (straight && flush) 
		   {
			   byte hi = ID_ExistsStraightFlush(h, major_suit);
			   if (hi > 0) 
			   {
				   ident = STRAIGHTFLUSH * ID_GROUP_SIZE;
				   ident += hi;
				   return ident;	        
			   }	    
		   }

		   if (group_size[4] != 0) 
		   {
			   ident = FOURKIND * ID_GROUP_SIZE;
			   ident += pair_rank[4][0] * Card.NUM_RANKS;
			   pair_rank[4][1] = (byte) Card.NUM_RANKS; // just in case 2 sets quads
			   ident += ID_KickerValue(paired, 1, pair_rank[4]);
		   } 
		   else if (group_size[3] >= 2) 
		   {
			   ident = FULLHOUSE * ID_GROUP_SIZE;
			   ident += pair_rank[3][0] * Card.NUM_RANKS;
			   ident += pair_rank[3][1];	     
		   } 
		   else if (group_size[3] == 1 && group_size[2] != 0) 
		   {
			   ident = FULLHOUSE * ID_GROUP_SIZE;
			   ident += pair_rank[3][0] * Card.NUM_RANKS;
			   ident += pair_rank[2][0];			   
		   } 
		   else if (flush) 
		   {
			   ident = FLUSH * ID_GROUP_SIZE;
			   ident += ID_KickerValueSuited(h, 5, major_suit);	     
		   } 
		   else if (straight) 
		   {
			   ident = STRAIGHT * ID_GROUP_SIZE;
			   ident += straight_high;	      
		   } 
		   else if (group_size[3] == 1) 
		   {
			   ident = THREEKIND * ID_GROUP_SIZE;
			   ident += pair_rank[3][0] * Card.NUM_RANKS * Card.NUM_RANKS;
			   ident += ID_KickerValue(paired, max_hand - 3, pair_rank[3]);	      
		   } 
		   else if (group_size[2] >= 2) 
		   {   
			   // TWO PAIR
			   ident = TWOPAIR * ID_GROUP_SIZE;    
			   ident += pair_rank[2][0] * Card.NUM_RANKS * Card.NUM_RANKS;
			   ident += pair_rank[2][1] * Card.NUM_RANKS;
			   ident += ID_KickerValue(paired, max_hand - 4, pair_rank[2]);	      
		   } 
		   else if (group_size[2] == 1) 
		   {   
			   // A PAIR
			   ident = PAIR * ID_GROUP_SIZE;
			   ident += pair_rank[2][0] * Card.NUM_RANKS * Card.NUM_RANKS * Card.NUM_RANKS;
			   ident += ID_KickerValue(paired, max_hand - 2, pair_rank[2]);	      
		   } 
		   else 
		   {                      
			   // A Low
			   ident = HIGH * ID_GROUP_SIZE;
			   ident += ID_KickerValue(paired, max_hand, pair_rank[2]);	     
		   }	     
		   return ident;	  
	   }

	   private static int pow(int n, int p) 
	   {
		   int res = 1;
		   while (p-- > 0)
		   {
			   res *= n;
		   }
		   return res;
	   }

	}
