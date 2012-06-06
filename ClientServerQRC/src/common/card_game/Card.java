package common.card_game;

public class Card {
	
	/*
	 * These are the different possible card values
	 */	
	public static int NUM_RANKS = 13;
	public static int NUM_SUITS = 4;
  
	public enum CardValue{      
		NOT_SET(0), CARD_ACE(1), CARD_2(2), CARD_3(3), CARD_4(4), CARD_5(5), CARD_6(6),     
		CARD_7(7), CARD_8(8), CARD_9(9), CARD_10(10), CARD_JACK(11), CARD_QUEEN(12),      
		CARD_KING(13);
		private int cardValue;
		CardValue(int cardValue)
		{
			this.setCardValue(cardValue);
		}
		public int getCardValue() {
			return cardValue;
		}
		public void setCardValue(int cardValue) {
			this.cardValue = cardValue;
		}
		public boolean isEqual(CardValue rhs)
		{
			return (this.getCardValue() == rhs.getCardValue());
		}
		public int getRankValue()
		{
			int cVal = -1;
			if (cardValue == 1)
			{
				cVal = 12;
			}
			else if (cardValue > 0)
			{
				cVal = cardValue - 2;
			}
			return cVal;
		}
		public static CardValue getEnum(int value)
		{
			if (value == NOT_SET.getCardValue()) 
	            return NOT_SET;
			else if (value == CARD_ACE.getCardValue()) 
				return CARD_ACE;
			else if (value == CARD_2.getCardValue()) 
				return CARD_2;
			else if (value == CARD_3.getCardValue()) 
				return CARD_3;
			else if (value == CARD_4.getCardValue()) 
				return CARD_4;
			else if (value == CARD_5.getCardValue()) 
				return CARD_5;
			else if (value == CARD_6.getCardValue()) 
				return CARD_6;
			else if (value == CARD_7.getCardValue()) 
				return CARD_7;
			else if (value == CARD_8.getCardValue()) 
				return CARD_8;
			else if (value == CARD_9.getCardValue()) 
				return CARD_9;
			else if (value == CARD_10.getCardValue()) 
				return CARD_10;
			else if (value == CARD_JACK.getCardValue()) 
				return CARD_JACK;
			else if (value == CARD_QUEEN.getCardValue()) 
				return CARD_QUEEN;
			else if (value == CARD_KING.getCardValue()) 
				return CARD_KING;	
			
			return NOT_SET;
		}		
		
	}
   
   /*
   * These are the different possible card suites
   */
	public enum CardSuite{
		NOT_SET(0), CLUB(1), SPADE(2), DIAMOND(4), HEART(8);
		private int cardSuite;
		CardSuite(int cardSuite)
		{
			this.setCardSuite(cardSuite);
		}
		public int getCardSuite() {
			return cardSuite;
		}
		public void setCardSuite(int cardSuite) {
			this.cardSuite = cardSuite;
		}
		public boolean isEqual(CardSuite rhs)
		{
			return (this.getCardSuite() == rhs.getCardSuite());
		}
		public int getSuitValue()
		{
			return (int)(Math.log10(cardSuite) / Math.log10(2.0));
		}
		public static String getEnum(int value)
		{
			
			if (value == NOT_SET.getCardSuite()) 
	            return "NOT_SET";
			else if (value == CLUB.getCardSuite()) 
				return "CLUB";
			else if (value == SPADE.getCardSuite()) 
				return "SPADE";
			else if (value == DIAMOND.getCardSuite()) 
				return "DIAMOND";
			else if (value == HEART.getCardSuite()) 
				return "HEART";
			
			return "NOT_SET";
		}
	}
   
   /* set the private variables */
   private CardSuite eSuite;
   private CardValue eValue;
   int val;
   int sui;
   
   public Card(CardSuite eSuite, CardValue eValue)
   {
      this.eSuite = eSuite;
      this.eValue = eValue;
      val = eValue.getCardValue();
      sui = eSuite.getCardSuite();
   }
   
   public void setCardValue(CardValue eValue)
   {
      this.eValue = eValue;
      val = eValue.getCardValue();
 
   }
   public CardValue getCardValue()
   {
	  eValue.setCardValue(val);
      return eValue;
      
   }
   
   public void setCardSuite(CardSuite eSuite)
   {
      this.eSuite = eSuite;
      sui = eSuite.getCardSuite();
   }
   public CardSuite getCardSuite()
   {
	   eSuite.setCardSuite(sui);
      return eSuite;
   }
   
   public String toString()
   {
	   String ret = "(" + CardSuite.getEnum(sui) + " " + CardValue.getEnum(val) + ")";
	   return ret;
   }

}
