package client.card_game;

/**
 * @author LeiYuan
 *
 */


import common.card_game.Card;

public class ClientPokerModel  {
	private long lBankAmount;
	private long lPotSize;
	private long lBetAmount;
	private int iAnte;
	private Card[] oPlayerCards;
	private Card[] oDealerCards;
	private Card[] oFlopCards;
	private Card   oTurnCard;
	private Card   oRiverCard;
	int winner;

	
   
	public ClientPokerModel()
	{
	
		reset();
	}

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

	public int getWinner() {
		return winner;
	}



	public void setWinner(int winner) {
		this.winner = winner;
	}



	public long getlBankAmount() {
		return lBankAmount;
	}


	public void setlBankAmount(long lBankAmount) {
		this.lBankAmount = lBankAmount;
	}


	public long getlPotSize() {
		return lPotSize;
	}


	public void setlPotSize(long lPotSize) {
		this.lPotSize = lPotSize;
	}


	public long getlBetAmount() {
		return lBetAmount;
	}


	public void setlBetAmount(long lBetAmount) {
		this.lBetAmount = lBetAmount;
	}


	public int getiAnte() {
		return iAnte;
	}


	public void setiAnte(int iAnte) {
		this.iAnte = iAnte;
	}


	public Card[] getoPlayerCards() {
		return oPlayerCards;
	}


	public void setoPlayerCards(Card[] oPlayerCards) {
		this.oPlayerCards[0] = oPlayerCards[0];
		this.oPlayerCards[1] = oPlayerCards[1];
	}


	public Card[] getoDealerCards() {
		return oDealerCards;
	}


	public void setoDealerCards(Card[] oDealerCards) {
		this.oDealerCards[0] = oDealerCards[0];
		this.oDealerCards[1] = oDealerCards[1];
	}


	public Card[] getoFlopCards() {
		return oFlopCards;
	}


	public void setoFlopCards(Card[] oCommunityCards) {
		this.oFlopCards[0] = oCommunityCards[0];
		this.oFlopCards[1] = oCommunityCards[1];
		this.oFlopCards[2] = oCommunityCards[2];
	
	}


	public Card getoTurnCard() {
		return oTurnCard;
	}


	public void setoTurnCard(Card oTurnCard) {
		this.oTurnCard = oTurnCard;
	}


	public Card getoRiverCard() {
		return oRiverCard;
	}


	public void setoRiverCard(Card oRiverCard) {
		this.oRiverCard = oRiverCard;
	}
	
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