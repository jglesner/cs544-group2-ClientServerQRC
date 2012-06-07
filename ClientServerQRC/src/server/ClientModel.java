/**
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  
 *  This is an example of a client side application using the Advanced Game Management Protocol to send messages   
 * 
 *  This application framework originally drew heavily from the following resource:
 *  1. Saleem, Usman. "A Pattern/Framework for Client/Server Programming in Java". Year accessed: 2012, Month accessed: 05, Day accessed: 2.
 *  http://www.developer.com/java/ent/article.php/10933_1356891_2/A-PatternFramework-for-ClientServer-Programming-in-Java.htm
 *  
 *  However, the code has changed significantly since that time. Other contributing resources: 
 *  
 *  2. Oracle Corporation. "Java™ Secure Socket Extension (JSSE) Reference Guide". Java SE Documentation. Year accessed: 2012,
 *  Month accessed: 05, Day accessed: 2. http://docs.oracle.com/javase/7/docs/technotes/guides/security/jsse/JSSERefGuide.html
 *   
 *  3. StackOverflow. "How to get a path to a resource in a Java JAR file". Year accessed: 2012, Month accessed: 05, Day accessed: 2.
 *  http://stackoverflow.com/questions/941754/how-to-get-a-path-to-a-resource-in-a-java-jar-file
 *  
 *  4. IBM. "Custom SSL for advanced JSSE developers". Year accessed: 2012, Month accessed: 05, Day accessed: 2.
 *  http://www.ibm.com/developerworks/java/library/j-customssl/
 *  
 */

package server;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import javax.net.ssl.SSLSocket;
import server.card_game.texas_holdem.TexasHoldemModel;
import java.util.logging.*;
import common.*;

/**
 * ClientModel is spawned, one per client to manage the socket connection
 * and in-game functions using AGMP as a communications protocol.
 * 
 * @author GROUP2
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
   private final Logger fLogger;
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
    
   public ClientModel(SSLSocket socket, XmlParser xmlParser, Logger fLogger) throws IOException {
      this.socket = socket;
      this.uniqueID = "" + socket.getInetAddress() + ":" + socket.getPort();
      this.fLogger = fLogger;
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
      this.fLogger.info(this.uniqueID +": Successfully connected");
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
    
   /* This functions are getters to be used by the server game models */
   public Logger getLogger()
   {
      return fLogger;
   }
   public XmlParser getXmlParser()
   {
      return xmlParser;
   }
   public MessageParser getMessageParser()
   {
      return messageParser;
   }
   public int getVersion()
   {
      return m_iVersion;
   }
   public long getClientBankAmount()
   {
      return m_lClientBankAmount;
   }
	
    /** 
     *Stops clients connection
     */
   public void stopClient()
   {
      try {
         this.fLogger.info(this.uniqueID + ": Stopping Client");
         this.gameState.setState(GameState.CLOSED);
         this.socket.close();
      }catch(IOException ioe){ };
   }

   public void run() 
   {
      try
      {
         // force the client to send version info
         this.timeoutTimer.schedule(this.m_lVersionOpTimer);
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
         this.fLogger.info(this.uniqueID + ": " + ioe);
      }
        
      //it's time to close the socket
      if (!this.socket.isClosed())
      {
         try {
            this.socket.close();
            this.fLogger.info(this.uniqueID + ": Closing Connection.");
         } catch (IOException ioe) { }
      }
      //notify the observers for cleanup etc.
      this.setChanged();              //inherit from Observable
      this.notifyObservers(this);     //inherit from Observable
   }
   
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
				this.fLogger.info(this.uniqueID + ": has finished authenticating!");
				try	{
               outputStream.writeByte((byte)this.messageParser.CreateVersionMessage(msg).length);
               outputStream.write(this.messageParser.CreateVersionMessage(msg)); 
               outputStream.flush();					
					this.gameState.setState(GameState.AUTHENTICATE);
				} catch (Exception e) {
               e.printStackTrace();
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
				this.fLogger.info(this.uniqueID + ": Invalid Version, closing connection");
				try {
               outputStream.writeByte((byte)this.messageParser.CreateVersionMessage(msg).length);
               outputStream.write(this.messageParser.CreateVersionMessage(msg)); 
               outputStream.flush();					
					this.timeoutTimer.stop();
					this.gameState.setState(GameState.CLOSED);
				} catch (Exception e) {
					e.printStackTrace();
					this.timeoutTimer.stop();
					this.gameState.setState(GameState.CLOSED);
				}
			}
			else
			{
				msg.setBankAmount((long)0);
				msg.setMinorVersion(this.m_iMinorVersion);
				msg.setTypeCode(MessageParser.TYPE_INDICATOR_VERSION);
				msg.setVersion(this.m_iVersion);
				msg.setVersionType(MessageParser.VERSION_INDICATOR_VERSION_REQUIREMENT);
				this.fLogger.info(this.uniqueID + ": Invalid message, need client protocol version");
				try {
					//oOutputStream.write(this.messageParser.CreateVersionMessage(msg));
					outputStream.writeByte((byte)this.messageParser.CreateVersionMessage(msg).length);
			        outputStream.write(this.messageParser.CreateVersionMessage(msg)); 
			        outputStream.flush();
				} catch (Exception e) {
					e.printStackTrace();
					this.timeoutTimer.stop();
					this.gameState.setState(GameState.CLOSED);
				}
			}
		}
		else
		{
			// client did not send the right message, server needs to force a version message
			this.fLogger.info(this.uniqueID + ": Need to finish authentication of version, got a different message");
			MessageParser.VersionMessage msg = this.messageParser.new VersionMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_VERSION, MessageParser.VERSION_INDICATOR_VERSION_REQUIREMENT, this.m_iMinorVersion,(long)0);
			try {
            outputStream.writeByte((byte)this.messageParser.CreateVersionMessage(msg).length);
            outputStream.write(this.messageParser.CreateVersionMessage(msg)); 
            outputStream.flush();				
			} catch (Exception e) {
				e.printStackTrace();
				this.gameState.setState(GameState.CLOSED);
			}
		}
		
	}	
   
   private void AuthenticateState(byte[] inputBuffer, int iByteCount) throws IOException {
		/*
      *  During this state the client has authenticated and the server is waiting for the get games message
      */
      // make sure the version is correct
      if (this.messageParser.GetVersion(inputBuffer, iByteCount) != this.m_iVersion)
      {
         this.fLogger.info(this.uniqueID + ": has sent an invalid version number, Ignoring Msg");
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
               this.fLogger.info(this.uniqueID + ": Received Server Get Game Message, Ignoring");
               return;
            }
            // send the client the game list
            int length = 12 + (int)(Math.ceil((double)this.oGameTypeList.size() / 4) * 4);
            MessageParser.ServerGetGameMessage svrMsg = this.messageParser.new ServerGetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_GET_GAME, length, this.oGameTypeList);
            this.timeoutTimer.reschedule(this.m_lGameOpTimer);
				this.fLogger.info(this.uniqueID + ": has sent get game message");
				try	{
               outputStream.writeByte((byte)this.messageParser.CreateServerGetGameMessage(svrMsg).length);
               outputStream.write(this.messageParser.CreateServerGetGameMessage(svrMsg)); 
               outputStream.flush();					
					this.gameState.setState(GameState.GAMELIST);
				} catch (Exception e) {
               e.printStackTrace();
					this.timeoutTimer.stop();
					this.gameState.setState(GameState.CLOSED);
				}
			}
			else
			{
				// The game indicator is incorrect
				this.fLogger.info(this.uniqueID + ": Invalid Game Indicator, should be Get Games");
			}
      }
      else
      {
			// The game indicator is incorrect
         this.fLogger.info(this.uniqueID + ": Invalid Type Indicator, should be the Game Indicator");
		}		
	}	
      
   private void GameListState(byte[] inputBuffer, int iByteCount) {
      /*
      * In this state there are two valid messages
      * 1) Set Game
      * 2) Close Connection
      */
		// First check to make sure the version is correct
		if (this.messageParser.GetVersion(inputBuffer, iByteCount) != this.m_iVersion)
		{
			this.fLogger.info(this.uniqueID + ": has sent an invalid version number, Ignoring Msg");
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
               this.fLogger.info(this.uniqueID + ": Received Server Set Game Message, Ignoring");
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
               MessageParser.ServerSetGameMessage svrMsg = this.messageParser.new ServerSetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_SET_GAME, msg.getGameTypeCode(), MessageParser.GAME_TYPE_RESPONSE_ACK);
               this.timeoutTimer.reschedule(m_lGameOpTimer);
               this.gameState.setState(GameState.GAMESET);
               this.m_iGameTypeCode = msg.getGameTypeCode();
               this.fLogger.info(this.uniqueID + ": has sent a valid Game Type Indicator");
               try	{
                  outputStream.writeByte((byte)this.messageParser.CreateServerSetGameMessage(svrMsg).length);
                  outputStream.write(this.messageParser.CreateServerSetGameMessage(svrMsg)); 
                  outputStream.flush();						
               } catch (Exception e) {
                  e.printStackTrace();
                  this.timeoutTimer.stop();
                  this.gameState.setState(GameState.CLOSED);
               }
            }
            else
            {
               MessageParser.ServerSetGameMessage svrMsg = this.messageParser.new ServerSetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_SET_GAME, msg.getGameTypeCode(), MessageParser.GAME_TYPE_RESPONSE_INVALID);
               this.timeoutTimer.reschedule(m_lGameOpTimer);
               this.fLogger.info(this.uniqueID + ": has sent an invalid Game Type Indicator");
               try	{
                  outputStream.writeByte((byte)this.messageParser.CreateServerSetGameMessage(svrMsg).length);
                  outputStream.write(this.messageParser.CreateServerSetGameMessage(svrMsg)); 
                  outputStream.flush();						
               } catch (Exception e) {
                  e.printStackTrace();
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
               this.fLogger.info(this.uniqueID + ": Received Server Get Game Message, Ignoring");
               return;
            }
            // send the client the game list
            int length = 12 + (int)(Math.ceil((double)this.oGameTypeList.size() / 4) * 4);
            MessageParser.ServerGetGameMessage svrMsg = this.messageParser.new ServerGetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_GET_GAME, length, this.oGameTypeList);
            this.timeoutTimer.reschedule(this.m_lGameOpTimer);
				this.fLogger.info(this.uniqueID + ": has sent get game message");
				try	{
               outputStream.writeByte((byte)this.messageParser.CreateServerGetGameMessage(svrMsg).length);
               outputStream.write(this.messageParser.CreateServerGetGameMessage(svrMsg)); 
               outputStream.flush();					
					this.gameState.setState(GameState.GAMELIST);
				} catch (Exception e) {
               e.printStackTrace();
					this.timeoutTimer.stop();
					this.gameState.setState(GameState.CLOSED);
				}
			}
         else
         {
            this.fLogger.info(this.uniqueID + ": has sent an invalid Game Indicator, should be Set Game or Get Game");
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
            this.fLogger.info(this.uniqueID + ": has sent a request to close the connection");
            this.gameState.setState(GameState.CLOSED);
            try	{
               outputStream.writeByte((byte)this.messageParser.CreateConnectionMessage(msg).length);
               outputStream.write(this.messageParser.CreateConnectionMessage(msg)); 
               outputStream.flush();	
            } catch (Exception e) {
               e.printStackTrace();
               this.timeoutTimer.stop();
               this.gameState.setState(GameState.CLOSED);
            }
         }
         else
         {
            this.fLogger.info(this.uniqueID + ": has sent an invalid connection message");
         }
      }
      else
      {
         this.fLogger.info(this.uniqueID + ": has sent an invalid message");
      }            
	}
   
   private void GameSetState(byte[] inputBuffer, int iByteCount) {
      /* 
      * In this state the client can only send the play game init message
      */
		// First check to make sure the version is correct
		if (this.messageParser.GetVersion(inputBuffer, iByteCount) != this.m_iVersion)
		{
			this.fLogger.info(this.uniqueID + ": has sent an invalid version number, Ignoring Msg");
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
					this.fLogger.info(this.uniqueID + ": Received Server Play Game Message, Ignoring");
					return;
				}
				if (msg.getGameTypeCode() != this.m_iGameTypeCode)
				{
					// invalid game type code - different than agreed to, ignore
					this.fLogger.info(this.uniqueID + ": Received Invalid Game Type Code, Ignoring");
					return;
				}
            
				// determine the game model to initialize
				if (msg.getGameTypeCode() == MessageParser.GAME_TYPE_TEXAS_HOLDEM)
				{
					if (msg.getGamePlayRequest() == MessageParser.GAME_PLAY_REQUEST_INIT)
					{
						this.oTHModel.Reset();
						this.gameState.setState(GameState.GAMEPLAY);
						MessageParser.ServerPlayGameMessage svrMsg = oTHModel.updateModel(msg);
						// update client bank account
						this.m_lClientBankAmount = svrMsg.getBankAmount();
						this.timeoutTimer.reschedule(m_lGameOpTimer);
						try	{
							outputStream.writeByte((byte)this.messageParser.CreateServerPlayGameMessage(svrMsg).length);
							outputStream.write(this.messageParser.CreateServerPlayGameMessage(svrMsg)); 
							outputStream.flush();						
						} catch (Exception e) {
							e.printStackTrace();
							this.timeoutTimer.stop();
							this.gameState.setState(GameState.CLOSED);
						}
					}
					else
					{
						this.fLogger.info(this.uniqueID + ": Received Invalid GamePlay message, Ignoring");
						return;
					}
				}
				else
				{
					this.fLogger.info(this.uniqueID + ": Received Invalid Game Type Code, Ignoring");
					return;
				}
			}
			else
			{
				this.fLogger.info(this.uniqueID + ": Received Invalid Game Indicator Code, Ignoring");
				return;
			}
		}
		else
		{
			this.fLogger.info(this.uniqueID + ": Recieved Invalid Type Indicator Code, Ignoring");
		}		
	}

   private void GamePlayState(byte[] inputBuffer, int iByteCount) {
      /*
      * While in this state the server can only transition to the GameList state or stay in the current state
      */
      if (this.messageParser.GetVersion(inputBuffer, iByteCount) != this.m_iVersion)
      {
         this.fLogger.info(this.uniqueID + ": has sent an invalid version number, Ignoring Msg");
         return;
      }	
      if (this.messageParser.GetTypeIndicator(inputBuffer, iByteCount) == MessageParser.TYPE_INDICATOR_GAME)
		{
         if (this.messageParser.GetGameIndicator(inputBuffer, iByteCount) == MessageParser.GAME_INDICATOR_PLAY_GAME)
         {
            MessageParser.ClientPlayGameMessage msg = this.messageParser.GetClientPlayGameMessage(inputBuffer, iByteCount);
            // make sure this was indeed a client get game message
            if (msg.getGameIndicator() != MessageParser.GAME_INDICATOR_PLAY_GAME)
            {
               // not a client message, must be a server message. Ignore
               this.fLogger.info(this.uniqueID + ": Received Server Play Game Message, Ignoring");
               return;
            }
            if (msg.getGameTypeCode() != this.m_iGameTypeCode)
            {
               // not a valid game type code, Ignore
               this.fLogger.info(this.uniqueID + ": Client Play Game Message contains invalid Game Type Code, Ignoring");
               return;
            }
            if (this.m_iGameTypeCode == MessageParser.GAME_TYPE_TEXAS_HOLDEM)
            {
               MessageParser.ServerPlayGameMessage svrMsg = oTHModel.updateModel(msg);
               // update client bank account
               this.m_lClientBankAmount = svrMsg.getBankAmount();
               this.timeoutTimer.reschedule(m_lGameOpTimer);
               try	{
                  outputStream.writeByte((byte)this.messageParser.CreateServerPlayGameMessage(svrMsg).length);
                  outputStream.write(this.messageParser.CreateServerPlayGameMessage(svrMsg)); 
                  outputStream.flush();						
               } catch (Exception e) {
                  e.printStackTrace();
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
               this.fLogger.info(this.uniqueID + ": Received Server Get Game Message, Ignoring");
               return;
            }
            // send the client the game list
            int length = 12 + (int)(Math.ceil((double)this.oGameTypeList.size() / 4) * 4);
            MessageParser.ServerGetGameMessage svrMsg = this.messageParser.new ServerGetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_GET_GAME, length, this.oGameTypeList);
            this.timeoutTimer.reschedule(this.m_lGameOpTimer);
				this.fLogger.info(this.uniqueID + ": has sent get game message");
				try	{
               outputStream.writeByte((byte)this.messageParser.CreateServerGetGameMessage(svrMsg).length);
               outputStream.write(this.messageParser.CreateServerGetGameMessage(svrMsg)); 
               outputStream.flush();					
					this.gameState.setState(GameState.GAMELIST);
				} catch (Exception e) {
               e.printStackTrace();
					this.timeoutTimer.stop();
					this.gameState.setState(GameState.CLOSED);
				}
         }
         else
         {
            this.fLogger.info(this.uniqueID + ": has sent an invalid Game Message, Ignoring");
            return;
         }		
      }
      else
      {
         this.fLogger.info(this.uniqueID + ": has sent an invalid Type Indicator, Ignoring");
         return;
      }
   }
	
	class TimeoutTask extends TimerTask {
		private ClientModel model;
		public TimeoutTask(ClientModel model) {
			this.model = model;
		}
		public void run() {
			/*
			 * A timeout occurred, determine if the connection was already closed, and if not close it
			 */
			if (this.model.socket.isClosed())
			{
				this.model.gameState.setState(GameState.CLOSED);
				this.model.timeoutTimer.stop();
				this.model.fLogger.info("Timeout event: " + this.model.uniqueID + ": has already closed the connection, exiting thread");
				return;
			}
			else
			{
				this.model.fLogger.info(this.model.uniqueID + ": Timer expired Closing connection");
				this.model.timeoutTimer.stop();
				this.model.gameState.setState(GameState.CLOSED);
			}
		}
	}
	
	class TimeoutTimer extends Timer {
		private TimerTask task;
		private Timer timer;
		private boolean bRunning;
		private boolean bSchedule;
		private ClientModel model;
		public TimeoutTimer(ClientModel model) {
			timer = new Timer();
			bRunning = false;
			bSchedule = false;
			this.model = model;
		}
		
		public void schedule(long delay)
		{
			this.task = new TimeoutTask(this.model);
			bRunning = true;
			bSchedule = true;
			timer.schedule(task, delay);
		}
		
		public void reschedule(long delay)
		{
			if (bSchedule)
			{
				this.stop();
				task = new TimeoutTask(this.model);
				this.schedule(task, delay);
				bRunning = true;
			}
		}
		
		public void stop()
		{
			if (bRunning)
			{
				bRunning = false;
				task.cancel();
			}
		}
	}
}
