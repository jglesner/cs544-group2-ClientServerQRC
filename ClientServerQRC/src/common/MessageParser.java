package common;

import java.util.ArrayList;
import common.card_game.Card;

/**
 *  The MessageParser Class
 *  
 *  This class is used to handle the following:
 *  1) Parsing incoming byte arrays to the proper message
 *  2) Converting the message to a byte array for output
 *
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  Members: Jeremy Glesner, Dustin Overmiller, Yiqi Ju, Lei Yuan
 *  Project: Advanced Game Message Protocol Implementation
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
   
   /**
   * VersionMessage class used to store the Version Message PDU
   *
   */
   public class VersionMessage
   {
      /* class members */
      private int iVersion;
      private int iTypeCode;
      private int iVersionType;
      private short nMinorVersion;
      private long  lBankAmount;
      /**
      * Constructor
      *
      */
      public VersionMessage(int version, int typecode, int versiontype, short minorversion, long bankamount)
      {
         /* make sure all parameters are valid */
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
         this.iVersionType = 0;
         if (versiontype > 0 && versiontype < 5)
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
      /**
      * setVersion - set version member
      * @param version
      * @return none
      */
      public void setVersion(int version)
      {
    	  this.iVersion = 0;
        /* make sure it is valid */
    	  if (version >= 0)
    	  {
    		  this.iVersion = version;
    	  }
      }
      /**
      * getVersion - get version member
      * @param none
      * @return int
      */
      public int getVersion()
      {
         return iVersion;
      }
      /**
      * setTypeCode - set type code member
      * @param typecode
      * @return none
      */
      public void setTypeCode(int typecode)
      {
    	  this.iTypeCode = 0;
        /* make sure it is valid */
          if (typecode > 0 && typecode < 4)
          {
         	 this.iTypeCode = typecode;
          }
      }
      /**
      * getTypeCode - get type code member
      * @param none
      * @return int
      */
      public int getTypeCode()
      {
         return iTypeCode;
      }
      /**
      * setVersionType - set version type member
      * @param versiontype
      * @return none
      */
      public void setVersionType(int versiontype)
      {
    	  this.iVersionType = 0;
        /* make sure it is valid */
          if (versiontype > 0 && versiontype < 5)
          {
         	 this.iVersionType = versiontype;
          }
      }
      /**
      * getVersionType - get version type member
      * @param none
      * @return int
      */
      public int getVersionType()
      {
         return iVersionType;
      }
      /**
      * setMinorVersion - set minor version member
      * @param minorversion
      * @return none
      */
      public void setMinorVersion(short minorversion)
      {
    	  this.nMinorVersion = 0;
        /* make sure it is valid */
          if (minorversion >= 0)
          {
         	 this.nMinorVersion = minorversion;
          }
      }
      /**
      * getMinorVersion - get minor version member
      * @param none
      * @return short
      */
      public short getMinorVersion()
      {
         return nMinorVersion;
      }
      /**
      * setBankAmount - set bank amount member
      * @param bankamount
      * @return none
      */
      public void setBankAmount(long bankamount)
      {
    	  this.lBankAmount = 0;
        /* make sure it is valid */
          if (bankamount >= 0)
          {
         	 this.lBankAmount = bankamount;
          }
      }
      /**
      * getBankAmount - get bank amount member
      * @param none
      * @return long
      */
      public long getBankAmount()
      {
         return lBankAmount;
      }   
      
      /**
      * toString - convert message to a string format
      * @param none
      * @return String
      */
      public String toString()
      {
         /* format the various type codes to strings */
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
   
   /**
   * ClientGetGameMessage class used to store the Client Get Game PDU Message
   *
   */
   public class ClientGetGameMessage
   {
      /* private members */
      private int iVersion;
      private int iTypeCode;
      private int iGameIndicator;
      /**
      * Constructor
      *
      */
      public ClientGetGameMessage(int version, int typecode, int gameindicator)
      {
         /* make sure all parameters are valid */
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
      }
      /**
      * setVersion - set the version member
      * @param version
      * @return none
      */
      public void setVersion(int version)
      {
    	  this.iVersion = 0;
        /* make sure it is valid */
    	  if (version >= 0)
    	  {
    		  this.iVersion = version;
    	  }
      }
      /**
      * getVersion - get version member
      * @param none
      * @return int
      */
      public int getVersion()
      {
         return iVersion;
      }
      /**
      * setTypeCode - set type code member
      * @param typecode
      * @return none
      */
      public void setTypeCode(int typecode)
      {
    	  this.iTypeCode = 0;
        /* make sure it is valid */
          if (typecode > 0 && typecode < 4)
          {
         	 this.iTypeCode = typecode; 
          }
      }
      /**
      * getTypeCode - get type code member
      * @param none
      * @return int
      */
      public int getTypeCode()
      {
         return iTypeCode;
      }
      /**
      * setGameIndicator - set game indicator member
      * @param gameindicator
      * @return none
      */
      public void setGameIndicator(int gameindicator)
      {
    	  this.iGameIndicator = 0;
          /* make sure it is valid */
          if (gameindicator > 0 && gameindicator < 4)
          {
         	 this.iGameIndicator = gameindicator;
          }
      }
      /**
      * getGameIndicator - get game indicator member
      * @param none
      * @return int
      */
      public int getGameIndicator()
      {
         return iGameIndicator;
      }      
   }
   
   /**
   * ServerGetGameMessage class used to store the Server Get Game PDU Message
   *
   */
   public class ServerGetGameMessage
   {
      /* private members */
      private int iVersion;
      private int iTypeCode;
      private int iGameIndicator;
      private long lMessageLength;
      private ArrayList<Integer> oGameTypeCodeList;
      /**
      * Constructor
      *
      */
      public ServerGetGameMessage(int version, int typecode, int gameindicator, long length, ArrayList<Integer> gametypecodelist)
      {
         /* make sure all parameters are valid */
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
      /**
      * setVersion - set version member
      * @param version
      * @return none
      */
      public void setVersion(int version)
      {
    	  this.iVersion = 0;
        /* make sure it is valid */
          if (version >= 0)
          {
         	 this.iVersion = version;
          }
      }
      /**
      * getVersion - get version member
      * @param none
      * @return int
      */
      public int getVersion()
      {
         return iVersion;
      }
      /**
      * setTypeCode - set type code member
      * @param typecode
      * @return none
      */
      public void setTypeCode(int typecode)
      {
    	  this.iTypeCode = 0;
        /* make sure it is valid */
          if (typecode > 0 && typecode < 4)
          {
         	 this.iTypeCode = typecode; 
          }
      }
      /**
      * getTypeCode - get typecode member
      * @param none
      * @return int
      */
      public int getTypeCode()
      {
         return iTypeCode;
      }
      /**
      * setGameIndicator - set game indicator member
      * @param gameindicator
      * @return none
      */
      public void setGameIndicator(int gameindicator)
      {
    	  this.iGameIndicator = 0;
        /* make sure it is valid */
          if (gameindicator > 0 && gameindicator < 4)
          {
         	 this.iGameIndicator = gameindicator;
          }
      }
      /**
      * getGameIndicator - get game indicator member
      * @param none
      * @return int
      */
      public int getGameIndicator()
      {
         return iGameIndicator;
      }   
      /**
      * setMessageLength - set message length member
      * @param length
      * @return none
      */
      public void setMessageLength(long length)
      {
    	  this.lMessageLength = 0;
        /* make sure it is valid */
          if (length >= 0)
          {
        	  this.lMessageLength = length;
          }
      }
      /**
      * getMessageLength - get message length member
      * @param none
      * @return long
      */
      public long getMessageLength()
      {
         return lMessageLength;
      } 
      /**
      * setGameTypeCodeList - set game type code list member
      * @param gametypecodelist
      * @return none
      */
      public void setGameTypeCodeList(ArrayList<Integer> gametypecodelist)
      {
    	  this.oGameTypeCodeList.clear();
        /* make sure it is valid */
          if (gametypecodelist.size() >= 0)
          {
        	  this.oGameTypeCodeList = gametypecodelist;
          }
      }
      /**
      * getGameTypeCodeList - get game type code list member
      * @param none
      * @return ArrayList<Integer>
      */
      public ArrayList<Integer> getGameTypeCodeList()
      {
         return oGameTypeCodeList;
      } 
      /**
      * toString - convert the message to a string for printing
      * @param none
      * @return String
      */
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
   
   /**
   * ClientSetGameMessage class used to store the Client Set Game PDU Message
   *
   */
   public class ClientSetGameMessage
   {
      private int iVersion;
      private int iTypeCode;
      private int iGameIndicator;
      private int  iGameTypeCode;
      /**
      * Constructor
      *
      */
      public ClientSetGameMessage(int version, int typecode, int gameindicator, int gametypecode)
      {
         /* make sure all parameters are valid */
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
      }
      /**
      * setVersion - set version member
      * @param version
      * @return none
      */
      public void setVersion(int version)
      {
    	  this.iVersion = 0;
        /* make sure it is valid */
          if (version >= 0)
          {
         	 this.iVersion = version;
          }
      }
      /**
      * getVersion - get version member
      * @param none
      * @return int
      */
      public int getVersion()
      {
         return iVersion;
      }
      /**
      * setTypeCode - set type code member
      * @param typecode
      * @return none
      */
      public void setTypeCode(int typecode)
      {
    	  this.iTypeCode = 0;
        /* make sure it is valid */
          if (typecode > 0 && typecode < 4)
          {
         	 this.iTypeCode = typecode; 
          }
      }
      /**
      * getTypeCode - get type code member
      * @param none
      * @return int
      */
      public int getTypeCode()
      {
         return iTypeCode;
      }
      /**
      * setGameIndicator - set game indicator member
      * @param gameindicator
      * @return none
      */
      public void setGameIndicator(int gameindicator)
      {
    	  this.iGameIndicator = 0;
         /* make sure it is valid */
          if (gameindicator > 0 && gameindicator < 4)
          {
         	 this.iGameIndicator = gameindicator;
          }
      }
      /**
      * getGameIndicator - get game indicator member
      * @param none
      * @return int
      */
      public int getGameIndicator()
      {
         return iGameIndicator;
      }  
      /**
      * setGameTypeCode - set game type code member
      * @param gametypecode
      * @return none
      */
      public void setGameTypeCode(int gametypecode)
      {
    	  this.iGameTypeCode = 0;
        /* make sure it is valid */
          if (gametypecode == 1)
          {
        	  this.iGameTypeCode = gametypecode;
          }
      }
      /**
      * getGameTypeCode - get game type code member
      * @param none
      * @return int
      */
      public int getGameTypeCode()
      {
         return iGameTypeCode;
      }
   }
   
   /**
   * ServerSetGameMessage class used to store the Server Set Game PDU Message
   *
   */
   public class ServerSetGameMessage
   {
      /* private members */
      private int iVersion;
      private int iTypeCode;
      private int iGameIndicator;
      private int  iGameTypeCode;
      private int iGameTypeResponse;
      /**
      * Constructor
      *
      */
      public ServerSetGameMessage(int version, int typecode, int gameindicator, int gametypecode, int gametyperesponse)
      {
         /* make sure all params are valid */
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
          this.iGameTypeResponse = 0;
          if (gametyperesponse > 0 && gametyperesponse < 3)
          {
        	  this.iGameTypeResponse = gametyperesponse;
          }
      }
      /**
      * setVersion - set version member
      * @param version
      * @return none
      */
      public void setVersion(int version)
      {
    	  this.iVersion = 0;
        /* make sure it is valid */
          if (version >= 0)
          {
         	 this.iVersion = version;
          }
      }
      /**
      * getVersion - get version member
      * @param none
      * @return int
      */
      public int getVersion()
      {
         return iVersion;
      }
      /**
      * setTypeCode - set type code member
      * @param typecode
      * @return none
      */
      public void setTypeCode(int typecode)
      {
    	  this.iTypeCode = 0;
        /* make sure it is valid */
          if (typecode > 0 && typecode < 4)
          {
         	 this.iTypeCode = typecode; 
          }
      }
      /**
      * getTypeCode - get type code member
      * @param none
      * @return int
      */
      public int getTypeCode()
      {
         return iTypeCode;
      }
      /**
      * setGameIndicator - set game indicator member
      * @param gameindicator
      * @return none
      */
      public void setGameIndicator(int gameindicator)
      {
    	  this.iGameIndicator = 0;
        /* make sure it is valid */
          if (gameindicator > 0 && gameindicator < 4)
          {
         	 this.iGameIndicator = gameindicator;
          }
      }
      /**
      * getGameIndicator - get game indicator member
      * @param none
      * @return int
      */
      public int getGameIndicator()
      {
         return iGameIndicator;
      }  
      /**
      * setGameTypeCode - set game type code member
      * @param gametypecode
      * @return none
      */
      public void setGameTypeCode(int gametypecode)
      {
    	  this.iGameTypeCode = 0;
        /* make sure it is valid */
          if (gametypecode == 1)
          {
        	  this.iGameTypeCode = gametypecode;
          }
      }
      /**
      * getGameTypeCode - get game type code member
      * @param none
      * @return int
      */
      public int getGameTypeCode()
      {
         return iGameTypeCode;
      }
      /**
      * setGameTypeResponse - set game type response member
      * @param gametyperesponse
      * @return none
      */
      public void setGameTypeResponse(int gametyperesponse)
      {
    	  this.iGameTypeResponse = 0;
        /* make sure it is valid */
          if (gametyperesponse > 0 && gametyperesponse < 3)
          {
        	  this.iGameTypeResponse = gametyperesponse;
          }
      }
      /**
      * getGameTypeResponse - get game type response member
      * @param none
      * @return int
      */
      public int getGameTypeResponse()
      {
         return iGameTypeResponse;
      }
      
      /**
      * toString - convert the class to a string for output
      * @param none
      * @return String
      */
      public String toString()
      {
         /* format types to strings */
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
   
    /**
   * ClientPlayGameMessage class used to store the Client Play Game PDU Message
   *
   */
   public class ClientPlayGameMessage
   {
      /* ember variables */
      private int iVersion;
      private int iTypeCode;
      private int iGameIndicator;
      private int  iGameTypeCode;
      private int iGamePlayRequest;
      private long lBetAmount;
      /**
      * Constructor
      *
      */
      public ClientPlayGameMessage(int version, int typecode, int gameindicator, int gametypecode, int gameplayrequest, long betamount)
      {
         /* make sure parameters are valid */
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
      /**
      * setVersion - set version member
      * @param version
      * @return none
      */
      public void setVersion(int version)
      {
    	  this.iVersion = 0;
        /* make sure it is valid */
          if (version >= 0)
          {
         	 this.iVersion = version;
          }
      }
      /**
      * getVersion - get version member
      * @param none
      * @return int
      */
      public int getVersion()
      {
         return iVersion;
      }
      /**
      * setTypeCode - set type code member
      * @param typecode
      * @return none
      */
      public void setTypeCode(int typecode)
      {
    	  this.iTypeCode = 0;
         /* make sure it is valid */
          if (typecode > 0 && typecode < 4)
          {
         	 this.iTypeCode = typecode; 
          }
      }
      /**
      * getTypeCode - get type code member
      * @param none
      * @return int
      */
      public int getTypeCode()
      {
         return iTypeCode;
      }
      /**
      * setGameIndicator - set game indicator member
      * @param gameindicator
      * @return none
      */
      public void setGameIndicator(int gameindicator)
      {
    	  this.iGameIndicator = 0;
        /* make sure it is valid */
          if (gameindicator > 0 && gameindicator < 4)
          {
         	 this.iGameIndicator = gameindicator;
          }
      }
      /**
      * getGameIndicator - get game indicator member
      * @param none
      * @return int
      */
      public int getGameIndicator()
      {
         return iGameIndicator;
      }  
      /**
      * setGameTypeCode - set game type code member
      * @param gametypecode
      * @return none
      */
      public void setGameTypeCode(int gametypecode)
      {
    	  this.iGameTypeCode = 0;
        /* make sure it is valid */
          if (gametypecode == 1)
          {
        	  this.iGameTypeCode = gametypecode;
          }
      }
      /**
      * getGameTypeCode - get game type code member
      * @param none
      * @return int
      */
      public int getGameTypeCode()
      {
         return iGameTypeCode;
      }
      /**
      * setGamePlayRequest - set game play request member
      * @param gameplayrequest
      * @return none
      */
      public void setGamePlayRequest(int gameplayrequest)
      {
    	  this.iGamePlayRequest = 0;
        /* make sure it is valid */
          if (gameplayrequest > 0 && gameplayrequest < 7)
          {
        	  this.iGamePlayRequest = gameplayrequest;
          }
      }
      /**
      * getGamePlayRequest - get game play request member
      * @param none
      * @return int
      */
      public int getGamePlayRequest()
      {
         return iGamePlayRequest;
      }
      /**
      * setBetAmount - set bet amount member
      * @param betamount
      * @return none
      */
      public void setBetAmount(long betamount)
      {
    	  this.lBetAmount = 0;
          if (betamount >= 0)
          {
        	  this.lBetAmount = betamount;
          }
      }
      /**
      * getBetAmount - get bet amount code member
      * @param none
      * @return long
      */
      public long getBetAmount()
      {
         return lBetAmount;
      }
   }
   
   /**
   * ServerPlayGameMessage class used to store the Server Play Game PDU Message
   *
   */
   public class ServerPlayGameMessage
   {
      /* member variables */
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
      /**
      * Constructor
      *
      */
      public ServerPlayGameMessage(int version, int typecode, int gameindicator, int gametypecode, int gameplayresponse, 
            int ante, Card p1, Card p2, Card d1, Card d2, Card f1, Card f2, Card f3, Card turn, Card river, int winner, long potsize, long betamount, long bankamount)
      {
         /* make sure the parameters are correct */
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
      /**
      * setVersion - set version member
      * @param version
      * @return none
      */
      public void setVersion(int version)
      {
    	  this.iVersion = 0;
        /* make sure it is valid */
    	  if (version >= 0)
    	  {
    		  this.iVersion = version;
    	  }
      }
      /**
      * getVersion - get version member
      * @param none
      * @return int
      */
      public int getVersion()
      {
         return iVersion;
      }
      /**
      * setTypeCode - set type code member
      * @param typecode
      * @return none
      */
      public void setTypeCode(int typecode)
      {
    	  this.iTypeCode = 0;
        /* make sure it is valid */
    	  if (typecode > 0 && typecode < 4)
    	  {
    		  this.iTypeCode = typecode;
    	  }
      }
      /**
      * getTypeCode - get type code member
      * @param none
      * @return int
      */
      public int getTypeCode()
      {
         return iTypeCode;
      }
      /**
      * setGameIndicator - set game indicator member
      * @param gameindicator
      * @return none
      */
      public void setGameIndicator(int gameindicator)
      {
    	  this.iGameIndicator = 0;
        /* make sure it is valid */
    	  if (gameindicator > 0 && gameindicator < 4)
    	  {
    		  this.iGameIndicator = gameindicator;
    	  }
      }
      /**
      * getGameIndicator - get game indicator member
      * @param none
      * @return int
      */
      public int getGameIndicator()
      {
         return iGameIndicator;
      }  
      /**
      * setGamTypeCode - set game type code member
      * @param gametypecode
      * @return none
      */
      public void setGameTypeCode(int gametypecode)
      {
    	  this.iGameTypeCode = 0;
        /* make sure it is valid */
    	  if (gametypecode == 1)
    	  {
    		  this.iGameTypeCode = gametypecode;
    	  }
      }
      /**
      * getGameTypeCode - get game type code member
      * @param none
      * @return int
      */
      public int getGameTypeCode()
      {
         return iGameTypeCode;
      }
      /**
      * setGamePlayResponse - set game play response member
      * @param gameplayresponse
      * @return none
      */
      public void setGamePlayResponse(int gameplayresponse)
      {
    	  this.iGamePlayResponse = 0;
        /* make sure it is valid */
    	  if (gameplayresponse > 0 && gameplayresponse < 12)
    	  {
    		  this.iGamePlayResponse = gameplayresponse;
    	  }
      }
      /**
      * getGamePlayResponse - get game play response member
      * @param none
      * @return int
      */
      public int getGamePlayResponse()
      {
         return iGamePlayResponse;
      }
      /**
      * setAnte - set ante member
      * @param ante
      * @return none
      */
      public void setAnte(int ante)
      {
    	  this.iAnte = 0;
        /* make sure it is valid */
    	  if (ante > 0)
    	  {
    		  this.iAnte = ante;
    	  }
      }
      /**
      * getAnte - get ante member
      * @param none
      * @return int
      */
      public int getAnte()
      {
         return iAnte;
      }
      /**
      * setPlayerCard1 - set player card 1 member
      * @param card
      * @return none
      */
      public void setPlayerCard1(Card card)
      {
         this.ePlayerCard1 = card;
      }
      /**
      * getPlayerCard1 - get player card 1 member
      * @param none
      * @return card
      */
      public Card getPlayerCard1()
      {
         return ePlayerCard1;
      }
      /**
      * setPlayerCard2 - set player card 2 member
      * @param card
      * @return none
      */
      public void setPlayerCard2(Card card)
      {
         this.ePlayerCard2 = card;
      }
      /**
      * getPlayerCard2 - get player card 2 member
      * @param none
      * @return card
      */
      public Card getPlayerCard2()
      {
         return ePlayerCard2;
      }
      /**
      * setDealerCard1 - set dealer card 1 member
      * @param card
      * @return none
      */
      public void setDealerCard1(Card card)
      {
         this.eDealerCard1 = card;
      }
      /**
      * getDealerCard1 - get dealer card 1 member
      * @param none
      * @return card
      */
      public Card getDealerCard1()
      {
         return eDealerCard1;
      }
      /**
      * setDealerCard2 - set dealer card 2 member
      * @param card
      * @return none
      */
      public void setDealerCard2(Card card)
      {
         this.eDealerCard2 = card;
      }
      /**
      * getDealerCard2 - get dealer card 2 member
      * @param none
      * @return card
      */
      public Card getDealerCard2()
      {
         return eDealerCard2;
      }
      /**
      * setFlopCard1 - set flop card 1 member
      * @param card
      * @return none
      */
      public void setFlopCard1(Card card)
      {
         this.eFlopCard1 = card;
      }
      /**
      * getFlopCard1 - get flop card 1 member
      * @param none
      * @return card
      */
      public Card getFlopCard1()
      {
         return eFlopCard1;
      }
      /**
      * setFlopCard2 - set flop card 2 member
      * @param card
      * @return none
      */
      public void setFlopCard2(Card card)
      {
         this.eFlopCard2 = card;
      }
      /**
      * getFlopCard2 - get flop card 2 member
      * @param none
      * @return card
      */
      public Card getFlopCard2()
      {
         return eFlopCard2;
      }
      /**
      * setFlopCard3 - set flop card 3 member
      * @param card
      * @return none
      */
      public void setFlopCard3(Card card)
      {
         this.eFlopCard3 = card;
      }
      /**
      * getFlopCard3 - get flop card 3 member
      * @param none
      * @return card
      */
      public Card getFlopCard3()
      {
         return eFlopCard3;
      }
      /**
      * setTurnCard - set turn card member
      * @param card
      * @return none
      */
      public void setTurnCard(Card card)
      {
         this.eTurnCard = card;
      }
      /**
      * getTurnCard - get turn card member
      * @param none
      * @return card
      */
      public Card getTurnCard()
      {
         return eTurnCard;
      }
      /**
      * setRiverCard - set river card member
      * @param card
      * @return none
      */
      public void setRiverCard(Card card)
      {
         this.eRiverCard = card;
      }
      /**
      * getRiverCard - get river card member
      * @param none
      * @return card
      */
      public Card getRiverCard()
      {
         return eRiverCard;
      }
      /**
      * setWinner - set winner member
      * @param winner
      * @return none
      */
      public void setWinner(int winner)
      {
    	  this.iWinner = 0;
        /* make sure it is valid */
    	  if (winner > 0 && winner < 4)
    	  {
    		  this.iWinner = winner;
    	  }
      }
      /**
      * getWinner - get winner member
      * @param none
      * @return int
      */
      public int getWinner()
      {
         return iWinner;
      }
      /**
      * setPotSize - set pot size member
      * @param potsize
      * @return none
      */
      public void setPotSize(long potsize)
      {
    	  this.lPotSize = 0;
        /* make sure it is valid */
    	  if (potsize >= 0)
    	  {
    		  this.lPotSize = potsize;
    	  }
      }
      /**
      * getPotSize - get pot size member
      * @param none
      * @return long
      */
      public long getPotSize()
      {
         return lPotSize;
      }
      /**
      * setBetAmount - set bet amount member
      * @param betamount
      * @return none
      */
      public void setBetAmount(long betamount)
      {
    	  this.lBetAmount = 0;
        /* make sure it is valid */
    	  if (betamount >= 0)
    	  {
    		  this.lBetAmount = betamount;
    	  }
      }
      /**
      * getBetAmount - get bet amount member
      * @param none
      * @return long
      */
      public long getBetAmount()
      {
         return lBetAmount;
      }
      /**
      * setBankAmount - set bank amount member
      * @param bankamount
      * @return none
      */
      public void setBankAmount(long bankamount)
      {
    	  this.lBankAmount = 0;
    	  if (bankamount >= 0)
    	  {
    		  this.lBankAmount = bankamount;
    	  }
      }
      /**
      * getBankAmount - get bank amount member
      * @param none
      * @return long
      */
      public long getBankAmount()
      {
         return lBankAmount;
      }
   }
   
   /**
   * ConnectionMessage class used to store the Connection Message PDU
   *
   */
   public class ConnectionMessage
   {
      /* member variables */
      private int iVersion;
      private int iTypeCode;
      private int iConnectionCode;
      /**
      * Constructor
      *
      */
      public ConnectionMessage(int version, int typecode, int concode)
      {
         /* make sure all parameters are valid */
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
      /**
      * setVersion - set version member
      * @param version
      * @return none
      */
      public void setVersion(int version)
      {
    	  this.iVersion = 0;
        /* make sure it is valid */
    	  if (version >= 0)
    	  {
    		  this.iVersion = version;
    	  }
      }
      /**
      * getVersion - get version member
      * @param none
      * @return int
      */
      public int getVersion()
      {
         return iVersion;
      }
      /**
      * setTypeCode - set type code member
      * @param typecode
      * @return none
      */
      public void setTypeCode(int typecode)
      {
    	  this.iTypeCode = 0;
    	  if (typecode > 0 && typecode < 4)
    	  {
    		  this.iTypeCode = typecode;
    	  }
      }
      /**
      * getTypeCode - get type code member
      * @param none
      * @return int
      */
      public int getTypeCode()
      {
         return iTypeCode;
      }
      /**
      * setConnectionCode - set connection code member
      * @param concode
      * @return none
      */
      public void setConnectionCode(int concode)
      {
    	  this.iConnectionCode = 0;
    	  if (concode > 0 && concode < 3)
    	  {
    		  this.iConnectionCode = concode;
    	  }
      }
      /**
      * getConnectionCode - get connection code member
      * @param none
      * @return int
      */
      public int getConnectionCode()
      {
         return iConnectionCode;
      }
   }
       
   /**
   * GetVersion - This function will return the version of the message
   * It will be -1 if the message is not the correct size
   * @param buffer
   * @param iSize
   * @return int
   */   
	public int GetVersion(byte[] buffer, int iSize)
	{
		int version = -1;
      /* make sure the mesage is the proper size */
		if (iSize >= 2)
		{
         /* get the data from the byte array */
			version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
		}
		return version;
	}
	
   /**
   * GetTypeIndicator - This function will return the type indicator of the message
   * It will be 0 (NOT_SET) if the message is not the correct size
   * @param buffer
   * @param iSize
   * @return int
   */   
	public int GetTypeIndicator(byte[] buffer, int iSize)
	{
		int Indicator = NOT_SET;
      /* make sure the message is the proper size */
		if (iSize >= 4)
		{
         /* get the data from the byte array */
			Indicator = ((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
			return Indicator;
		}
      return Indicator;  
	}
	
   /**
   * GetVersionMessage - Function will get the version message
    * Values will be NOT_SET or -1 if the message is not the correct type
   * @param buffer
   * @param iSize
   * @return VersionMessage
   */
	public VersionMessage GetVersionMessage(byte[] buffer, int iSize)
	{
		int version = -1;
		int indicator = NOT_SET;
		int verType = NOT_SET;
		short minorversion = -1;
		long bankamount = -1;
      /* make sure the message is the proper size */
		if (iSize == 12)
		{
         /* get the data from the byte array */
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
	
   /**
   * CreateVersionMessage - This function will create the byte array
   * associated with the versio message to send over the socket
   * @param VersionMessage
   * @return byte[]
   */
	public byte[] CreateVersionMessage(VersionMessage message)
	{
		byte[] buffer = new byte[12];
      /* store the Version message into the byte array */
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
	
   /**
   * GetGameIndicator - Function to get the GameIndicator
    * Value will be 0 (NOT_SET) if the message is not the right size
   * @param buffer
   * @param iSize
   * @return int
   */
	public int GetGameIndicator(byte[] buffer, int iSize)
	{
		int indicator = NOT_SET;
      /* make sure the message is the proper size */
		if (iSize >= 5)
		{
         /* get the data from the byte array */
			indicator = ((short)(buffer[4] & 0xFF));
		}
		return indicator;
	}
   
   /**
   * GetClientGetGameMessage - Function to get the client get game message
   * The values will be -1 and 0 (NOT_SET) if the message is not the correct size
   * @param buffer
   * @param iSize
   * @return ClientGetGameMessage
   */
   public ClientGetGameMessage GetClientGetGameMessage(byte[] buffer, int iSize)
   {
	   int version = -1;
	   int typecode = NOT_SET;
	   int gameindicator = NOT_SET;
      /* make sure the message is the proper size */
      if (iSize == 8)
      {
         /* get the data from the byte array */
         version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
         typecode = ((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
         gameindicator = ((int)(buffer[4] & 0xFF));
      }
      
      ClientGetGameMessage message = new ClientGetGameMessage(version, typecode, gameindicator);
      return message;
   }
   
   /**
   * CreateClientGetGameMessage - Function to convert the Client Get Game Message
   * into a byte array to be sent across the socket connection
   * @param ClientGetGameMessage
   * @return byte[]
   */
   public byte[] CreateClientGetGameMessage(ClientGetGameMessage message)
   {
      byte[] buffer = new byte[8];
      /* convert the message to a byte array */
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
	
	/**
   * GetServerGetGameMessage - Function to get the server get game message
   * The values will be -1 and 0 (NOT_SET) if the message is not the correct size
   * @param buffer
   * @param iSize
   * @return ServerGetGameMessage
   */
    public ServerGetGameMessage GetServerGetGameMessage(byte[] buffer, int iSize)
    {
    	int version = -1;
    	int typecode = NOT_SET;
    	int gameindicator = NOT_SET;
    	long length = -1;
    	ArrayList<Integer> GameTypeCodes = new ArrayList<Integer>();
    	GameTypeCodes.clear();
      /* make sure the message is the proper size */
    	if ((iSize >= 16) && ((iSize % 4) == 0))
    	{
         /* get the data from the byte array */
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
            /* grab the list of games and fill in the array */
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
	
	/**
   * CreateServerGetGameMessage - Function to convert the Server Get Game Message
   * into a byte array to be sent across the socket connection
   * @param ServerGetGameMessage
   * @return byte[]
   */
	public byte[] CreateServerGetGameMessage(ServerGetGameMessage message)
	{
      /* get the length field of the message */
		int gameCodeLen = ((int)Math.ceil(message.getGameTypeCodeList().size() / 4.0) * 4);
		int length = gameCodeLen + 12;
		long uilength = (long)length;
		byte[] buffer = new byte[length];
      /* convert to a byte array */
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
	
	/**
   * GetClientSetGameMessage - Function to get the client set game message
   * The values will be -1 and 0 (NOT_SET) if the message is not the correct size
   * @param buffer
   * @param iSize
   * @return ClientSetGameMessage
   */
	public ClientSetGameMessage GetClientSetGameMessage(byte[] buffer, int iSize)
	{
		int version = -1;
		int typecode = NOT_SET;
		int gameindicator = NOT_SET;
		int gamecode = NOT_SET;
      /* make sure the message is the proper size */
		if (iSize == 8)
		{
         /* get the data from the byte array */
			version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
    		typecode = ((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
    		gameindicator = ((short)(buffer[4] & 0xFF));
    		gamecode = (int)((buffer[5] & 0xFF));
		}
		
		ClientSetGameMessage message = new ClientSetGameMessage(version, typecode, gameindicator, gamecode);
		return message;
	}
	
	/**
   * CreateClientSetGameMessage - Function to convert the Client Set Game Message
   * into a byte array to be sent across the socket connection
   * @param ClientSetGameMessage
   * @return byte[]
   */
	public byte[] CreateClientSetGameMessage(ClientSetGameMessage message)
	{
		byte[] buffer = new byte[8];
      /* convert to a byte array */
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
    
   /**
   * GetServerSetGameMessage - Function to get the server set game message
   * The values will be -1 and 0 (NOT_SET) if the message is not the correct size
   * @param buffer
   * @param iSize
   * @return ServerSetGameMessage
   */
	public ServerSetGameMessage GetServerSetGameMessage(byte[] buffer, int iSize)
	{
		int version = -1;
		int typecode = NOT_SET;
		int gameindicator = NOT_SET;
		int gamecode = NOT_SET;
		int gameresponse = NOT_SET;
      /* make sure the message is the proper size */
		if (iSize == 8)
		{
         /* get the data from the byte array */
			version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
    		typecode = ((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
    		gameindicator = ((int)(buffer[4] & 0xFF));
    		gamecode = ((int)(buffer[5] & 0xFF));
    		gameresponse = ((int)(buffer[6] & 0xFF));
		}
		
		ServerSetGameMessage message = new ServerSetGameMessage(version, typecode, gameindicator, gamecode, gameresponse);
		return message;
	}
	
	/**
   * CreateServerSetGameMessage - Function to convert the Server Set Game Message
   * into a byte array to be sent across the socket connection
   * @param ServerSetGameMessage
   * @return byte[]
   */
	public byte[] CreateServerSetGameMessage(ServerSetGameMessage message)
	{
		byte[] buffer = new byte[8];
      /* convert message to a byte array */
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
	
	/**
   * GetClientPlayGameMessage - Function to get the client play game message
   * The values will be -1 and 0 (NOT_SET) if the message is not the correct size
   * @param buffer
   * @param iSize
   * @return ClientPlayGameMessage
   */
	public ClientPlayGameMessage GetClientPlayGameMessage(byte[] buffer, int iSize)
	{
		int version = -1;
		int typecode = NOT_SET;
		int gameindicator = NOT_SET;
		int gamecode = NOT_SET;
		int gamerequest = NOT_SET;
		long betamount = -1;
      /* make sure the message is the proper size */
		if (iSize == 12)
		{
         /* get the data from the byte array */
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
	
	/**
   * CreateClientPlayGameMessage - Function to convert the Client Play Game Message
   * into a byte array to be sent across the socket connection
   * @param ClientPlayGameMessage
   * @return byte[]
   */
	public byte[] CreateClientPlayGameMessage(ClientPlayGameMessage message)
	{
		byte[] buffer = new byte[12];
      /* convert message to a byte array */
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
	
	/**
   * GetServerPlayGameMessage - Function to get the server play game message
   * The values will be -1 and 0 (NOT_SET) if the message is not the correct size
   * @param buffer
   * @param iSize
   * @return ServerPlayGameMessage
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
      /* make sure the message is the proper size */
		if (iSize == 32)
		{
         /* convert the byte array to the data */
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
	
	/**
   * CreateServerPlayGameMessage - Function to convert the Server Play Game Message
   * into a byte array to be sent across the socket connection
   * @param ServerPlayGameMessage
   * @return byte[]
   */
	public byte[] CreateServerPlayGameMessage(ServerPlayGameMessage message)
	{
		byte[] buffer = new byte[32];
      /* convert message to the byte array */
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
	
	/**
   * GetConnectionMessage - Function to get the connection message
   * The values will be -1 and 0 (NOT_SET) if the message is not the correct size
   * @param buffer
   * @param iSize
   * @return ConnectionMessage
   */
	public ConnectionMessage GetConnectionMessage(byte[] buffer, int iSize)
	{
		int version = -1;
		int typecode = NOT_SET;
		int concode = NOT_SET;
      /* make sure the message is the proper size */
		if (iSize == 8)
		{
         /* get the data from the byte array */
			version = (int)(((buffer[0] << 8) | buffer[1]) & 0xFFFF );
    		typecode = ((int)(((buffer[2] << 8) | buffer[3]) & 0xFFFF));
    		concode = (int)((buffer[4] & 0xFF));
		}
		
		ConnectionMessage message = new ConnectionMessage(version, typecode, concode);
		return message;
	}
	
	/**
   * CreateConnectionMessage - Function to convert the Connection Message
   * into a byte array to be sent across the socket connection
   * @param ConnectionMessage
   * @return byte[]
   */
	public byte[] CreateConnectionMessage(ConnectionMessage message)
	{
		byte[] buffer = new byte[8];
      /* convert the message to a byte array */
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
