package server.card_game;

import server.ClientModel;
import common.MessageParser;
import common.MessageParser.GamePlayRequest;
import common.MessageParser.ServerPlayGameMessage;
import common.card_game.Card;
import common.card_game.Card.CardSuite;
import common.card_game.Card.CardValue;

public class TexasHoldemModel {
	private Deck deck;
	private GamePlayRequest prevGamePlayRequest;
	private long lBankAmount;
	private long lPotSize;
	private long lBetAmount;
	private int iAnte;
	private Card[] oPlayerCards;
	private Card[] oDealerCards;
	private Card[] oCommunityCards;
	private ClientModel model;
	
	public TexasHoldemModel(ClientModel model)
	{
		this.model = model;
		this.lBankAmount = this.model.getClientBankAmount();
		this.model.getLogger().info(this.model.uniqueID + ": Creating Texas Holdem Server Model");
		deck = new Deck();
		oPlayerCards = new Card[2];
		oDealerCards = new Card[2];
		oCommunityCards = new Card[5];
		Init();
		
	}
	
	public void Init()
	{
		this.model.getLogger().info(this.model.uniqueID + ": Setting up Texas Holdem Server Model");
		deck.shuffle();
		oPlayerCards = deck.getCards(2);
		oDealerCards = deck.getCards(2);
		oCommunityCards = deck.getCards(5);
		lPotSize = 0;
		lBetAmount = 0;
		iAnte = Integer.parseInt(this.model.getXmlParser().getServerTagValue("MIN_ANTE"));
		prevGamePlayRequest = GamePlayRequest.NOT_SET;
	}
	
	public void Reset()
	{
		this.Init();
	}
	
	public MessageParser.ServerPlayGameMessage updateModel(MessageParser.ClientPlayGameMessage clientMsg)
	{
		ServerPlayGameMessage serverMsg = null;
		
		return serverMsg;
	}
	
	private MessageParser.Winner calculateWinner(Card[] playerCards, Card[] dealerCards, Card[] communitycards)
	{
		MessageParser.Winner winner = MessageParser.Winner.NOT_SET;
		
		return winner;
	}

}
