/**
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  Members: Jeremy Glesner, Dustin Overmiller, Yiqi Ju, Lei Yuan
 *	Project: Advanced Game Message Protocol Implementation
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
package client;

import java.io.*;
import java.security.*;
import java.util.ArrayList;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import client.card_game.ClientPokerModel;
import client.findServer.EchoFinder;
import client.view.TexasGame;
import client.view.TexasGame.InputState;
import client.view.Welcome;
import common.card_game.*;
import common.*;


/**
 *  SecureClientController class manages the logic for the client side
 *  within the AGMP example application.
 *   
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  Members: Jeremy Glesner, Dustin Overmiller, Yiqi Ju, Lei Yuan
 *  Project: Advanced Game Message Protocol Implementation
 *  
 */
public class SecureClientController implements Runnable {

	/* PKI references */
	private String trustStore=null;
	private String trustStorePassword=null;	
	private String keyStore=null;
	private String keyStorePassword=null; 
	
	/* management mechanisms */
    private GameState gameState;
    private MessageParser messageParser;
	private boolean connected;
	private LogAndPublish logAndPublish;
	private long bankAmount;
	private long betAmount;
	
	/* connectivity mechanisms */    
	private SecureClientController c = null;
	private SSLSocketFactory ssf = null;	
    private SSLSocket socket;
	private final XmlParser xmlParser;
	private BufferedReader br;
	private InputStreamReader isr;
    private InputStream oInputStream;
    private OutputStream oOutputStream;	
	private ObjectOutputStream outputStream; 
	private ObjectInputStream inputstream;     
    private int port = 0; 
    private String hostName= null;
    /* Game parameters */
    private int m_iVersion = -1;
    private int m_iMinorVersion = -1;
    private GamePlayState gamePlayState = null;
	
	/*UI */
    private Welcome welcomeFrame=null;
    private TexasGame texasFrame=null;
    private ClientPokerModel holdemModel= null;
    
    /**
     * Constructor for this class.
     * @param xmlParser Incoming XML Parser
     * @param fLogger Incoming Logger
     */
    public SecureClientController(XmlParser xmlParser, LogAndPublish logAndPublish, Welcome welcomeFrame, ClientPokerModel pokerModel, TexasGame texasgame) 
    {
        
		/* setup parser */
    	this.xmlParser = xmlParser;
    	this.logAndPublish = logAndPublish;
    	
    	/* instantiate remaining variables */
    	this.gameState = new GameState();
        this.gameState.setState(GameState.LISTENING);
        this.messageParser = new MessageParser();
        this.isr = new InputStreamReader(System.in);
        this.br = new BufferedReader(isr);
	    this.port = Integer.parseInt(this.xmlParser.getClientTagValue("PORT_NUMBER"));
	    this.connected = false;
		this.trustStore=this.xmlParser.getClientTagValue("DEFAULT_TRUSTSTORE");
		this.trustStorePassword=this.xmlParser.getClientTagValue("DEFAULT_TRUSTSTORE_PASSWORD");
		this.keyStore=this.xmlParser.getClientTagValue("DEFAULT_KEYSTORE");
		this.keyStorePassword=this.xmlParser.getClientTagValue("DEFAULT_KEYSTORE_PASSWORD");    
		this.hostName = this.xmlParser.getClientTagValue("HOSTNAME");
		this.m_iVersion = Integer.parseInt(this.xmlParser.getClientTagValue("VERSION"));
		this.m_iMinorVersion = Integer.parseInt(this.xmlParser.getClientTagValue("MINOR_VERSION"));
		this.bankAmount = 0;
		this.gamePlayState = new GamePlayState();
		this.welcomeFrame = welcomeFrame;
		this.welcomeFrame.setVisible(true);
		this.holdemModel = pokerModel;
		this.texasFrame = texasgame;
		
		// log this information to the log file
		logAndPublish.write("Setting port number to:" + this.port, true, false);
		logAndPublish.write("Using TrustStore: " + this.trustStore, true, false);
		logAndPublish.write("Setting KeyStore: " + this.keyStore, true, false);
	
    }    
    
    /**
     * isConnected - Accessor method to see if the client is still connected
     * @param none
     * @return boolean
     */
    public boolean isConnected() {
		return connected;
    }

    /**
     * getPort - Accessor method to get port
     * @param none    
     * @return
     */
    public int getPort(){
            return port;
        }

    /**
     * setPort - Mutator method to set port
     * @param int port
	 * @return none
     */      
    public void setPort(int port){
            this.port = port;
        }

    /**
     * getHostName - Accessor method to get hostname      
     * @param none
     * @return String
     */
    public String getHostName(){
            return hostName;
        }

    /**
     * setHostName - Mutator method to set hostname
     * @param String hostName
     * @return none 
     */       
    public void setHostName(String hostName){
            this.hostName = hostName;
        }   
    
    
    /**
     * start - start method to initiate Secure Client Connection to an AGMP Server
	 * @param none
	 * @return none
     */   
	public void start()
	{
		
		/* Print to console and log file */
		logAndPublish.write("Client connecting to server...", true, true);

		/* instantiate a new SecureClientController */
		try {
			c = new SecureClientController(this.xmlParser, logAndPublish, this.welcomeFrame, this.holdemModel, this.texasFrame);
	        ssf=getSSLSocketFactory();
		}
		catch(Exception e)
		{
			/* Print to console and log file */
			logAndPublish.write(e, true, true);			
		}		
        
		/* Connect to server identified in configuration settings */
		try
		{
	        c.connect(ssf,c.hostName,c.port);
		} catch (IOException e) 
		{
			/* Log and Publish */
			logAndPublish.write(e, true, false);
		}
		
		/* find server using ICMP/TCP methods */
		if (!c.connected) {

			
			try {
				c.findConnect(ssf, port);
			} catch (Exception e) 
			{
				/* Log and Publish */
				logAndPublish.write(e, true, true);				
			}
		}
		
		/* Log and Publish */
		logAndPublish.write("Client connected.", true, true);

	}

    /**
     * run - run method executes when a new SecureClientController thread, 
     * complete with socket is started 
     * @param none
     * @return none
     */
    public void run() {
       try 
       {
    	   /* Game loop */
    	   while(connected && this.gameState.getState() != GameState.CLOSED) 
    	   {
    		   if (this.gameState.getState() == GameState.LISTENING)
    		   {
    			   /* Enter the Listening state */
    			   logAndPublish.write("Enter Listening State.", false, false);
    			   GameListeningState();
    		   }
    		   else if (this.gameState.getState() == GameState.AUTHENTICATE)
    		   {
    				/* Enter the Connection Negotiation state */
    				logAndPublish.write("Enter Authentication State.", false, false);
    				GameAuthenticateState();
    		   	}
 
    		   	else if (this.gameState.getState() == GameState.GAMELIST)
	       		{
    				/* Enter the Game List State */
    				logAndPublish.write("Enter List State.", false, false);    				
	       			GameListState();
	       		}    	        		
    		   	else if (this.gameState.getState() == GameState.GAMESET)
        		{
    				/* Enter the Game Set State */
    				logAndPublish.write("Enter Set State.", false, false);    				
        			GameSetState();
        		}        		
        		
        		else if (this.gameState.getState() == GameState.GAMEPLAY)
        		{
    				/* Enter the Game Play State */
    				logAndPublish.write("Enter Play State.", false, false);    				
        			GamePlayGameState();
        		}
				/* Log and Publish - currently disabled */
				logAndPublish.write("Connection status: " + connected, false, false);
				
				/* Log and Publish - currently disabled*/
				logAndPublish.write("Game state: " + this.gameState.getState(), false, false);				

        	}
    	   /* Connection was closed */
    	   connected = false;
    	   System.exit(0);
		}
		catch(Exception e)
		{
      		/* Log and Publish */
      		logAndPublish.write(e, true, false); 
      		logAndPublish.write("Closing Connection", true, true);
      		this.disconnect();
      		System.exit(0);
		}	    		   
        finally { connected = false; }
    }
	
	/**
	 * GamePlayGameState - method contains all the logic to interface with the server
	 * during game play. It will read the input, transition to the proper state and
	 * send an output message
	 * @param none
	 * @return none
	 */
	private void GamePlayGameState() 
	{

		/* set variables */
		int command = 0;
		while(this.gameState.getState() == GameState.GAMEPLAY)
		{
			try
			{
				/* Get the server response message */
				ServerResponse sr = this.receiveMessage();
				/* Check to make sure it has the correct version number */
				if (this.messageParser.GetVersion(sr.getMessage(), sr.getSize()) != this.m_iVersion)
				{
					logAndPublish.write("Ignoring message with incorrect version number", true, false);
					break;
				}
				/* Verify the message received was a Play Game Message */
				if (this.messageParser.GetTypeIndicator(sr.getMessage(), sr.getSize()) == MessageParser.TYPE_INDICATOR_GAME)
				{
					if (this.messageParser.GetGameIndicator(sr.getMessage(), sr.getSize()) == MessageParser.GAME_INDICATOR_PLAY_GAME)
					{
						/* Get the server message */
						MessageParser.ServerPlayGameMessage svrPlayMsg = this.messageParser.GetServerPlayGameMessage(sr.getMessage(), sr.getSize());	
						if (svrPlayMsg.getGameIndicator() != MessageParser.GAME_INDICATOR_PLAY_GAME)
						{
							/* May have received a client play game message but this is not the correct server msg, ignore it */
							logAndPublish.write("Ignoring Incorrect Play Game Message", true, false);
							break;
						}
						/* Handle the different Game Play States */
						if (this.gamePlayState.getPlayState() == GamePlayState.INIT)
						{
							if (svrPlayMsg.getGamePlayResponse() == MessageParser.GAME_PLAY_RESPONSE_INIT_ACK)
							{
								/* minimum ante allowable */
								int min_ante = svrPlayMsg.getAnte();
								this.bankAmount = svrPlayMsg.getBankAmount();
								/* sub-state: Verify player has enough money to play and send NOT_SET*/
								if (this.bankAmount < min_ante)
								{
									logAndPublish.write("Bank Account too low, sending game list message", true, false);
									this.texasFrame.popMessage("Insufficient funds in your account.  Returning to game list");
									MessageParser.ClientGetGameMessage oMsg1 = this.messageParser.new ClientGetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_GET_GAME);
									this.sendMessage(this.messageParser.CreateClientGetGameMessage(oMsg1));
									this.texasFrame.setVisible(false);
									this.gameState.setState(GameState.GAMELIST); 
									break;
								}	
							
								// make sure the client wants to play
								this.welcomeFrame.setVisible(false);
								this.texasFrame.setVisible(false);
								this.holdemModel.reset();
								/* update the GUI with the server's response */
							    this.texasFrame=new TexasGame(holdemModel);
								this.texasFrame.setMinAnte(min_ante);
								this.texasFrame.getPokerModel().setlBankAmount(bankAmount);
								this.texasFrame.init();
								this.texasFrame.setVisible(true);
								this.texasFrame.setInfo("The minimum ante is "+min_ante);
								this.texasFrame.setAnte();
								
								/* wait for client to bet */
								while(this.texasFrame.getbPlayGames() == InputState.NOT_SET)
								{
									try {
										Thread.currentThread();
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// do nothing
									}
								}
								
								/* get the user ante and then send the appropriate response and transition to the appropriate
								 * state
								 */
								int userAnte=this.texasFrame.getUserAnte();
								if(this.texasFrame.getbPlayGames() == InputState.FOLD)
									command=2;
								else
									command=1;
								if (command == 2)
								{
									/* send the Get Game List message and transition to GAMELIST state */
									logAndPublish.write("Sending Get GameList message.", true, false);
									MessageParser.ClientGetGameMessage oMsg1 = this.messageParser.new ClientGetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_GET_GAME);
									this.sendMessage(this.messageParser.CreateClientGetGameMessage(oMsg1));
									this.texasFrame.setVisible(false);
									this.gameState.setState(GameState.GAMELIST); 
									break;
								}
								// send GET_HOLE message and transition to the next phase
								MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_GET_HOLE, (long)userAnte);  
								this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));	
								logAndPublish.write("Sending GET_HOLE request", true, false);
								this.gamePlayState.setPlayState(GamePlayState.GET_HOLE);
							}
							else
							{
								/* write error to log file */
								logAndPublish.write("Ignoring invalid gameplay response from server", true, false);
							}
						}
						else if (this.gamePlayState.getPlayState() == GamePlayState.GET_HOLE)
						{
							
							if (svrPlayMsg.getGamePlayResponse() == MessageParser.GAME_PLAY_RESPONSE_GET_HOLE_ACK)
							{
								/* display server msg in the GUI*/
								getGameInfo(sr);
								this.bankAmount = svrPlayMsg.getBankAmount();
								this.texasFrame.setPokerModel(this.holdemModel);
								this.texasFrame.setInfo("Continue To Play, you must bet twice your ante amount.");
								this.texasFrame.getPokerModel().getoDealerCards()[0].setVisible(false);
								this.texasFrame.getPokerModel().getoDealerCards()[1].setVisible(false);
								this.texasFrame.setHoleCards();
								
								int orig_ante = svrPlayMsg.getAnte();
					             while(this.texasFrame.getFollow() == InputState.NOT_SET)
					             {
					            	 try {
											Thread.currentThread();
											Thread.sleep(100);
										} catch (InterruptedException e) {
											// do nothing
										}
					             }
					             if(this.texasFrame.getFollow() == InputState.FOLLOW)
					            	 command=1;
					             else
					            	 command=2;
								/* handle user selection */
								if (command == 1)
								{
									if ((orig_ante*2) > bankAmount)
									{
										// not enough money in the users account send them to the Game List state
										logAndPublish.write("Bank Account too low sending Game List message.", true, false);
										this.gameState.setState(GameState.GAMELIST);
										this.welcomeFrame.popMessage("You do not have enough money to keep playing.  Goodbye.");
										this.texasFrame.setVisible(false);
										MessageParser.ClientGetGameMessage getMsg = this.messageParser.new ClientGetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_GET_GAME);
										this.sendMessage(this.messageParser.CreateClientGetGameMessage(getMsg));
										break;
									}		
									else
									{
										/* send GET_FLOP message */
										MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_GET_FLOP, (long)(orig_ante*2));
										this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));	
										logAndPublish.write("Getting the Flop Cards", true, false);
										this.texasFrame.setFollow(InputState.NOT_SET);
										this.gamePlayState.setPlayState(GamePlayState.GET_FLOP);
									}
								}
								else
								{
									// send fold message and go to fold phase
									logAndPublish.write("Sending a fold message", true, false);
									MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_FOLD, (long)(orig_ante));
									this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));	
									this.gamePlayState.setPlayState(GamePlayState.FOLD);
								}
							}
							else if (svrPlayMsg.getGamePlayResponse() == MessageParser.GAME_PLAY_RESPONSE_INVALID_ANTE_BET)
							{
								int min_ante = svrPlayMsg.getAnte();
								this.bankAmount = svrPlayMsg.getBankAmount();
								/* sub-state: Verify player has enough money to play and send NOT_SET*/
								if (this.bankAmount < min_ante)
								{
									logAndPublish.write("Bank Account too low to play, sending GameList message", true, false);
									MessageParser.ClientGetGameMessage oMsg1 = this.messageParser.new ClientGetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_GET_GAME);
									this.sendMessage(this.messageParser.CreateClientGetGameMessage(oMsg1));
									this.gameState.setState(GameState.GAMELIST); 
									this.texasFrame.popMessage("Insufficient funds in your account.  Returning to game list");
									this.texasFrame.setVisible(false);
									break;
								}	
							
								// print the server response
								this.getGameInfo(sr);
								// get the ante from the client
								this.texasFrame.popMessage("ERROR: Invalid Ante Bet.");
								this.texasFrame.basicRefresh();
								this.texasFrame.setbPlayGames(InputState.NOT_SET);
								/* loop to handle user input */
								
								while(this.texasFrame.getbPlayGames() == InputState.NOT_SET)
								{
									try {
										Thread.currentThread();
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// do nothing
									}
								}
								
								int userAnte=this.texasFrame.getUserAnte();
								// send GET_HOLE message and transition to the next phase
								MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_GET_HOLE, (long)userAnte);  
								this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));	
								logAndPublish.write("Sending GET_HOLE request", true, false);
							}
							else
							{
								/* log the invalid gamplay message */
								logAndPublish.write("Ignoring invalid gameplay message", true, false);
							}
						}
						else if (this.gamePlayState.getPlayState() == GamePlayState.GET_FLOP)
						{
							if (svrPlayMsg.getGamePlayResponse() == MessageParser.GAME_PLAY_RESPONSE_GET_FLOP_ACK)
							{
								// received a flop acknowledgement update the GUI with the data
								int orig_ante = svrPlayMsg.getAnte();
								this.bankAmount = svrPlayMsg.getBankAmount();
								/* display server response */
								this.getGameInfo(sr);
								this.texasFrame.setInfo("To bet, you may only bet the original ante amount.");
								this.texasFrame.setPokerModel(holdemModel);
								this.texasFrame.setFlopCards();
								this.texasFrame.setFollow(InputState.NOT_SET);
								
								/* loop to handle user input */
					            while(this.texasFrame.getFollow() == InputState.NOT_SET)
					            {
					            	try {
										Thread.currentThread();
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// do nothing
									}
					            }
					            if(this.texasFrame.getFollow() == InputState.FOLLOW)
					            	command=1;
					            else if(this.texasFrame.getFollow() == InputState.CHECK)
					            	command=2;
					            else
					            	command=3;
								/* handle user selection */
								if (command == 1) 
								{								
									if ((orig_ante) > bankAmount)
									{
										logAndPublish.write("Sending Get Turn Card message", true, false);
										/* send GET_TURN message with bet set to zero */
										this.texasFrame.popMessage("You do not have enough money to bet, checking.");
										this.texasFrame.setFollow(InputState.NOT_SET);
										MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_GET_TURN, (long)0);
								        this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));						
										this.gamePlayState.setPlayState(GamePlayState.GET_TURN);
									} 
									else
									{				
										/* send GET_TURN message */
										logAndPublish.write("Sending Get Turn Card message", true, false);
										/* send GET_TURN message */
										this.texasFrame.setFollow(InputState.NOT_SET);
										MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_GET_TURN, (long)orig_ante);
								        this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));						
										this.gamePlayState.setPlayState(GamePlayState.GET_TURN);
										}
									}
								else if (command == 2) 
								{	
									logAndPublish.write("Sending Get Turn Card message", true, false);
									/* send GET_TURN message with the bet set to zero*/
									this.texasFrame.setFollow(InputState.NOT_SET);
									MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_GET_TURN, (long)0);
							        this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));						
									this.gamePlayState.setPlayState(GamePlayState.GET_TURN);
								}	
								else
								{
									// send fold message and go to fold phase
									logAndPublish.write("Sending a fold message", true, false);
									this.texasFrame.setFollow(InputState.NOT_SET);
									MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_FOLD, (long)(orig_ante));
									this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));	
									this.gamePlayState.setPlayState(GamePlayState.FOLD);
								}
							}
							else if (svrPlayMsg.getGamePlayResponse() == MessageParser.GAME_PLAY_RESPONSE_INVALID_HOLE_BET)
							{
								/* display server msg */
								this.getGameInfo(sr);
								this.texasFrame.popMessage("ERROR: Invalid Hole Bet");
								this.texasFrame.setPokerModel(holdemModel);
								this.texasFrame.basicRefresh();
								this.texasFrame.setFollow(InputState.NOT_SET);
								/* wait on input from the user */
								while(this.texasFrame.getFollow() == InputState.NOT_SET)
								{
									logAndPublish.write("waiting for user to choose", false, false);
									try {
										Thread.currentThread();
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// do nothing
									}
								}
								if(this.texasFrame.getFollow() == InputState.FOLLOW)
									command=1;
								else
									command=2;
								
								this.bankAmount = svrPlayMsg.getBankAmount();
								int orig_ante = svrPlayMsg.getAnte();
								/* handle user selection */
								if (command == 1)
								{
									if ((orig_ante*2) > bankAmount)
									{
										/* user does not have enough money to play */
										logAndPublish.write("Bank Account too low to continue, sending GameList Message", true, false);
										this.welcomeFrame.popMessage("You do not have enough money to keep playing.  Goodbye.");
										this.texasFrame.setVisible(false);
										this.gameState.setState(GameState.GAMELIST);
										MessageParser.ClientGetGameMessage getMsg = this.messageParser.new ClientGetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_GET_GAME);
										this.sendMessage(this.messageParser.CreateClientGetGameMessage(getMsg));
										break;
									}		
									else
									{
										/* send GET_FLOP message */
										MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_GET_FLOP, (long)(orig_ante*2));
										this.texasFrame.setFollow(InputState.NOT_SET);
										this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));	
										logAndPublish.write("Getting the Flop Cards", true, false);
										this.gamePlayState.setPlayState(GamePlayState.GET_FLOP);
									}
								}
								else
								{
									// send fold message and go to fold phase
									logAndPublish.write("Sending a fold message", true, false);
									this.texasFrame.setFollow(InputState.NOT_SET);
									MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_FOLD, (long)(orig_ante));
									this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));	
									this.gamePlayState.setPlayState(GamePlayState.FOLD);
								}
							}
							else
							{
								/* log this incorrect msg to the log file */
								logAndPublish.write("Ignoring invalid gameplay message", true, false);
							}
						}
						else if (this.gamePlayState.getPlayState() == GamePlayState.GET_TURN)
						{
							if (svrPlayMsg.getGamePlayResponse() == MessageParser.GAME_PLAY_RESPONSE_GET_TURN_ACK)
							{
								// update GUI with Turn ack data
								int orig_ante = svrPlayMsg.getAnte();
								this.bankAmount = svrPlayMsg.getBankAmount();
								/* display server msg */
								this.getGameInfo(sr);
								this.texasFrame.setPokerModel(holdemModel);
								this.texasFrame.setInfo("To bet, you may only bet the original ante amount.");
								this.texasFrame.setTurnCard();
								this.texasFrame.setFollow(InputState.NOT_SET);
								
								/* wait for user input */
								while(this.texasFrame.getFollow() == InputState.NOT_SET)
								{
									try {
										Thread.currentThread();
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// do nothing
									}
								}
								
					            if(this.texasFrame.getFollow() == InputState.FOLLOW)
					            	command=1;
					            else if(this.texasFrame.getFollow() == InputState.CHECK)
					            	command=2;
					            else
					            	command=3;
									
								/* handle user selection */
								if (command == 1) 
								{								
									if ((orig_ante) > bankAmount)
									{
										// user does not have enough money, checking
										logAndPublish.write("You do not have enough money to bet, checking.", false, false);
										logAndPublish.write("Sending Get River Card message", true, false);
										this.texasFrame.popMessage("You do not have enough money to bet, checking.");
										/* send GET_TURN message */
										MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_GET_RIVER, (long)0);
								        this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));						
										this.gamePlayState.setPlayState(GamePlayState.GET_RIVER);
									} 
									else
									{				
										/* send GET_RIVER message */
										logAndPublish.write("Sending Get River Card message", true, false);
										/* send GET_TURN message */
										MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_GET_RIVER, (long)orig_ante);
								        this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));						
										this.gamePlayState.setPlayState(GamePlayState.GET_RIVER);
										}
									}
								else if (command == 2) 
								{	
									logAndPublish.write("Sending Get River Card message", true, false);
									/* send GET_TURN message */
									MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_GET_RIVER, (long)0);
							        this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));						
									this.gamePlayState.setPlayState(GamePlayState.GET_RIVER);
								}	
								else
								{
									// send fold message and go to fold phase
									logAndPublish.write("Sending a fold message", true, false);
									MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_FOLD, (long)(orig_ante));
									this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));	
									this.gamePlayState.setPlayState(GamePlayState.FOLD);
								}
							}
							else if (svrPlayMsg.getGamePlayResponse() == MessageParser.GAME_PLAY_RESPONSE_INVALID_FLOP_BET)
							{
								// update GUI with invalid flop bet
								int orig_ante = svrPlayMsg.getAnte();
								this.bankAmount = svrPlayMsg.getBankAmount();
								/* display server msg */
								this.getGameInfo(sr);
								this.texasFrame.setPokerModel(this.holdemModel);
								this.texasFrame.setInfo("To bet, you may only bet the original ante amount.");
								this.texasFrame.popMessage("ERROR: Invalid Flop Bet.");
								this.texasFrame.basicRefresh();
								this.texasFrame.setFollow(InputState.NOT_SET);
								
								// wait for user input
								while(this.texasFrame.getFollow() == InputState.NOT_SET)
								{
									try {
										Thread.currentThread();
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// do nothing
									}
								}
								
					            if(this.texasFrame.getFollow() == InputState.FOLLOW)
					            	command=1;
					            else if(this.texasFrame.getFollow() == InputState.CHECK)
					            	command=2;
					            else
					            	command=3;
					            
								/* handle user selection */
								if (command == 1) 
								{								
									if ((orig_ante) > bankAmount)
									{
										// Insufficient funds in the bank account so send check command
										logAndPublish.write("Bank account too low, checking.", true, false);
										logAndPublish.write("Sending Get Turn Card message", true, false);
										this.texasFrame.popMessage("You do not have enough money to bet, checking.");
										/* send GET_TURN message */
										MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_GET_TURN, (long)0);
								        this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));						
										this.gamePlayState.setPlayState(GamePlayState.GET_TURN);
									} 
									else
									{				
										/* send GET_TURN message */
										logAndPublish.write("Sending Get Turn Card message", true, false);
										/* send GET_TURN message */
										MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_GET_TURN, (long)orig_ante);
								        this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));						
										this.gamePlayState.setPlayState(GamePlayState.GET_TURN);
										}
									}
								else if (command == 2) 
								{	
									logAndPublish.write("Sending Get Turn Card message", true, false);
									/* send GET_TURN message */
									MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_GET_TURN, (long)0);
							        this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));						
									this.gamePlayState.setPlayState(GamePlayState.GET_TURN);
								}	
								else
								{
									// send fold message and go to fold phase
									logAndPublish.write("Sending a fold message", true, false);
									MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_FOLD, (long)(orig_ante));
									this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));	
									this.gamePlayState.setPlayState(GamePlayState.FOLD);
								}
							}
							else
							{
								/* log this incorrect msg to the log file and ignore it */
								logAndPublish.write("Ignoring invalid gameplay message", true, false);
							}
						}
						else if (this.gamePlayState.getPlayState() == GamePlayState.GET_RIVER)
						{
							if (svrPlayMsg.getGamePlayResponse() == MessageParser.GAME_PLAY_RESPONSE_GET_RIVER_ACK)
							{
								// Got the River Ack, update the GUI
								this.bankAmount = svrPlayMsg.getBankAmount();
								/* print server msg and see if the player wants to play again */
							    this.getGameInfo(sr);
							    this.texasFrame.setPokerModel(holdemModel);
							    String swinner="";
							    if(this.holdemModel.getWinner()==1)
							    	swinner="Dealer is winner";
							    else if(this.holdemModel.getWinner()==2)
							    	swinner="You are the winner";
							    else if(this.holdemModel.getWinner()==3)
							    	swinner="it's a draw";
							    this.texasFrame.setInfo(swinner);
							    this.texasFrame.getPokerModel().getoDealerCards()[0].setVisible(true);
							    this.texasFrame.getPokerModel().getoDealerCards()[1].setVisible(true);
							    this.texasFrame.setRiverCard();
							    this.texasFrame.setFollow(InputState.NOT_SET);
							    /* wait for user input */
							    while(this.texasFrame.getFollow() == InputState.NOT_SET)
							    {
							    	try {
										Thread.currentThread();
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// do nothing
									}
							    }
							    
							    if(this.texasFrame.getFollow() == InputState.FOLLOW)
							    	command=1;
							    else
							    	command=2;
							    
							    /* handle user input */
								if (command == 2)
								{
									// go to the gamelist state
									logAndPublish.write("Sending GameList message", true, false);
									this.gameState.setState(GameState.GAMELIST);
									this.texasFrame.setVisible(false);
									MessageParser.ClientGetGameMessage getMsg = this.messageParser.new ClientGetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_GET_GAME);
									this.sendMessage(this.messageParser.CreateClientGetGameMessage(getMsg));
									break;
								}
								// send the init message and change to the init phase
								logAndPublish.write("Sending Play Game Init Message", true, false);
			        			MessageParser.ClientPlayGameMessage playMsg = this.messageParser.new ClientPlayGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_INIT,(long)0);
			        			this.sendMessage(this.messageParser.CreateClientPlayGameMessage(playMsg));
			        			this.gamePlayState.setPlayState(GamePlayState.INIT);
							}
							else if (svrPlayMsg.getGamePlayResponse() == MessageParser.GAME_PLAY_RESPONSE_INVALID_TURN_BET)
							{
								// invalid turn bet, update GUI
								int orig_ante = svrPlayMsg.getAnte();
								this.bankAmount = svrPlayMsg.getBankAmount();
								/* display server msg */
								this.getGameInfo(sr);
								this.texasFrame.setPokerModel(holdemModel);
								this.texasFrame.popMessage("ERROR: Invalid Turn Bet.");
								this.texasFrame.setInfo("To bet, you may only bet the original ante amount.");
								this.texasFrame.basicRefresh();
								this.texasFrame.setFollow(InputState.NOT_SET);
								/* wait for user input */
							    while(this.texasFrame.getFollow() == InputState.NOT_SET)
							    {
							    	try {
										Thread.currentThread();
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// do nothing
									}
							    }
								 if(this.texasFrame.getFollow() == InputState.FOLLOW)
									 command=1;
						         else if(this.texasFrame.getFollow() == InputState.CHECK)
						        	 command=2;
						         else
						        	 command=3;
								
								/* handle user selection */
								if (command == 1) 
								{								
									if ((orig_ante) > bankAmount)
									{
										// user does not have enough money, sending check command
										logAndPublish.write("Insufficient funds, checking", true, false);
										logAndPublish.write("Sending Get River Card message", true, false);
										this.texasFrame.popMessage("You do not have enough money to bet, checking.");
										/* send GET_TURN message */
										MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_GET_RIVER, (long)0);
								        this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));						
										this.gamePlayState.setPlayState(GamePlayState.GET_RIVER);
									} 
									else
									{				
										/* send GET_RIVER message */
										logAndPublish.write("Sending Get River Card message", true, false);
										/* send GET_TURN message */
										MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_GET_RIVER, (long)orig_ante);
								        this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));						
										this.gamePlayState.setPlayState(GamePlayState.GET_RIVER);
										}
									}
								else if (command == 2) 
								{	
									logAndPublish.write("Sending Get River Card message", true, false);
									/* send GET_TURN message */
									MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_GET_RIVER, (long)0);
							        this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));						
									this.gamePlayState.setPlayState(GamePlayState.GET_RIVER);
								}	
								else
								{
									// send fold message and go to fold phase
									logAndPublish.write("Sending a fold message", true, false);
									MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_FOLD, (long)(orig_ante));
									this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));	
									this.gamePlayState.setPlayState(GamePlayState.FOLD);
								}
							}
							else
							{
								// logging the invalid gameplay message
								logAndPublish.write("Ignoring invalid gameplay message", true, false);
							}
						}
						else if (this.gamePlayState.getPlayState() == GamePlayState.FOLD)
						{
							if (svrPlayMsg.getGamePlayResponse() == MessageParser.GAME_PLAY_RESPONSE_FOLD_ACK)
							{
								// received a fold acknowledgement from the server, update the GUI
								this.bankAmount = svrPlayMsg.getBankAmount();
								/* display server msg and see if player wants to play again */
								this.getGameInfo(sr);
								this.texasFrame.setPokerModel(holdemModel);
								this.texasFrame.setInfo("The dealer has won. Do you want to play again?");
								this.texasFrame.setFold();
							    this.texasFrame.setFollow(InputState.NOT_SET);
							    /* wait for user input */
							    while(this.texasFrame.getFollow() == InputState.NOT_SET)
							    {
							    	try {
										Thread.currentThread();
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// do nothing
									}
							    }
							    if(this.texasFrame.getFollow() == InputState.FOLLOW)
							       command=1;
							    else
							       command=2;
							    /* handle user input */
								if (command == 2)
								{
									// go back to the gamelist state
									logAndPublish.write("Sending GameList message", true, false);
									this.texasFrame.setVisible(false);
									this.gameState.setState(GameState.GAMELIST);
									MessageParser.ClientGetGameMessage getMsg = this.messageParser.new ClientGetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_GET_GAME);
									this.sendMessage(this.messageParser.CreateClientGetGameMessage(getMsg));
									break;
								}
								// send the init message and change to the init phase
								logAndPublish.write("Sending Play Game Init Message", true, false);
			        			MessageParser.ClientPlayGameMessage playMsg = this.messageParser.new ClientPlayGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_INIT,(long)0);
			        			this.sendMessage(this.messageParser.CreateClientPlayGameMessage(playMsg));
			        			this.gamePlayState.setPlayState(GamePlayState.INIT);
							}
							else
							{
								/* log the incorrectly received message and ignore it */
								logAndPublish.write("Ignoring invalid gameplay message", true, false);
							}
						}
					}
					else
					{
						/* log the incorrectly received message and ignore it */
						logAndPublish.write("Ignoring invalid game message", true, false);
					}
				}
				else
				{
					/* log the incorrectly received message and ignore it */
					logAndPublish.write("Ignoring invalid type message", true, false);
				}
			} catch(Exception e) {
				logAndPublish.write(e,true,false);
				logAndPublish.write("Closing Connection", true, true);
				this.disconnect();
				System.exit(0);
			}
		}
	}
					
	/**
	 * getGameInfo - this function is used to update the client poker model based
	 * on the input message received from the server
	 * @param sr
	 * @return none
	 */
	private void getGameInfo (ServerResponse sr) 
	{
	    try
	    {
	        /* verify version number is correct and ignore msg if not */
	    	if (this.messageParser.GetVersion(sr.getMessage(), sr.getSize()) != this.m_iVersion)
	    	{
	    		logAndPublish.write("Ignoring message with incorrect version", true, false);
	    		return;
	    	}
	    	/* verify the message is a Play game message and then print to the screen */
	        if (this.messageParser.GetTypeIndicator(sr.getMessage(), sr.getSize()) == MessageParser.TYPE_INDICATOR_GAME)
	        {
	        	if (this.messageParser.GetGameIndicator(sr.getMessage(), sr.getSize()) == MessageParser.GAME_INDICATOR_PLAY_GAME)
	        	{
	        		MessageParser.ServerPlayGameMessage msg = this.messageParser.GetServerPlayGameMessage(sr.getMessage(), sr.getSize());
	        		// make sure this is a server play game message
	        		if (msg.getGameIndicator() != MessageParser.GAME_INDICATOR_PLAY_GAME)
	        		{
	        			logAndPublish.write("Ignoring Incorrect Play Game Message for displaying", true, false);
	        			return;
	        		}
	        		/* Get all the data so it can be printed to the screen */
	        		bankAmount = msg.getBankAmount();
	        	   	betAmount = msg.getBetAmount();
	        	   	this.holdemModel.setiAnte(msg.getAnte());
	        	    this.holdemModel.setlPotSize(msg.getPotSize()); 
	        	    this.holdemModel.setlBankAmount(bankAmount);
	        	    this.holdemModel.setlBetAmount(betAmount);
	        	    
	        	    Card[] playerCards={msg.getPlayerCard1(),msg.getPlayerCard2()};
	        	   	this.holdemModel.setoPlayerCards(playerCards);
	        	   	Card[] dealerCards={msg.getDealerCard1(),msg.getDealerCard2()};
	        	    this.holdemModel.setoDealerCards(dealerCards);  
	        	    
	        	    Card[] flopCards={msg.getFlopCard1(),msg.getFlopCard2(),msg.getFlopCard3()};
	        	   	this.holdemModel.setoFlopCards(flopCards);
	        	   		          
	        	   	this.holdemModel.setoTurnCard(msg.getTurnCard());
	        	   	this.holdemModel.setoRiverCard(msg.getRiverCard());
		            this.holdemModel.setWinner(msg.getWinner());
	        	}
	        	else
	        	{
	        		/* Write to log file the message being ignored */
	        		logAndPublish.write("Ignoring invalid game message", true, false);
	        	}
	        }
	        else
	        {
	        	/* Write to log file the message being ignored */
	        	logAndPublish.write("Ignoring invalid type message", true, false);
	        }
	    } catch (Exception e) {
	    	logAndPublish.write(e,true,false);
	    	logAndPublish.write("Closing Connection", true, true);
	    	this.disconnect();
	    }
	    
	}
	
	/**
	 * GameSetState - method simply informs the client which game has been set
	 * and will send an invalid message if the user selected an invalid game
	 * @param none
	 * @return none
	 * @throws IOException
	 */
	private void GameSetState() throws IOException 
	{
        /* get server response */
        ServerResponse sr = this.receiveMessage();
        /* verify the message has the correct version number */
        if (this.messageParser.GetVersion(sr.getMessage(), sr.getSize()) != this.m_iVersion)
        {
        	/* Ignore it if the message has the wrong version */
        	logAndPublish.write("Ignoring message with an incorrect Version", true, false);
        	return;
        }   
		
        /* verify type as SET GAME */
        if (this.messageParser.GetTypeIndicator(sr.getMessage(), sr.getSize()) == MessageParser.TYPE_INDICATOR_GAME)
        {
        	if (this.messageParser.GetGameIndicator(sr.getMessage(), sr.getSize()) == MessageParser.GAME_INDICATOR_SET_GAME)
        	{
        		/* Get the server message */
        		MessageParser.ServerSetGameMessage msg = this.messageParser.GetServerSetGameMessage(sr.getMessage(), sr.getSize());
        		/* Perform a state change depending upon the server response */
        		if (msg.getGameTypeResponse() == MessageParser.GAME_TYPE_RESPONSE_ACK)
        		{
        			// got a valid response
        			// Send the game play init message and switch to the game play state
        			logAndPublish.write("Sending Play Game Init Message", true, false);
        			MessageParser.ClientPlayGameMessage playMsg = this.messageParser.new ClientPlayGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_PLAY_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM, MessageParser.GAME_PLAY_REQUEST_INIT,(long)0);
        			this.sendMessage(this.messageParser.CreateClientPlayGameMessage(playMsg));
        			this.gameState.setState(GameState.GAMEPLAY);
        			this.gamePlayState.setPlayState(GamePlayState.INIT);
        		}
        		else if (msg.getGameTypeCode() == MessageParser.GAME_TYPE_RESPONSE_INVALID)
        		{
        			// need to resend the get games message and transition to games list state
        			MessageParser.ClientGetGameMessage oMsg1 = this.messageParser.new ClientGetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_GET_GAME);
        			this.sendMessage(this.messageParser.CreateClientGetGameMessage(oMsg1));
        			this.gameState.setState(GameState.GAMELIST); 
        		}
        		else
        		{
        			/* Ignore invalid message */
        			logAndPublish.write("Ignoring invalid Server Set Game Message", true, false);
        		}
        	}
        	else
        	{
        		/* Ignore invalid message */
    			logAndPublish.write("Ignoring invalid Server Game Message", true, false);
        	}
        }
        else
        {
        	/* Ignore invalid message */
			logAndPublish.write("Ignoring invalid Server Type Message", true, false);
        }
	}
	
	/**
	 * GameListState - method enables the user to select which game they 
	 * would like to play or to exit, disconnect and close the client application.
	 * @param none
	 * @return none
	 * @throws IOException
	 */
	private void GameListState() throws IOException 
	{
		
		/* set variables */
		int command = 0;
		
        /* get server response */
        ServerResponse sr = this.receiveMessage();
        /* verify the version of the message is correct and ignore if not */
        if (this.messageParser.GetVersion(sr.getMessage(), sr.getSize()) != this.m_iVersion)
        {
        	logAndPublish.write("Ignoring message with an incorrect Version", true, false);
        	return;
        }
        /* verify the message received is a Get Game Message */
        if (this.messageParser.GetTypeIndicator(sr.getMessage(), sr.getSize()) == MessageParser.TYPE_INDICATOR_GAME)
        {
        	if (this.messageParser.GetGameIndicator(sr.getMessage(), sr.getSize()) == MessageParser.GAME_INDICATOR_GET_GAME)
        	{
        		/* Get the server message and transition appropriately depending on the server response */
        		MessageParser.ServerGetGameMessage svrMsg = this.messageParser.GetServerGetGameMessage(sr.getMessage(), sr.getSize());
        		ArrayList<Integer> gameList = svrMsg.getGameTypeCodeList();
        		/* Log and Publish */
        		logAndPublish.write(svrMsg.toString(), false, false);   
        		this.welcomeFrame.setbGetGameList(true);
        		String []gamebox={"Play Texas Hold\'em","Close Connection"};
        		this.welcomeFrame.setGamelist(gamebox);
        		this.welcomeFrame.update();
        		this.welcomeFrame.resetGameList();
        		this.welcomeFrame.setVisible(true);
        		
                while(this.welcomeFrame.getGameChoice()==null)
                {
                	logAndPublish.write("waiting for user choosing game",false,false);
                	try {
						Thread.currentThread();
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// do nothing
					}
                }
                
        		if(this.welcomeFrame.getGameChoice().contains("Play Texas Hold\'em"))
        			command=1;
        		else
        			command=-1;
		      
        		/* handle user selection */
        		if (command ==1)
        		{
        			/* inform server that user has selected Texas Hold'em */
        			logAndPublish.write("Sending Game Selection Message", true, false);
        			MessageParser.ClientSetGameMessage msg = this.messageParser.new ClientSetGameMessage(1, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_SET_GAME, MessageParser.GAME_TYPE_TEXAS_HOLDEM);  
        			this.sendMessage(this.messageParser.CreateClientSetGameMessage(msg));
        			this.gameState.setState(GameState.GAMESET);
        		} 
        		else 
        		{
        			// send a close connection message and close the connection
        			logAndPublish.write("Sending Close Connection Message", true, false);
        			MessageParser.ConnectionMessage msg = this.messageParser.new ConnectionMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_CLOSE_CONNECTION, MessageParser.CONNECTION_INDICATOR_CLOSE_CONNECTION);
        			this.sendMessage(this.messageParser.CreateConnectionMessage(msg));
        			this.gameState.setState(GameState.CLOSED);
        		}
        	}
        	else
        	{
        		/* Ignore the invalid message for this state */
        		logAndPublish.write("Ignoring wrong Game Message", true, false);
        	}
		}
        else
        {
        	/* Ignore the invalid message for this state */
        	logAndPublish.write("Ignoring wrong Type Indicator", true, false);
        }		
	} 
	/**
	 * GameAuthenticateState - client has sent the version message and is waiting for an acknowledgment
	 * If the client gets acknowledged then it will request to get the game list
	 * @param none
	 * @return none
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void GameAuthenticateState() throws IOException, InterruptedException 
	{

		/* Log and Publish */
		logAndPublish.write("Entered Connection Negotiation State", true, false);
        
        /* get server response */
        ServerResponse sr = this.receiveMessage();
        
		/* verify type as VERSION */
        if (this.messageParser.GetTypeIndicator(sr.getMessage(), sr.getSize()) == MessageParser.TYPE_INDICATOR_VERSION) 
        {
        	/* Get the message response from the server and change state depending upon the response */
        	MessageParser.VersionMessage iMsg = this.messageParser.GetVersionMessage(sr.getMessage(), sr.getSize());
        	/* server has validated the client */
        	if (iMsg.getVersionType() == MessageParser.VERSION_INDICATOR_VERSION_ACK)
        	{
        		/* make sure the version number of the message is the same */
        		if (iMsg.getVersion() == this.m_iVersion)
        		{
        			logAndPublish.write("Successfully Authenticated with the server\n", true, true);
        			this.bankAmount = iMsg.getBankAmount();
        			/* Log and Publish */
        			logAndPublish.write(iMsg.toString(), false, false); 
        			// get the list of games and transition to the game listed state
        			MessageParser.ClientGetGameMessage oMsg1 = this.messageParser.new ClientGetGameMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_GAME, MessageParser.GAME_INDICATOR_GET_GAME);
        			this.sendMessage(this.messageParser.CreateClientGetGameMessage(oMsg1));
        			this.gameState.setState(GameState.GAMELIST); 
        		}
        		else
        		{
        			/* version number was different, ignore the message */
        			logAndPublish.write("Invalid message from the server, incorrect version number", true, true);
        		}
        	}
        	else if (iMsg.getVersionType() == MessageParser.VERSION_INDICATOR_VERSION_UPGRADE)
        	{
        		/* An upgrade is required, need to close the connection */
        		logAndPublish.write("Upgrade Required", true, true);
        		this.gameState.setState(GameState.CLOSED);
        	}
        	else if (iMsg.getVersionType() == MessageParser.VERSION_INDICATOR_VERSION_REQUIREMENT)
        	{
        		/* Version message needs to be sent, transition back to listening state to send it */
        		logAndPublish.write("Version Required message received from the server", true, false);
        		this.gameState.setState(GameState.LISTENING);
        	}
        	else
        	{
        		/* Ignore invalid message received */
        		logAndPublish.write("Invalid version message received from the server", true, false);
        	}    		       	
        }
        else
        {
        	/* Ignore invalid message received */
        	logAndPublish.write("Invalid message received from the server", true, false);
        }
	}	
	
	/**
	 * GameListeningState - client has just connected and needs to send the Client Version Message
	 * If this message is not sent the server will send a version requirement message
	 * @param none
	 * @return none
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void GameListeningState() throws IOException, InterruptedException 
	{

		/* Log and Publish */
		logAndPublish.write("Sending Version Message", true, true);
        
		/*
		 * Send the client version message and then transition to the authentication state
		 */
        MessageParser.VersionMessage oMsg1 = this.messageParser.new VersionMessage(this.m_iVersion, MessageParser.TYPE_INDICATOR_VERSION, MessageParser.VERSION_INDICATOR_CLIENT_VERSION, (short)this.m_iMinorVersion, (long)0);
        this.sendMessage(this.messageParser.CreateVersionMessage(oMsg1));
        
        this.gameState.setState(GameState.AUTHENTICATE);
        logAndPublish.write("Changing State to Connection Negotiation", true, false);
	}	
    
	/**
	 * getSSLSocketFactory - method uses the KeyManagers and TrustManagers packaged with
	 * this JAR to instantiate a new SSL Context using the TLS protocol.
	 * @param none
	 * @return SSLSocketFactory
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
    protected SSLSocketFactory getSSLSocketFactory() throws IOException, GeneralSecurityException
    {
      /* load TrustManagers packaged with this JAR */
      TrustManager[] tms=getTrustManagers();
      
      /* load KeyManagers packaged with this JAR */
      KeyManager[] kms=getKeyManagers();

      /* Create TLS Protocol SSL Context using the  KeyManagers and TrustManagers packaged with this JAR */
      SSLContext context=SSLContext.getInstance("TLS");
      context.init(kms, tms, null);

      /* Create an SSL Socket Factory and return */
      SSLSocketFactory ssf=context.getSocketFactory();
      return ssf;
    }  
    

    /**
     * getTrustManagers - method loads the key manager packaged with this JAR, 
     * initializes and returns the TrustManager array
     * @param none
     * @return TrustManager[]
     * @throws IOException
     * @throws GeneralSecurityException
     */
    protected TrustManager[] getTrustManagers() throws IOException, GeneralSecurityException
    {
    	/* get default trust manager factory */
    	String alg=TrustManagerFactory.getDefaultAlgorithm();
    	TrustManagerFactory tmFact=TrustManagerFactory.getInstance(alg);
      
    	/* get KeyStore instance and type */
    	KeyStore ks=KeyStore.getInstance("jks");
      
  	  	/* initiate class loader to retrieve jks file from this JAR */      
      	ClassLoader classLoader = getClass().getClassLoader();
      	InputStream keystoreStream = classLoader.getResourceAsStream(trustStore); // note, not getSYSTEMResourceAsStream  
      	ks.load(keystoreStream, trustStorePassword.toCharArray());

      	/* initialize the KeyManagerFactory with this KeyStore */
      	tmFact.init(ks);

      	/* retrieve the key manager(s) and return */
      	TrustManager[] tms=tmFact.getTrustManagers();
      	return tms;
    }  

    /**
     * getKeyManagers - method loads the key manager packaged with this JAR, 
     * initializes and returns the KeyManager array
     * @param none
     * @return returns an array of key managers
     * @throws IOException
     * @throws GeneralSecurityException
     */
    protected KeyManager[] getKeyManagers() throws IOException, GeneralSecurityException
    {

    	/* get default key manager factory */
    	String alg=KeyManagerFactory.getDefaultAlgorithm();
    	KeyManagerFactory kmFact=KeyManagerFactory.getInstance(alg);
      
    	/* construct KeyStore instance and type */
    	KeyStore ks=KeyStore.getInstance("jks");

    	/* initiate class loader to retrieve jks file from this JAR */
    	ClassLoader classLoader = getClass().getClassLoader();
    	InputStream keystoreStream = classLoader.getResourceAsStream(keyStore); // note, not getSYSTEMResourceAsStream  
    	ks.load(keystoreStream, keyStorePassword.toCharArray()); 

    	/* initialize the KeyManagerFactory with this KeyStore */
    	kmFact.init(ks, keyStorePassword.toCharArray());

    	/* retrieve the key manager(s) and return */
    	KeyManager[] kms=kmFact.getKeyManagers();
    	return kms;
    }  
    
	/**
	 * connect - method forms a connection to the server defined by the configuration
	 * @param ssf SSL Socket Factory instance
	 * @param hostName Host name of the Server
	 * @param port Port of the AGMP Server
	 * @return none
	 * @throws IOException
	 */
    public void connect(SSLSocketFactory ssf, String hostName, int port) throws IOException 
    {
        
		/* Log and Publish */
		logAndPublish.write("Open secure connection to " + hostName + ".", true, true);    	
    	
    	if(!connected)
        {
    		/* set variables */
        	this.hostName = hostName;
            this.port = port;
            
            /* open secure socket */
            socket = (SSLSocket)ssf.createSocket(hostName, port); 
            
            /* get input and output screens */            
            oInputStream = socket.getInputStream();
            oOutputStream = socket.getOutputStream();
   		 	outputStream = new ObjectOutputStream(oOutputStream); 
   		 	inputstream = new ObjectInputStream(oInputStream); 
   		 	this.connected = true;
    		 	
   		 	
   		 	/* Spawn thread with 'this' instance.  Initiate reading from server. */
   		 	Thread t = new Thread(this);
   		 	t.start(); 
        }
    }
    
    /**
     * findConnect - method will use the Java InetAddress .isReachable method, 
     * which uses ICMP if logged in as root in linux or OSX, or uses a TCP RST,ACK 
     * ping to Port 7 Echo Request.  
     * @param ssf SSL Socket Factory instance
     * @param port Port of the AGMP Server
     * @return none
     * @throws IOException
     */
    public void findConnect(SSLSocketFactory ssf, int port) throws IOException 
    {
        
		/* Log and Publish */
		logAndPublish.write("Attempting to find server using ICMP and/or TCP.", true, true); 
    	
    	if(!connected)
        { 	
    		
    		/* set variables */
        	this.port = port;
        	
        	/* open secure socket */
        	EchoFinder ef = new EchoFinder(ssf, port);
	        socket = ef.findAGMPServer(port);
	        
	        /* get input and output streams if the connection was found*/ 
	        if (socket != null) {
		        oInputStream = socket.getInputStream();
		        oOutputStream = socket.getOutputStream();
				outputStream = new ObjectOutputStream(oOutputStream); 
				inputstream = new ObjectInputStream(oInputStream); 
				connected = true;

	  			/* Log and Publish */
	   			logAndPublish.write("'findConnect' status: " + connected, false, true);   		 	
				
				/* Spawn thread with 'this' instance.  Initiate reading from server. */
		        Thread t = new Thread(this);
		        t.start();	
	        }
        }
    }  
    
    /**
     * receiveMessage - method reads server response from ObjectInputStream 
     * @param none
     * @return ServerResponse
     * @throws IOException
     */
    public ServerResponse receiveMessage () throws IOException
    {
    	/* get the message from the server */
		byte iByteCount = inputstream.readByte();
		byte [] inputBuffer = new byte[iByteCount];
		inputstream.readFully(inputBuffer);   
		
		/* store it into the Server Response object */
		ServerResponse sr = new ServerResponse(inputBuffer, (int)iByteCount);
		return sr;
    }    
    
    /**
     * sendMessage - method dispatches messages to the server
     * @param msg MessageParser assembled byte message
     * @return none
     * @throws IOException
     */
    public void sendMessage (byte[] msg) throws IOException
    {
    	/* write the response to the server */
		outputStream.writeByte((byte)msg.length);
        outputStream.write(msg); 
        outputStream.flush();
    }

    /**
     * disconnect - disconnect this client from the server
     * @param none
     * @return none
     */
    public void disconnect() 
    {
    	
    	/* Disconnect from server */
		if(socket != null && connected)
        {
			try {
				this.socket.close();
			}catch(IOException ioe) {
        	  
				/* Log and Publish */
				logAndPublish.write(ioe, true, false); 
			}
			finally {
				this.connected = false;
			}
        }
    }
}


/**
 * ServerResponse class used to store a response message and length from an AGMP Server
 * @author root
 *
 */
class ServerResponse {
	private byte[] message = null;
	private int size = -1;
	
	/**
	 * constructor method to set byte array and size
	 * @param msg
	 * @param size
	 */
	public ServerResponse (byte[] msg, int size) 
	{
		this.message = msg;
		this.size = size;
	}
	
	/**
	 * setMessage - method to set byte array
	 * @param msg
	 * @return none
	 */
	public void setMessage (byte[] msg) 
	{
		this.message = msg;
	}

	/**
	 * setSize - method to set array size
	 * @param size
	 * @return none
	 */
	public void setSize (int size)
	{
		this.size = size;
	}
	
	/**
	 * getSize - method returns array size
	 * @param none
	 * @return int
	 */
	public int getSize ()
	{
		return size;
	}
	
	/**
	 * getMessage - method returns byte array
	 * @param none
	 * @return byte[]
	 */
	public byte[] getMessage ()
	{
		return message;
	}
}    

	

