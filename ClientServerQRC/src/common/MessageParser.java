package common;

import java.util.ArrayList;
import java.util.List;
import common.card_game.Card;
import common.card_game.Card.CardSuite;
import common.card_game.Card.CardValue;

/* This class will be used by all classes to parse the incoming message or to create the outgoing messages
 * 
 */
public class MessageParser {
	
	/*
	 * This is the different possible type indicators
	 */
	public enum TypeIndicator{
		NOT_SET(0), VERSION(1), GAME(2), CLOSE_CONNECTION(3), CHALLENGE_CONNECTION(4);
		private int indicator;
		TypeIndicator(int indicator)
		{
			this.setIndicator(indicator);
		}
		public int getIndicator() {
			return indicator;
		}
		public void setIndicator(int indicator) {
			this.indicator = indicator;
		}
		public boolean isEqual(TypeIndicator rhs)
		{
			return (this.indicator == rhs.getIndicator());
		}
	}
	
   /*
   * This is the different possible version indicators
   */
	public enum VersionIndicator{
		NOT_SET(0), CLIENT_VERSION(1), VERSION_REQUIREMENT(2), VERSION_ACK(3), VERSION_UPGRADE(4);
		private int indicator;
		VersionIndicator(int indicator)
		{
			this.setIndicator(indicator);
		}
		public int getIndicator() {
			return indicator;
		}
		public void setIndicator(int indicator) {
			this.indicator = indicator;
		}
		public boolean isEqual(VersionIndicator rhs)
		{
			return (this.indicator == rhs.getIndicator());
		}
	}
	
   /*
   * This is the different possible game indicators
   */
	public enum GameIndicator{
		NOT_SET(0), GET_GAME(1), SET_GAME(2), PLAY_GAME(3);
		private int indicator;
		GameIndicator(int indicator)
		{
			this.setIndicator(indicator);
		}
		public int getIndicator() {
			return indicator;
		}
		public void setIndicator(int indicator) {
			this.indicator = indicator;
		}
		public boolean isEqual(GameIndicator rhs)
		{
			return (this.indicator == rhs.getIndicator());
		}
	}
   
   /*
   * These are the different possible game type codes
   */
   public enum GameTypeCode{
		NOT_SET(0), TEXAS_HOLDEM(1);
		private int gameTypeCode;
		GameTypeCode(int gameTypeCode)
		{
			this.setGameTypeCode(gameTypeCode);
		}
		public int getGameTypeCode() {
			return gameTypeCode;
		}
		public void setGameTypeCode(int gameTypeCode) {
			this.gameTypeCode = gameTypeCode;
		}
		public boolean isEqual(GameTypeCode rhs)
		{
			return (this.gameTypeCode == rhs.getGameTypeCode());
		}
	}
   
   /* 
   * These are the different possible game type responses
   */
   public enum GameTypeResponse{
		NOT_SET(0), ACK(1), INVALID(2);
		private int gameTypeResponse;
		GameTypeResponse(int gameTypeResponse)
		{
			this.setGameTypeResponse(gameTypeResponse);
		}
		public int getGameTypeResponse() {
			return gameTypeResponse;
		}
		public void setGameTypeResponse(int gameTypeResponse) {
			this.gameTypeResponse = gameTypeResponse;
		}
		public boolean isEqual(GameTypeResponse rhs)
		{
			return (this.gameTypeResponse == rhs.getGameTypeResponse());
		}
	}
   
   /*
   * These are the different possible game play requests
   */
   public enum GamePlayRequest{
      NOT_SET(0), INIT(1), GET_HOLE(2), GET_FLOP(3), GET_TURN(4), GET_RIVER(5), FOLD(6);
      private int gamePlayRequest;
		GamePlayRequest(int gamePlayRequest)
		{
			this.setGamePlayRequest(gamePlayRequest);
		}
		public int getGamePlayRequest() {
			return gamePlayRequest;
		}
		public void setGamePlayRequest(int gamePlayRequest) {
			this.gamePlayRequest = gamePlayRequest;
		}
		public boolean isEqual(GamePlayRequest rhs)
		{
			return (this.gamePlayRequest == rhs.getGamePlayRequest());
		}
	}
   
   /*
   * These are the different possible server game responses
   */
   public enum GamePlayResponse{
      NOT_SET(0), INIT_ACK(1), GET_HOLE_ACK(2), GET_FLOP_ACK(3), GET_TURN_ACK(4), GET_RIVER_ACK(5), FOLD_ACK(6),
      INVALID_ANTE_BET(7), INVALID_HOLE_BET(8), INVALID_FLOP_BET(9), INVALID_TURN_BET(10);
      private int gamePlayResponse;
		GamePlayResponse(int gamePlayResponse)
		{
			this.setGamePlayResponse(gamePlayResponse);
		}
		public int getGamePlayResponse() {
			return gamePlayResponse;
		}
		public void setGamePlayResponse(int gamePlayResponse) {
			this.gamePlayResponse = gamePlayResponse;
		}
		public boolean isEqual(GamePlayResponse rhs)
		{
			return (this.gamePlayResponse == rhs.getGamePlayResponse());
		}
	}
   
   /* Card Values and Suites are in Card.java */
         
   /*
   * These are the different winners for the game
   */
   public enum Winner{
      NOT_SET(0), DEALER(1), PLAYER(2), DRAW(3);
      private int winner;
		Winner(int winner)
		{
			this.setWinner(winner);
		}
		public int getWinner() {
			return winner;
		}
		public void setWinner(int winner) {
			this.winner = winner;
		}
		public boolean isEqual(Winner rhs)
		{
			return (this.winner == rhs.getWinner());
		}
	}
   
   /*
   * This indicates the possible connection indicators
   * It is currently used for closing the connection
   */
   public enum ConnectionIndicator{
      NOT_SET(0), CLOSE_CONNECTION(1), CLOSE_CONNECTION_ACK(2);
      private int conindicator;
		ConnectionIndicator(int conindicator)
		{
			this.setConnectionIndicator(conindicator);
		}
		public int getConnectionIndicator() {
			return conindicator;
		}
		public void setConnectionIndicator(int conindicator) {
			this.conindicator = conindicator;
		}
		public boolean isEqual(ConnectionIndicator rhs)
		{
			return (this.conindicator == rhs.getConnectionIndicator());
		}
	}
   
   /*
   * This indicates the possible challenge indicators
   */
   public enum ChallengeIndicator{
      NOT_SET(0), CHALLENGE_CONNECTION(1), CHALLENGE_CONNECTION_ACK(2);
      private int challndicator;
		ChallengeIndicator(int challndicator)
		{
			this.setChallengeIndicator(challndicator);
		}
		public int getChallengeIndicator() {
			return challndicator;
		}
		public void setChallengeIndicator(int challndicator) {
			this.challndicator = challndicator;
		}
		public boolean isEqual(ChallengeIndicator rhs)
		{
			return (this.challndicator == rhs.getChallengeIndicator());
		}
	}      
   
   /*
   * This class represents the message structure for the version message
   */
   public class VersionMessage
   {
      private int iVersion;
      private TypeIndicator eTypeCode;
      private VersionIndicator eVersionType;
      private short nMinorVersion;
      private long  lBankAmount;
      public VersionMessage(int version, TypeIndicator typecode, VersionIndicator versiontype, short minorversion, long bankamount)
      {
         this.iVersion = version;
         this.eTypeCode = typecode;
         this.eVersionType = versiontype;
         this.nMinorVersion = minorversion;
         this.lBankAmount = bankamount;
      }
      public void setVersion(int version)
      {
         this.iVersion = version;
      }
      public int getVersion()
      {
         return iVersion;
      }
      public void setTypeCode(TypeIndicator typecode)
      {
         this.eTypeCode = typecode;
      }
      public TypeIndicator getTypeCode()
      {
         return eTypeCode;
      }
      public void setVersionType(VersionIndicator versiontype)
      {
         this.eVersionType = versiontype;
      }
      public VersionIndicator getVersionType()
      {
         return eVersionType;
      }
      public void setMinorVersion(short minorversion)
      {
         this.nMinorVersion = minorversion;
      }
      public short getMinorVersion()
      {
         return nMinorVersion;
      }
      public void setBankAmount(long bankamount)
      {
         this.lBankAmount = bankamount;
      }
      public long getBankAmount()
      {
         return lBankAmount;
      }      
   }
   
   /*
   * This class represents the structure for the client
   * get game message
   */
   public class ClientGetGameMessage
   {
      private int iVersion;
      private TypeIndicator eTypeCode;
      private GameIndicator eGameIndicator;
      public ClientGetGameMessage(int version, TypeIndicator typecode, GameIndicator gameindicator)
      {
         this.iVersion = version;
         this.eTypeCode = typecode;
         this.eGameIndicator = gameindicator;
      }
      public void setVersion(int version)
      {
         this.iVersion = version;
      }
      public int getVersion()
      {
         return iVersion;
      }
      public void setTypeCode(TypeIndicator typecode)
      {
         this.eTypeCode = typecode;
      }
      public TypeIndicator getTypeCode()
      {
         return eTypeCode;
      }
      public void setGameIndicator(GameIndicator gameindicator)
      {
         this.eGameIndicator = gameindicator;
      }
      public GameIndicator getGameIndicator()
      {
         return eGameIndicator;
      }      
   }
   
   /*
   * This class represents the structure for the server
   * get game message
   */
   public class ServerGetGameMessage
   {
      private int iVersion;
      private TypeIndicator eTypeCode;
      private GameIndicator eGameIndicator;
      private long lMessageLength;
      private List<GameTypeCode> oGameTypeCodeList;
      public ServerGetGameMessage(int version, TypeIndicator typecode, GameIndicator gameindicator, long length, List<GameTypeCode> gametypecodelist)
      {
         this.iVersion = version;
         this.eTypeCode = typecode;
         this.eGameIndicator = gameindicator;
         this.lMessageLength = length;
         this.oGameTypeCodeList = gametypecodelist;
      }
      public void setVersion(int version)
      {
         this.iVersion = version;
      }
      public int getVersion()
      {
         return iVersion;
      }
      public void setTypeCode(TypeIndicator typecode)
      {
         this.eTypeCode = typecode;
      }
      public TypeIndicator getTypeCode()
      {
         return eTypeCode;
      }
      public void setGameIndicator(GameIndicator gameindicator)
      {
         this.eGameIndicator = gameindicator;
      }
      public GameIndicator getGameIndicator()
      {
         return eGameIndicator;
      }   
      public void setMessageLength(long length)
      {
         this.lMessageLength = length;
      }
      public long getMessageLength()
      {
         return lMessageLength;
      } 
      public void setGameTypeCodeList(List<GameTypeCode> gametypecodelist)
      {
         this.oGameTypeCodeList = gametypecodelist;
      }
      public List<GameTypeCode> getGameTypeCodeList()
      {
         return oGameTypeCodeList;
      } 
   }
   
   /*
   * This class represents the structure for the client
   * set game message
   */
   public class ClientSetGameMessage
   {
      private int iVersion;
      private TypeIndicator eTypeCode;
      private GameIndicator eGameIndicator;
      private GameTypeCode  eGameTypeCode;
      public ClientSetGameMessage(int version, TypeIndicator typecode, GameIndicator gameindicator, GameTypeCode gametypecode)
      {
         this.iVersion = version;
         this.eTypeCode = typecode;
         this.eGameIndicator = gameindicator;
         this.eGameTypeCode = gametypecode;
      }
      public void setVersion(int version)
      {
         this.iVersion = version;
      }
      public int getVersion()
      {
         return iVersion;
      }
      public void setTypeCode(TypeIndicator typecode)
      {
         this.eTypeCode = typecode;
      }
      public TypeIndicator getTypeCode()
      {
         return eTypeCode;
      }
      public void setGameIndicator(GameIndicator gameindicator)
      {
         this.eGameIndicator = gameindicator;
      }
      public GameIndicator getGameIndicator()
      {
         return eGameIndicator;
      }  
      public void setGameTypeCode(GameTypeCode gametypecode)
      {
         this.eGameTypeCode = gametypecode;
      }
      public GameTypeCode getGameTypeCode()
      {
         return eGameTypeCode;
      }
   }
   
   /*
   * This class represents the structure for the server
   * set game message
   */
   public class ServerSetGameMessage
   {
      private int iVersion;
      private TypeIndicator eTypeCode;
      private GameIndicator eGameIndicator;
      private GameTypeCode  eGameTypeCode;
      private GameTypeResponse eGameTypeResponse;
      public ServerSetGameMessage(int version, TypeIndicator typecode, GameIndicator gameindicator, GameTypeCode gametypecode, GameTypeResponse gametyperesponse)
      {
         this.iVersion = version;
         this.eTypeCode = typecode;
         this.eGameIndicator = gameindicator;
         this.eGameTypeCode = gametypecode;
         this.eGameTypeResponse = gametyperesponse;
      }
      public void setVersion(int version)
      {
         this.iVersion = version;
      }
      public int getVersion()
      {
         return iVersion;
      }
      public void setTypeCode(TypeIndicator typecode)
      {
         this.eTypeCode = typecode;
      }
      public TypeIndicator getTypeCode()
      {
         return eTypeCode;
      }
      public void setGameIndicator(GameIndicator gameindicator)
      {
         this.eGameIndicator = gameindicator;
      }
      public GameIndicator getGameIndicator()
      {
         return eGameIndicator;
      }  
      public void setGameTypeCode(GameTypeCode gametypecode)
      {
         this.eGameTypeCode = gametypecode;
      }
      public GameTypeCode getGameTypeCode()
      {
         return eGameTypeCode;
      }
      public void setGameTypeResponse(GameTypeResponse gametyperesponse)
      {
         this.eGameTypeResponse = gametyperesponse;
      }
      public GameTypeResponse getGameTypeResponse()
      {
         return eGameTypeResponse;
      }
   }
   
    /*
   * This class represents the structure for the client
   * play game message
   */
   public class ClientPlayGameMessage
   {
      private int iVersion;
      private TypeIndicator eTypeCode;
      private GameIndicator eGameIndicator;
      private GameTypeCode  eGameTypeCode;
      private GamePlayRequest eGamePlayRequest;
      private long lBetAmount;
      public ClientPlayGameMessage(int version, TypeIndicator typecode, GameIndicator gameindicator, GameTypeCode gametypecode, GamePlayRequest gameplayrequest, long betamount)
      {
         this.iVersion = version;
         this.eTypeCode = typecode;
         this.eGameIndicator = gameindicator;
         this.eGameTypeCode = gametypecode;
         this.eGamePlayRequest = gameplayrequest;
         this.lBetAmount = betamount;
      }
      public void setVersion(int version)
      {
         this.iVersion = version;
      }
      public int getVersion()
      {
         return iVersion;
      }
      public void setTypeCode(TypeIndicator typecode)
      {
         this.eTypeCode = typecode;
      }
      public TypeIndicator getTypeCode()
      {
         return eTypeCode;
      }
      public void setGameIndicator(GameIndicator gameindicator)
      {
         this.eGameIndicator = gameindicator;
      }
      public GameIndicator getGameIndicator()
      {
         return eGameIndicator;
      }  
      public void setGameTypeCode(GameTypeCode gametypecode)
      {
         this.eGameTypeCode = gametypecode;
      }
      public GameTypeCode getGameTypeCode()
      {
         return eGameTypeCode;
      }
      public void setGamePlayRequest(GamePlayRequest gameplayrequest)
      {
         this.eGamePlayRequest = gameplayrequest;
      }
      public GamePlayRequest getGamePlayRequest()
      {
         return eGamePlayRequest;
      }
      public void setBetAmount(long betamount)
      {
         this.lBetAmount = betamount;
      }
      public long getBetAmount()
      {
         return lBetAmount;
      }
   }
   
    /*
   * This class represents the structure for the server
   * play game message
   */
   public class ServerPlayGameMessage
   {
      private int iVersion;
      private TypeIndicator eTypeCode;
      private GameIndicator eGameIndicator;
      private GameTypeCode  eGameTypeCode;
      private GamePlayResponse eGamePlayResponse;
      private int iAnte;
      private Card ePlayerCard1;
      private Card ePlayerCard2;
      private Card eDealerCard1;
      private Card eDealerCard2;
      private Card eFlopCard1;
      private Card eFlopCard2;
      private Card eFlopCard3;
      private Card eTurnCard;
      private Card eRiverCard;
      private Winner eWinner;
      private long lPotSize;
      private long lBetAmount;
      private long lBankAmount;
      public ServerPlayGameMessage(int version, TypeIndicator typecode, GameIndicator gameindicator, GameTypeCode gametypecode, GamePlayResponse gameplayresponse, 
            int ante, Card p1, Card p2, Card d1, Card d2, Card f1, Card f2, Card f3, Card turn, Card river, Winner winner, long potsize, long betamount, long bankamount)
      {
         this.iVersion = version;
         this.eTypeCode = typecode;
         this.eGameIndicator = gameindicator;
         this.eGameTypeCode = gametypecode;
         this.eGamePlayResponse = gameplayresponse;
         this.iAnte = ante;
         this.ePlayerCard1 = p1;
         this.ePlayerCard2 = p2;
         this.eDealerCard1 = d1;
         this.eDealerCard2 = d2;
         this.eFlopCard1 = f1;
         this.eFlopCard2 = f2;
         this.eFlopCard3 = f3;
         this.eTurnCard = turn;
         this.eRiverCard = river;
         this.eWinner = winner;
         this.lPotSize = potsize;
         this.lBetAmount = betamount;
         this.lBankAmount = bankamount;
      }
      public void setVersion(int version)
      {
         this.iVersion = version;
      }
      public int getVersion()
      {
         return iVersion;
      }
      public void setTypeCode(TypeIndicator typecode)
      {
         this.eTypeCode = typecode;
      }
      public TypeIndicator getTypeCode()
      {
         return eTypeCode;
      }
      public void setGameIndicator(GameIndicator gameindicator)
      {
         this.eGameIndicator = gameindicator;
      }
      public GameIndicator getGameIndicator()
      {
         return eGameIndicator;
      }  
      public void setGameTypeCode(GameTypeCode gametypecode)
      {
         this.eGameTypeCode = gametypecode;
      }
      public GameTypeCode getGameTypeCode()
      {
         return eGameTypeCode;
      }
      public void setGamePlayResponse(GamePlayResponse gameplayresponse)
      {
         this.eGamePlayResponse = gameplayresponse;
      }
      public GamePlayResponse getGamePlayResponse()
      {
         return eGamePlayResponse;
      }
      public void setAnte(int ante)
      {
         this.iAnte = ante;
      }
      public int getAnte()
      {
         return iAnte;
      }
      public void setPlayerCard1(Card card)
      {
         this.ePlayerCard1 = card;
      }
      public Card getPlayerCard1()
      {
         return ePlayerCard1;
      }
      public void setPlayerCard2(Card card)
      {
         this.ePlayerCard2 = card;
      }
      public Card getPlayerCard2()
      {
         return ePlayerCard2;
      }
      public void setDealerCard1(Card card)
      {
         this.eDealerCard1 = card;
      }
      public Card getDealerCard1()
      {
         return eDealerCard1;
      }
      public void setDealerCard2(Card card)
      {
         this.eDealerCard2 = card;
      }
      public Card getDealerCard2()
      {
         return eDealerCard2;
      }
      public void setFlopCard1(Card card)
      {
         this.eFlopCard1 = card;
      }
      public Card getFlopCard1()
      {
         return eFlopCard1;
      }
      public void setFlopCard2(Card card)
      {
         this.eFlopCard2 = card;
      }
      public Card getFlopCard2()
      {
         return eFlopCard2;
      }
      public void setFlopCard3(Card card)
      {
         this.eFlopCard3 = card;
      }
      public Card getFlopCard3()
      {
         return eFlopCard3;
      }
      public void setTurnCard(Card card)
      {
         this.eTurnCard = card;
      }
      public Card getTurnCard()
      {
         return eTurnCard;
      }
      public void setRiverCard(Card card)
      {
         this.eRiverCard = card;
      }
      public Card getRiverCard()
      {
         return eRiverCard;
      }
      public void setWinner(Winner winner)
      {
         this.eWinner = winner;
      }
      public Winner getWinner()
      {
         return eWinner;
      }
      public void setPotSize(long potsize)
      {
         this.lPotSize = potsize;
      }
      public long getPotSize()
      {
         return lPotSize;
      }
      public void setBetAmount(long betamount)
      {
         this.lBetAmount = betamount;
      }
      public long getBetAmount()
      {
         return lBetAmount;
      }
      public void setBankAmount(long bankamount)
      {
         this.lBankAmount = bankamount;
      }
      public long getBankAmount()
      {
         return lBankAmount;
      }
   }
   
   /*
   * This class is for Connection Message
   */
   public class ConnectionMessage
   {
      private int iVersion;
      private TypeIndicator eTypeCode;
      private ConnectionIndicator eConnectionCode;
      public ConnectionMessage(int version, TypeIndicator typecode, ConnectionIndicator concode)
      {
         this.iVersion = version;
         this.eTypeCode = typecode;
         this.eConnectionCode = concode;
      }
      public void setVersion(int version)
      {
         this.iVersion = version;
      }
      public int getVersion()
      {
         return iVersion;
      }
      public void setTypeCode(TypeIndicator typecode)
      {
         this.eTypeCode = typecode;
      }
      public TypeIndicator getTypeCode()
      {
         return eTypeCode;
      }
      public void setConnectionCode(ConnectionIndicator concode)
      {
         this.eConnectionCode = concode;
      }
      public ConnectionIndicator getConnectionCode()
      {
         return eConnectionCode;
      }
   }
   
   /*
   * This class is for the Challenge Message
   */
   public class ChallengeMessage
   {
      private int iVersion;
      private TypeIndicator eTypeCode;
      private ChallengeIndicator eChallengeCode;
      public ChallengeMessage(int version, TypeIndicator typecode, ChallengeIndicator chalcode)
      {
         this.iVersion = version;
         this.eTypeCode = typecode;
         this.eChallengeCode = chalcode;
      }
      public void setVersion(int version)
      {
         this.iVersion = version;
      }
      public int getVersion()
      {
         return iVersion;
      }
      public void setTypeCode(TypeIndicator typecode)
      {
         this.eTypeCode = typecode;
      }
      public TypeIndicator getTypeCode()
      {
         return eTypeCode;
      }
      public void setChallengeCode(ChallengeIndicator chalcode)
      {
         this.eChallengeCode = chalcode;
      }
      public ChallengeIndicator getChallengeCode()
      {
         return eChallengeCode;
      }
   }
      
      
	/*
   * This function will return the version of the message
   * It will be -1 if the message is not the correct size
   */
	public int GetVersion(byte[] buffer, int iSize)
	{
		int version = -1;
		if (iSize >= 2)
		{
			version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
		}
		return version;
	}
	
   /* 
   * This function will return the type indicator of the message
   * It will be -1 if the message is not the correct size
   */  
	public TypeIndicator GetTypeIndicator(byte[] buffer, int iSize)
	{
		TypeIndicator Indicator = TypeIndicator.NOT_SET;
		if (iSize >= 4)
		{
			Indicator.setIndicator((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
			return Indicator;
		}
      return Indicator;  
	}
	
	/* 
	 * Function will get the version message
    * Values will be -1 if the message is not the correct type
	 */
	public VersionMessage GetVersionMessage(byte[] buffer, int iSize)
	{
		int version = -1;
		TypeIndicator indicator = TypeIndicator.NOT_SET;
		VersionIndicator verType = VersionIndicator.NOT_SET;
		short minorversion = -1;
		long bankamount = -1;
		if (iSize == 12)
		{
			version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
			indicator.setIndicator((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
			verType.setIndicator((short)(buffer[4] & 0xFF));
			minorversion = (short)(buffer[5] & 0xFF);
			short byte1 = (short)(buffer[8] & 0xFF);
			short byte2 = (short)(buffer[9] & 0xFF);
			short byte3 = (short)(buffer[10] & 0xFF);
			short byte4 = (short)(buffer[11] & 0xFF);
			bankamount = (long)(((byte1 << 24) |
							(byte2 << 16) |
							(byte3 << 8) |
							byte4) & 0xFFFFFFFF);
		}
		VersionMessage message = new VersionMessage(version, indicator, verType, minorversion, bankamount);
		return message;
	}
	
	/* 
   * This function will create the version message 
   */
	public byte[] CreateVersionMessage(VersionMessage message)
	{
		byte[] buffer = new byte[12];
		buffer[0] = (byte)((message.getVersion() & 0xFF00) >> 8);
		buffer[1] = (byte)(message.getVersion() & 0xFF);
		buffer[2] = (byte)((message.getTypeCode().getIndicator() & 0xFF00) >> 8);
		buffer[3] = (byte)(message.getTypeCode().getIndicator() & 0xFF);
		buffer[4] = (byte)(message.getVersionType().getIndicator() & 0xFF);
		buffer[5] = (byte)(message.getMinorVersion() & 0xFF);
		buffer[6] = 0;
		buffer[7] = 0;
		buffer[8] = (byte)((message.getBankAmount() & 0xFF000000) >> 24);
		buffer[9] = (byte)((message.getBankAmount() & 0xFF0000) >> 16);
		buffer[10] = (byte)((message.getBankAmount() & 0xFF00) >> 8);
		buffer[11] = (byte)(message.getBankAmount() & 0xFF);
		return buffer;
	}
	
	/*
	 * Function to get the GameIndicator
    * Value will be -1 if the message is not the right size
	 */
	public GameIndicator GetGameIndicator(byte[] buffer, int iSize)
	{
		GameIndicator indicator = GameIndicator.NOT_SET;
		if (iSize >= 5)
		{
			indicator.setIndicator((short)(buffer[4] & 0xFF));
		}
		return indicator;
	}
   
   /*
   * Function to get the client get-game message
   * Values will be -1 if the message is incorrect
   */
   public ClientGetGameMessage GetClientGetGameMessage(byte[] buffer, int iSize)
   {
	   int version = -1;
	   TypeIndicator typecode = TypeIndicator.NOT_SET;
	   GameIndicator gameindicator = GameIndicator.NOT_SET;
      if (iSize == 8)
      {
         version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
         typecode.setIndicator((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
         gameindicator.setIndicator((short)(buffer[4] & 0xFF));
      }
      
      ClientGetGameMessage message = new ClientGetGameMessage(version, typecode, gameindicator);
      return message;
   }
   
   /* 
   * Function to create the client get-game message
   */
   public byte[] CreateClientGetGameMessage(ClientGetGameMessage message)
   {
      byte[] buffer = new byte[8];
		buffer[0] = (byte)((message.getVersion() & 0xFF00) >> 8);
		buffer[1] = (byte)(message.getVersion() & 0xFF);
		buffer[2] = (byte)((message.getTypeCode().getIndicator() & 0xFF00) >> 8);
		buffer[3] = (byte)(message.getTypeCode().getIndicator() & 0xFF);
		buffer[4] = (byte)(message.getGameIndicator().getIndicator() & 0xFF);
		buffer[5] = 0;
		buffer[6] = 0;
		buffer[7] = 0;
		return buffer;
   }
	
	/*
	 * Function to get the server get game message
	 */
    public ServerGetGameMessage GetServerGetGameMessage(byte[] buffer, int iSize)
    {
    	int version = -1;
    	TypeIndicator typecode = TypeIndicator.NOT_SET;
    	GameIndicator gameindicator = GameIndicator.NOT_SET;
    	long length = -1;
    	GameTypeCode eGameCode = GameTypeCode.NOT_SET;
    	List<GameTypeCode> GameTypeCodes = new ArrayList<GameTypeCode>();
    	if ((iSize >= 16) && ((iSize % 4) == 0))
    	{
    		version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
    		typecode.setIndicator((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
    		gameindicator.setIndicator((short)(buffer[4] & 0xFF));
            byte byte1 = (byte)(buffer[8] & 0xFF);
            byte byte2 = (byte)(buffer[9] & 0xFF);
            byte byte3 = (byte)(buffer[10] & 0xFF);
            byte byte4 = (byte)(buffer[11] & 0xFF);
            length = (long)(((byte1 << 24) |
						    (byte2 << 16) |
						    (byte3 << 8)  |
						    (byte4)) & 0xFFFFFFFF);
            if (length != iSize)
            {
               length = (long)iSize;
            }
            for (int iI = 12; iI < length; iI++)
            {
               short gameCode = (short)(buffer[iI] & 0xFF);
               if (gameCode > 0)
               {
            	   eGameCode.setGameTypeCode(gameCode);
            	   GameTypeCodes.add(eGameCode);
               }
            }
         }
        ServerGetGameMessage message = new ServerGetGameMessage(version, typecode, gameindicator, length, GameTypeCodes);
        return message;
    }
	
	/*
	 * Function to create the server Get game message
	 */
	public byte[] CreateServerGetGameMessage(ServerGetGameMessage message)
	{
		int gameCodeLen = ((int)Math.ceil(message.getGameTypeCodeList().size() / 4.0) * 4);
		int length = gameCodeLen + 12;
		long uilength = (long)length;
		byte[] buffer = new byte[length];
		buffer[0] = (byte)((message.getVersion() & 0xFF00) >> 8);
		buffer[1] = (byte)(message.getVersion() & 0xFF);
		buffer[2] = (byte)((message.getTypeCode().getIndicator() & 0xFF00) >> 8);
		buffer[3] = (byte)(message.getTypeCode().getIndicator() & 0xFF);
		buffer[4] = (byte)(message.getGameIndicator().getIndicator() & 0xFF);
		buffer[5] = 0;
		buffer[6] = 0;
		buffer[7] = 0;
		buffer[8] = (byte)((uilength & 0xFF000000) >> 24);
		buffer[9] = (byte)((uilength & 0xFF0000) >> 16);
		buffer[10] = (byte)((uilength & 0xFF00) >> 8);
		buffer[11] = (byte)((uilength & 0xFF));
		for (int iI = 12; iI < uilength; iI++)
		{
			if (iI < (12 + message.getGameTypeCodeList().size()))
			{
				GameTypeCode gameCode = message.getGameTypeCodeList().get((iI-12));
				buffer[iI] = (byte)((short)gameCode.getGameTypeCode() & 0xFF);
			}
			else
			{
				buffer[iI] = 0;
			}
		}
		return buffer;
	}
	
	/*
	 * Function to get the Client Set Game Message
	 */
	public ClientSetGameMessage GetClientSetGameMessage(byte[] buffer, int iSize)
	{
		int version = -1;
		TypeIndicator typecode = TypeIndicator.NOT_SET;
		GameIndicator gameindicator = GameIndicator.NOT_SET;
		GameTypeCode gamecode = GameTypeCode.NOT_SET;
		if (iSize == 8)
		{
			version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
    		typecode.setIndicator((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
    		gameindicator.setIndicator((short)(buffer[4] & 0xFF));
    		gamecode.setGameTypeCode((short)(buffer[5] & 0xFF));
		}
		
		ClientSetGameMessage message = new ClientSetGameMessage(version, typecode, gameindicator, gamecode);
		return message;
	}
	
	/*
	 *  Function to create the Client Set Game Message
	 */
	public byte[] CreateClientSetGameMessage(ClientSetGameMessage message)
	{
		byte[] buffer = new byte[8];
		buffer[0] = (byte)((message.getVersion() & 0xFF00) >> 8);
		buffer[1] = (byte)(message.getVersion() & 0xFF);
		buffer[2] = (byte)((message.getTypeCode().getIndicator() & 0xFF00) >> 8);
		buffer[3] = (byte)(message.getTypeCode().getIndicator() & 0xFF);
		buffer[4] = (byte)(message.getGameIndicator().getIndicator() & 0xFF);
		buffer[5] = (byte)(message.getGameTypeCode().getGameTypeCode() & 0xFF);
		buffer[6] = 0;
		buffer[7] = 0;
		
		return buffer;
	}
    
    /*
     * Function to get the Server Set Game Message
     */
	public ServerSetGameMessage GetServerSetGameMessage(byte[] buffer, int iSize)
	{
		int version = -1;
		TypeIndicator typecode = TypeIndicator.NOT_SET;
		GameIndicator gameindicator = GameIndicator.NOT_SET;
		GameTypeCode gamecode = GameTypeCode.NOT_SET;
		GameTypeResponse gameresponse = GameTypeResponse.NOT_SET;
		if (iSize == 8)
		{
			version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
    		typecode.setIndicator((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
    		gameindicator.setIndicator((short)(buffer[4] & 0xFF));
    		gamecode.setGameTypeCode((short)(buffer[5] & 0xFF));
    		gameresponse.setGameTypeResponse((short)(buffer[6] & 0xFF));
		}
		
		ServerSetGameMessage message = new ServerSetGameMessage(version, typecode, gameindicator, gamecode, gameresponse);
		return message;
	}
	
	/*
	 *  Function to create the Server Set Game Message
	 */
	public byte[] CreateServerSetGameMessage(ServerSetGameMessage message)
	{
		byte[] buffer = new byte[8];
		buffer[0] = (byte)((message.getVersion() & 0xFF00) >> 8);
		buffer[1] = (byte)(message.getVersion() & 0xFF);
		buffer[2] = (byte)((message.getTypeCode().getIndicator() & 0xFF00) >> 8);
		buffer[3] = (byte)(message.getTypeCode().getIndicator() & 0xFF);
		buffer[4] = (byte)(message.getGameIndicator().getIndicator() & 0xFF);
		buffer[5] = (byte)(message.getGameTypeCode().getGameTypeCode() & 0xFF);
		buffer[6] = (byte)(message.getGameTypeResponse().getGameTypeResponse() & 0xFF);
		buffer[7] = 0;
		
		return buffer;
	}
	
	/*
	 * Function to get the Client Play Game Message
	 */
	public ClientPlayGameMessage GetClientPlayGameMessage(byte[] buffer, int iSize)
	{
		int version = -1;
		TypeIndicator typecode = TypeIndicator.NOT_SET;
		GameIndicator gameindicator = GameIndicator.NOT_SET;
		GameTypeCode gamecode = GameTypeCode.NOT_SET;
		GamePlayRequest gamerequest = GamePlayRequest.NOT_SET;
		long betamount = -1;
		if (iSize == 12)
		{
			version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
    		typecode.setIndicator((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
    		gameindicator.setIndicator((short)(buffer[4] & 0xFF));
    		gamecode.setGameTypeCode((short)(buffer[5] & 0xFF));
    		gamerequest.setGamePlayRequest((short)(buffer[6] & 0xFF));
    		byte byte1 = (byte)(buffer[8] & 0xFF);
    		byte byte2 = (byte)(buffer[9] & 0xFF);
    		byte byte3 = (byte)(buffer[10] & 0xFF);
    		byte byte4 = (byte)(buffer[11] & 0xFF);
    		betamount = (long)(((byte1 << 24) & 0xFF000000) |
    						   ((byte2 << 16) & 0xFF0000) |
    						   ((byte3 << 8) & 0xFF00) |
    						   (byte4));
		}
		
		ClientPlayGameMessage message = new ClientPlayGameMessage(version, typecode, gameindicator, gamecode, gamerequest, betamount);
		return message;
	}
	
	/*
	 *  Function to create the Client Play Game Message
	 */
	public byte[] CreateClientPlayGameMessage(ClientPlayGameMessage message)
	{
		byte[] buffer = new byte[12];
		buffer[0] = (byte)((message.getVersion() & 0xFF00) >> 8);
		buffer[1] = (byte)(message.getVersion() & 0xFF);
		buffer[2] = (byte)((message.getTypeCode().getIndicator() & 0xFF00) >> 8);
		buffer[3] = (byte)(message.getTypeCode().getIndicator() & 0xFF);
		buffer[4] = (byte)(message.getGameIndicator().getIndicator() & 0xFF);
		buffer[5] = (byte)(message.getGameTypeCode().getGameTypeCode() & 0xFF);
		buffer[6] = (byte)(message.getGamePlayRequest().getGamePlayRequest() & 0xFF);
		buffer[7] = 0;
		buffer[8] = (byte)((message.getBetAmount() & 0xFF000000) >> 24);
		buffer[9] = (byte)((message.getBetAmount() & 0xFF0000) >> 16);
		buffer[10] = (byte)((message.getBetAmount() & 0xFF00) >> 8);
		buffer[11] = (byte)(message.getBetAmount() & 0xFF);
		
		return buffer;
	}
	
	/*
	 *  Function to get the server play game message
	 */
	public ServerPlayGameMessage GetServerPlayGameMessage(byte[] buffer, int iSize)
	{
		int version = -1;
		TypeIndicator typecode = TypeIndicator.NOT_SET;
		GameIndicator gameindicator = GameIndicator.NOT_SET;
		GameTypeCode gamecode = GameTypeCode.NOT_SET;
		GamePlayResponse gameresponse = GamePlayResponse.NOT_SET;
		int ante = -1;
		Card p1 = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
		Card p2 = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
		Card d1 = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
		Card d2 = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
		Card f1 = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
		Card f2 = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
		Card f3 = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
		Card t1 = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
		Card r1 = new Card(CardSuite.NOT_SET, CardValue.NOT_SET);
		Winner winner = Winner.NOT_SET;
		long potsize = -1;
		long betamount = -1;
		long bankamount = -1;
		CardSuite suite = CardSuite.NOT_SET;
		CardValue value = CardValue.NOT_SET;
		if (iSize == 32)
		{
			version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
    		typecode.setIndicator((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
    		gameindicator.setIndicator((short)(buffer[4] & 0xFF));
    		gamecode.setGameTypeCode((short)(buffer[5] & 0xFF));
    		gameresponse.setGamePlayResponse((short)(buffer[6] & 0xFF));
    		ante = (int)(((buffer[8] << 8) | buffer[9]) & 0xFFFF);
    		suite.setCardSuite((buffer[10] & 0xF0) >> 4);
    		p1.setCardSuite(suite);
    		value.setCardValue(buffer[10] & 0x0F);
    		p1.setCardValue(value);
    		suite.setCardSuite((buffer[11] & 0xF0) >> 4);
    		p2.setCardSuite(suite);
    		value.setCardValue(buffer[11] & 0x0F);
    		p2.setCardValue(value);
    		suite.setCardSuite((buffer[12] & 0xF0) >> 4);
    		d1.setCardSuite(suite);
    		value.setCardValue(buffer[12] & 0x0F);
    		d1.setCardValue(value);
    		suite.setCardSuite((buffer[13] & 0xF0) >> 4);
    		d2.setCardSuite(suite);
    		value.setCardValue(buffer[13] & 0x0F);
    		d2.setCardValue(value);
    		suite.setCardSuite((buffer[14] & 0xF0) >> 4);
    		f1.setCardSuite(suite);
    		value.setCardValue(buffer[14] & 0x0F);
    		f1.setCardValue(value);
    		suite.setCardSuite((buffer[15] & 0xF0) >> 4);
    		f2.setCardSuite(suite);
    		value.setCardValue(buffer[15] & 0x0F);
    		f2.setCardValue(value);
    		suite.setCardSuite((buffer[16] & 0xF0) >> 4);
    		f3.setCardSuite(suite);
    		value.setCardValue(buffer[16] & 0x0F);
    		f3.setCardValue(value);
    		suite.setCardSuite((buffer[17] & 0xF0) >> 4);
    		t1.setCardSuite(suite);
    		value.setCardValue(buffer[17] & 0x0F);
    		t1.setCardValue(value);
    		suite.setCardSuite((buffer[18] & 0xF0) >> 4);
    		r1.setCardSuite(suite);
    		value.setCardValue(buffer[18] & 0x0F);
    		r1.setCardValue(value);
    		winner.setWinner((buffer[19] & 0xF0) >> 4);
    		byte byte1 = (byte)(buffer[20] & 0xFF);
    		byte byte2 = (byte)(buffer[21] & 0xFF);
    		byte byte3 = (byte)(buffer[22] & 0xFF);
    		byte byte4 = (byte)(buffer[23] & 0xFF);
    		potsize = (long)(((byte1 << 24) & 0xFF000000) |
    						   ((byte2 << 16) & 0xFF0000) |
    						   ((byte3 << 8) & 0xFF00) |
    						   (byte4));
    		byte1 = (byte)(buffer[24] & 0xFF);
    		byte2 = (byte)(buffer[25] & 0xFF);
    		byte3 = (byte)(buffer[26] & 0xFF);
    		byte4 = (byte)(buffer[27] & 0xFF);
    		betamount = (long)(((byte1 << 24) & 0xFF000000) |
    						   ((byte2 << 16) & 0xFF0000) |
    						   ((byte3 << 8) & 0xFF00) |
    						   (byte4));
    		byte1 = (byte)(buffer[28] & 0xFF);
    		byte2 = (byte)(buffer[29] & 0xFF);
    		byte3 = (byte)(buffer[30] & 0xFF);
    		byte4 = (byte)(buffer[31] & 0xFF);
    		bankamount = (long)(((byte1 << 24) & 0xFF000000) |
    						   ((byte2 << 16) & 0xFF0000) |
    						   ((byte3 << 8) & 0xFF00) |
    						   (byte4));
		}
		
		ServerPlayGameMessage message = new ServerPlayGameMessage(version, typecode, gameindicator, gamecode, gameresponse, 
				ante, p1, p2, d1, d2, f1, f2, f3, t1, r1, winner, potsize, betamount, bankamount);
		return message;
	}
	
	/*
	 *  Function to create the Server Play Game Message
	 */
	public byte[] CreateServerPlayGameMessage(ServerPlayGameMessage message)
	{
		byte[] buffer = new byte[32];
		buffer[0] = (byte)((message.getVersion() & 0xFF00) >> 8);
		buffer[1] = (byte)(message.getVersion() & 0xFF);
		buffer[2] = (byte)((message.getTypeCode().getIndicator() & 0xFF00) >> 8);
		buffer[3] = (byte)(message.getTypeCode().getIndicator() & 0xFF);
		buffer[4] = (byte)(message.getGameIndicator().getIndicator() & 0xFF);
		buffer[5] = (byte)(message.getGameTypeCode().getGameTypeCode() & 0xFF);
		buffer[6] = (byte)(message.getGamePlayResponse().getGamePlayResponse() & 0xFF);
		buffer[7] = 0;
		buffer[8] = (byte)((message.getAnte() & 0xFF00) >> 8);
		buffer[9] = (byte)(message.getAnte() & 0xFF);
		buffer[10] = (byte)(((message.getPlayerCard1().getCardSuite().getCardSuite() & 0x0F) << 4) | (message.getPlayerCard1().getCardValue().getCardValue() & 0x0F)); 
		buffer[11] = (byte)(((message.getPlayerCard2().getCardSuite().getCardSuite() & 0x0F) << 4) | (message.getPlayerCard2().getCardValue().getCardValue() & 0x0F));
		buffer[12] = (byte)(((message.getDealerCard1().getCardSuite().getCardSuite() & 0x0F) << 4) | (message.getDealerCard1().getCardValue().getCardValue() & 0x0F)); 
		buffer[13] = (byte)(((message.getDealerCard2().getCardSuite().getCardSuite() & 0x0F) << 4) | (message.getDealerCard2().getCardValue().getCardValue() & 0x0F));
		buffer[14] = (byte)(((message.getFlopCard1().getCardSuite().getCardSuite() & 0x0F) << 4) | (message.getFlopCard1().getCardValue().getCardValue() & 0x0F));
		buffer[15] = (byte)(((message.getFlopCard2().getCardSuite().getCardSuite() & 0x0F) << 4) | (message.getFlopCard2().getCardValue().getCardValue() & 0x0F));
		buffer[16] = (byte)(((message.getFlopCard3().getCardSuite().getCardSuite() & 0x0F) << 4) | (message.getFlopCard3().getCardValue().getCardValue() & 0x0F));
		buffer[17] = (byte)(((message.getTurnCard().getCardSuite().getCardSuite() & 0x0F) << 4) | (message.getTurnCard().getCardValue().getCardValue() & 0x0F));
		buffer[18] = (byte)(((message.getRiverCard().getCardSuite().getCardSuite() & 0x0F) << 4) | (message.getRiverCard().getCardValue().getCardValue() & 0x0F));
		buffer[19] = (byte)(((message.getWinner().getWinner() & 0x0F) << 4) & 0xF0);
		buffer[20] = (byte)((message.getPotSize() & 0xFF000000) >> 24);
		buffer[21] = (byte)((message.getPotSize() & 0xFF0000) >> 16);
		buffer[22] = (byte)((message.getPotSize() & 0xFF00) >> 8);
		buffer[23] = (byte)(message.getPotSize() & 0xFF);
		buffer[24] = (byte)((message.getBetAmount() & 0xFF000000) >> 24);
		buffer[25] = (byte)((message.getBetAmount() & 0xFF0000) >> 16);
		buffer[26] = (byte)((message.getBetAmount() & 0xFF00) >> 8);
		buffer[27] = (byte)(message.getBetAmount() & 0xFF);
		buffer[28] = (byte)((message.getBankAmount() & 0xFF000000) >> 24);
		buffer[29] = (byte)((message.getBankAmount() & 0xFF0000) >> 16);
		buffer[30] = (byte)((message.getBankAmount() & 0xFF00) >> 8);
		buffer[31] = (byte)(message.getBankAmount() & 0xFF);
		
		return buffer;
	}
	
	/*
	 * Function to get the Connection Message
	 */
	public ConnectionMessage GetConnectionMessage(byte[] buffer, int iSize)
	{
		int version = -1;
		TypeIndicator typecode = TypeIndicator.NOT_SET;
		ConnectionIndicator concode = ConnectionIndicator.NOT_SET;
		if (iSize == 8)
		{
			version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
    		typecode.setIndicator((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
    		concode.setConnectionIndicator((buffer[4] & 0xFF));
		}
		
		ConnectionMessage message = new ConnectionMessage(version, typecode, concode);
		return message;
	}
	
	/*
	 *  Function to create the Connection Message
	 */
	public byte[] CreateConnectionMessage(ConnectionMessage message)
	{
		byte[] buffer = new byte[8];
		buffer[0] = (byte)((message.getVersion() & 0xFF00) >> 8);
		buffer[1] = (byte)(message.getVersion() & 0xFF);
		buffer[2] = (byte)((message.getTypeCode().getIndicator() & 0xFF00) >> 8);
		buffer[3] = (byte)(message.getTypeCode().getIndicator() & 0xFF);
		buffer[4] = (byte)(message.getConnectionCode().getConnectionIndicator() & 0xFF);
		buffer[5] = 0;
		buffer[6] = 0;
		buffer[7] = 0;
		
		return buffer;
	}
	
	/*
	 * Function to get the Challenge Message
	 */
	public ChallengeMessage GetChallengeMessage(byte[] buffer, int iSize)
	{
		int version = -1;
		TypeIndicator typecode = TypeIndicator.NOT_SET;
		ChallengeIndicator challcode = ChallengeIndicator.NOT_SET;
		if (iSize == 8)
		{
			version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
    		typecode.setIndicator((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
    		challcode.setChallengeIndicator((buffer[4] & 0xFF));
		}
		
		ChallengeMessage message = new ChallengeMessage(version, typecode, challcode);
		return message;
	}
	
	/*
	 *  Function to create the Challenge Message
	 */
	public byte[] CreateChallengeMessage(ChallengeMessage message)
	{
		byte[] buffer = new byte[8];
		buffer[0] = (byte)((message.getVersion() & 0xFF00) >> 8);
		buffer[1] = (byte)(message.getVersion() & 0xFF);
		buffer[2] = (byte)((message.getTypeCode().getIndicator() & 0xFF00) >> 8);
		buffer[3] = (byte)(message.getTypeCode().getIndicator() & 0xFF);
		buffer[4] = (byte)(message.getChallengeCode().getChallengeIndicator() & 0xFF);
		buffer[5] = 0;
		buffer[6] = 0;
		buffer[7] = 0;
		
		return buffer;
	}

}
