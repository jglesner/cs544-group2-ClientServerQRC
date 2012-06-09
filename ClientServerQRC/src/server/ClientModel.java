package server;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import javax.net.ssl.SSLSocket;
import server.card_game.texas_holdem.TexasHoldemModel;
import common.*;

/**
 * ClientModel is spawned, one per client to manage the socket connection
 * and in-game functions using AGMP as a communications protocol.
 * 
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  Members: Jeremy Glesner, Dustin Overmiller, Yiqi Ju, Lei Yuan
 *  Project: Advanced Game Message Protocol Implementation
 *  
 */
public class ClientModel extends Observable implements Runnable {

	/** For reading input from socket */
   private InputStream oInputStream;
   /** For writing output to socket. */
   private OutputStream oOutputStream;
   ObjectOutputStream outputStream;
   ObjectInputStream inputstream;
   /** Socket object representing client connection */

   private SSLSocket socket;
   private boolean running;
   private GameState gameState;
    
   public String uniqueID;
   private final LogAndPublish logAndPublish;
   private XmlParser xmlParser;
   private MessageParser messageParser = null;
   private TexasHoldemModel oTHModel = null;
   private int m_iVersion = -1;
   private short m_iMinorVersion = -1;
   private long m_lClientBankAmount = -1;
   private long m_lVersionOpTimer = -1;
   private long m_lGameOpTimer = -1;

   private TimeoutTimer timeoutTimer;
   private int m_iGameTypeCode = MessageParser.NOT_SET;
   private ArrayList<Integer> oGameTypeList = null;
   /**
   * Constructor class to create the ClientModel Object
   *
   */
   public ClientModel(SSLSocket socket, XmlParser xmlParser, LogAndPublish logAndPublish) throws IOException {
      this.socket = socket;
      this.uniqueID = "" + socket.getInetAddress() + ":" + socket.getPort();
      this.logAndPublish = logAndPublish;
      this.xmlParser = xmlParser;
      this.gameState = new GameState();
      this.gameState.setState(GameState.LISTENING);
      this.messageParser = new MessageParser();
      this.m_iVersion = Integer.parseInt(this.xmlParser.getServerTagValue("VERSION"));
      this.m_iMinorVersion = (short)Integer.parseInt(this.xmlParser.getServerTagValue("MINOR_VERSION"));
      this.m_lClientBankAmount = (long)Integer.parseInt(this.xmlParser.getServerTagValue("CLIENT_BANK_AMOUNT"));
      this.m_lVersionOpTimer = Integer.parseInt((this.xmlParser.getServerTagValue("VERSION_OPERATION_TIMER")));
      this.m_lGameOpTimer = Integer.parseInt((this.xmlParser.getServerTagValue("GAME_OPERATION_TIMER")));
      this.timeoutTimer = new TimeoutTimer(this);
      this.oTHModel = new TexasHoldemModel(this);
      this.m_iGameTypeCode = MessageParser.NOT_SET;
      // instantiate the server game list
      this.oGameTypeList = new ArrayList<Integer>();
      // store the current typecodes
      oGameTypeList.add(MessageParser.GAME_TYPE_TEXAS_HOLDEM);
      this.logAndPublish.write(this.uniqueID +": Successfully connected", true, true);
      running = false;
      //get I/O from socket
      try {
         this.oInputStream = socket.getInputStream();
         this.oOutputStream = socket.getOutputStream();
         outputStream = new ObjectOutputStream(oOutputStream);
         inputstream = new ObjectInputStream(oInputStream);
         running = true; //set status
      }
      catch (IOException ioe) {
         throw ioe;
      }
   }
    
   /**
   * getLogAndPublish - get the LogAndPublish object
   * @param none
   * @return LogAndPublish
   */
   public LogAndPublish getLogAndPublish()
   {
      return logAndPublish;
   }
   /**
   * getXmlParser - get the XmlParser object
   * @param none
   * @return XmlParser
   */
   public XmlParser getXmlParser()
   {
      return xmlParser;
   }
   /**
   * getMessageParser - get the MessageParser object
   * @param none
   * @return MessageParser
   */
   public MessageParser getMessageParser()
   {
      return messageParser;
   }
   /**
   * getVersion - get the version of the server
   * @param none
   * @return int
   */
   public int getVersion()
   {
      return m_iVersion;
   }
   /**
   * getClientBankAmount - get the client bank amount
   * @param none
   * @return long
   */
   public long getClientBankAmount()
   {
      return m_lClientBankAmount;
   }
	
   /**
   * stopClient - closes the socket connection
   * @param none
   * @return none
   */
   public void stopClient()
   {
      try {
         this.logAndPublish.write(this.uniqueID + ": Stopping Client", true, false);
         this.gameState.setState(GameState.CLOSED);
         this.socket.close();
      }catch(IOException ioe){ };
   }
   /**
   * run - main routine for the thread
   * @param none
   * @return none
   */
   public void run() 
   {
      try
      {
         // force the client to send version info by setting timeouts
         this.timeoutTimer.schedule(this.m_lVersionOpTimer);
         this.socket.setSoTimeout((int) (this.m_lVersionOpTimer+2000));
         
         /* DFA STATE MANAGEMENT */
         while (running && !this.socket.isClosed() && this.gameState.getState() != GameState.CLOSED)
         {
            // get the input from the client
            byte iByteCount = inputstream.readByte();
            byte [] inputBuffer = new byte[iByteCount];
            inputstream.readFully(inputBuffer); 
        		
            if (this.gameState.getState() == GameState.LISTENING)
            {
               // need to validate the client
               ListeningState(inputBuffer, iByteCount);
            }
            else if (this.gameState.getState() == GameState.AUTHENTICATE)
            {
               // Connection Negotiation state
               // Client has authenticated and needs to get the games
               AuthenticateState(inputBuffer, iByteCount);
            }
            else if (this.gameState.getState() == GameState.GAMELIST)
            {
               // Game List state
               // Client has gotten the games and needs to select one
               GameListState(inputBuffer, iByteCount);
            }
            else if (this.gameState.getState() == GameState.GAMESET)
            {
               // Game Set state
               GameSetState(inputBuffer, iByteCount);
            }        		
            else if (this.gameState.getState() == GameState.GAMEPLAY)
            {
               // Game Play state
               GamePlayState(inputBuffer, iByteCount);
            }
         }
         running = false;
      }
      catch (IOException ioe) {
         running = false;
         this.logAndPublish.write(this.uniqueID + ": " + ioe, true, false);
      }
        
      //it's time to close the socket
      if (!this.socket.isClosed())
      {
         try {
            this.socket.close();
            this.logAndPublish.write(this.uniqueID + ": Closing Connection.", true, true);
         } catch (IOException ioe) { }
      }
      //notify the observers for cleanup etc.
      this.setChanged();              //inherit from Observable
      this.notifyObservers(this);     //inherit from Observable
   }
   
   /**
   * ListeningState - In this state the client has to negotiate the version and validate proper
   * communication. The server will transition to the Authentication (Connection Negotiation) state when this
   * is successful
   * @param inputBuffer
   * @param iByteCount
   * @return none
   */
   private void ListeningState(byte[] inputBuffer, int iByteCount) {
      /*
      *  During this state the client has to authenticate itself by giving its version to the server
      *  If the client sends a version message the server will check to see if it is valid and reply appropriately.
      *  The server will start the counter for the verification. If the client does not finish authentication within the 
      *  timeout time the connection will be closed
      */
		if (this.messageParser.GetTypeIndicator(inputBuffer, iByteCount) == MessageParser.TYPE_INDICATOR_VERSION)
		{
         MessageParser.VersionMessage msg = this.messageParser.GetVersionMessage(inputBuffer, iByteCount);
         if ((msg.getVersion() == this.m_iVersion) && (msg.getVersionType() == MessageParser.VERSION_INDICATOR_CLIENT_VERSION))
			{
				// reset the timer. Use the game time because it is longer and the client has been verified.
				this.timeoutTimer.reschedule(this.m_lGameOpTimer);
				msg.setBankAmount(this.m_lClientBankAmount);
				msg.setVersionType(MessageParser.VERSION_INDICATOR_VERSION_ACK);
				this.logAndPublish.write(this.uniqueID + ": has finished authenticating!", true, false);
				try	{
					/* MESSAGE MANAGEMENT */ 
					outputStream.writeByte((byte)this.messageParser.CreateVersionMessage(msg).length);
					outputStream.write(this.messageParser.CreateVersionMessage(msg)); 
					outputStream.flush();	
					// reset the socket timeout to the game because it is longer
					this.socket.setSoTimeout((int) (this.m_lGameOpTimer+2000));
					this.gameState.setState(GameState.AUTHENTICATE);
				} catch (Exception e) {
					logAndPublish.write(e, true, false);
					this.timeoutTimer.stop();
					this.gameState.setState(GameState.CLOSED);
				}
			}
			else if (msg.getVersionType() == MessageParser.VERSION_INDICATOR_CLIENT_VERSION)
			{
				// client needs to upgrade
				// The server cannot communicate so send the message and close the connection
				msg.setBankAmount((long)0);
				msg.setVersion(this.m_iVersion);
				msg.setMinorVersion(this.m_iMinorVersion);
				msg.setTypeCode(MessageParser.TYPE_INDICATOR_VERSION);
				msg.setVersionType(MessageParser.VERSION_INDICATOR_VERSION_UPGRADE);
				this.logAndPublish.write(this.uniqueID + ": Invalid Version, closing connection", true, false);
				try {
					/* MESSAGE MANAGEMENT */ 
	                outputStream.writeByte((byte)this.messageParser.CreateVersionMessage(msg).length);
	                outputStream.write(this.messageParser.CreateVersionMessage(msg)); 
	                outputStream.flush();					
					this.timeoutTimer.stop();
					this.gameState.setState(GameState.CLOSED);
				} catch (Exception e) {
					logAndPublish.write(e, true, false);
					this.timeoutTimer.stop();
					this.gameState.setState(GameState.CLOSED);
				}
			}
			else
			{
            // send version requirement message and stay in this state
				msg.setBankAmount((long)0);
				msg.setMinorVersion(this.m_iMinorVersion);
				msg.setTypeCode(MessageParser.TYPE_INDICATOR_VERSION);
				msg.setVersion(this.m_iVersion);
				msg.setVersionType(MessageParser.VERSION_INDICATOR_VERSION_REQUIREMENT);
				this.logAndPublish.write(this.uniqueID + ": Invalid message, need client protocol version", true, false);
				try {
					/* MESSAGE MANAGEMENT */ 
					outputStream.writeByte((byte)this.messageParser.CreateVersionMessage(msg).length);
			        outputStream.write(this.messageParser.CreateVersionMessage(msg)); 
			        outputStream.flush();
				} catch (Exception e) {
					logAndPublish.write(e, true, false);
					this.timeoutTimer.stop();
					this.gameState.setState(GameState.CLOSED);
				}
			}
		}
		else
		{
			// client did not send the right message, server needs to force a version message
			this.logAndPublish.write(this.uniqueID + ": Need to finish authentication of version, got a different message", true, false);
			MessageParser.VersionMessage msg = this.messageParser.new VersionMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_VERSION, MessageParser.VERSION_INDICATOR_VERSION_REQUIREMENT, this.m_iMinorVersion,(long)0);
			try {
				/* MESSAGE MANAGEMENT */
	            outputStream.writeByte((byte)this.messageParser.CreateVersionMessage(msg).length);
	            outputStream.write(this.messageParser.CreateVersionMessage(msg)); 
	            outputStream.flush();				
			} catch (Exception e) {
				logAndPublish.write(e, true, false);
				this.gameState.setState(GameState.CLOSED);
			}
		}
		
	}	
   /**
   * AuthenticateState - In this state the client has negotiated the version and the 
   * server is waiting for the get game list message
   * @param inputBuffer
   * @param iByteCount
   * @return none
   */
   private void AuthenticateState(byte[] inputBuffer, int iByteCount) throws IOException {
	  /*
      *  During this state the client has authenticated and the server is waiting for the get games message
      */
      // make sure the version is correct
      if (this.messageParser.GetVersion(inputBuffer, iByteCount) != this.m_iVersion)
      {
         this.logAndPublish.write(this.uniqueID + ": has sent an invalid version number, Ignoring Msg", true, false);
         return;
      }	
      if (this.messageParser.GetTypeIndicator(inputBuffer, iByteCount) == MessageParser.TYPE_INDICATOR_GAME)
      {
    	  if (this.messageParser.GetGameIndicator(inputBuffer, iByteCount) == MessageParser.GAME_INDICATOR_GET_GAME)
    	  {
    		  MessageParser.ClientGetGameMessage msg = this.messageParser.GetClientGetGameMessage(inputBuffer, iByteCount);
    		  // make sure this was indeed a client get game message
    		  if (msg.getGameIndicator() != MessageParser.GAME_INDICATOR_GET_GAME)
    		  {
    			  // not a client message, must be a server message. Ignore
    			  this.logAndPublish.write(this.uniqueID + ": Received Server Get Game Message, Ignoring", true, false);
    			  return;
    		  }
    		  // send the client the game list and update to the game list state
    		  int length = 12 + (int)(Math.ceil((double)this.oGameTypeList.size() / 4) * 4);
    		  MessageParser.ServerGetGameMessage svrMsg = this.messageParser.new ServerGetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_GET_GAME, length, this.oGameTypeList);
    		  // reset the timer
    		  this.timeoutTimer.reschedule(this.m_lGameOpTimer);
    		  this.logAndPublish.write(this.uniqueID + ": has sent get game message", true, false);
    		  try	{
    			  /* MESSAGE MANAGEMENT */
    			  outputStream.writeByte((byte)this.messageParser.CreateServerGetGameMessage(svrMsg).length);
    			  outputStream.write(this.messageParser.CreateServerGetGameMessage(svrMsg)); 
    			  outputStream.flush();					
    			  this.gameState.setState(GameState.GAMELIST);
    			  // reset the socket timeout
    			  this.socket.setSoTimeout((int) (this.m_lGameOpTimer+2000));
    		  } catch (Exception e) {
    			 logAndPublish.write(e, true, false);
    			  this.timeoutTimer.stop();
    			  this.gameState.setState(GameState.CLOSED);
    		  }
    	  }
    	  else
    	  {
    		  // The game indicator is incorrect
    		  this.logAndPublish.write(this.uniqueID + ": Invalid Game Indicator, should be Get Games", true, false);
    	  }
      }
      else
      {
			// The game indicator is incorrect
         this.logAndPublish.write(this.uniqueID + ": Invalid Type Indicator, should be the Game Indicator", true, false);
		}		
	}	
   /**
   * GameListState - In this state the client has received the list of games and can 
   * either select one or close the connection
   * @param inputBuffer
   * @param iByteCount
   * @return none
   */   
   private void GameListState(byte[] inputBuffer, int iByteCount) {
      /*
      * In this state there are two valid messages
      * 1) Set Game
      * 2) Close Connection
      */
		// First check to make sure the version is correct
		if (this.messageParser.GetVersion(inputBuffer, iByteCount) != this.m_iVersion)
		{
			this.logAndPublish.write(this.uniqueID + ": has sent an invalid version number, Ignoring Msg", true, false);
			return;
		}
		if (this.messageParser.GetTypeIndicator(inputBuffer, iByteCount) == MessageParser.TYPE_INDICATOR_GAME)
		{
         if (this.messageParser.GetGameIndicator(inputBuffer, iByteCount) == MessageParser.GAME_INDICATOR_SET_GAME)
         {
            MessageParser.ClientSetGameMessage msg = this.messageParser.GetClientSetGameMessage(inputBuffer, iByteCount);
            if (msg.getGameIndicator() != MessageParser.GAME_INDICATOR_SET_GAME)
            {
               // not a client message, must be a server message. Ignore
               this.logAndPublish.write(this.uniqueID + ": Received Server Set Game Message, Ignoring", true, false);
               return;
            }
            // make sure the game type code is valid
            boolean bFound = false;
            for (int iI = 0; iI < this.oGameTypeList.size(); iI++)
            {
               int typecode = oGameTypeList.get(iI).intValue();
               if (typecode == msg.getGameTypeCode())
               {
                  bFound = true;
               }
            }
            
            // determine whether to send an ack or invalid response
            if (bFound)
            {
				/* send an ack and switch to the game set state */
               MessageParser.ServerSetGameMessage svrMsg = this.messageParser.new ServerSetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_SET_GAME, msg.getGameTypeCode(), MessageParser.GAME_TYPE_RESPONSE_ACK);
               // reset the timer
               this.timeoutTimer.reschedule(m_lGameOpTimer);
               this.gameState.setState(GameState.GAMESET);
               this.m_iGameTypeCode = msg.getGameTypeCode();
               this.logAndPublish.write(this.uniqueID + ": has sent a valid Game Type Indicator", true, false);
               try	{
            	   /* MESSAGE MANAGEMENT */
                  outputStream.writeByte((byte)this.messageParser.CreateServerSetGameMessage(svrMsg).length);
                  outputStream.write(this.messageParser.CreateServerSetGameMessage(svrMsg)); 
                  outputStream.flush();						
                  // reset the socket timeout
                  this.socket.setSoTimeout((int) (this.m_lGameOpTimer+2000));
               } catch (Exception e) {
            	   logAndPublish.write(e, true, false);
            	   this.timeoutTimer.stop();
            	   this.gameState.setState(GameState.CLOSED);
               }
            }
            else
            {
				/* invalid request, send an invalid response code and stay in this state */
               MessageParser.ServerSetGameMessage svrMsg = this.messageParser.new ServerSetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_SET_GAME, msg.getGameTypeCode(), MessageParser.GAME_TYPE_RESPONSE_INVALID);
               // reset the timer
               this.timeoutTimer.reschedule(m_lGameOpTimer);
               this.logAndPublish.write(this.uniqueID + ": has sent an invalid Game Type Indicator", true, false);
               try	{
            	   /* MESSAGE MANAGEMENT */
                  outputStream.writeByte((byte)this.messageParser.CreateServerSetGameMessage(svrMsg).length);
                  outputStream.write(this.messageParser.CreateServerSetGameMessage(svrMsg)); 
                  outputStream.flush();		
                  // reset the socket timeout
                  this.socket.setSoTimeout((int) (this.m_lGameOpTimer+2000));
               } catch (Exception e) {
            	   logAndPublish.write(e, true, false);
                  this.timeoutTimer.stop();
                  this.gameState.setState(GameState.CLOSED);
               }
            }
         }
         else if (this.messageParser.GetGameIndicator(inputBuffer, iByteCount) == MessageParser.GAME_INDICATOR_GET_GAME)
         {
        	 MessageParser.ClientGetGameMessage msg = this.messageParser.GetClientGetGameMessage(inputBuffer, iByteCount);
        	 // make sure this was indeed a client get game message
        	 if (msg.getGameIndicator() != MessageParser.GAME_INDICATOR_GET_GAME)
        	 {
        		 // not a client message, must be a server message. Ignore
        		 this.logAndPublish.write(this.uniqueID + ": Received Server Get Game Message, Ignoring", true, false);
        		 return;
        	 }
        	 // send the client the game list
        	 int length = 12 + (int)(Math.ceil((double)this.oGameTypeList.size() / 4) * 4);
        	 MessageParser.ServerGetGameMessage svrMsg = this.messageParser.new ServerGetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_GET_GAME, length, this.oGameTypeList);
        	 // reset timeer
        	 this.timeoutTimer.reschedule(this.m_lGameOpTimer);
        	 this.logAndPublish.write(this.uniqueID + ": has sent get game message", true, false);
        	 try	{
        		 /* MESSAGE MANAGEMENT */
        		 outputStream.writeByte((byte)this.messageParser.CreateServerGetGameMessage(svrMsg).length);
        		 outputStream.write(this.messageParser.CreateServerGetGameMessage(svrMsg)); 
        		 outputStream.flush();					
        		 this.gameState.setState(GameState.GAMELIST);
        		 // reset socket timeout
        		 this.socket.setSoTimeout((int) (this.m_lGameOpTimer+2000));
        	 } catch (Exception e) {
        		 logAndPublish.write(e, true, false);
        		 this.timeoutTimer.stop();
        		 this.gameState.setState(GameState.CLOSED);
        	 }
         }
         else
         {
            this.logAndPublish.write(this.uniqueID + ": has sent an invalid Game Indicator, should be Set Game or Get Game", true, false);
         }
      }
      else if (this.messageParser.GetTypeIndicator(inputBuffer, iByteCount) == MessageParser.TYPE_INDICATOR_CLOSE_CONNECTION)
      {
         // could be a close connection request
         MessageParser.ConnectionMessage msg = this.messageParser.GetConnectionMessage(inputBuffer, iByteCount);
         if (msg.getConnectionCode() == MessageParser.CONNECTION_INDICATOR_CLOSE_CONNECTION)
         {
            // send the ack and close the connection
            msg.setConnectionCode(MessageParser.CONNECTION_INDICATOR_CLOSE_CONNECTION_ACK);
            this.timeoutTimer.stop();
            this.logAndPublish.write(this.uniqueID + ": has sent a request to close the connection", true, false);
            this.gameState.setState(GameState.CLOSED);
            try	{
            	/* MESSAGE MANAGEMENT */
               outputStream.writeByte((byte)this.messageParser.CreateConnectionMessage(msg).length);
               outputStream.write(this.messageParser.CreateConnectionMessage(msg)); 
               outputStream.flush();	
            } catch (Exception e) {
            	logAndPublish.write(e, true, false);
               this.timeoutTimer.stop();
               this.gameState.setState(GameState.CLOSED);
            }
         }
         else
         {
            this.logAndPublish.write(this.uniqueID + ": has sent an invalid connection message", true, false);
         }
      }
      else
      {
         this.logAndPublish.write(this.uniqueID + ": has sent an invalid message", true, false);
      }            
	}
   /**
   * GameSetState - In this state the client has sent a valid game type code and must now initiate the
   * game play by sending the game play init message
   * @param inputBuffer
   * @param iByteCount
   * @return none
   */   
   private void GameSetState(byte[] inputBuffer, int iByteCount) {
      /* 
      * In this state the client can only send the play game init message
      */
		// First check to make sure the version is correct
		if (this.messageParser.GetVersion(inputBuffer, iByteCount) != this.m_iVersion)
		{
			this.logAndPublish.write(this.uniqueID + ": has sent an invalid version number, Ignoring Msg", true, false);
			return;
		}		
		
		if (this.messageParser.GetTypeIndicator(inputBuffer, iByteCount) == MessageParser.TYPE_INDICATOR_GAME)
		{
			if (this.messageParser.GetGameIndicator(inputBuffer, iByteCount) == MessageParser.GAME_INDICATOR_PLAY_GAME)
			{
				// reset the model
				MessageParser.ClientPlayGameMessage msg = this.messageParser.GetClientPlayGameMessage(inputBuffer, iByteCount);
				if (msg.getGameIndicator() != MessageParser.GAME_INDICATOR_PLAY_GAME)
				{
					// not a client message, must be a server message. Ignore
					this.logAndPublish.write(this.uniqueID + ": Received Server Play Game Message, Ignoring", true, false);
					return;
				}
				if (msg.getGameTypeCode() != this.m_iGameTypeCode)
				{
					// invalid game type code - different than agreed to, ignore
					this.logAndPublish.write(this.uniqueID + ": Received Invalid Game Type Code, Ignoring", true, false);
					return;
				}
            
				// determine the game model to initialize
				if (msg.getGameTypeCode() == MessageParser.GAME_TYPE_TEXAS_HOLDEM)
				{
					if (msg.getGamePlayRequest() == MessageParser.GAME_PLAY_REQUEST_INIT)
					{
						/* game play init message has arrived, reinitalize the Texas Holdem Model and switch to the game play state */
						this.oTHModel.Reset();
						this.gameState.setState(GameState.GAMEPLAY);
						/* get the server response message from the texas hold'em model */
						MessageParser.ServerPlayGameMessage svrMsg = oTHModel.updateModel(msg);
						// update client bank account
						this.m_lClientBankAmount = svrMsg.getBankAmount();
						// reset the timer
						this.timeoutTimer.reschedule(m_lGameOpTimer);
						try	{
							/* MESSAGE MANAGEMENT */
							outputStream.writeByte((byte)this.messageParser.CreateServerPlayGameMessage(svrMsg).length);
							outputStream.write(this.messageParser.CreateServerPlayGameMessage(svrMsg)); 
							outputStream.flush();				
							// reset the timeout
							this.socket.setSoTimeout((int) (this.m_lGameOpTimer+2000));
						} catch (Exception e) {
							logAndPublish.write(e, true, false);
							this.timeoutTimer.stop();
							this.gameState.setState(GameState.CLOSED);
						}
					}
					else
					{
						// Invalid game play message, log it and ignore
						this.logAndPublish.write(this.uniqueID + ": Received Invalid GamePlay message, Ignoring", true, false);
						return;
					}
				}
				else
				{
					// invalid game type code, than what was agreed to, log it and ignore
					this.logAndPublish.write(this.uniqueID + ": Received Invalid Game Type Code, Ignoring", true, false);
					return;
				}
			}
			else
			{
				// invalid game indicator log it and ignore the message
				this.logAndPublish.write(this.uniqueID + ": Received Invalid Game Indicator Code, Ignoring", true, false);
				return;
			}
		}
		else
		{
			// invalid type indicator for this state, log it and ignore the message
			this.logAndPublish.write(this.uniqueID + ": Recieved Invalid Type Indicator Code, Ignoring", true, false);
		}		
	}
	/**
   * GamePlayState - In this state the client is playing the game. It can only leave this state by sending
   * a get game list request.
   * @param inputBuffer
   * @param iByteCount
   * @return none
   */   
   private void GamePlayState(byte[] inputBuffer, int iByteCount) {
      /*
      * While in this state the server can only transition to the GameList state or stay in the current state
      */
      if (this.messageParser.GetVersion(inputBuffer, iByteCount) != this.m_iVersion)
      {
		/* Invalid version, ignoring this message */
         this.logAndPublish.write(this.uniqueID + ": has sent an invalid version number, Ignoring Msg", true, false);
         return;
      }	
	  // Make sure the message is the correct play game message
      if (this.messageParser.GetTypeIndicator(inputBuffer, iByteCount) == MessageParser.TYPE_INDICATOR_GAME)
		{
         if (this.messageParser.GetGameIndicator(inputBuffer, iByteCount) == MessageParser.GAME_INDICATOR_PLAY_GAME)
         {
            MessageParser.ClientPlayGameMessage msg = this.messageParser.GetClientPlayGameMessage(inputBuffer, iByteCount);
            // make sure this was indeed a client get game message
            if (msg.getGameIndicator() != MessageParser.GAME_INDICATOR_PLAY_GAME)
            {
               // not a client message, must be a server message. Ignore
               this.logAndPublish.write(this.uniqueID + ": Received Server Play Game Message, Ignoring", true, false);
               return;
            }
            if (msg.getGameTypeCode() != this.m_iGameTypeCode)
            {
               // not a valid game type code, Ignore
               this.logAndPublish.write(this.uniqueID + ": Client Play Game Message contains invalid Game Type Code, Ignoring", true, false);
               return;
            }
            if (this.m_iGameTypeCode == MessageParser.GAME_TYPE_TEXAS_HOLDEM)
            {
				// get the message that should be sent from the server by the game model
               MessageParser.ServerPlayGameMessage svrMsg = oTHModel.updateModel(msg);
               // update client bank account
               this.m_lClientBankAmount = svrMsg.getBankAmount();
               // reset the timer
               this.timeoutTimer.reschedule(m_lGameOpTimer);
               try	{
            	   /* MESSAGE MANAGEMENT */
                  outputStream.writeByte((byte)this.messageParser.CreateServerPlayGameMessage(svrMsg).length);
                  outputStream.write(this.messageParser.CreateServerPlayGameMessage(svrMsg)); 
                  outputStream.flush();		
                  // reset the timeout
                  this.socket.setSoTimeout((int) (this.m_lGameOpTimer+2000));
               } catch (Exception e) {
            	   logAndPublish.write(e, true, false);
                  this.timeoutTimer.stop();
                  this.gameState.setState(GameState.CLOSED);
               }
            }
			else
			{
				// invalid game type, log it and ignore it
				logAndPublish.write(this.uniqueID + " has sent an invalid Game Type Code, Ignoring", true, false);
			}
         }
         else if (this.messageParser.GetGameIndicator(inputBuffer, iByteCount) == MessageParser.GAME_INDICATOR_GET_GAME)
         {
        	 MessageParser.ClientGetGameMessage msg = this.messageParser.GetClientGetGameMessage(inputBuffer, iByteCount);
        	 // make sure this was indeed a client get game message
        	 if (msg.getGameIndicator() != MessageParser.GAME_INDICATOR_GET_GAME)
        	 {
        		 // not a client message, must be a server message. Ignore
        		 this.logAndPublish.write(this.uniqueID + ": Received Server Get Game Message, Ignoring", true, false);
        		 return;
        	 }
        	 // send the client the game list and switch to the GameList state
        	 int length = 12 + (int)(Math.ceil((double)this.oGameTypeList.size() / 4) * 4);
        	 MessageParser.ServerGetGameMessage svrMsg = this.messageParser.new ServerGetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_GET_GAME, length, this.oGameTypeList);
        	 // reset the timeout
        	 this.timeoutTimer.reschedule(this.m_lGameOpTimer);
        	 this.logAndPublish.write(this.uniqueID + ": has sent get game message", true, false);
        	 try	{
        		 /* MESSAGE MANAGEMENT */
        		 outputStream.writeByte((byte)this.messageParser.CreateServerGetGameMessage(svrMsg).length);
        		 outputStream.write(this.messageParser.CreateServerGetGameMessage(svrMsg)); 
        		 outputStream.flush();		
        		 // reset the socket timeout
        		 this.socket.setSoTimeout((int) (this.m_lGameOpTimer+2000));
        		 this.gameState.setState(GameState.GAMELIST);
        	 } catch (Exception e) {
        		 logAndPublish.write(e, true, false);
        		 this.timeoutTimer.stop();
        		 this.gameState.setState(GameState.CLOSED);
        	 }
         }
         else
         {
			// invalid Game Message that was received for this state, ignore it
            this.logAndPublish.write(this.uniqueID + ": has sent an invalid Game Message, Ignoring", true, false);
            return;
         }		
      }
      else
      {
		// invalid type code for this state, ignore it
         this.logAndPublish.write(this.uniqueID + ": has sent an invalid Type Indicator, Ignoring", true, false);
         return;
      }
   }
	/**
	* The TimeoutTask Class
	*
	*	This class is used to create a timeout to avoid looping and provide reliability
	*	It also handles closing the connection when the timeout occurs
	*  
	*/
	class TimeoutTask extends TimerTask {
		private ClientModel model;
		/**
		* Constructor
		*
		*/
		public TimeoutTask(ClientModel model) {
			this.model = model;
		}
		/**
		* run - main method for the thread to run
		* @param none
		* @return none
		*/
		public void run() {
			/*
			 * A timeout occurred, determine if the connection was already closed, and if not close it
			 */
			if (this.model.socket.isClosed())
			{
				this.model.gameState.setState(GameState.CLOSED);
				this.model.timeoutTimer.stop();
				this.model.logAndPublish.write("Timeout event: " + this.model.uniqueID + ": has already closed the connection, exiting thread", true, false);
				return;
			}
			else
			{
				this.model.logAndPublish.write(this.model.uniqueID + ": Timer expired Closing connection", true, false);
				this.model.timeoutTimer.stop();
				this.model.gameState.setState(GameState.CLOSED);
			}
		}
	}
	/**
	* The TimeoutTimer Class
	*
	*	This class actually maintains the timer and stores the time for
	*	it to go off
	*  
	*/
	class TimeoutTimer extends Timer {
		/* private members */
		private TimerTask task;
		private Timer timer;
		private boolean bRunning;
		private boolean bSchedule;
		private ClientModel model;
		/**
		* Constructor
		*
		*/
		public TimeoutTimer(ClientModel model) {
			timer = new Timer();
			bRunning = false;
			bSchedule = false;
			this.model = model;
		}
		/**
		* schedule - method to create a timer and assign it the task
		* @param delay
		* @return none
		*/
		public void schedule(long delay)
		{
			this.task = new TimeoutTask(this.model);
			bRunning = true;
			bSchedule = true;
			timer.schedule(task, delay);
		}
		/**
		* reschedule - method to reschedule a timeout
		* this is used when a valid message is received by the client
		* @param delay
		* @return none
		*/
		public void reschedule(long delay)
		{
			/* only reschedule if one is first scheduled */
			if (bSchedule)
			{
				this.stop();
				task = new TimeoutTask(this.model);
				this.schedule(task, delay);
				bRunning = true;
			}
		}
		/**
		* stop - method to stop the timer
		* @param none
		* @return none
		*/
		public void stop()
		{
			/* if it is running stop it */
			if (bRunning)
			{
				bRunning = false;
				task.cancel();
			}
		}
	}
}
