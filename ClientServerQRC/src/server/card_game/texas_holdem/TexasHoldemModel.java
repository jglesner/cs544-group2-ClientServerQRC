/**
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  
 *  This page contains game logic for playing Texas Holdem.
 *  
 *  This model class was downloaded and modified from
 *  //TODO WHERE WAS THIS CODE TAKEN FROM? OR IS IT ORIGINAL?
 *  
 */

package server.card_game.texas_holdem;

import server.ClientModel;
import server.card_game.Deck;
import common.GamePlayState;
import common.GamePlayState.PlayState;
import common.MessageParser;
import common.MessageParser.GamePlayRequest;
import common.MessageParser.GamePlayResponse;
import common.MessageParser.ServerPlayGameMessage;
import common.MessageParser.Winner;
import common.card_game.Card;
import common.card_game.Card.CardSuite;
import common.card_game.Card.CardValue;

public class TexasHoldemModel {
	private Deck deck;
	private GamePlayState prevGamePlayState;
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
		prevGamePlayState = new GamePlayState();
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
	}
	
	public void Reset()
	{
		this.Init();
	}
	
	public MessageParser.ServerPlayGameMessage updateModel(MessageParser.ClientPlayGameMessage clientMsg)
	{
		ServerPlayGameMessage serverMsg = null;
		
		// determine the current state of the game
		if ((prevGamePlayState.getPlayState().isEqual(PlayState.NOT_SET)) || (prevGamePlayState.getPlayState().isEqual(PlayState.GET_RIVER)))
		{
			//System.out.println("FIRST");
			// first initialized, the client should have sent an init request
			if (clientMsg.getGamePlayRequest().isEqual(GamePlayRequest.INIT))
			{
				//System.out.println("FIRST-1");
				// fill in the server message
				Card noCard = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
				
				serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), GamePlayResponse.INIT_ACK, 
			            this.iAnte, noCard, noCard, noCard, noCard, noCard, noCard, noCard, noCard, noCard, Winner.NOT_SET, this.lPotSize, this.lBetAmount, this.lBankAmount);
				
				// update latest request
				prevGamePlayState.setPlayState(PlayState.INIT);
				this.model.getLogger().info(this.model.uniqueID + ": has sent game play init message.");
			}
			else
			{
				//System.out.println("FIRST-2");
				// client sent the wrong message
				this.model.getLogger().info(this.model.uniqueID + ": needs to send init message for game play, Ignoring Msg.");
			}
		}
		else if (prevGamePlayState.getPlayState().isEqual(PlayState.INIT))
		{
			//System.out.println("SECOND");
			if (clientMsg.getGamePlayRequest().isEqual(GamePlayRequest.GET_HOLE))
			{
				//System.out.println("TEST: " + clientMsg.getBetAmount() + " " + this.iAnte + " " + clientMsg.getBetAmount() + " " + (this.lBankAmount / 3));
				// client wants to get the cards. Check to make sure the ante is valid
				if (clientMsg.getBetAmount() >= this.iAnte && clientMsg.getBetAmount() <= (this.lBankAmount / 3))
				{
					// update the ante amount to what the client gave
					this.lPotSize += clientMsg.getBetAmount();
					this.iAnte = (int)clientMsg.getBetAmount();
					this.lBetAmount = clientMsg.getBetAmount();
					// update the client's bank amount by subtracting the ante
					this.lBankAmount -= (long)this.iAnte;
					// fill in the server message
					Card noCard = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
					
					serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), GamePlayResponse.GET_HOLE_ACK, 
				            this.iAnte, oPlayerCards[0], oPlayerCards[1], noCard, noCard, noCard, noCard, noCard, noCard, noCard, Winner.NOT_SET, this.lPotSize, this.lBetAmount, this.lBankAmount);

					
					// update latest request
					prevGamePlayState.setPlayState(PlayState.GET_HOLE);
					this.model.getLogger().info(this.model.uniqueID + ": has sent game play get hole cards message.");
				}
				else
				{

					
					// invalid request
					this.model.getLogger().info(this.model.uniqueID + ": has sent an invalid ante amount");
					// fill in the server message
					Card noCard = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
					
					serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), GamePlayResponse.INVALID_ANTE_BET, 
				            this.iAnte, noCard, noCard, noCard, noCard, noCard, noCard, noCard, noCard, noCard, Winner.NOT_SET, this.lPotSize, this.lBetAmount, this.lBankAmount);
				}
			}
			else
			{
				System.out.println("OH OH");	
				
				// invalid request when in the init phase
				this.model.getLogger().info(this.model.uniqueID + ": sent invalid request during Init phase");
			}
		}
		else if (prevGamePlayState.getPlayState().isEqual(PlayState.GET_HOLE))
		{
			
			//System.out.println("THIRD");
			
			if (clientMsg.getGamePlayRequest().isEqual(GamePlayRequest.GET_FLOP))
			{
				// check to make sure the client's bet amount is twice the ante
				if (clientMsg.getBetAmount() == (2 * this.iAnte))
				{
					// this is a valid request so update the potsize, and bank amount
					this.lPotSize += clientMsg.getBetAmount();
					this.lBetAmount = clientMsg.getBetAmount();
					this.lBankAmount -= clientMsg.getBetAmount();
					
					System.out.println("Account: " + clientMsg.getBetAmount());
					System.out.println("Bank Account " + this.lBankAmount);
					// fill in the server message
					Card noCard = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
					
					serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), GamePlayResponse.GET_FLOP_ACK, 
				            this.iAnte, oPlayerCards[0], oPlayerCards[1], noCard, noCard, oCommunityCards[0], oCommunityCards[1], oCommunityCards[2], noCard, noCard, Winner.NOT_SET, this.lPotSize, this.lBetAmount, this.lBankAmount);
					
					// update latest request
					prevGamePlayState.setPlayState(PlayState.GET_FLOP);
					this.model.getLogger().info(this.model.uniqueID + ": has sent game play get flop cards message.");
					
				}
				else
				{
					// invalid request
					this.model.getLogger().info(this.model.uniqueID + ": has sent an invalid hole bet amount");
					// fill in the server message
					Card noCard = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
					
					serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), GamePlayResponse.INVALID_HOLE_BET, 
				            this.iAnte, oPlayerCards[0], oPlayerCards[1], noCard, noCard, noCard, noCard, noCard, noCard, noCard, Winner.NOT_SET, this.lPotSize, this.lBetAmount, this.lBankAmount);
				}
			}
			else if (clientMsg.getGamePlayRequest().isEqual(GamePlayRequest.FOLD))
			{
				
				
				// client has sent a fold request. ReInitialize the model and send back an acknowledgment
				Reset();
				this.model.getLogger().info(this.model.uniqueID + ": has sent a fold request");
				
				// fill in the server message
				Card noCard = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
				
				serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), GamePlayResponse.FOLD_ACK, 
			            this.iAnte, noCard, noCard, noCard, noCard, noCard, noCard, noCard, noCard, noCard, Winner.DEALER, this.lPotSize, this.lBetAmount, this.lBankAmount);
				
			}
			else
			{
				// invalid message in Hole phase
				this.model.getLogger().info(this.model.uniqueID + ": sent invalid request during Hole phase");
			}
		}
		else if (prevGamePlayState.getPlayState().isEqual(PlayState.GET_FLOP))
		{
			//System.out.println("FOURTH");
			if (clientMsg.getGamePlayRequest().isEqual(GamePlayRequest.GET_TURN))
			{
				// check to make sure the client's bet amount is the ante or 0 (for a check) and they have that amount available
				if ((clientMsg.getBetAmount() == this.iAnte || clientMsg.getBetAmount() == 0) && (this.lBankAmount - clientMsg.getBetAmount() >= 0))
				{
					// this is a valid request so update the potsize, and bank amount
					this.lPotSize += clientMsg.getBetAmount();
					this.lBetAmount = clientMsg.getBetAmount();
					this.lBankAmount -= clientMsg.getBetAmount();
					// fill in the server message
					Card noCard = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
					
					serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), GamePlayResponse.GET_TURN_ACK, 
				            this.iAnte, oPlayerCards[0], oPlayerCards[1], noCard, noCard, oCommunityCards[0], oCommunityCards[1], oCommunityCards[2], oCommunityCards[3], noCard, Winner.NOT_SET, this.lPotSize, this.lBetAmount, this.lBankAmount);
					
					// update latest request
					prevGamePlayState.setPlayState(PlayState.GET_TURN);
					this.model.getLogger().info(this.model.uniqueID + ": has sent game play get turn card message.");
					
				}
				else
				{
					// invalid request
					this.model.getLogger().info(this.model.uniqueID + ": has sent an invalid turn bet amount");
					// fill in the server message
					Card noCard = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
					
					serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), GamePlayResponse.INVALID_FLOP_BET, 
				            this.iAnte, oPlayerCards[0], oPlayerCards[1], noCard, noCard, oCommunityCards[0], oCommunityCards[1], oCommunityCards[2], noCard, noCard, Winner.NOT_SET, this.lPotSize, this.lBetAmount, this.lBankAmount);
				}
			}
			else if (clientMsg.getGamePlayRequest().isEqual(GamePlayRequest.FOLD))
			{
				// client has sent a fold request. ReInitialize the model and send back an acknowledgment
				Reset();
				this.model.getLogger().info(this.model.uniqueID + ": has sent a fold request");
				
				// fill in the server message
				Card noCard = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
				
				serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), GamePlayResponse.FOLD_ACK, 
			            this.iAnte, noCard, noCard, noCard, noCard, noCard, noCard, noCard, noCard, noCard, Winner.DEALER, this.lPotSize, this.lBetAmount, this.lBankAmount);
				
			}
			else
			{
				// invalid message in Hole phase
				this.model.getLogger().info(this.model.uniqueID + ": sent invalid request during Turn phase");
			}
		}
		else if (prevGamePlayState.getPlayState().isEqual(PlayState.GET_TURN))
		{
			//System.out.println("FIFTH");
			if (clientMsg.getGamePlayRequest().isEqual(GamePlayRequest.GET_RIVER))
			{
				// check to make sure the client's bet amount is the ante or 0 (for a check) and they have that amount available
				if ((clientMsg.getBetAmount() == this.iAnte || clientMsg.getBetAmount() == 0) && (this.lBankAmount - clientMsg.getBetAmount() >= 0))
				{
					// this is a valid request so update the potsize, and bank amount
					this.lPotSize += clientMsg.getBetAmount();
					this.lBetAmount = clientMsg.getBetAmount();
					this.lBankAmount -= clientMsg.getBetAmount();
					// fill in the server message
					Winner winner = calculateWinner(oPlayerCards, oDealerCards, oCommunityCards);
					
					// update the client's bank amount if needed
					if (winner.isEqual(Winner.PLAYER))
					{
						this.lBankAmount += 2*this.lPotSize + this.iAnte;
					}
					else if (winner.isEqual(Winner.DRAW))
					{
						this.lBankAmount += this.lPotSize + this.iAnte;
					}
					
					serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), GamePlayResponse.GET_RIVER_ACK, 
				            this.iAnte, oPlayerCards[0], oPlayerCards[1], oDealerCards[0], oDealerCards[1], oCommunityCards[0], oCommunityCards[1], oCommunityCards[2], oCommunityCards[3], oCommunityCards[4], winner, this.lPotSize, this.lBetAmount, this.lBankAmount);
					
					// update latest request
					prevGamePlayState.setPlayState(PlayState.GET_RIVER);
					this.model.getLogger().info(this.model.uniqueID + ": has sent game play get river card message.");
					// reinitialize the model for the next hand
					Reset();
					
				}
				else
				{
					// invalid request
					this.model.getLogger().info(this.model.uniqueID + ": has sent an invalid river bet amount");
					// fill in the server message
					Card noCard = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
					
					serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), GamePlayResponse.INVALID_TURN_BET, 
				            this.iAnte, oPlayerCards[0], oPlayerCards[1], noCard, noCard, oCommunityCards[0], oCommunityCards[1], oCommunityCards[2], oCommunityCards[3], noCard, Winner.NOT_SET, this.lPotSize, this.lBetAmount, this.lBankAmount);
				}
			}
			else if (clientMsg.getGamePlayRequest().isEqual(GamePlayRequest.FOLD))
			{
				// client has sent a fold request. ReInitialize the model and send back an acknowledgment
				Reset();
				this.model.getLogger().info(this.model.uniqueID + ": has sent a fold request");
				
				// fill in the server message
				Card noCard = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
				
				serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), GamePlayResponse.FOLD_ACK, 
			            this.iAnte, noCard, noCard, noCard, noCard, noCard, noCard, noCard, noCard, noCard, Winner.DEALER, this.lPotSize, this.lBetAmount, this.lBankAmount);
				
			}
			else
			{
				// invalid message in Hole phase
				this.model.getLogger().info(this.model.uniqueID + ": sent invalid request during River phase");
			}
		}
		else
		{
			//System.out.println("SIXTH");
			// entered an invalid state
			// this is here just for validating the program
			this.model.getLogger().info(this.model.uniqueID + ": Error entered invalid Game Play Phase!");
		}
		//System.out.println("SEVENTH");
		return serverMsg;
	}
	
	private MessageParser.Winner calculateWinner(Card[] playerCards, Card[] dealerCards, Card[] communityCards)
	{
		MessageParser.Winner winner = MessageParser.Winner.NOT_SET;
		Card[] playerHand = new Card[7];
		for (int iI = 0; iI < playerHand.length; iI++)
		{
			if (iI < playerCards.length)
			{
				playerHand[iI] = playerCards[iI];
			}
			else
			{
				playerHand[iI] = communityCards[iI-playerCards.length];
			}
		}
		
		Card[] dealerHand = new Card[7];
		for (int iI = 0; iI < dealerHand.length; iI++)
		{
			if (iI < dealerCards.length)
			{
				dealerHand[iI] = dealerCards[iI];
			}
			else
			{
				dealerHand[iI] = communityCards[iI-dealerCards.length];
			}
		}
		TexasHoldemHandEval evaluator = new TexasHoldemHandEval();
		int compareHands = evaluator.compareHands(playerHand, dealerHand);
		
		if (compareHands > 0)
		{
			// Player won
			winner = Winner.PLAYER;
		}
		else if (compareHands < 0)
		{
			// dealer won
			winner = Winner.DEALER;
		}
		else
		{
			// draw
			winner = Winner.DRAW;
		}
		
		return winner;
	}

}
