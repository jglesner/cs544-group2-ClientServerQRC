package common;

import java.util.ArrayList;
import common.card_game.Card;

/* This class will be used by all classes to parse the incoming message or to create the outgoing messages
 * 
 */
public class MessageParser {
	
	/*
	 * This is the different possible indicators for the messages
	 */
	public static int NOT_SET = 0;
	/*
	 * These are the valid type indicators
	 */
	public static int TYPE_INDICATOR_VERSION = 1;
	public static int TYPE_INDICATOR_GAME = 2;
	public static int TYPE_INDICATOR_CLOSE_CONNECTION = 3;
	/*
	 * These are the valid version indicators
	 */
	public static int VERSION_INDICATOR_CLIENT_VERSION = 1;
	public static int VERSION_INDICATOR_VERSION_REQUIREMENT = 2;
	public static int VERSION_INDICATOR_VERSION_ACK = 3;
	public static int VERSION_INDICATOR_VERSION_UPGRADE = 4;
	/*
	 * These are the valid game indicators
	 */
	public static int GAME_INDICATOR_GET_GAME = 1;
	public static int GAME_INDICATOR_SET_GAME = 2;
	public static int GAME_INDICATOR_PLAY_GAME = 3;
	/*
	 * These are the valid game types
	 */
	public static int GAME_TYPE_TEXAS_HOLDEM = 1;
	/*
	 * These are the valid game type responses
	 */
	public static int GAME_TYPE_RESPONSE_ACK = 1;
	public static int GAME_TYPE_RESPONSE_INVALID = 2;
	/*
	 * These are the valid GAME_PLAY_REQUEST
	 */
	public static int GAME_PLAY_REQUEST_INIT = 1;
	public static int GAME_PLAY_REQUEST_GET_HOLE = 2;
	public static int GAME_PLAY_REQUEST_GET_FLOP = 3;
	public static int GAME_PLAY_REQUEST_GET_TURN = 4;
	public static int GAME_PLAY_REQUEST_GET_RIVER = 5;
	public static int GAME_PLAY_REQUEST_FOLD = 6;
	/*
	 * These are the valid server game play responses
	 */
	public static int GAME_PLAY_RESPONSE_INIT_ACK = 1;
	public static int GAME_PLAY_RESPONSE_GET_HOLE_ACK = 2;
	public static int GAME_PLAY_RESPONSE_GET_FLOP_ACK = 3;
	public static int GAME_PLAY_RESPONSE_GET_TURN_ACK = 4;
	public static int GAME_PLAY_RESPONSE_GET_RIVER_ACK = 5;
	public static int GAME_PLAY_RESPONSE_FOLD_ACK = 6;
	public static int GAME_PLAY_RESPONSE_INVALID_ANTE_BET = 7;
	public static int GAME_PLAY_RESPONSE_INVALID_HOLE_BET = 8;
	public static int GAME_PLAY_RESPONSE_INVALID_FLOP_BET = 9;
	public static int GAME_PLAY_RESPONSE_INVALID_TURN_BET = 10;
	/*
	 * These are the valid Winner choices
	 */
	public static int WINNER_DEALER = 1;
	public static int WINNER_PLAYER = 2;
	public static int WINNER_DRAW = 3;
	/*
	 * These are the valid connection indicators
	 */
	public static int CONNECTION_INDICATOR_CLOSE_CONNECTION = 1;
	public static int CONNECTION_INDICATOR_CLOSE_CONNECTION_ACK = 2;
   
   /*
   * This class represents the message structure for the version message
   */
   public class VersionMessage
   {
      private int iVersion;
      private int iTypeCode;
      private int iVersionType;
      private short nMinorVersion;
      private long  lBankAmount;
      public VersionMessage(int version, int typecode, int versiontype, short minorversion, long bankamount)
      {
         this.iVersion = 0;
         if (version >= 0)
         {
        	 this.iVersion = version;
         }
         this.iTypeCode = 0;
         if (typecode == 1 || typecode == 2 || typecode == 3)
         {
        	 this.iTypeCode = typecode;
         }
         this.iVersionType = 0;
         if (versiontype == 1 || versiontype == 2 || versiontype == 3 || versiontype == 4)
         {
        	 this.iVersionType = versiontype;
         }
         this.nMinorVersion = 0;
         if (minorversion >= 0)
         {
        	 this.nMinorVersion = minorversion;
         }
         this.lBankAmount = 0;
         if (bankamount >= 0)
         {
        	 this.lBankAmount = bankamount;
         }
      }
      public void setVersion(int version)
      {
    	  this.iVersion = 0;
    	  if (version >= 0)
    	  {
    		  this.iVersion = version;
    	  }
      }
      public int getVersion()
      {
         return iVersion;
      }
      public void setTypeCode(int typecode)
      {
    	  this.iTypeCode = 0;
          if (typecode == 1 || typecode == 2 || typecode == 3)
          {
         	 this.iTypeCode = typecode;
          }
      }
      public int getTypeCode()
      {
         return iTypeCode;
      }
      public void setVersionType(int versiontype)
      {
    	  this.iVersionType = 0;
          if (versiontype == 1 || versiontype == 2 || versiontype == 3 || versiontype == 4)
          {
         	 this.iVersionType = versiontype;
          }
      }
      public int getVersionType()
      {
         return iVersionType;
      }
      public void setMinorVersion(short minorversion)
      {
    	  this.nMinorVersion = 0;
          if (minorversion >= 0)
          {
         	 this.nMinorVersion = minorversion;
          }
      }
      public short getMinorVersion()
      {
         return nMinorVersion;
      }
      public void setBankAmount(long bankamount)
      {
    	  this.lBankAmount = 0;
          if (bankamount >= 0)
          {
         	 this.lBankAmount = bankamount;
          }
      }
      public long getBankAmount()
      {
         return lBankAmount;
      }   
      
      public String toString()
      {
    	  String sTypeCode = "UNKNOWN";
    	  if (iTypeCode == 0)
    	  {
    		  sTypeCode = "NOT_SET";
    	  }
    	  else if (iTypeCode == 1)
    	  {
    		  sTypeCode = "VERSION";
    	  }
    	  else if (iTypeCode == 2)
    	  {
    		  sTypeCode = "GAME";
    	  }
    	  else if (iTypeCode == 3)
    	  {
    		  sTypeCode = "CLOSE_CONNECTION";
    	  }
    	  
    	  String sVersionCode = "UNKNOWN";
    	  if (iVersionType == 0)
    	  {
    		  sVersionCode = "NOT_SET";
    	  }
    	  else if (iVersionType == 1)
    	  {
    		  sVersionCode = "CLIENT_VERSION";
    	  }
    	  else if (iVersionType == 2)
    	  {
    		  sVersionCode = "VERSION_REQUIRED";
    	  }
    	  else if (iVersionType == 3)
    	  {
    		  sVersionCode = "VERSION_ACK";
    	  }
    	  else if (iVersionType == 4)
    	  {
    		  sVersionCode = "VERSION_UPGRADE";
    	  }
    	  String message = "";
    	  message += "Type Indicator: " + sTypeCode + "\n";
    	  message += "Version Type: " + sVersionCode + "\n";
    	  message += "Your bank account value is: " + this.lBankAmount + "\n";
    	  return message;
    	  
      }      
   }
   
   /*
   * This class represents the structure for the client
   * get game message
   */
   public class ClientGetGameMessage
   {
      private int iVersion;
      private int iTypeCode;
      private int iGameIndicator;
      public ClientGetGameMessage(int version, int typecode, int gameindicator)
      {
         this.iVersion = 0;
         if (version >= 0)
         {
        	 this.iVersion = version;
         }
         this.iTypeCode = 0;
         if (typecode == 1 || typecode == 2 || typecode == 3)
         {
        	 this.iTypeCode = typecode; 
         }
         this.iGameIndicator = 0;
         if (gameindicator == 1 || gameindicator == 2 || gameindicator == 3)
         {
        	 this.iGameIndicator = gameindicator;
         }
      }
      public void setVersion(int version)
      {
    	  this.iVersion = 0;
    	  if (version >= 0)
    	  {
    		  this.iVersion = version;
    	  }
      }
      public int getVersion()
      {
         return iVersion;
      }
      public void setTypeCode(int typecode)
      {
    	  this.iTypeCode = 0;
          if (typecode == 1 || typecode == 2 || typecode == 3)
          {
         	 this.iTypeCode = typecode; 
          }
      }
      public int getTypeCode()
      {
         return iTypeCode;
      }
      public void setGameIndicator(int gameindicator)
      {
    	  this.iGameIndicator = 0;
          if (gameindicator == 1 || gameindicator == 2 || gameindicator == 3)
          {
         	 this.iGameIndicator = gameindicator;
          }
      }
      public int getGameIndicator()
      {
         return iGameIndicator;
      }      
   }
   
   /*
   * This class represents the structure for the server
   * get game message
   */
   public class ServerGetGameMessage
   {
      private int iVersion;
      private int iTypeCode;
      private int iGameIndicator;
      private long lMessageLength;
      private ArrayList<Integer> oGameTypeCodeList;
      public ServerGetGameMessage(int version, int typecode, int gameindicator, long length, ArrayList<Integer> gametypecodelist)
      {
    	  this.iVersion = 0;
          if (version >= 0)
          {
         	 this.iVersion = version;
          }
          this.iTypeCode = 0;
          if (typecode == 1 || typecode == 2 || typecode == 3)
          {
         	 this.iTypeCode = typecode; 
          }
          this.iGameIndicator = 0;
          if (gameindicator == 1 || gameindicator == 2 || gameindicator == 3)
          {
         	 this.iGameIndicator = gameindicator;
          }
          this.lMessageLength = 0;
          if (length >= 0)
          {
        	  this.lMessageLength = length;
          }
          this.oGameTypeCodeList = new ArrayList<Integer>();
          this.oGameTypeCodeList.clear();
          if (gametypecodelist.size() >= 0)
          {
        	  this.oGameTypeCodeList = gametypecodelist;
          }
      }
      public void setVersion(int version)
      {
    	  this.iVersion = 0;
          if (version >= 0)
          {
         	 this.iVersion = version;
          }
      }
      public int getVersion()
      {
         return iVersion;
      }
      public void setTypeCode(int typecode)
      {
    	  this.iTypeCode = 0;
          if (typecode == 1 || typecode == 2 || typecode == 3)
          {
         	 this.iTypeCode = typecode; 
          }
      }
      public int getTypeCode()
      {
         return iTypeCode;
      }
      public void setGameIndicator(int gameindicator)
      {
    	  this.iGameIndicator = 0;
          if (gameindicator == 1 || gameindicator == 2 || gameindicator == 3)
          {
         	 this.iGameIndicator = gameindicator;
          }
      }
      public int getGameIndicator()
      {
         return iGameIndicator;
      }   
      public void setMessageLength(long length)
      {
    	  this.lMessageLength = 0;
          if (length >= 0)
          {
        	  this.lMessageLength = length;
          }
      }
      public long getMessageLength()
      {
         return lMessageLength;
      } 
      public void setGameTypeCodeList(ArrayList<Integer> gametypecodelist)
      {
    	  this.oGameTypeCodeList.clear();
          if (gametypecodelist.size() >= 0)
          {
        	  this.oGameTypeCodeList = gametypecodelist;
          }
      }
      public ArrayList<Integer> getGameTypeCodeList()
      {
         return oGameTypeCodeList;
      } 
      
      public String toString()
      {
    	  String message = "";
    	  int i=0;
    	  for (Integer gameList : oGameTypeCodeList)
    	  {
    		  i++;
    		  message += "Option " + i + ": " + gameList.intValue();
    	  }
		return message;
    	  
      }
      
   }
   
   /*
   * This class represents the structure for the client
   * set game message
   */
   public class ClientSetGameMessage
   {
      private int iVersion;
      private int iTypeCode;
      private int iGameIndicator;
      private int  iGameTypeCode;
      public ClientSetGameMessage(int version, int typecode, int gameindicator, int gametypecode)
      {
    	  this.iVersion = 0;
          if (version >= 0)
          {
         	 this.iVersion = version;
          }
          this.iTypeCode = 0;
          if (typecode == 1 || typecode == 2 || typecode == 3)
          {
         	 this.iTypeCode = typecode; 
          }
          this.iGameIndicator = 0;
          if (gameindicator == 1 || gameindicator == 2 || gameindicator == 3)
          {
         	 this.iGameIndicator = gameindicator;
          }
          this.iGameTypeCode = 0;
          if (gametypecode == 1)
          {
        	  this.iGameTypeCode = gametypecode;
          }
      }
      public void setVersion(int version)
      {
    	  this.iVersion = 0;
          if (version >= 0)
          {
         	 this.iVersion = version;
          }
      }
      public int getVersion()
      {
         return iVersion;
      }
      public void setTypeCode(int typecode)
      {
    	  this.iTypeCode = 0;
          if (typecode == 1 || typecode == 2 || typecode == 3)
          {
         	 this.iTypeCode = typecode; 
          }
      }
      public int getTypeCode()
      {
         return iTypeCode;
      }
      public void setGameIndicator(int gameindicator)
      {
    	  this.iGameIndicator = 0;
          if (gameindicator == 1 || gameindicator == 2 || gameindicator == 3)
          {
         	 this.iGameIndicator = gameindicator;
          }
      }
      public int getGameIndicator()
      {
         return iGameIndicator;
      }  
      public void setGameTypeCode(int gametypecode)
      {
    	  this.iGameTypeCode = 0;
          if (gametypecode == 1)
          {
        	  this.iGameTypeCode = gametypecode;
          }
      }
      public int getGameTypeCode()
      {
         return iGameTypeCode;
      }
   }
   
   /*
   * This class represents the structure for the server
   * set game message
   */
   public class ServerSetGameMessage
   {
      private int iVersion;
      private int iTypeCode;
      private int iGameIndicator;
      private int  iGameTypeCode;
      private int iGameTypeResponse;
      public ServerSetGameMessage(int version, int typecode, int gameindicator, int gametypecode, int gametyperesponse)
      {
    	  this.iVersion = 0;
          if (version >= 0)
          {
         	 this.iVersion = version;
          }
          this.iTypeCode = 0;
          if (typecode == 1 || typecode == 2 || typecode == 3)
          {
         	 this.iTypeCode = typecode; 
          }
          this.iGameIndicator = 0;
          if (gameindicator == 1 || gameindicator == 2 || gameindicator == 3)
          {
         	 this.iGameIndicator = gameindicator;
          }
          this.iGameTypeCode = 0;
          if (gametypecode == 1)
          {
        	  this.iGameTypeCode = gametypecode;
          }
          this.iGameTypeResponse = 0;
          if (gametyperesponse == 1 || gametyperesponse == 2)
          {
        	  this.iGameTypeResponse = gametyperesponse;
          }
      }
      public void setVersion(int version)
      {
    	  this.iVersion = 0;
          if (version >= 0)
          {
         	 this.iVersion = version;
          }
      }
      public int getVersion()
      {
         return iVersion;
      }
      public void setTypeCode(int typecode)
      {
    	  this.iTypeCode = 0;
          if (typecode == 1 || typecode == 2 || typecode == 3)
          {
         	 this.iTypeCode = typecode; 
          }
      }
      public int getTypeCode()
      {
         return iTypeCode;
      }
      public void setGameIndicator(int gameindicator)
      {
    	  this.iGameIndicator = 0;
          if (gameindicator == 1 || gameindicator == 2 || gameindicator == 3)
          {
         	 this.iGameIndicator = gameindicator;
          }
      }
      public int getGameIndicator()
      {
         return iGameIndicator;
      }  
      public void setGameTypeCode(int gametypecode)
      {
    	  this.iGameTypeCode = 0;
          if (gametypecode == 1)
          {
        	  this.iGameTypeCode = gametypecode;
          }
      }
      public int getGameTypeCode()
      {
         return iGameTypeCode;
      }
      public void setGameTypeResponse(int gametyperesponse)
      {
    	  this.iGameTypeResponse = 0;
          if (gametyperesponse == 1 || gametyperesponse == 2)
          {
        	  this.iGameTypeResponse = gametyperesponse;
          }
      }
      public int getGameTypeResponse()
      {
         return iGameTypeResponse;
      }
      
      public String toString()
      {
    	  String sGameResponse = "ERROR";
    	  if (iGameTypeResponse == 0)
    	  {
    		  sGameResponse = "NOT_SET";
    	  }
    	  else if (iGameTypeResponse == 1)
    	  {
    		  sGameResponse = "ACK";
    	  }
    	  else if (iGameTypeResponse == 2)
    	  {
    		  sGameResponse = "INVALID";
    	  }
    	  String message = "";
    	  message += "Response: " + sGameResponse + "\n";
    	  return message;
    	  
      }      
      
   }
   
    /*
   * This class represents the structure for the client
   * play game message
   */
   public class ClientPlayGameMessage
   {
      private int iVersion;
      private int iTypeCode;
      private int iGameIndicator;
      private int  iGameTypeCode;
      private int iGamePlayRequest;
      private long lBetAmount;
      public ClientPlayGameMessage(int version, int typecode, int gameindicator, int gametypecode, int gameplayrequest, long betamount)
      {
    	  this.iVersion = 0;
          if (version >= 0)
          {
         	 this.iVersion = version;
          }
          this.iTypeCode = 0;
          if (typecode == 1 || typecode == 2 || typecode == 3)
          {
         	 this.iTypeCode = typecode; 
          }
          this.iGameIndicator = 0;
          if (gameindicator == 1 || gameindicator == 2 || gameindicator == 3)
          {
         	 this.iGameIndicator = gameindicator;
          }
          this.iGameTypeCode = 0;
          if (gametypecode == 1)
          {
        	  this.iGameTypeCode = gametypecode;
          }
          this.iGamePlayRequest = 0;
          if (gameplayrequest > 0 && gameplayrequest < 7)
          {
        	  this.iGamePlayRequest = gameplayrequest;
          }
          this.lBetAmount = 0;
          if (betamount >= 0)
          {
        	  this.lBetAmount = betamount;
          }
      }
      public void setVersion(int version)
      {
    	  this.iVersion = 0;
          if (version >= 0)
          {
         	 this.iVersion = version;
          }
      }
      public int getVersion()
      {
         return iVersion;
      }
      public void setTypeCode(int typecode)
      {
    	  this.iTypeCode = 0;
          if (typecode == 1 || typecode == 2 || typecode == 3)
          {
         	 this.iTypeCode = typecode; 
          }
      }
      public int getTypeCode()
      {
         return iTypeCode;
      }
      public void setGameIndicator(int gameindicator)
      {
    	  this.iGameIndicator = 0;
          if (gameindicator == 1 || gameindicator == 2 || gameindicator == 3)
          {
         	 this.iGameIndicator = gameindicator;
          }
      }
      public int getGameIndicator()
      {
         return iGameIndicator;
      }  
      public void setGameTypeCode(int gametypecode)
      {
    	  this.iGameTypeCode = 0;
          if (gametypecode == 1)
          {
        	  this.iGameTypeCode = gametypecode;
          }
      }
      public int getGameTypeCode()
      {
         return iGameTypeCode;
      }
      public void setGamePlayRequest(int gameplayrequest)
      {
    	  this.iGamePlayRequest = 0;
          if (gameplayrequest > 0 && gameplayrequest < 7)
          {
        	  this.iGamePlayRequest = gameplayrequest;
          }
      }
      public int getGamePlayRequest()
      {
         return iGamePlayRequest;
      }
      public void setBetAmount(long betamount)
      {
    	  this.lBetAmount = 0;
          if (betamount >= 0)
          {
        	  this.lBetAmount = betamount;
          }
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
      private int iTypeCode;
      private int iGameIndicator;
      private int  iGameTypeCode;
      private int iGamePlayResponse;
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
      private int iWinner;
      private long lPotSize;
      private long lBetAmount;
      private long lBankAmount;
      public ServerPlayGameMessage(int version, int typecode, int gameindicator, int gametypecode, int gameplayresponse, 
            int ante, Card p1, Card p2, Card d1, Card d2, Card f1, Card f2, Card f3, Card turn, Card river, int winner, long potsize, long betamount, long bankamount)
      {
    	  this.iVersion = 0;
    	  if (version >= 0)
    	  {
    		  this.iVersion = version;
    	  }
    	  this.iTypeCode = 0;
    	  if (typecode > 0 && typecode < 4)
    	  {
    		  this.iTypeCode = typecode;
    	  }
    	  this.iGameIndicator = 0;
    	  if (gameindicator > 0 && gameindicator < 4)
    	  {
    		  this.iGameIndicator = gameindicator;
    	  }
    	  this.iGameTypeCode = 0;
    	  if (gametypecode == 1)
    	  {
    		  this.iGameTypeCode = gametypecode;
    	  }
    	  this.iGamePlayResponse = 0;
    	  if (gameplayresponse > 0 && gameplayresponse < 11)
    	  {
    		  this.iGamePlayResponse = gameplayresponse;
    	  }
    	  this.iAnte = 0;
    	  if (ante >= 0)
    	  {
    		  this.iAnte = ante;
    	  }
    	  this.ePlayerCard1 = p1;
    	  this.ePlayerCard2 = p2;
    	  this.eDealerCard1 = d1;
    	  this.eDealerCard2 = d2;
    	  this.eFlopCard1 = f1;
    	  this.eFlopCard2 = f2;
    	  this.eFlopCard3 = f3;
    	  this.eTurnCard = turn;
    	  this.eRiverCard = river;
    	  this.iWinner = 0;
    	  if (winner > 0 && winner < 4)
    	  {
    		  this.iWinner = winner;
    	  }
    	  this.lPotSize = 0;
    	  if (potsize >= 0)
    	  {
    		  this.lPotSize = potsize;
    	  }
    	  this.lBetAmount = 0;
    	  if (betamount >= 0)
    	  {
    		  this.lBetAmount = betamount;
    	  }
    	  this.lBankAmount = 0;
    	  if (bankamount >= 0)
    	  {
    		  this.lBankAmount = bankamount;
    	  }
      }
      public void setVersion(int version)
      {
    	  this.iVersion = 0;
    	  if (version >= 0)
    	  {
    		  this.iVersion = version;
    	  }
      }
      public int getVersion()
      {
         return iVersion;
      }
      public void setTypeCode(int typecode)
      {
    	  this.iTypeCode = 0;
    	  if (typecode > 0 && typecode < 4)
    	  {
    		  this.iTypeCode = typecode;
    	  }
      }
      public int getTypeCode()
      {
         return iTypeCode;
      }
      public void setGameIndicator(int gameindicator)
      {
    	  this.iGameIndicator = 0;
    	  if (gameindicator > 0 && gameindicator < 4)
    	  {
    		  this.iGameIndicator = gameindicator;
    	  }
      }
      public int getGameIndicator()
      {
         return iGameIndicator;
      }  
      public void setGameTypeCode(int gametypecode)
      {
    	  this.iGameTypeCode = 0;
    	  if (gametypecode == 1)
    	  {
    		  this.iGameTypeCode = gametypecode;
    	  }
      }
      public int getGameTypeCode()
      {
         return iGameTypeCode;
      }
      public void setGamePlayResponse(int gameplayresponse)
      {
    	  this.iGamePlayResponse = 0;
    	  if (gameplayresponse > 0 && gameplayresponse < 12)
    	  {
    		  this.iGamePlayResponse = gameplayresponse;
    	  }
      }
      public int getGamePlayResponse()
      {
         return iGamePlayResponse;
      }
      public void setAnte(int ante)
      {
    	  this.iAnte = 0;
    	  if (ante > 0)
    	  {
    		  this.iAnte = ante;
    	  }
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
      public void setWinner(int winner)
      {
    	  this.iWinner = 0;
    	  if (winner > 0 && winner < 4)
    	  {
    		  this.iWinner = winner;
    	  }
      }
      public int getWinner()
      {
         return iWinner;
      }
      public void setPotSize(long potsize)
      {
    	  this.lPotSize = 0;
    	  if (potsize >= 0)
    	  {
    		  this.lPotSize = potsize;
    	  }
      }
      public long getPotSize()
      {
         return lPotSize;
      }
      public void setBetAmount(long betamount)
      {
    	  this.lBetAmount = 0;
    	  if (betamount >= 0)
    	  {
    		  this.lBetAmount = betamount;
    	  }
      }
      public long getBetAmount()
      {
         return lBetAmount;
      }
      public void setBankAmount(long bankamount)
      {
    	  this.lBankAmount = 0;
    	  if (bankamount >= 0)
    	  {
    		  this.lBankAmount = bankamount;
    	  }
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
      private int iTypeCode;
      private int iConnectionCode;
      public ConnectionMessage(int version, int typecode, int concode)
      {
    	  this.iVersion = 0;
    	  if (version > 0)
    	  {
    		  this.iVersion = version;
    	  }
    	  this.iTypeCode = 0;
    	  if (typecode > 0 && typecode < 4)
    	  {
    		  this.iTypeCode = typecode;
    	  }
    	  this.iConnectionCode = 0;
    	  if (concode > 0 && concode < 3)
    	  {
    		  this.iConnectionCode = concode;
    	  }
      }
      public void setVersion(int version)
      {
    	  this.iVersion = 0;
    	  if (version >= 0)
    	  {
    		  this.iVersion = version;
    	  }
      }
      public int getVersion()
      {
         return iVersion;
      }
      public void setTypeCode(int typecode)
      {
    	  this.iTypeCode = 0;
    	  if (typecode > 0 && typecode < 4)
    	  {
    		  this.iTypeCode = typecode;
    	  }
      }
      public int getTypeCode()
      {
         return iTypeCode;
      }
      public void setConnectionCode(int concode)
      {
    	  this.iConnectionCode = 0;
    	  if (concode > 0 && concode < 3)
    	  {
    		  this.iConnectionCode = concode;
    	  }
      }
      public int getConnectionCode()
      {
         return iConnectionCode;
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
	public int GetTypeIndicator(byte[] buffer, int iSize)
	{
		int Indicator = NOT_SET;
		if (iSize >= 4)
		{
			Indicator = ((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
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
		int indicator = NOT_SET;
		int verType = NOT_SET;
		short minorversion = -1;
		long bankamount = -1;
		if (iSize == 12)
		{
			version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF ); //FFFF
			indicator = ((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF)); //FFFF
			verType = ((int)(buffer[4] & 0xFF));
			minorversion = (short)(buffer[5] & 0xFF);
			short byte1 = (short)(buffer[8] & 0xFF);
			short byte2 = (short)(buffer[9] & 0xFF);
			short byte3 = (short)(buffer[10] & 0xFF);
			short byte4 = (short)(buffer[11] & 0xFF);
			bankamount = (long)(((long)(byte1 << 24) |
							(long)(byte2 << 16) |
							(long)(byte3 << 8) |
							(long)byte4) & 0xFFFFFFFF);
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
		buffer[2] = (byte)((message.getTypeCode() & 0xFF00) >> 8);
		buffer[3] = (byte)(message.getTypeCode() & 0xFF);
		buffer[4] = (byte)(message.getVersionType() & 0xFF);
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
	public int GetGameIndicator(byte[] buffer, int iSize)
	{
		int indicator = NOT_SET;
		if (iSize >= 5)
		{
			indicator = ((short)(buffer[4] & 0xFF));
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
	   int typecode = NOT_SET;
	   int gameindicator = NOT_SET;
      if (iSize == 8)
      {
         version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
         typecode = ((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
         gameindicator = ((int)(buffer[4] & 0xFF));
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
		buffer[2] = (byte)((message.getTypeCode() & 0xFF00) >> 8);
		buffer[3] = (byte)(message.getTypeCode() & 0xFF);
		buffer[4] = (byte)(message.getGameIndicator() & 0xFF);
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
    	int typecode = NOT_SET;
    	int gameindicator = NOT_SET;
    	long length = -1;
    	ArrayList<Integer> GameTypeCodes = new ArrayList<Integer>();
    	GameTypeCodes.clear();
    	if ((iSize >= 16) && ((iSize % 4) == 0))
    	{
    		version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
    		typecode = ((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
    		gameindicator = ((int)(buffer[4] & 0xFF));
            byte byte1 = (byte)(buffer[8] & 0xFF);
            byte byte2 = (byte)(buffer[9] & 0xFF);
            byte byte3 = (byte)(buffer[10] & 0xFF);
            byte byte4 = (byte)(buffer[11] & 0xFF);
            length = (long)(((long)(byte1 << 24) |
						    (long)(byte2 << 16) |
						    (long)(byte3 << 8)  |
						   (long)(byte4)) & 0xFFFFFFFF);
            if (length != iSize)
            {
               length = (long)iSize;
            }
            for (int iI = 12; iI < length; iI++)
            {
               short gameCode = (short)(buffer[iI] & 0xFF);
               if (gameCode > 0)
               {
            	   GameTypeCodes.add((int)gameCode);
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
		buffer[2] = (byte)((message.getTypeCode() & 0xFF00) >> 8);
		buffer[3] = (byte)(message.getTypeCode() & 0xFF);
		buffer[4] = (byte)(message.getGameIndicator() & 0xFF);
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
				int gameCode = message.getGameTypeCodeList().get((iI-12)).intValue();
				buffer[iI] = (byte)((short)gameCode & 0xFF);
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
		int typecode = NOT_SET;
		int gameindicator = NOT_SET;
		int gamecode = NOT_SET;
		if (iSize == 8)
		{
			version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
    		typecode = ((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
    		gameindicator = ((short)(buffer[4] & 0xFF));
    		gamecode = (int)((buffer[5] & 0xFF));
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
		buffer[2] = (byte)((message.getTypeCode() & 0xFF00) >> 8);
		buffer[3] = (byte)(message.getTypeCode() & 0xFF);
		buffer[4] = (byte)(message.getGameIndicator() & 0xFF);
		buffer[5] = (byte)(message.getGameTypeCode() & 0xFF);
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
		int typecode = NOT_SET;
		int gameindicator = NOT_SET;
		int gamecode = NOT_SET;
		int gameresponse = NOT_SET;
		if (iSize == 8)
		{
			version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
    		typecode = ((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
    		gameindicator = ((int)(buffer[4] & 0xFF));
    		gamecode = ((int)(buffer[5] & 0xFF));
    		gameresponse = ((int)(buffer[6] & 0xFF));
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
		buffer[2] = (byte)((message.getTypeCode() & 0xFF00) >> 8);
		buffer[3] = (byte)(message.getTypeCode() & 0xFF);
		buffer[4] = (byte)(message.getGameIndicator() & 0xFF);
		buffer[5] = (byte)(message.getGameTypeCode() & 0xFF);
		buffer[6] = (byte)(message.getGameTypeResponse() & 0xFF);
		buffer[7] = 0;
		
		return buffer;
	}
	
	/*
	 * Function to get the Client Play Game Message
	 */
	public ClientPlayGameMessage GetClientPlayGameMessage(byte[] buffer, int iSize)
	{
		int version = -1;
		int typecode = NOT_SET;
		int gameindicator = NOT_SET;
		int gamecode = NOT_SET;
		int gamerequest = NOT_SET;
		long betamount = -1;
		if (iSize == 12)
		{
			version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
    		typecode = ((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
    		gameindicator = ((int)(buffer[4] & 0xFF));
    		gamecode = ((int)(buffer[5] & 0xFF));
    		gamerequest = ((int)(buffer[6] & 0xFF));
    		betamount = (long)((long)(0xff & buffer[8]) << 24 |
    					(long)(0xff & buffer[9]) << 16 |
    					(long)(0xff & buffer[10]) << 8 |
    					(long)(0xff & buffer[11]) << 0);
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
		buffer[2] = (byte)((message.getTypeCode() & 0xFF00) >> 8);
		buffer[3] = (byte)(message.getTypeCode() & 0xFF);
		buffer[4] = (byte)(message.getGameIndicator() & 0xFF);
		buffer[5] = (byte)(message.getGameTypeCode() & 0xFF);
		buffer[6] = (byte)(message.getGamePlayRequest() & 0xFF);
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
		int typecode = NOT_SET;
		int gameindicator = NOT_SET;
		int gamecode = NOT_SET;
		int gameresponse = NOT_SET;
		int ante = -1;
		int winner = NOT_SET;
		long potsize = -1;
		long betamount = -1;
		long bankamount = -1;
		int suite = 0;
		int value = 0;
		Card p1 = new Card(Card.NOT_SET, Card.NOT_SET);
		Card p2 = new Card(Card.NOT_SET, Card.NOT_SET);
		Card d1 = new Card(Card.NOT_SET, Card.NOT_SET);
		Card d2 = new Card(Card.NOT_SET, Card.NOT_SET);
		Card f1 = new Card(Card.NOT_SET, Card.NOT_SET);
		Card f2 = new Card(Card.NOT_SET, Card.NOT_SET);
		Card f3 = new Card(Card.NOT_SET, Card.NOT_SET);
		Card t1 = new Card(Card.NOT_SET, Card.NOT_SET);
		Card r1 = new Card(Card.NOT_SET, Card.NOT_SET);
		if (iSize == 32)
		{
			version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
    		typecode = ((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
    		gameindicator = ((int)(buffer[4] & 0xFF));
    		gamecode = ((int)(buffer[5] & 0xFF));
    		gameresponse = ((int)(buffer[6] & 0xFF));
    		ante = (int)(((buffer[8] << 8) | buffer[9]) & 0xFFFF);
    		suite = ((int)((buffer[10] & 0xF0) >> 4));
    		p1.setCardSuite(suite);
    		value = ((int)(buffer[10] & 0x0F));
    		p1.setCardValue(value);
    		suite = ((int)((buffer[11] & 0xF0) >> 4));
    		p2.setCardSuite(suite);
    		value = ((int)(buffer[11] & 0x0F));
    		p2.setCardValue(value);
    		suite = ((int)((buffer[12] & 0xF0) >> 4));
    		d1.setCardSuite(suite);
    		value = ((int)(buffer[12] & 0x0F));
    		d1.setCardValue(value);
    		suite = ((int)((buffer[13] & 0xF0) >> 4));
    		d2.setCardSuite(suite);
    		value = ((int)(buffer[13] & 0x0F));
    		d2.setCardValue(value);
    		suite = ((int)((buffer[14] & 0xF0) >> 4));
    		f1.setCardSuite(suite);
    		value = ((int)(buffer[14] & 0x0F));
    		f1.setCardValue(value);
    		suite = ((int)((buffer[15] & 0xF0) >> 4));
    		f2.setCardSuite(suite);
    		value = ((int)(buffer[15] & 0x0F));
    		f2.setCardValue(value);
    		suite = ((int)((buffer[16] & 0xF0) >> 4));
    		f3.setCardSuite(suite);
    		value = ((int)(buffer[16] & 0x0F));
    		f3.setCardValue(value);
    		suite = ((int)((buffer[17] & 0xF0) >> 4));
    		t1.setCardSuite(suite);
    		value = ((int)(buffer[17] & 0x0F));
    		t1.setCardValue(value);
    		suite = ((int)((buffer[18] & 0xF0) >> 4));
    		r1.setCardSuite(suite);
    		value = ((int)(buffer[18] & 0x0F));
    		r1.setCardValue(value);
    		winner = (int)((buffer[19] & 0xF0) >> 4);
    		potsize = ((long)(0xff & buffer[20]) << 24 |
    				(long)(0xff & buffer[21]) << 16 |
    				(long)(0xff & buffer[22]) << 8 |
    				(long)(0xff & buffer[23]) << 0);
    		betamount = ((long)(0xff & buffer[24]) << 24 |
    				(long)(0xff & buffer[25]) << 16 |
    				(long)(0xff & buffer[26]) << 8 |
    				(long)(0xff & buffer[27]) << 0);
    		bankamount = ((long)(0xff & buffer[28]) << 24 |
    				(long)(0xff & buffer[29]) << 16 |
    				(long)(0xff & buffer[30]) << 8 |
    				(long)(0xff & buffer[31]) << 0);

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
		buffer[2] = (byte)((message.getTypeCode() & 0xFF00) >> 8);
		buffer[3] = (byte)(message.getTypeCode() & 0xFF);
		buffer[4] = (byte)(message.getGameIndicator() & 0xFF);
		buffer[5] = (byte)(message.getGameTypeCode() & 0xFF);
		buffer[6] = (byte)(message.getGamePlayResponse() & 0xFF);
		buffer[7] = 0;
		buffer[8] = (byte)((message.getAnte() & 0xFF00) >> 8);
		buffer[9] = (byte)(message.getAnte() & 0xFF);
		buffer[10] = (byte)(((message.getPlayerCard1().getCardSuite() & 0x0F) << 4) | (message.getPlayerCard1().getCardValue() & 0x0F)); 		
		buffer[11] = (byte)(((message.getPlayerCard2().getCardSuite() & 0x0F) << 4) | (message.getPlayerCard2().getCardValue() & 0x0F));
		buffer[12] = (byte)(((message.getDealerCard1().getCardSuite() & 0x0F) << 4) | (message.getDealerCard1().getCardValue() & 0x0F)); 
		buffer[13] = (byte)(((message.getDealerCard2().getCardSuite() & 0x0F) << 4) | (message.getDealerCard2().getCardValue() & 0x0F));
		buffer[14] = (byte)(((message.getFlopCard1().getCardSuite() & 0x0F) << 4) | (message.getFlopCard1().getCardValue() & 0x0F));
		buffer[15] = (byte)(((message.getFlopCard2().getCardSuite() & 0x0F) << 4) | (message.getFlopCard2().getCardValue() & 0x0F));
		buffer[16] = (byte)(((message.getFlopCard3().getCardSuite() & 0x0F) << 4) | (message.getFlopCard3().getCardValue() & 0x0F));
		buffer[17] = (byte)(((message.getTurnCard().getCardSuite() & 0x0F) << 4) | (message.getTurnCard().getCardValue() & 0x0F));
		buffer[18] = (byte)(((message.getRiverCard().getCardSuite() & 0x0F) << 4) | (message.getRiverCard().getCardValue() & 0x0F));
		buffer[19] = (byte)(((message.getWinner() & 0x0F) << 4) & 0xF0);
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
		int typecode = NOT_SET;
		int concode = NOT_SET;
		if (iSize == 8)
		{
			version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
    		typecode = ((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
    		concode = (int)((buffer[4] & 0xFF));
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
		buffer[2] = (byte)((message.getTypeCode() & 0xFF00) >> 8);
		buffer[3] = (byte)(message.getTypeCode() & 0xFF);
		buffer[4] = (byte)(message.getConnectionCode() & 0xFF);
		buffer[5] = 0;
		buffer[6] = 0;
		buffer[7] = 0;
		
		return buffer;
	}

}
