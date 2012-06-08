package common.card_game;

/**
 *  The Card Class
 *  
 *  This class is used to represent a card in card games
 *
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  Members: Jeremy Glesner, Dustin Overmiller, Yiqi Ju, Lei Yuan
 *  Project: Advanced Game Message Protocol Implementation
 *  
 */
public class Card {
	
	/* The total number of suits and ranks */
	public static int NUM_RANKS = 13;
	public static int NUM_SUITS = 4;	
   
	/* set the private variables */
	private int value;
   private int suite;	
   public static int NOT_SET = 0;
   /*
	 * These are the different possible card suits
	 */	
   public static int CLUB = 1;
   public static int SPADE = 2;
   public static int DIAMOND = 4;
   public static int HEART = 8;
   /*
	 * These are the different possible card values
	 */	
   public static int CARD_ACE = 1;
   public static int CARD_2 = 2;
   public static int CARD_3 = 3;
   public static int CARD_4 = 4;
   public static int CARD_5 = 5;
   public static int CARD_6 = 6;
   public static int CARD_7 = 7;
   public static int CARD_8 = 8;
   public static int CARD_9 = 9;
   public static int CARD_10 = 10;
   public static int CARD_JACK = 11;
   public static int CARD_QUEEN = 12;
   public static int CARD_KING = 13;
   
   /**
   * Constructor - stores the card suit and value
   *
   */
   public Card(int suite, int value)
   {
	   this.suite = 0;
	   this.value = 0;
      /* make sure the suite is valid */
	   if (suite == 1 || suite == 2 || suite == 4 || suite == 8)
	   {
		   this.suite = suite;
	   }
      /* make sure the value is valid */
	   if (value >= 0 && value <= NUM_RANKS)
	   {
		   this.value = value;
	   }	   
   }
   /**
   * setCardValue - set the card value
   * @param value
   * @return none
   */
   public void setCardValue(int value)
   {
	   this.value = 0;
      /* make sure it is valid */
	   if (value >= 0 && value <= NUM_RANKS)
	   {
		   this.value = value;
	   }
   }
   /**
   * getCardValue - get the value of the card
   * @param none
   * @return int
   */
   public int getCardValue()
   {
	   return value;      
   }
   
   /**
   * setCardSuite - set the card suite
   * @param suite
   * @return none
   */
   public void setCardSuite(int suite)
   {
	   this.suite = 0;
      /* make sure it is valid */
	   if (suite == 1 || suite == 2 || suite == 4 || suite == 8)
	   {
		   this.suite = suite;
	   }
   }
   /**
   * getCardSuite - get the suite of the card
   * @param none
   * @return int
   */
   public int getCardSuite()
   {
	   return suite;
   }
   
   /**
   * getSuiteValue - used to compare card suites
   * @param none
   * @return int
   */
   public int getSuitValue()
	{
		return (int)(Math.log10(suite) / Math.log10(2.0));
	}
   
   /**
   * getRankValue - used to compare card values
   * @param none
   * @return int
   */
   public int getRankValue()
	{
		int cVal = -1;
      /* make sure the ace has the highest value */
		if (value == 1)
		{
			cVal = 12;
		}
		else if (value > 0)
		{
			cVal = value - 2;
		}
		return cVal;
	}
   
   /**
   * toString - convert the class to a string for printing
   * @param none
   * @return String
   */
   public String toString()
   {
	   String sValue = "ERROR";
	   if (this.value == 0)
	   {
		   sValue = "NOT_SET";
	   }
	   else if (this.value == 1)
	   {
		   sValue = "ACE";
	   }
	   else if (this.value > 1 && this.value < 11)
	   {
		   sValue = "" + this.value;
	   }
	   else if (this.value == 11)
	   {
		   sValue = "JACK";
	   }
	   else if (this.value == 12)
	   {
		   sValue = "QUEEN";
	   }
	   else if (this.value == 13)
	   {
		   sValue = "KING";
	   }
	   String sSuite = "ERROR";
	   if (this.suite == 0)
	   {
		   sSuite = "NOT_SET";
	   }
	   else if (this.suite == 1)
	   {
		   sSuite = "CLUB";
	   }
	   else if (this.suite == 2)
	   {
		   sSuite = "SPADE";
	   }
	   else if (this.suite == 4)
	   {
		   sSuite = "DIAMOND";
	   }
	   else if (this.suite == 8)
	   {
		   sSuite = "HEART";
	   }
	   String ret = "(" + sSuite + " " + sValue + ")";
	   return ret;
   }

}
