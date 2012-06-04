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
import common.GameState.State;
import common.MessageParser.ChallengeIndicator;
import common.MessageParser.ConnectionIndicator;
import common.MessageParser.GameIndicator;
import common.MessageParser.GameTypeCode;
import common.MessageParser.GameTypeResponse;
import common.MessageParser.TypeIndicator;
import common.MessageParser.VersionIndicator;

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
    private boolean m_bSendClose = false;
    
    public String uniqueID;
    private final Logger fLogger;
    private XmlParser xmlParser;
    private MessageParser messageParser = null;
    private TexasHoldemModel oTHModel = null;
    private int m_iVersion = -1;
    private short m_iMinorVersion = -1;
    private long m_lClientBankAmount = -1;
    private long m_lOpTimer = -1;

    private TimeoutTimer timeoutTimer;
    private boolean bSentChallenge = false;
    private boolean m_bGotGames = false;
    private boolean m_bSetGames = false;
    private GameTypeCode m_eGameTypeCode = GameTypeCode.NOT_SET;
    private List<MessageParser.GameTypeCode> oGameTypeList = null;
    
    public ClientModel(SSLSocket socket, XmlParser xmlParser, Logger fLogger) throws IOException {
        this.socket = socket;
        this.uniqueID = "" + socket.getInetAddress() + ":" + socket.getPort();
        this.fLogger = fLogger;
        this.xmlParser = xmlParser;
        this.gameState = new GameState();
        this.gameState.setState(State.AUTHENTICATE);
        this.messageParser = new MessageParser();
        this.m_iVersion = Integer.parseInt(this.xmlParser.getServerTagValue("VERSION"));
        this.m_iMinorVersion = (short)Integer.parseInt(this.xmlParser.getServerTagValue("MINOR_VERSION"));
        this.m_lClientBankAmount = Integer.parseInt(this.xmlParser.getServerTagValue("CLIENT_BANK_AMOUNT"));
        this.m_lOpTimer = Integer.parseInt((this.xmlParser.getServerTagValue("OPERATION_TIMER")));
        this.timeoutTimer = new TimeoutTimer(this);
        this.oTHModel = new TexasHoldemModel(this);
        this.m_bGotGames = false;
        this.m_bSetGames = false;
        this.m_eGameTypeCode = GameTypeCode.NOT_SET;
        // instantiate the server game list
        this.oGameTypeList = new ArrayList<MessageParser.GameTypeCode>();
        for (MessageParser.GameTypeCode typecode : MessageParser.GameTypeCode.values())
        {
        	oGameTypeList.add(typecode);
        }
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
        this.gameState.setState(State.CLOSED);
		this.socket.close();
        }catch(IOException ioe){ };
    }

    public void run() {
        //byte[] inputBuffer = new byte[100];
        //int iByteCount = -1;
        
        //start listening message from client//
        try 
        {
        	this.timeoutTimer.schedule(this.m_lOpTimer);
        	
        	
        	while (running && !this.socket.isClosed() && this.gameState.getState() != State.CLOSED && !this.m_bSendClose ) // && (iByteCount = oInputStream.read(inputBuffer)) > 0)
        	{
        		
        		//ByteArrayInputStream baos = new ByteArrayInputStream(oInputStream);
        		//int bytesWritten = 0;
//        		while(true){
//        			iByteCount = oInputStream.read(inputBuffer);
//        			//baos.write(inputBuffer, bytesWritten, iByteCount);
//        			baos.write(inputBuffer);
//        			//bytesWritten += iByteCount;
//        			
//        			if (this.messageParser.GetVersion(baos.toByteArray(), baos.size()) == this.m_iVersion)
//        			{
//        				break;
//        			}		
//        		}
        		 
        		byte iByteCount = inputstream.readByte();
        		byte [] inputBuffer = new byte[iByteCount];
        		inputstream.readFully(inputBuffer); 
        		
        		if (this.gameState.getState().isEqual(State.AUTHENTICATE))
        		{
        			System.out.println("entering authenticate");
        			AuthenticateState(inputBuffer, iByteCount);
        		}
        		else if (this.gameState.getState().isEqual(State.GAMELIST))
        		{
        			System.out.println("entering list");
        			GameListState(inputBuffer, iByteCount);
        		}
        		else if (this.gameState.getState().isEqual(State.GAMESET))
        		{
        			System.out.println("entering set");
        			GameSetState(inputBuffer, iByteCount);
        		}        		
        		
        		else if (this.gameState.getState().isEqual(State.GAMEPLAY))
        		{
        			System.out.println("entering play");
        			GamePlayState(inputBuffer, iByteCount);
        		}
        		else if (this.gameState.getState().isEqual(State.CLOSING))
        		{
        			System.out.println("entering closing");
        			ClosingState(inputBuffer, iByteCount);
        		}
        		System.out.println(running);
        		System.out.println(this.gameState.getState());
        		System.out.println(this.m_bSendClose);
        	}
        	
        	
        	if (this.gameState.getState() == State.CLOSING)
    		{
    			SendCloseAck();
    		}
        	running = false;
        }
        catch (IOException ioe) {
        	running = false;
        	this.fLogger.info(this.uniqueID + ": " + ioe);
        	ioe.printStackTrace();
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
    
    private void SendCloseAck()
    {
    	MessageParser.ConnectionMessage msg = this.messageParser.new ConnectionMessage(this.m_iVersion, TypeIndicator.CLOSE_CONNECTION, ConnectionIndicator.CLOSE_CONNECTION_ACK);
    	try	{
    		this.timeoutTimer.stop();
			oOutputStream.write(this.messageParser.CreateConnectionMessage(msg));
			this.gameState.setState(State.CLOSED);
			this.fLogger.info(this.uniqueID + ": Sent Close Ack");
		} catch (Exception e) {
			e.printStackTrace();
			this.timeoutTimer.stop();
			this.gameState.setState(State.CLOSED);
		}
    }

	private void ClosingState(byte[] inputBuffer, int iByteCount) {
		/*
		 * The server is now in the closing state
		 * In order to be here it must have sent the closing message and is waiting for the ack
		 */
		MessageParser.ConnectionMessage msg = null;
		// Make sure the version and identifier are correct
		if (this.messageParser.GetVersion(inputBuffer, iByteCount) == this.m_iVersion && this.messageParser.GetTypeIndicator(inputBuffer, iByteCount) == TypeIndicator.CLOSE_CONNECTION)
		{
			msg = this.messageParser.GetConnectionMessage(inputBuffer, iByteCount);
			if (msg.getConnectionCode().isEqual(ConnectionIndicator.CLOSE_CONNECTION_ACK))
			{
				this.fLogger.info(this.uniqueID + ": Got Close Ack, Closing connection");
				this.timeoutTimer.stop();
				this.gameState.setState(State.CLOSED);
			}
		}
	}

	private void GamePlayState(byte[] inputBuffer, int iByteCount) {
		// The server should never be receiving a message in this state
		// It should be in the wait state and then transition to the game state to complete the request
		// but then transition back once it is done
		//this.fLogger.info(this.uniqueID + ": Error Server Caught in Game State!");
		//this.fLogger.info(this.uniqueID + ": Trying to fix by switching to Wait State");
		//this.gameState.setState(State.GAMELIST);	
		if (this.messageParser.GetVersion(inputBuffer, iByteCount) != this.m_iVersion)
		{
			this.fLogger.info(this.uniqueID + ": has sent an invalid version number, Ignoring Msg");
			return;
		}	
		if (this.messageParser.GetGameIndicator(inputBuffer, iByteCount).isEqual(GameIndicator.PLAY_GAME) && this.m_bSetGames)
		{
			this.gameState.setState(State.GAMEPLAY);
			MessageParser.ClientPlayGameMessage msg = this.messageParser.GetClientPlayGameMessage(inputBuffer, iByteCount);
			// make sure this was indeed a client get game message
			if (!msg.getGameIndicator().isEqual(GameIndicator.PLAY_GAME))
			{
				// not a client message, must be a server message. Ignore
				this.fLogger.info(this.uniqueID + ": Received Server Play Game Message, Ignoring");
				this.gameState.setState(State.GAMEPLAY);
				return;
			}
			if (!msg.getGameTypeCode().isEqual(this.m_eGameTypeCode))
			{
				// not a valid game type code, Ignore
				this.fLogger.info(this.uniqueID + ": Client Play Game Message contains invalid Game Type Code, Ignoring");
				this.gameState.setState(State.GAMEPLAY);
				return;
			}
			if (this.m_eGameTypeCode.isEqual(GameTypeCode.TEXAS_HOLDEM))
			{
				MessageParser.ServerPlayGameMessage svrMsg = oTHModel.updateModel(msg);
				// update client bank account
				if (svrMsg != null)
				{
					this.m_lClientBankAmount = svrMsg.getBankAmount();
					this.timeoutTimer.reschedule(m_lOpTimer);
					try	{
						oOutputStream.write(this.messageParser.CreateServerPlayGameMessage(svrMsg));
						this.gameState.setState(State.GAMEPLAY);
					} catch (Exception e) {
						e.printStackTrace();
						this.timeoutTimer.stop();
						this.gameState.setState(State.CLOSED);
					}
				}
			}
		}
		else if (this.messageParser.GetGameIndicator(inputBuffer, iByteCount).isEqual(GameIndicator.GET_GAME))
		{
			this.fLogger.info(this.uniqueID + ": is exiting Game Play State and returning to Game List State");
			this.gameState.setState(State.GAMELIST);
			return;
		}
		else
		{
			this.fLogger.info(this.uniqueID + ": has sent an invalid Game Message, Ignoring");
		}		
	}
	
	private void GameSetState(byte[] inputBuffer, int iByteCount) {
		// First check to make sure the version is correct
		if (this.messageParser.GetVersion(inputBuffer, iByteCount) != this.m_iVersion)
		{
			this.fLogger.info(this.uniqueID + ": has sent an invalid version number, Ignoring Msg");
			return;
		}		
		
		if (this.messageParser.GetGameIndicator(inputBuffer, iByteCount).isEqual(GameIndicator.SET_GAME) & this.m_bGotGames)
		{
			//this.gameState.setState(State.GAMEPLAY);
			MessageParser.ClientSetGameMessage msg = this.messageParser.GetClientSetGameMessage(inputBuffer, iByteCount);
			// make sure this was indeed a client get game message
			if (!msg.getGameIndicator().isEqual(GameIndicator.SET_GAME))
			{
				// not a client message, must be a server message. Ignore
				this.fLogger.info(this.uniqueID + ": Received Server Set Game Message, Ignoring");
				this.gameState.setState(State.GAMESET);
				return;
			}
			// Check to make sure it is one of the valid Game Type codes
			if (msg.getGameTypeCode().isEqual(GameTypeCode.TEXAS_HOLDEM))
			{
				// reset the game model
				this.oTHModel.Reset();
				this.m_eGameTypeCode = msg.getGameTypeCode();
				this.gameState.setState(State.GAMEPLAY);
			}
			else
			{
				this.fLogger.info(this.uniqueID + ": Received Invalid Game Type Code");
				this.m_bSetGames = false;
				this.timeoutTimer.reschedule(m_lOpTimer);
				MessageParser.ServerSetGameMessage svrMsg = this.messageParser.new ServerSetGameMessage(m_iVersion, TypeIndicator.SET, GameIndicator.SET_GAME, msg.getGameTypeCode(), GameTypeResponse.INVALID);
				try	{
					//oOutputStream.write(this.messageParser.CreateServerSetGameMessage(svrMsg));
					outputStream.writeByte((byte)this.messageParser.CreateServerSetGameMessage(svrMsg).length);
			        outputStream.write(this.messageParser.CreateServerSetGameMessage(svrMsg)); 
			        outputStream.flush();
					this.gameState.setState(State.GAMELIST);
				} catch (Exception e) {
					e.printStackTrace();
					this.timeoutTimer.stop();
					this.gameState.setState(State.CLOSED);
				}
				return;
			}
			this.fLogger.info(this.uniqueID + ": Received Client Set Game Message, Sending Ack");
			this.m_bSetGames = true;
			this.timeoutTimer.reschedule(m_lOpTimer);
			MessageParser.ServerSetGameMessage svrMsg = this.messageParser.new ServerSetGameMessage(m_iVersion, TypeIndicator.SET, GameIndicator.SET_GAME, this.m_eGameTypeCode, GameTypeResponse.ACK);
			try	{
				//oOutputStream.write(this.messageParser.CreateServerSetGameMessage(svrMsg));
				outputStream.writeByte((byte)this.messageParser.CreateServerSetGameMessage(svrMsg).length);
		        outputStream.write(this.messageParser.CreateServerSetGameMessage(svrMsg)); 
		        outputStream.flush();				
				this.gameState.setState(State.GAMEPLAY);
			} catch (Exception e) {
				e.printStackTrace();
				this.timeoutTimer.stop();
				this.gameState.setState(State.CLOSED);
			}
		}
		
	}
	

	private void GameListState(byte[] inputBuffer, int iByteCount) {
		// First check to make sure the version is correct
		if (this.messageParser.GetVersion(inputBuffer, iByteCount) != this.m_iVersion)
		{
			this.fLogger.info(this.uniqueID + ": has sent an invalid version number, Ignoring Msg");
			return;
		}
		if (this.messageParser.GetTypeIndicator(inputBuffer, iByteCount).isEqual(TypeIndicator.LIST))
		{
			// first transition to GameListState
			//this.gameState.setState(State.GAMESET);
			if (this.messageParser.GetGameIndicator(inputBuffer, iByteCount).isEqual(GameIndicator.GET_GAME))
			{
				MessageParser.ClientGetGameMessage msg = this.messageParser.GetClientGetGameMessage(inputBuffer, iByteCount);
				// make sure this was indeed a client get game message
				if (!msg.getGameIndicator().isEqual(GameIndicator.GET_GAME))
				{
					// not a client message, must be a server message. Ignore
					this.fLogger.info(this.uniqueID + ": Received Server Get Game Message, Ignoring");
					this.gameState.setState(State.GAMELIST);
					return;
				}
				this.fLogger.info(this.uniqueID + ": Received Client Get Game Message, Sending Games");
				this.m_bGotGames = true;
				this.m_bSetGames = false;
				this.m_eGameTypeCode = GameTypeCode.NOT_SET;
				this.timeoutTimer.reschedule(m_lOpTimer);
				MessageParser.ServerGetGameMessage svrMsg = this.messageParser.new ServerGetGameMessage(m_iVersion, TypeIndicator.LIST, GameIndicator.GET_GAME, 0, oGameTypeList);
				try	{
					//oOutputStream.write(this.messageParser.CreateServerGetGameMessage(svrMsg));
					outputStream.writeByte((byte)this.messageParser.CreateServerGetGameMessage(svrMsg).length);
			        outputStream.write(this.messageParser.CreateServerGetGameMessage(svrMsg)); 
			        outputStream.flush();
					this.gameState.setState(State.GAMESET);
				} catch (Exception e) {
					e.printStackTrace();
					this.timeoutTimer.stop();
					this.gameState.setState(State.CLOSED);
				}
			}
			else if (this.messageParser.GetTypeIndicator(inputBuffer, iByteCount).isEqual(TypeIndicator.CLOSE_CONNECTION))
			{
				MessageParser.ConnectionMessage msg = this.messageParser.GetConnectionMessage(inputBuffer, iByteCount);
				if (msg.getConnectionCode().isEqual(ConnectionIndicator.CLOSE_CONNECTION))
				{
					this.fLogger.info(this.uniqueID + ": Close connection message received");
					this.timeoutTimer.reschedule(this.m_lOpTimer);
					this.gameState.setState(State.CLOSING);
					this.m_bSendClose = true;
				}
				else
				{
					this.fLogger.info(this.uniqueID + ": sent invalid Close Connection Msg, Ignoring");
				}
			}
			else
			{
				this.fLogger.info(this.uniqueID + ": has sent an invalid Msg for the List State");
			}
			
		}
	}

	
//		if (this.messageParser.GetTypeIndicator(inputBuffer, iByteCount).isEqual(TypeIndicator.CHALLENGE_CONNECTION))
//		{
//			MessageParser.ChallengeMessage msg = this.messageParser.GetChallengeMessage(inputBuffer, iByteCount);
//			if (msg.getChallengeCode().isEqual(ChallengeIndicator.CHALLENGE_CONNECTION))
//			{
//				// server needs to send an ack
//				this.fLogger.info(this.uniqueID + ": has sent a Challenge Connection Msg, Sending Ack");
//				this.timeoutTimer.reschedule(m_lOpTimer);
//				msg.setChallengeCode(ChallengeIndicator.CHALLENGE_CONNECTION_ACK);
//				try	{
//					oOutputStream.write(this.messageParser.CreateChallengeMessage(msg));
//				} catch (Exception e) {
//					e.printStackTrace();
//					this.timeoutTimer.stop();
//					this.gameState.setState(State.CLOSED);
//				}
//			}
//			else if (msg.getChallengeCode().isEqual(ChallengeIndicator.CHALLENGE_CONNECTION_ACK) && this.bSentChallenge)
//			{
//				// received the challenge ack
//				this.fLogger.info(this.uniqueID + ": has sent back the Challenge Connection Ack!");
//				this.bSentChallenge = false;
//				this.timeoutTimer.reschedule(m_lOpTimer);
//			}
//			else
//			{
//				this.fLogger.info(this.uniqueID + ": has sent an invalid Challenge Connection Message, Ignoring");
//			}
//		}

	private void AuthenticateState(byte[] inputBuffer, int iByteCount) throws IOException {
		/*
		 *  During this state the client has to authenticate itself by giving its version to the server
		 *  If the client sends a version message the server will check to see if it is valid and reply appropriately.
		 *  The server will start the counter for the verification. If the client does not finish authentication within the 
		 *  timeout time the connection will be closed
		 */
		if (this.messageParser.GetTypeIndicator(inputBuffer, iByteCount).isEqual(TypeIndicator.VERSION))
		{

			MessageParser.VersionMessage msg = this.messageParser.GetVersionMessage(inputBuffer, iByteCount);
			if ((msg.getVersion() == this.m_iVersion) && (msg.getVersionType().isEqual(VersionIndicator.CLIENT_VERSION)))
			{
				// reset the timer
				this.timeoutTimer.reschedule(this.m_lOpTimer);
				msg.setBankAmount(this.m_lClientBankAmount);
				msg.setVersionType(VersionIndicator.VERSION_ACK);
				this.fLogger.info(this.uniqueID + ": has finished authenticating!");
				try	{
					//oOutputStream.write(this.messageParser.CreateVersionMessage(msg));
					outputStream.writeByte((byte)this.messageParser.CreateVersionMessage(msg).length);
			        outputStream.write(this.messageParser.CreateVersionMessage(msg)); 
			        outputStream.flush();					
					this.gameState.setState(State.GAMELIST);
				} catch (Exception e) {
					e.printStackTrace();
					this.timeoutTimer.stop();
					this.gameState.setState(State.CLOSED);
				}
			}
			else if (msg.getVersionType().isEqual(VersionIndicator.CLIENT_VERSION))
			{
				// client needs to upgrade
				// The server cannot communicate so send the message and close the connection
				msg.setBankAmount((long)0);
				msg.setVersion(this.m_iVersion);
				msg.setMinorVersion(this.m_iMinorVersion);
				msg.setTypeCode(TypeIndicator.VERSION);
				msg.setVersionType(VersionIndicator.VERSION_UPGRADE);
				this.fLogger.info(this.uniqueID + ": Invalid Version, closing connection");
				try {
					//oOutputStream.write(this.messageParser.CreateVersionMessage(msg));
					outputStream.writeByte((byte)this.messageParser.CreateVersionMessage(msg).length);
			        outputStream.write(this.messageParser.CreateVersionMessage(msg)); 
			        outputStream.flush();					
					this.timeoutTimer.stop();
					this.gameState.setState(State.CLOSED);
				} catch (Exception e) {
					e.printStackTrace();
					this.timeoutTimer.stop();
					this.gameState.setState(State.CLOSED);
				}
			}
			else
			{
				msg.setBankAmount((long)0);
				msg.setMinorVersion(this.m_iMinorVersion);
				msg.setTypeCode(TypeIndicator.VERSION);
				msg.setVersion(this.m_iVersion);
				msg.setVersionType(VersionIndicator.VERSION_REQUIREMENT);
				this.fLogger.info(this.uniqueID + ": Invalid message, need client protocol version");
				try {
					//oOutputStream.write(this.messageParser.CreateVersionMessage(msg));
					outputStream.writeByte((byte)this.messageParser.CreateVersionMessage(msg).length);
			        outputStream.write(this.messageParser.CreateVersionMessage(msg)); 
			        outputStream.flush();
				} catch (Exception e) {
					e.printStackTrace();
					this.timeoutTimer.stop();
					this.gameState.setState(State.CLOSED);
				}
			}
		}
//		else if (this.messageParser.GetTypeIndicator(inputBuffer, iByteCount).isEqual(TypeIndicator.CLOSE_CONNECTION))
//		{
//			MessageParser.ConnectionMessage msg = this.messageParser.GetConnectionMessage(inputBuffer, iByteCount);
//			if (msg.getConnectionCode().isEqual(ConnectionIndicator.CLOSE_CONNECTION))
//			{
//				/*
//				 *  Client wishes to close the connection. The server will gracefully close the connection
//				 *  Indicate that we are transitioning to the closing state and that the server was not the
//				 *  one to issue the request
//				 */
//				this.fLogger.info(this.uniqueID + ": Close connection message received");
//				this.timeoutTimer.reschedule(this.m_lOpTimer);
//				this.gameState.setState(State.CLOSING);
//				this.m_bSendClose = true;
//			}
//			else
//			{
//				/*
//				 * This was an erroneous close connection acknowledgment message, ignore it and require a login
//				 */
//				this.fLogger.info(this.uniqueID + ": Need to finish authentication of version, got a different message");
//				MessageParser.VersionMessage vmsg = this.messageParser.new VersionMessage(this.m_iVersion, TypeIndicator.VERSION, VersionIndicator.VERSION_REQUIREMENT, this.m_iMinorVersion,(long)0);
//				try {
//					oOutputStream.write(this.messageParser.CreateVersionMessage(vmsg));
//				} catch (Exception e) {
//					e.printStackTrace();
//					this.timeoutTimer.stop();
//					this.gameState.setState(State.CLOSED);
//				}
//			}
//		}	
		else
		{
			// client did not send the right message, server needs to force a version message
			this.fLogger.info(this.uniqueID + ": Need to finish authentication of version, got a different message");
			MessageParser.VersionMessage msg = this.messageParser.new VersionMessage(this.m_iVersion, TypeIndicator.VERSION, VersionIndicator.VERSION_REQUIREMENT, this.m_iMinorVersion,(long)0);
			try {
				//oOutputStream.write(this.messageParser.CreateVersionMessage(msg));
				outputStream.writeByte((byte)this.messageParser.CreateVersionMessage(msg).length);
		        outputStream.write(this.messageParser.CreateVersionMessage(msg)); 
		        outputStream.flush();				
			} catch (Exception e) {
				e.printStackTrace();
				this.gameState.setState(State.CLOSED);
			}
		}
		
	}	
	
	class TimeoutTask extends TimerTask {
		private ClientModel model;
		public TimeoutTask(ClientModel model) {
			this.model = model;
		}
		public void run() {
			/*
			 * A timeout occurred, determine if a challenge connection needs to be sent or if the connection needs to be closed
			 */
			if (this.model.socket.isClosed())
			{
				this.model.gameState.setState(State.CLOSED);
				this.model.timeoutTimer.stop();
				this.model.fLogger.info("Timeout event: " + this.model.uniqueID + ": has already closed the connection, exiting thread");
				return;
			}
			if (this.model.gameState.getState() == State.GAMELIST || this.model.gameState.getState() == State.GAMEPLAY)
			{
				if (this.model.bSentChallenge)
				{
					// consider connection dead
					this.model.fLogger.info(this.model.uniqueID + ": Timer expired Closing connection");
					this.model.timeoutTimer.stop();
					this.model.gameState.setState(State.CLOSED);
				}
				else
				{
					// send the challenge connection message
					this.model.timeoutTimer.reschedule(this.model.m_lOpTimer);
					this.model.bSentChallenge = true;
					this.model.fLogger.info(this.model.uniqueID + ": Timer expired Sending Challenge Connection request");
					MessageParser.ChallengeMessage msg = this.model.messageParser.new ChallengeMessage(this.model.m_iVersion, TypeIndicator.CHALLENGE_CONNECTION, ChallengeIndicator.CHALLENGE_CONNECTION);
					try {
						this.model.oOutputStream.write(this.model.messageParser.CreateChallengeMessage(msg));
					} catch (Exception e) {
						e.printStackTrace();
						this.model.gameState.setState(State.CLOSED);
						this.model.timeoutTimer.stop();
					}
				}
			}
			else
			{
				this.model.fLogger.info(this.model.uniqueID + ": Timer expired Closing connection");
				this.model.timeoutTimer.stop();
				this.model.gameState.setState(State.CLOSED);
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
