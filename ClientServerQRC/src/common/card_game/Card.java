package common.card_game;

public class Card {
	
	/*
	 * These are the different possible card values
	 */	
	public static int NUM_RANKS = 13;
	public static int NUM_SUITS = 4;	
   
	/* set the private variables */
	private int value;
   private int suite;
   public static int NOT_SET = 0;
   public static int CLUB = 1;
   public static int SPADE = 2;
   public static int DIAMOND = 4;
   public static int HEART = 8;
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
   
   public Card(int suite, int value)
   {
	   this.suite = 0;
	   this.value = 0;
	   if (suite == 1 || suite == 2 || suite == 4 || suite == 8)
	   {
		   this.suite = suite;
	   }
	   if (value >= 0 && value <= NUM_RANKS)
	   {
		   this.value = value;
	   }	   
   }
   
   public void setCardValue(int value)
   {
	   this.value = 0;
	   if (value >= 0 && value <= NUM_RANKS)
	   {
		   this.value = value;
	   }
   }
   public int getCardValue()
   {
	   return value;      
   }
   
   public void setCardSuite(int suite)
   {
	   this.suite = 0;
	   if (suite == 1 || suite == 2 || suite == 4 || suite == 8)
	   {
		   this.suite = suite;
	   }
   }
   public int getCardSuite()
   {
	   return suite;
   }
   
   public int getSuitValue()
	{
		return (int)(Math.log10(suite) / Math.log10(2.0));
	}
   
   public int getRankValue()
	{
		int cVal = -1;
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
