package common.card_game;

public class Card {
	
	/*
	 * These are the different possible card values
	 */	
	public static int NUM_RANKS = 13;
	public static int NUM_SUITS = 4;
    private boolean is_visible=true;
	public boolean isIs_visible() {
		return is_visible;
	}

	public void setIs_visible(boolean is_visible) {
		this.is_visible = is_visible;
	}

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
	}
   
   /* set the private variables */
   private CardSuite eSuite;
   private CardValue eValue;
   
   public Card(CardSuite eSuite, CardValue eValue)
   {
      this.eSuite = eSuite;
      this.eValue = eValue;
   }
   
   public void setCardValue(CardValue eValue)
   {
      this.eValue = eValue;
   }
   public CardValue getCardValue()
   {
      return eValue;
   }
   
   public void setCardSuite(CardSuite eSuite)
   {
      this.eSuite = eSuite;
   }
   public CardSuite getCardSuite()
   {
      return eSuite;
   }
   
   public String toString()
   {
	   String ret = "(" + this.getCardSuite() + " " + this.getCardValue() + ")";
	   return ret;
   }

}
