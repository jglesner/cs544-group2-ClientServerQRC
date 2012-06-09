package client.card_game;

import common.card_game.Card;

/**
 * ClientPokerModel defines all necessary methods of client side
 *  
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  Members: Jeremy Glesner, Dustin Overmiller, Yiqi Ju, Lei Yuan
 *  Project: Advanced Game Message Protocol Implementation
 *  
 */

public class ClientPokerModel  {
	private long lBankAmount; //bank amount
	private long lPotSize; //pot size
	private long lBetAmount; //bet amount
	private int iAnte; //ante amount
	private Card[] oPlayerCards; // player's cards' array
	private Card[] oDealerCards; // dealer's cards' array
	private Card[] oFlopCards; // flop cards' array
	private Card   oTurnCard; // turn card
	private Card   oRiverCard; //river card
	int winner; //winner

	/**
	 * Constructor
	 * @param none
	 * @return none
	 */
	public ClientPokerModel()
	{
	
		reset();
	}

	/**
	 * reset - reset all variables
	 * @param none
	 * @return none
	 */
    public void reset(){
    	oPlayerCards=new Card[2];
		oDealerCards=new Card[2];
		oFlopCards=new Card[3];
		lBankAmount=0;
		lPotSize=0;
		iAnte=0;
		this.winner=0;
		this.lBetAmount=0;
		this.oRiverCard=null;
		this.oTurnCard=null;
    }

    /**
     * getWinner - return the winner of the game
     * @param none
     * @return int
     */
	public int getWinner() {
		return winner;
	}

	/**
	 * setWinner - set the winner value
	 * @param winner
	 * @return none
	 */
	public void setWinner(int winner) {
		this.winner = winner;
	}

	/**
	 * getlBankAmount - return the bank amount
	 * @param none
	 * @return long
	 */
	public long getlBankAmount() {
		return lBankAmount;
	}

	/**
	 * setlBankAmount - set the bank amount
	 * @param lBankAmount
	 * @return none
	 */
	public void setlBankAmount(long lBankAmount) {
		this.lBankAmount = lBankAmount;
	}

	/**
	 * getlPotSize - return the pot amount
	 * @param none
	 * @return long
	 */
	public long getlPotSize() {
		return lPotSize;
	}

	/**
	 * setlPotSize - set the pot amount
	 * @param lPotSize
	 * @return none
	 */
	public void setlPotSize(long lPotSize) {
		this.lPotSize = lPotSize;
	}

	/**
	 * getlBetAmount - get the bet amount
	 * @param none
	 * @return long
	 */
	public long getlBetAmount() {
		return lBetAmount;
	}

	/**
	 * setlBetAmount - set the bet amount
	 * @param lBetAmount
	 * @return none
	 */
	public void setlBetAmount(long lBetAmount) {
		this.lBetAmount = lBetAmount;
	}

	/**
	 * getiAnte - get the ante amount
	 * @param none
	 * @return int
	 */
	public int getiAnte() {
		return iAnte;
	}

	/**
	 * setiAnte - set the ante amount
	 * @param iAnte
	 * @return none
	 */
	public void setiAnte(int iAnte) {
		this.iAnte = iAnte;
	}

	/**
	 * getoPlayerCards - return players' cards
	 * @param none
	 * @return array
	 */
	public Card[] getoPlayerCards() {
		return oPlayerCards;
	}

	/**
	 * setoPlayerCards - set players' cards
	 * @param oPlayerCards
	 * @return none
	 */
	public void setoPlayerCards(Card[] oPlayerCards) {
		this.oPlayerCards[0] = oPlayerCards[0];
		this.oPlayerCards[1] = oPlayerCards[1];
	}

	/**
	 * getDealerCards - get dealers' cards
	 * @param none
	 * @return array
	 */
	public Card[] getoDealerCards() {
		return oDealerCards;
	}

	/**
	 * setoDealerCards - set dealer's cards
	 * @param oDealerCards
	 * @return none
	 */
	public void setoDealerCards(Card[] oDealerCards) {
		this.oDealerCards[0] = oDealerCards[0];
		this.oDealerCards[1] = oDealerCards[1];
	}

	/**
	 * geoFlopCards - get the flop cards
	 * @param none
	 * @return array
	 */
	public Card[] getoFlopCards() {
		return oFlopCards;
	}

	/**
	 * setoFlopCards - set the flop cards
	 * @param oCommunityCards
	 * @return none
	 */
	public void setoFlopCards(Card[] oCommunityCards) {
		this.oFlopCards[0] = oCommunityCards[0];
		this.oFlopCards[1] = oCommunityCards[1];
		this.oFlopCards[2] = oCommunityCards[2];
	
	}

	/**
	 * getoTurnCard - get the turn card
	 * @param none
	 * @return oTurnCard
	 */
	public Card getoTurnCard() {
		return oTurnCard;
	}

	/**
	 * setoTurnCard - set the turn card
	 * @param oTurnCard
	 * @return none
	 */
	public void setoTurnCard(Card oTurnCard) {
		this.oTurnCard = oTurnCard;
	}

	/**
	 * getoRiverCard - get the river card
	 * @param none
	 * @return oRiverCard
	 */
	public Card getoRiverCard() {
		return oRiverCard;
	}

	/**
	 * setoRiverCard - set the river card
	 * @param oRiverCard
	 * @return none
	 */
	public void setoRiverCard(Card oRiverCard) {
		this.oRiverCard = oRiverCard;
	}
	
	/**
	 * init - initialize the client poker model
	 * @param none
	 * @return none
	 */
	public void init()
	{
		oPlayerCards=new Card[2];
		oDealerCards=new Card[2];
		oFlopCards=new Card[3];
		lBankAmount=0;
		lPotSize=0;
		iAnte=0;
		this.lBetAmount=0;
		this.oRiverCard=null;
		this.oTurnCard=null;
		
	}
}