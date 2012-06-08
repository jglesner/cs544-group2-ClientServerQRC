/**
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  
 *  This page contains game logic for playing Texas Holdem.
 *  
 *  
 */

package server.card_game.texas_holdem;

import server.ClientModel;
import server.card_game.Deck;
import common.GamePlayState;
import common.MessageParser;
import common.MessageParser.ServerPlayGameMessage;
import common.card_game.Card;
/**
 * TexasHoldemModel is spawned, one per client (if this game is chosen)
 * to manage the game play of the server. It will get the client request
 * compare it to the current game state and send the appropriate response
 * (if there is one)
 * 
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  Members: Jeremy Glesner, Dustin Overmiller, Yiqi Ju, Lei Yuan
 *  Project: Advanced Game Message Protocol Implementation
 *  
 */
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
	
	/**
	* Constructor
	*
	*/
	public TexasHoldemModel(ClientModel model)
	{
		prevGamePlayState = new GamePlayState();
		this.model = model;
		this.lBankAmount = this.model.getClientBankAmount();
		this.model.getLogAndPublish().write(this.model.uniqueID + ": Creating Texas Holdem Server Model", true, false);
		deck = new Deck();
		oPlayerCards = new Card[2];
		oDealerCards = new Card[2];
		oCommunityCards = new Card[5];
		Init();
		
	}
	/**
   * Init - reset everything in order to start the game
   * @param none
   * @return none
   */
	public void Init()
	{
		this.model.getLogAndPublish().write(this.model.uniqueID + ": Setting up Texas Holdem Server Model", true, false);
		/* initialize the state to the first state */
		prevGamePlayState.setPlayState(GamePlayState.NOT_SET);
		/* shuffle the deck and get the cards */
		deck.shuffle();
		oPlayerCards = deck.getCards(2);
		oDealerCards = deck.getCards(2);
		oCommunityCards = deck.getCards(5);
		/* zero out the table variables and get the minimum ante */
		lPotSize = 0;
		lBetAmount = 0;
		iAnte = Integer.parseInt(this.model.getXmlParser().getServerTagValue("MIN_ANTE"));		
	}
	/**
   * Init - function to convert back to the first 
   * @param none
   * @return none
   */
	public void Reset()
	{
		this.Init();
	}
	/**
   * updateModel - this is the main function which will take in the client message and depending on the 
   * current game state send the appropriate server response
   * @param ClientPlayGameMessage
   * @return ServerPlayGameMessage
   */
	public MessageParser.ServerPlayGameMessage updateModel(MessageParser.ClientPlayGameMessage clientMsg)
	{
		ServerPlayGameMessage serverMsg = null;
		
		// determine the current state of the game
		if (prevGamePlayState.getPlayState() == GamePlayState.NOT_SET)
		{
			// first initialized, the client should have sent an init request
			if (clientMsg.getGamePlayRequest() == MessageParser.GAME_PLAY_REQUEST_INIT)
			{
				// fill in the server message				
				serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), MessageParser.GAME_PLAY_RESPONSE_INIT_ACK, 
			            this.iAnte, new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), 
                     new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), MessageParser.NOT_SET, this.lPotSize, this.lBetAmount, this.lBankAmount);
				
				// update latest request
				prevGamePlayState.setPlayState(GamePlayState.INIT);
				this.model.getLogAndPublish().write(this.model.uniqueID + ": has sent game play init message.", true, false);
			}
			else
			{
				// client sent the wrong message
				this.model.getLogAndPublish().write(this.model.uniqueID + ": needs to send init message for game play, Ignoring Msg.", true, false);
			}
		}
		else if (prevGamePlayState.getPlayState() == GamePlayState.INIT)
		{
			if (clientMsg.getGamePlayRequest() == MessageParser.GAME_PLAY_REQUEST_GET_HOLE)
			{
				// client wants to get the cards. Check to make sure the ante is valid
				if (clientMsg.getBetAmount() >= this.iAnte && clientMsg.getBetAmount() <= (this.lBankAmount / 3))
				{
					// update the ante amount to what the client gave
					this.iAnte = (int)clientMsg.getBetAmount();
					this.lBetAmount = clientMsg.getBetAmount();
					// update the client's bank amount by subtracting the ante
					this.lBankAmount -= (long)this.iAnte;
					// fill in the server message					
					serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), MessageParser.GAME_PLAY_RESPONSE_GET_HOLE_ACK, 
				            this.iAnte, oPlayerCards[0], oPlayerCards[1], new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), 
                        new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), MessageParser.NOT_SET, this.lPotSize, this.lBetAmount, this.lBankAmount);
					
					// update latest request
					prevGamePlayState.setPlayState(GamePlayState.GET_HOLE);
					this.model.getLogAndPublish().write(this.model.uniqueID + ": has sent game play get hole cards message.", true, false);
				}
				else
				{
					// invalid request
					this.model.getLogAndPublish().write(this.model.uniqueID + ": has sent an invalid ante amount", true, false);
					// fill in the server message					
					serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), MessageParser.GAME_PLAY_RESPONSE_INVALID_ANTE_BET, 
				            this.iAnte, new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), 
                        new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), MessageParser.NOT_SET, this.lPotSize, this.lBetAmount, this.lBankAmount);
				}
			}
			else
			{
				// invalid request when in the init phase
				this.model.getLogAndPublish().write(this.model.uniqueID + ": sent invalid request during Init phase", true, false);
			}
		}
		else if (prevGamePlayState.getPlayState() == GamePlayState.GET_HOLE)
		{			
			if (clientMsg.getGamePlayRequest() == MessageParser.GAME_PLAY_REQUEST_GET_FLOP)
			{
				// check to make sure the client's bet amount is twice the ante
				if (clientMsg.getBetAmount() == (2 * this.iAnte))
				{
					// this is a valid request so update the potsize, and bank amount
					this.lPotSize += clientMsg.getBetAmount();
					this.lBetAmount = clientMsg.getBetAmount();
					this.lBankAmount -= clientMsg.getBetAmount();
					// fill in the server message					
					serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), MessageParser.GAME_PLAY_RESPONSE_GET_FLOP_ACK, 
				            this.iAnte, oPlayerCards[0], oPlayerCards[1], new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), oCommunityCards[0], oCommunityCards[1], oCommunityCards[2], new Card(Card.NOT_SET, Card.NOT_SET), 
                        new Card(Card.NOT_SET, Card.NOT_SET), MessageParser.NOT_SET, this.lPotSize, this.lBetAmount, this.lBankAmount);
					
					// update latest request
					prevGamePlayState.setPlayState(GamePlayState.GET_FLOP);
					this.model.getLogAndPublish().write(this.model.uniqueID + ": has sent game play get flop cards message.", true, false);
					
				}
				else
				{
					// invalid request
					this.model.getLogAndPublish().write(this.model.uniqueID + ": has sent an invalid hole bet amount", true, false);
					// fill in the server message
					serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), MessageParser.GAME_PLAY_RESPONSE_INVALID_HOLE_BET, 
				            this.iAnte, oPlayerCards[0], oPlayerCards[1], new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), 
                        new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), MessageParser.NOT_SET, this.lPotSize, this.lBetAmount, this.lBankAmount);
				}
			}
			else if (clientMsg.getGamePlayRequest() == MessageParser.GAME_PLAY_REQUEST_FOLD)
			{
				// client has sent a fold request. ReInitialize the model and send back an acknowledgment
				Reset();
				this.model.getLogAndPublish().write(this.model.uniqueID + ": has sent a fold request", true, false);
				
				// fill in the server message				
				serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), MessageParser.GAME_PLAY_RESPONSE_FOLD_ACK, 
			            this.iAnte, new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), 
                     new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), 
                     MessageParser.WINNER_DEALER, this.lPotSize, this.lBetAmount, this.lBankAmount);
				
			}
			else
			{
				// invalid message in Hole phase
				this.model.getLogAndPublish().write(this.model.uniqueID + ": sent invalid request during Hole phase", true, false);
			}
		}
		else if (prevGamePlayState.getPlayState() == GamePlayState.GET_FLOP)
		{
			if (clientMsg.getGamePlayRequest() == MessageParser.GAME_PLAY_REQUEST_GET_TURN)
			{
				// check to make sure the client's bet amount is the ante or 0 (for a check) and they have that amount available
				if ((clientMsg.getBetAmount() == this.iAnte || clientMsg.getBetAmount() == 0) && (this.lBankAmount - clientMsg.getBetAmount() >= 0))
				{
					// this is a valid request so update the potsize, and bank amount
					this.lPotSize += clientMsg.getBetAmount();
					this.lBetAmount = clientMsg.getBetAmount();
					this.lBankAmount -= clientMsg.getBetAmount();
					// fill in the server message					
					serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), MessageParser.GAME_PLAY_RESPONSE_GET_TURN_ACK, 
				            this.iAnte, oPlayerCards[0], oPlayerCards[1], new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), oCommunityCards[0], oCommunityCards[1], oCommunityCards[2], 
                        oCommunityCards[3], new Card(Card.NOT_SET, Card.NOT_SET), MessageParser.NOT_SET, this.lPotSize, this.lBetAmount, this.lBankAmount);
					
					// update latest request
					prevGamePlayState.setPlayState(GamePlayState.GET_TURN);
					this.model.getLogAndPublish().write(this.model.uniqueID + ": has sent game play get turn card message.", true, false);
					
				}
				else
				{
					// invalid request
					this.model.getLogAndPublish().write(this.model.uniqueID + ": has sent an invalid turn bet amount", true, false);
					// fill in the server message					
					serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), MessageParser.GAME_PLAY_RESPONSE_INVALID_FLOP_BET, 
				            this.iAnte, oPlayerCards[0], oPlayerCards[1], new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), oCommunityCards[0], oCommunityCards[1], oCommunityCards[2], 
                        new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), MessageParser.NOT_SET, this.lPotSize, this.lBetAmount, this.lBankAmount);
				}
			}
			else if (clientMsg.getGamePlayRequest() == MessageParser.GAME_PLAY_REQUEST_FOLD)
			{
				// client has sent a fold request. ReInitialize the model and send back an acknowledgment
				Reset();
				this.model.getLogAndPublish().write(this.model.uniqueID + ": has sent a fold request", true, false);
				
				// fill in the server message		
				serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), MessageParser.GAME_PLAY_RESPONSE_FOLD_ACK, 
			            this.iAnte, new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), 
                     new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), MessageParser.WINNER_DEALER, this.lPotSize, this.lBetAmount, this.lBankAmount);
				
			}
			else
			{
				// invalid message in Hole phase
				this.model.getLogAndPublish().write(this.model.uniqueID + ": sent invalid request during Turn phase", true, false);
			}
		}
		else if (prevGamePlayState.getPlayState() == GamePlayState.GET_TURN)
		{
			if (clientMsg.getGamePlayRequest() == MessageParser.GAME_PLAY_REQUEST_GET_RIVER)
			{
				// check to make sure the client's bet amount is the ante or 0 (for a check) and they have that amount available
				if ((clientMsg.getBetAmount() == this.iAnte || clientMsg.getBetAmount() == 0) && (this.lBankAmount - clientMsg.getBetAmount() >= 0))
				{
					// this is a valid request so update the potsize, and bank amount
					this.lPotSize += clientMsg.getBetAmount();
					this.lBetAmount = clientMsg.getBetAmount();
					this.lBankAmount -= clientMsg.getBetAmount();
					// fill in the server message
					int winner = calculateWinner(oPlayerCards, oDealerCards, oCommunityCards);
					
					// update the client's bank amount if needed
					if (winner == MessageParser.WINNER_PLAYER)
					{
						this.lBankAmount += 2*this.lPotSize + this.iAnte;
					}
					else if (winner == MessageParser.WINNER_DRAW)
					{
						this.lBankAmount += this.lPotSize + this.iAnte;
					}
					
					serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), MessageParser.GAME_PLAY_RESPONSE_GET_RIVER_ACK, 
				            this.iAnte, oPlayerCards[0], oPlayerCards[1], oDealerCards[0], oDealerCards[1], oCommunityCards[0], oCommunityCards[1], oCommunityCards[2], oCommunityCards[3], oCommunityCards[4], winner, this.lPotSize, this.lBetAmount, this.lBankAmount);
					
					// update latest request
					this.model.getLogAndPublish().write(this.model.uniqueID + ": has sent game play get river card message.", true, false);
					// reinitialize the model for the next hand
					Reset();
					
				}
				else
				{
					// invalid request
					this.model.getLogAndPublish().write(this.model.uniqueID + ": has sent an invalid river bet amount", true, false);
					// fill in the server message					
					serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), MessageParser.GAME_PLAY_RESPONSE_INVALID_TURN_BET, 
				            this.iAnte, oPlayerCards[0], oPlayerCards[1], new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), oCommunityCards[0], oCommunityCards[1], oCommunityCards[2], oCommunityCards[3], 
                        new Card(Card.NOT_SET, Card.NOT_SET), MessageParser.NOT_SET, this.lPotSize, this.lBetAmount, this.lBankAmount);
				}
			}
			else if (clientMsg.getGamePlayRequest() == MessageParser.GAME_PLAY_REQUEST_FOLD)
			{
				// client has sent a fold request. ReInitialize the model and send back an acknowledgment
				Reset();
				this.model.getLogAndPublish().write(this.model.uniqueID + ": has sent a fold request", true, false);
				
				// fill in the server message				
				serverMsg = this.model.getMessageParser().new ServerPlayGameMessage(clientMsg.getVersion(), clientMsg.getTypeCode(), clientMsg.getGameIndicator(), clientMsg.getGameTypeCode(), MessageParser.GAME_PLAY_RESPONSE_FOLD_ACK, 
			            this.iAnte, new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), 
                     new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), new Card(Card.NOT_SET, Card.NOT_SET), MessageParser.WINNER_DEALER, this.lPotSize, this.lBetAmount, this.lBankAmount);
				
			}
			else
			{
				// invalid message in Hole phase
				this.model.getLogAndPublish().write(this.model.uniqueID + ": sent invalid request during River phase", true, false);
			}
		}
		else
		{
			// entered an invalid state
			// this is here just for validating the program
			this.model.getLogAndPublish().write(this.model.uniqueID + ": Error entered invalid Game Play Phase!", true, false);
		}
		return serverMsg;
	}
	/**
   * calculateWinner - This function takes the cards on the table and will use a hand evaluator object
   * to determine whether the dealer had the best hand, the player had the best hand, or it was a draw
   * @param Card[] playerCards
   * @param Card[] dealerCards
   * @param Card[] communityCards
   * @return int
   */
	private int calculateWinner(Card[] playerCards, Card[] dealerCards, Card[] communityCards)
	{
		int winner = MessageParser.NOT_SET;
		/* Get the player's hand which is the player's two cards plus the five community cards */
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
		/* Get the dealer's hand which is the dealer's two cards plus the five community cards */
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
		/* create the hand evaluator object and pass in the hands to be evaluated */
		TexasHoldemHandEval evaluator = new TexasHoldemHandEval();
		int compareHands = evaluator.compareHands(playerHand, dealerHand);
		
		/* determine the winner */
		if (compareHands > 0)
		{
			// Player won
			winner = MessageParser.WINNER_PLAYER;
		}
		else if (compareHands < 0)
		{
			// dealer won
			winner = MessageParser.WINNER_DEALER;
		}
		else
		{
			// draw
			winner = MessageParser.WINNER_DRAW;
		}
		
		return winner;
	}

}
