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

import java.util.List;
import java.io.*;
import java.security.*;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import client.card_game.ClientPokerModel;
import client.view.TexasGame;
import client.view.TexasGame.InputState;
import client.view.Welcome;
import common.GamePhase;
import common.GamePhase.Phase;
import common.GameState;
import common.MessageParser;
import common.GameState.State;
import common.MessageParser.ConnectionIndicator;
import common.MessageParser.GameIndicator;
import common.MessageParser.GamePlayRequest;
import common.MessageParser.GamePlayResponse;
import common.MessageParser.GameTypeCode;
import common.MessageParser.GameTypeResponse;
import common.MessageParser.TypeIndicator;
import common.MessageParser.VersionIndicator;
import common.MessageParser.Winner;
import common.card_game.Card;
import common.findServer.EchoFinder;
import common.XmlParser;
import common.MessageParser.GameTypeCode;

/**
 * SecureClientController class manages the logic for the client side
 * within the AGMP example application.
 * 
 */
public class SecureClientController implements Runnable {

	/* PKI references */
	private String trustStore=null;
	private String trustStorePassword=null;	
	private String keyStore=null;
	private String keyStorePassword=null; 
	
	/* logging utility */
	private final Logger fLogger; 
	
	/* management mechanisms */
    private GameState gameState;
    private MessageParser messageParser;
	private boolean connected;
	
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

    public String uniqueID;
    private GamePhase gamePhase;
    private int m_iVersion = -1;
    private short m_iMinorVersion = -1;
    private long m_lClientBankAmount = -1;
    private int m_lClientAnte=-1;
    private long m_lClientBetAmount=-1;
    private boolean m_bGotGames = false;
    private boolean m_bSetGames = false;
    private boolean m_bPlayGames= false;
    private boolean m_bGetHole = false;
    private boolean m_bGetFlog =false;
    private boolean m_bGetTurn = false;
    private boolean m_bGetRiver =false;
    
    private ClientPokerModel holdemModel= null;
    
   
    private GameTypeCode m_eGameTypeCode = GameTypeCode.NOT_SET;
        
        //GUI methods
     public TexasGame TG = new TexasGame();
     private Welcome welcome=null;
     
	public Welcome getWelcome() {
		return welcome;
	}

	public void setWelcome(Welcome welcome) {
		this.welcome = welcome;
	}
	private ArrayList<MessageParser.GameTypeCode> oGameTypeList;
    
    /**
     * Constructor for this class.
     * @param xmlParser Incoming XML Parser
     * @param fLogger Incoming Logger
     */
    public SecureClientController(XmlParser xmlParser, Logger fLogger,Welcome welcome) {
        
		/* setup parser */
    	this.xmlParser = xmlParser;
    	
		/* setup logger */
    	if (fLogger != null)
    	{
    		this.fLogger = fLogger;
    	}
    	else
    	{
    		System.out.println("instantiate scc");
    		this.fLogger = Logger.getLogger(this.xmlParser.getClientTagValue("LOG_FILE"));
    		this.fLogger.setUseParentHandlers(false);
    		this.fLogger.removeHandler(new ConsoleHandler());
    		try {
    			FileHandler fh = new FileHandler(this.xmlParser.getClientTagValue("LOG_FILE"), true);
    			fh.setFormatter(new SimpleFormatter());
    			this.fLogger.addHandler(fh);
    		} catch (Exception e) {
    			e.printStackTrace();
    			System.exit(1);
    		}
    	}    	
    	
    	/* instantiate remaining variables */
    	this.gameState = new GameState();
        this.gameState.setState(State.AUTHENTICATE);
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
		this.fLogger.info("Setting port number to:" + this.port);
		this.fLogger.info("Using TrustStore: " + this.trustStore);
		this.fLogger.info("Setting KeyStore: " + this.keyStore);   
		this.welcome=welcome;
    }    
    
    /**
     * Accessor method to verify connection state
	 * 
     */       
    public boolean isConnected() {
		return connected;
    }

    /**
     * Accessor method to get port
	 * 
     */    
    public int getPort(){
            return port;
        }

    /**
     * Mutator method to set port
     * 
     * @param int port
	 * 
     */      
    public void setPort(int port){
            this.port = port;
        }

    /**
     * Accessor method to get hostname
	 * 
     */      
    public String getHostName(){
            return hostName;
        }

    /**
     * Mutator method to set hostname
     * 
     * @param String hostName
	 * 
     */       
    public void setHostName(String hostName){
            this.hostName = hostName;
        }   
    
    
    /**
     * start method to initiate Secure Client Connection to an AGMP Server
	 * 
     */   
	public void start()
	{
		
		/* Log and Publish */
		logAndPublish("Client connecting to server...", true, true);
//        System.out.println("in controller start");
		/* instantiate a new SecureClientController */
		try {
			c = new SecureClientController(this.xmlParser, this.fLogger,this.welcome);
	        ssf=getSSLSocketFactory();
		}
		catch(Exception e)
		{
			/* Log and Publish */
			logAndPublish(e, true, true);			
		}		
        
		/* Connect to server identified in configuration settings */
		try
		{
	        c.connect(ssf,c.hostName,c.port);
		} catch (IOException e) 
		{
			/* Log and Publish */
			logAndPublish(e, true, true);
		}
		
		/* find server using ICMP/TCP methods */
		if (!c.connected) {
			System.out.println("PROBLEM");
			
			try {
				c.findConnect(ssf, port);
			} catch (Exception e) 
			{
				/* Log and Publish */
				logAndPublish(e, true, true);				
			}
		}
		
		/* Log and Publish */
		logAndPublish("Client connected.", true, true);

	}

    /**
     * run method executes when a new SecureClientController thread, 
     * complete with socket is started 
     */
    @SuppressWarnings("unchecked")
	public void run() {
       try 
       {
    	   
    	   
    	   /* Game loop */
    		this.m_iVersion=Integer.parseInt(this.xmlParser.getClientTagValue("VERSION"));
	    	this.m_iMinorVersion=Short.parseShort(xmlParser.getClientTagValue("MINOR_VERSION"));
	    	
    	   while(connected && this.gameState.getState() != State.CLOSED) 
    	   {
   		    
    		   	if (this.gameState.getState().isEqual(State.AUTHENTICATE))
    		   	{
    				/* Log and Publish */
    				logAndPublish("Enter Authentication State.", false, true);
    				
    				versionNegotiate();
    		   	}
 
    		   	else if (this.gameState.getState().isEqual(State.GAMELIST))
	       		{
    				/* Log and Publish */
    				logAndPublish("Enter List State.", false, true);
    				
    				getGame();
	       		}    	        		
    		   	else if (this.gameState.getState().isEqual(State.GAMESET))
        		{
    				/* Log and Publish */
    				logAndPublish("Enter Set State.", false, true);
//    				UI render game list 
//    				UI	if user click TexasHoldem Button : SetGames=true, gametypecode=1
    				       welcome.setList(true);
    	                   welcome.initComponents();
    	                   this.m_bSetGames=welcome.isSetGames();
    	                   this.m_eGameTypeCode.setGameTypeCode(welcome.getGametypecode());
    	                   
    					if(this.m_bSetGames==true){
    						this.TG=welcome.getTexasGame();
    						if(this.TG!=null)
    							System.out.println("right poker");
    						setGame();
    						this.holdemModel=new ClientPokerModel();
    						this.gamePhase=new GamePhase();
    						this.gamePhase.setPhase(Phase.INIT);
    						this.m_bPlayGames=true;
    						this.m_bSetGames=false;
    					}
    					else{
//    						UI pop out "need to choose an available game"
    						System.out.println("need to choose an availabe game");
    					}
        		}        		
        		
        		else if (this.gameState.getState().isEqual(State.GAMEPLAY))
        		{
    				/* Log and Publish */
    				logAndPublish("Enter Play State.", false, true);
    				
        			playGame();
        		}
        		else if (this.gameState.getState().isEqual(State.CLOSING))
        		{
    				/* Log and Publish */
    				logAndPublish("Enter Closing State.", false, true);
    				
        			ClosingState();
        			break;
        		}
				/* Log and Publish */
				logAndPublish("Connection status: " + connected, false, false);
				
				/* Log and Publish */
				logAndPublish("Game state: " + this.gameState.getState(), false, false);				

        	}
    	   connected = false;
		
		}
		catch(Exception e)
		{
      		/* Log and Publish */
      		logAndPublish(e, true, true); 
		}	    		   
        finally { connected = false; }

    }
	
	
	  
	public void versionNegotiate() throws IOException {
	//  send client Version Message
		  sendClientVersionMsg();
//			waiting for server Ack Message if receive Version Requirement then send client Version message again.
		  while(!recvServerVersionAck())
			  sendClientVersionMsg();
	       this.gameState.setState(State.GAMELIST);
			
		}
	public void sendClientVersionMsg(){
//			construct client's version message
			System.out.println("start of sending client version message");
			int version=this.m_iVersion;
			TypeIndicator typecode=TypeIndicator.VERSION;
			VersionIndicator versiontype=VersionIndicator.CLIENT_VERSION;
			short minorversion=this.m_iMinorVersion;
			long bankamount=0;
			MessageParser.VersionMessage clientVersionMsg=this.messageParser.new VersionMessage(version, typecode, versiontype, minorversion, bankamount);
//			send to the server
			byte[] outputBuffer=this.messageParser.CreateVersionMessage(clientVersionMsg);
			
				try {
					sendMessage(outputBuffer);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
	public boolean recvServerVersionAck() throws IOException{
			System.out.println("start of receiving server verison ACK");
//			recieving message from server
			while(true){
				     ServerResponse sr = this.receiveMessage();
					 if(this.messageParser.GetTypeIndicator(sr.getMessage(), sr.getSize()).isEqual(TypeIndicator.VERSION)){
						MessageParser.VersionMessage serverVersionMsg=this.messageParser.GetVersionMessage(sr.getMessage(), sr.getSize());
			    		logAndPublish(serverVersionMsg.toString(), false, true); 
						if(serverVersionMsg.getVersionType().isEqual(VersionIndicator.VERSION_ACK)){
								this.fLogger.info(this.uniqueID+": recieve server version Ack");
								this.m_lClientBankAmount=serverVersionMsg.getBankAmount();
								return true;
							}
						else if(serverVersionMsg.getVersionType().isEqual(VersionIndicator.VERSION_REQUIREMENT)){
							   this.fLogger.info(this.uniqueID+": receive server version requirement");
							   return false;
						}
						else if(serverVersionMsg.getVersionType().isEqual(VersionIndicator.VERSION_UPGRADE)){
							   this.fLogger.info(this.uniqueID+": recieve server update");
							   System.exit(0);
						}
					 }
			 }
		}

		
	public void getGame() throws IOException{
			sendGetGamelistMsg();
			if(recvServerGamelistMsg())
//			should rendering gamelist in GUI 
			this.gameState.setState(State.GAMESET);
		}
	public void sendGetGamelistMsg() throws IOException{
//			construct client get game list message
			int version=this.m_iVersion;
			TypeIndicator typecode=TypeIndicator.LIST;
			GameIndicator gameIndicator=GameIndicator.GET_GAME;
			MessageParser.ClientGetGameMessage getGameMsg= this.messageParser.new ClientGetGameMessage(version, typecode, gameIndicator);
//	      send it to the server
			System.out.print(getGameMsg.toString());
			byte[]outputBuffer=this.messageParser.CreateClientGetGameMessage(getGameMsg);
			sendMessage(outputBuffer);
		}
		
	public boolean recvServerGamelistMsg() throws IOException{
			
			while(true){
				 ServerResponse sr = this.receiveMessage();
				 System.out.println(sr.getMessage().toString());
				 TypeIndicator typecode=this.messageParser.GetTypeIndicator(sr.getMessage(), sr.getSize());
				 GameIndicator gameIndicator=this.messageParser.GetGameIndicator(sr.getMessage(),sr.getSize());
				 if(typecode.isEqual(TypeIndicator.LIST)
					 && gameIndicator.isEqual(GameIndicator.GET_GAME)){
					 System.out.println("right message");
					MessageParser.ServerGetGameMessage serverGamelistMsg=this.messageParser.GetServerGetGameMessage(sr.getMessage(), sr.getSize());
					this.fLogger.info(uniqueID+": receive server game List");
					this.oGameTypeList=(ArrayList<GameTypeCode>) serverGamelistMsg.getGameTypeCodeList();
					return true;
			    }
			}
		}
	public void setGame() throws IOException{
			sendSetGameMsg();
			if(recvServerSetGameMsg()){
				this.gameState.setState(State.GAMEPLAY);
			}
			else{
				this.m_eGameTypeCode=GameTypeCode.NOT_SET;
				this.m_bSetGames=false;
				this.gameState.setState(State.GAMESET);
//				UI send error and ask client to reclick the game
	             
			}
			
		}
	public void sendSetGameMsg() throws IOException{
//			construct client set game message
		   System.out.println("Send setgame message");
			int version=this.m_iVersion;
			TypeIndicator typecode=TypeIndicator.GAME;
			GameIndicator gameIndicator=GameIndicator.SET_GAME;
			GameTypeCode gameType=this.m_eGameTypeCode;
			MessageParser.ClientSetGameMessage setGameMsg=this.messageParser.new ClientSetGameMessage(version, typecode, gameIndicator, gameType);
//			send to server
			byte[]outputBuffer=this.messageParser.CreateClientSetGameMessage(setGameMsg);
			sendMessage(outputBuffer);
		}
	
	public boolean recvServerSetGameMsg() throws IOException{
			
			   while(true){
				   
				    System.out.println("into recv Server set message");
				    ServerResponse sr = this.receiveMessage();
					TypeIndicator typecode=this.messageParser.GetTypeIndicator(sr.getMessage(), sr.getSize());
					GameIndicator gameIndicator=this.messageParser.GetGameIndicator(sr.getMessage(), sr.getSize());
					
					System.out.println(typecode.getIndicator());
					System.out.println(gameIndicator.getIndicator());
					
					if(typecode.getIndicator()==TypeIndicator.SET.getIndicator()
						&& gameIndicator.getIndicator()== GameIndicator.SET_GAME.getIndicator()){
						System.out.println("typecode and gameindicator right");
						MessageParser.ServerSetGameMessage serverSetGameMsg=this.messageParser.GetServerSetGameMessage(sr.getMessage(), sr.getSize());
						if(serverSetGameMsg.getGameTypeResponse().getGameTypeResponse()==GameTypeResponse.ACK.getGameTypeResponse()){
							System.out.println("get respond from server setgame message");
						  this.fLogger.info(uniqueID+": receive server gameType ACK");
						  return true;
						}
						else{
							this.fLogger.info(uniqueID+": gameType invalid ");
							return false;
						}
					}
					
			}
				
	}
		
	public void playGame() throws IOException{
		
			while(m_bPlayGames){
				
				Phase currentPhase=this.gamePhase.getPhase();
				if(currentPhase.isEqual(Phase.INIT)){
					System.out.println("into Init phase");
				    sendInitGameMsg();
				    if(recvInitGameAckMsg()){
				    	this.gamePhase.setPhase(Phase.HOLE);
				    }
				}
				else if(currentPhase.isEqual(Phase.HOLE)){
//					 Update UI
		//			UI Pop out want to play game showing the minium Ante and bankamount
		//			if client inputs Ante and click yes :  m_bGetHole=true m_lClientAnte=input
		//			else click no                       :  m_bGetHole=false       
					this.TG=new TexasGame();
					System.out.println("into Hole phase");
					System.out.println(this.m_lClientAnte);
					if(holdemModel!=null)
						System.out.println("right model");
				     this.TG.setMinAnte(this.m_lClientAnte);
	                 this.TG.setPokerModel(holdemModel);
	                 System.out.println(this.TG.getPokerModel().getlBankAmount());
	                 System.out.println(this.TG.getMinAnte());
	                 this.TG.initComponents();
	                 TG.setVisible(true);
	                  while(this.TG.getbGetHole().getIndicator()==InputState.NOT_SET.getIndicator()){
	                	  System.out.println("no input");
	                  }
	                  if(this.TG.getbGetHole().getIndicator()==InputState.YES.getIndicator())
	                  {
	                	  this.m_bGetHole=true;
	                	  this.m_lClientAnte=this.TG.getUserAnte();
	                  }
	                  else
	                      this.m_bGetHole=false;
	                  
					if(this.m_bGetHole){
					  System.out.println(this.m_lClientAnte);
			           sendHoleRequestMsg();
			           if(recvHoleAckMsg()){
			        	   this.gamePhase.setPhase(Phase.FLOP);
			        	   this.m_bGetHole=false;
			           }
			           else{
//			        	   UI Pop out "ante is invalid"  then go back to HOLE Phase
			        	   this.gamePhase.setPhase(Phase.HOLE); 
			           }
					}
					else{
						this.gamePhase.setPhase(Phase.QUIT);
						  this.m_bGetHole=false;
					}
				}
				else if(currentPhase.isEqual(Phase.FLOP)){
//					UpdateUI
//					UI Pop out want to follow betting 2*Ante or Fold
					//			if click follow : m_bGetFlop=true
					//			else click fold : m_bGetFlop=false
	                     
					System.out.println("into Flop phase");              
					if(this.m_bGetFlog){
					    sendFlopRequestMsg();
					    if(recvFlopAckMsg()){
					    	this.gamePhase.setPhase(Phase.TURN);
					    	this.m_bGetFlog=false;
					    }
					    else{
//					    	UI pop out "flop bet"
	                                        
					    	this.gamePhase.setPhase(Phase.FLOP);
					    }
					}
					else{
						this.gamePhase.setPhase(Phase.FOLD);
						this.m_bGetFlog=false;
					}
				}
				else if(currentPhase.isEqual(Phase.TURN)){
//					Update UI
//					UI Pop out want to check 0*Ante, follow bet 1*Ante, or Fold
//						if client clicks check :  m_bGetTurn=true m_lClientBetAmount=0
//					    if client clicks folow  : m_bGetTurn=true  m_lClientBetAmount=m_lClientAnte;
					//	else client clicks fold :	m_bGetTurn=false;
					System.out.println("into Turn phase");
					if(this.m_bGetTurn){
						sendTurnRequestMsg();
						if(recvTurnAckMsg()){
							this.gamePhase.setPhase(Phase.RIVER);
							this.m_bGetTurn=false;
						}
						else{
//							UI pop out "Invalid Turn Bet"
							this.gamePhase.setPhase(Phase.TURN);
						}
					}
					else{
						this.gamePhase.setPhase(Phase.FOLD);
						this.m_bGetTurn=false;
					}
				}
				else if(currentPhase.isEqual(Phase.RIVER)){
//					Updtate UI
//					UI Pop out want to check 0*Ante, follow bet 1*Ante, or Fold
//					if client clicks check :  m_bGetRiver=true m_lClientBetAmount=0
//				    if client clicks folow  : m_bGetRiver=true  m_lClientBetAmount=m_lClientAnte;
				//	else client clicks fold :	m_bGetRiver=false;
					System.out.println("into River phase");
	                            
					if(this.m_bGetRiver==true){
						sendRiverRequestMsg();
						if(recvRiverAckMsg()){
//							UI should pop out the winner and update() the result
	                                            
							this.gamePhase.setPhase(Phase.INIT);
							this.holdemModel.init();
						    this.m_bGetRiver=false;
						}
						else{
							this.gamePhase.setPhase(Phase.RIVER);
						}
					}
					else{
						this.gamePhase.setPhase(Phase.FOLD);
						this.m_bGetRiver=false;
					}
				}
				else if(currentPhase.isEqual(Phase.FOLD)){
					System.out.println("into Fold phase");
					sendFoldRequestMsg();
					if(recvFoldAckMsg()){
//					UI	Pop out the winner and update UI
	                                   
						this.gamePhase.setPhase(Phase.INIT);
					}
					else{
//				    UI pop out "invalid FolD"
	                                  
					}
				}
				else if(currentPhase.isEqual(Phase.QUIT)){
					System.out.println("into quit phase");
					this.gameState.setState(State.CLOSING);
					this.m_bPlayGames=false;
				}
					
				
			}
		}
	public void sendInitGameMsg() throws IOException{
//			construct Init game message
			int version=this.m_iVersion;
			TypeIndicator typecode=TypeIndicator.GAME;
			GameIndicator gameIndicator=GameIndicator.PLAY_GAME;
			GameTypeCode gameType=GameTypeCode.TEXAS_HOLDEM;
			GamePlayRequest gameRequest=GamePlayRequest.INIT;
			long betamount=0;
			MessageParser.ClientPlayGameMessage initGameMsg=this.messageParser.new ClientPlayGameMessage(version, typecode, gameIndicator, gameType,gameRequest ,betamount);
//		    send to server 
			byte[]outputBuffer=this.messageParser.CreateClientPlayGameMessage(initGameMsg);
			sendMessage(outputBuffer);
		}
		
	public boolean recvInitGameAckMsg() throws IOException{
			
			while(true){
				    ServerResponse sr=this.receiveMessage();
					TypeIndicator typecode=this.messageParser.GetTypeIndicator(sr.getMessage(), sr.getSize());
					GameIndicator gameIndicator=this.messageParser.GetGameIndicator(sr.getMessage(), sr.getSize());
					if(typecode.getIndicator()==TypeIndicator.GAME.getIndicator()
						&& gameIndicator.getIndicator()==GameIndicator.PLAY_GAME.getIndicator()){
						MessageParser.ServerPlayGameMessage initGameResponseMsg=this.messageParser.GetServerPlayGameMessage(sr.getMessage(), sr.getSize());
						if(initGameResponseMsg.getGamePlayResponse().getGamePlayResponse()==GamePlayResponse.INIT_ACK.getGamePlayResponse()){
						  this.fLogger.info(uniqueID+": receive server init game ACK");
						  this.m_lClientBankAmount=initGameResponseMsg.getBankAmount();
					      this.m_lClientAnte=initGameResponseMsg.getAnte();
					      this.holdemModel.setlBankAmount(initGameResponseMsg.getBankAmount());
					      System.out.println(this.holdemModel.getlBankAmount());
					      System.out.println(initGameResponseMsg.getBankAmount());
						  return true;
						}
				}
			}
		}
    public void sendHoleRequestMsg() throws IOException{
//	    	construct Hole request game message
    	    System.out.println("send Hole requeset");
			int version=this.m_iVersion;
			TypeIndicator typecode=TypeIndicator.GAME;
			GameIndicator gameIndicator=GameIndicator.PLAY_GAME;
			GameTypeCode gameType=GameTypeCode.TEXAS_HOLDEM;
			GamePlayRequest gameRequest=GamePlayRequest.GET_HOLE;
			long betamount=this.m_lClientAnte;
			MessageParser.ClientPlayGameMessage getHoleMsg=this.messageParser.new ClientPlayGameMessage(version, typecode, gameIndicator, gameType,gameRequest ,betamount);
//		    send to server 
			byte[]outputBuffer=this.messageParser.CreateClientPlayGameMessage(getHoleMsg);
		    sendMessage(outputBuffer);
	    }
	public boolean recvHoleAckMsg() throws IOException{
		
			  while(true){
				  System.out.println("into recieve Hole Card");
				  ServerResponse sr=this.receiveMessage();
				  TypeIndicator typecode=this.messageParser.GetTypeIndicator(sr.getMessage(), sr.getSize());
				  GameIndicator gameIndicator=this.messageParser.GetGameIndicator(sr.getMessage(), sr.getSize());
				  if(typecode.getIndicator()==TypeIndicator.GAME.getIndicator()
					  && gameIndicator.getIndicator()==GameIndicator.PLAY_GAME.getIndicator()){
					 MessageParser.ServerPlayGameMessage serverHoleMsg=this.messageParser.GetServerPlayGameMessage(sr.getMessage(), sr.getSize());
					 if(serverHoleMsg.getGamePlayResponse().getGamePlayResponse()==GamePlayResponse.GET_HOLE_ACK.getGamePlayResponse()){
						 this.fLogger.info(uniqueID+": receive server get Hole ACK");
						 long bankamount=serverHoleMsg.getBankAmount();
						 this.m_lClientAnte=serverHoleMsg.getAnte();
						 Card []playerCards=new Card[2];
						 playerCards[0]=serverHoleMsg.getPlayerCard1();
						 playerCards[1]=serverHoleMsg.getPlayerCard2();
						 Card []dealerCards=new Card[2];
						 dealerCards[0]=serverHoleMsg.getDealerCard1();
						 dealerCards[1]=serverHoleMsg.getDealerCard2();
						 dealerCards[0].setIs_visible(false);
						 dealerCards[1].setIs_visible(false);
						 this.holdemModel.setlBankAmount(bankamount);
						 this.holdemModel.setoPlayerCards(playerCards);
						 this.holdemModel.setoDealerCards(dealerCards);
						 this.holdemModel.setiAnte(this.m_lClientAnte);
						 System.out.println("getresponse from server");
						    return true;
						}
						else if(serverHoleMsg.getGamePlayResponse().isEqual(GamePlayResponse.INVALID_ANTE_BET)) {
							this.fLogger.info(uniqueID+": receive invalid Ante Bet");
							return false;
						}
					}
			   }
		}
	public void sendFlopRequestMsg() throws IOException{
//	    	construct Flop request game message
			int version=this.m_iVersion;
			TypeIndicator typecode=TypeIndicator.GAME;
			GameIndicator gameIndicator=GameIndicator.PLAY_GAME;
			GameTypeCode gameType=GameTypeCode.TEXAS_HOLDEM;
			GamePlayRequest gameRequest=GamePlayRequest.GET_FLOP;
			long betamount=this.m_lClientAnte*2;
			MessageParser.ClientPlayGameMessage getFlopMsg=this.messageParser.new ClientPlayGameMessage(version, typecode, gameIndicator, gameType,gameRequest ,betamount);
//		    send to server 
			byte[]outputBuffer=this.messageParser.CreateClientPlayGameMessage(getFlopMsg);
		    sendMessage(outputBuffer);
		    
	    }
	public boolean recvFlopAckMsg() throws IOException{
	
			while(true){
				    ServerResponse sr=this.receiveMessage();
					TypeIndicator typecode=this.messageParser.GetTypeIndicator(sr.getMessage(), sr.getSize());
					GameIndicator gameIndicator=this.messageParser.GetGameIndicator(sr.getMessage(), sr.getSize());
					if(typecode.isEqual(TypeIndicator.GAME)
						&& gameIndicator.isEqual(GameIndicator.PLAY_GAME)){
						MessageParser.ServerPlayGameMessage serverFlopMsg=this.messageParser.GetServerPlayGameMessage(sr.getMessage(), sr.getSize());
						if(serverFlopMsg.getGamePlayResponse().isEqual(GamePlayResponse.GET_FLOP_ACK)){
						  this.fLogger.info(uniqueID+": receive server get FLOP ACK");
						  
						  Card []flopCards=new Card[3];
						  flopCards[0]=serverFlopMsg.getFlopCard1();
						  flopCards[1]=serverFlopMsg.getFlopCard2();
						  flopCards[2]=serverFlopMsg.getFlopCard3();
						  long pot=serverFlopMsg.getPotSize();
						  long betamount=serverFlopMsg.getBetAmount();
						  long bankamount=serverFlopMsg.getBankAmount();
						  this.m_lClientAnte=serverFlopMsg.getAnte();
						  
						  this.holdemModel.setlBankAmount(bankamount);
						  this.holdemModel.setoFlopCards(flopCards);
						  this.holdemModel.setlPotSize(pot);
						  this.holdemModel.setlBetAmount(betamount);
						
						  return true;
						}
						else if(serverFlopMsg.getGamePlayResponse().isEqual(GamePlayResponse.INVALID_FLOP_BET)) {
							this.fLogger.info(uniqueID+": receive invalid Flop Bet");
							return false;
						}
						
					}
			   }
	    }
	public void sendTurnRequestMsg() throws IOException{
//	    	construct Turn request game message
			int version=this.m_iVersion;
			TypeIndicator typecode=TypeIndicator.GAME;
			GameIndicator gameIndicator=GameIndicator.PLAY_GAME;
			GameTypeCode gameType=GameTypeCode.TEXAS_HOLDEM;
			GamePlayRequest gameRequest=GamePlayRequest.GET_TURN;
			long betamount=this.m_lClientBetAmount;
			MessageParser.ClientPlayGameMessage getTurnMsg=this.messageParser.new ClientPlayGameMessage(version, typecode, gameIndicator, gameType,gameRequest ,betamount);
//		    send to server 
			byte[]outputBuffer=this.messageParser.CreateClientPlayGameMessage(getTurnMsg);
		    sendMessage(outputBuffer);
	    }
	public boolean recvTurnAckMsg() throws IOException{
	    	
			   while(true){
			        ServerResponse sr=this.receiveMessage();
					TypeIndicator typecode=this.messageParser.GetTypeIndicator(sr.getMessage(), sr.getSize());
					GameIndicator gameIndicator=this.messageParser.GetGameIndicator(sr.getMessage(), sr.getSize());
					if(typecode.isEqual(TypeIndicator.GAME)
						&& gameIndicator.isEqual(GameIndicator.PLAY_GAME)){
						
						MessageParser.ServerPlayGameMessage serverTurnMsg=this.messageParser.GetServerPlayGameMessage(sr.getMessage(), sr.getSize());
						if(serverTurnMsg.getGamePlayResponse().isEqual(GamePlayResponse.GET_TURN_ACK)){
						  this.fLogger.info(uniqueID+": receive server get Turn ACK");
						  Card turnCard=serverTurnMsg.getTurnCard();
						  long betamount=serverTurnMsg.getBetAmount();
						  long bankamount=serverTurnMsg.getBankAmount();
						  long pot=serverTurnMsg.getPotSize();
						  this.m_lClientAnte=serverTurnMsg.getAnte();
						  
						  this.holdemModel.setlBankAmount(bankamount);
						  this.holdemModel.setoTurnCard(turnCard);
						  this.holdemModel.setlPotSize(pot);
						  this.holdemModel.setlBetAmount(betamount);
						   return true;
						}
						else if(serverTurnMsg.getGamePlayResponse().isEqual(GamePlayResponse.INVALID_TURN_BET)) {
							 this.fLogger.info(uniqueID+": receive invalid Turn Bet");
							 return false;
						}
					}
			}
	    }
	    
    public void sendRiverRequestMsg() throws IOException{
//	    	construct river request game message
			int version=this.m_iVersion;
			TypeIndicator typecode=TypeIndicator.GAME;
			GameIndicator gameIndicator=GameIndicator.PLAY_GAME;
			GameTypeCode gameType=GameTypeCode.TEXAS_HOLDEM;
			GamePlayRequest gameRequest=GamePlayRequest.GET_RIVER;
			long betamount=this.m_lClientBetAmount;
			MessageParser.ClientPlayGameMessage getRiverMsg=this.messageParser.new ClientPlayGameMessage(version, typecode, gameIndicator, gameType,gameRequest ,betamount);
//		    send to server 
			byte[]outputBuffer=this.messageParser.CreateClientPlayGameMessage(getRiverMsg);
			sendMessage(outputBuffer);
	    }
    public boolean recvRiverAckMsg() throws IOException{
			while(true){
				     ServerResponse sr=this.receiveMessage();
					TypeIndicator typecode=this.messageParser.GetTypeIndicator(sr.getMessage(), sr.getSize());
					GameIndicator gameIndicator=this.messageParser.GetGameIndicator(sr.getMessage(), sr.getSize());
					if(typecode.isEqual(TypeIndicator.GAME)
						&& gameIndicator.isEqual(GameIndicator.PLAY_GAME)){
						
						MessageParser.ServerPlayGameMessage serverRiverMsg=this.messageParser.GetServerPlayGameMessage(sr.getMessage(), sr.getSize());
						if(serverRiverMsg.getGamePlayResponse().isEqual(GamePlayResponse.GET_RIVER_ACK)){
						  this.fLogger.info(uniqueID+": receive server get Turn ACK");
						  Card riverCard=serverRiverMsg.getRiverCard();
						  long betamount=serverRiverMsg.getBetAmount();
						  long bankamount=serverRiverMsg.getBankAmount();
						  long pot=serverRiverMsg.getPotSize();
						  Winner winner=serverRiverMsg.getWinner();
						  this.m_lClientAnte=serverRiverMsg.getAnte();
						  this.holdemModel.setlBankAmount(bankamount);
						  this.holdemModel.setoRiverCard(riverCard);
						  this.holdemModel.setlPotSize(pot);
						  this.holdemModel.setlBetAmount(betamount);
						  this.holdemModel.setOwinner(winner);
						  return true;
						}
//						Have problem here there's no INVALID_RIVER_BET
						else if(serverRiverMsg.getGamePlayResponse().isEqual(GamePlayResponse.INVALID_ANTE_BET)) {
							this.fLogger.info(uniqueID+": receive invalid River Bet");
							return false;
						}
					}
			 }
	    }
	public void sendFoldRequestMsg() throws IOException{
//	    	construct Fold request game message
			int version=this.m_iVersion;
			TypeIndicator typecode=TypeIndicator.GAME;
			GameIndicator gameIndicator=GameIndicator.PLAY_GAME;
			GameTypeCode gameType=GameTypeCode.TEXAS_HOLDEM;
			GamePlayRequest gameRequest=GamePlayRequest.FOLD;
			long betamount=0;
			MessageParser.ClientPlayGameMessage sendFoldMsg=this.messageParser.new ClientPlayGameMessage(version, typecode, gameIndicator, gameType,gameRequest ,betamount);
//		    send to server 
			byte[]outputBuffer=this.messageParser.CreateClientPlayGameMessage(sendFoldMsg);
			sendMessage(outputBuffer);
	    }
	public boolean recvFoldAckMsg() throws IOException{

			while(true){
			        ServerResponse sr=this.receiveMessage();
					TypeIndicator typecode=this.messageParser.GetTypeIndicator(sr.getMessage(), sr.getSize());
					GameIndicator gameIndicator=this.messageParser.GetGameIndicator(sr.getMessage(), sr.getSize());
					if(typecode.isEqual(TypeIndicator.GAME)
						&& gameIndicator.isEqual(GameIndicator.PLAY_GAME)){
						
						MessageParser.ServerPlayGameMessage foldAckMsg=this.messageParser.GetServerPlayGameMessage(sr.getMessage(), sr.getSize());
						if(foldAckMsg.getGamePlayResponse().isEqual(GamePlayResponse.FOLD_ACK)){
						  this.fLogger.info(uniqueID+": receive server get Fold ACK");
						  long betamount=foldAckMsg.getBetAmount();
						  long bankamount=foldAckMsg.getBankAmount();
						  long pot=foldAckMsg.getPotSize();
						  Winner winner=foldAckMsg.getWinner();
						  this.m_lClientAnte=foldAckMsg.getAnte();
						  this.holdemModel.setlBankAmount(bankamount);
						  this.holdemModel.setlPotSize(pot);
						  this.holdemModel.setlBetAmount(betamount);
						  this.holdemModel.setOwinner(winner);
						  this.holdemModel.getoDealerCards()[0].setIs_visible(true);
						  this.holdemModel.getoDealerCards()[1].setIs_visible(true);
						  return true;
						}
//						Have problem here there's no INVALID_Fold_BET
						else if(foldAckMsg.getGamePlayResponse().isEqual(GamePlayResponse.INVALID_TURN_BET)) {
							this.fLogger.info(uniqueID+": receive invalid fold bet");
							return false;
						}
					}
			}
	    }
	public void sendCloseMsg() throws IOException{
//	    	construct close  message
			int version=this.m_iVersion;
			TypeIndicator typecode=TypeIndicator.CLOSE_CONNECTION;
			ConnectionIndicator concode=ConnectionIndicator.CLOSE_CONNECTION;

			MessageParser.ConnectionMessage closeMsg=this.messageParser.new ConnectionMessage(version, typecode, concode);
//		    send to server 
			byte[]outputBuffer=this.messageParser.CreateConnectionMessage(closeMsg);
			sendMessage(outputBuffer);
		}
    /**
     * ClosingState method gracefully closes the connection
     * and disconnects
     */
	private void ClosingState() 
	{
		//TODO: ADD MESSAGE TO GRACEFULLY CLOSE THE CONNECTION//
		this.gameState.setState(State.CLOSED);	
		this.disconnect();
		connected = false;
	}
	
	/**
	 * GamePlayState method contains all the logic to interface with the server
	 * during game play
	 */
	private void GamePlayState() 
	{

		/* set variables */
		int command = 0;
	
		System.exit(0);
		
		DISPLAY:
		while(true)
		{
		
			try
			{

			//REQUIRES X SUB STATES
			
			//STATE1
	//			1) Player puts down an ante
			System.out.println("Place your bet using a whole integer: ");
			//command = Integer.parseInt(br.readLine());
			
			//STATE2
	//			2) Dealer will give the player two cards
			//DISPLAY TWO CARDS
	//			3) Player will decide to play or fold
	//			a) if the player plays they must bet twice their ante amount
	//			b) if they fold they have to go back to 1)
			System.out.println("To play this hand, enter 1. To fold this hand, enter 2");
			//command = Integer.parseInt(br.readLine());
			if (command == 1) {
				
			}
			
	//			4) If player bets then the dealer gives 3 flop cards
	//			5) Player can bet (only can bet the ante amount) , check, or fold
	//			6) If Player bets or checks the dealer gives a turn card
	//			7) Player can bet (only can bet the ante amount) check or fold
	//			8) If player checks or bets the dealer determines the winner and updates the players bank amount, etc.			
			
			
			}
			catch(Exception e)
			{
				continue DISPLAY;
			}
		}
	}
	
	/**
	 * GameSetState method simply informs the client which game has been set
	 * @throws IOException
	 */
	private void GameSetState() throws IOException 
	{

        /* get server response */
        ServerResponse sr = this.receiveMessage();
		
        /* verify type as SET */
        if (this.messageParser.GetTypeIndicator(sr.getMessage(), sr.getSize()).isEqual(TypeIndicator.SET)) 
        {
        	MessageParser.ServerGetGameMessage msg = this.messageParser.GetServerGetGameMessage(sr.getMessage(), sr.getSize());
    		
        	/* Log and Publish */
    		logAndPublish(msg.toString(), false, true);  
        }
        else
        {
           	//TODO: ADD ERROR HANDLING AND STATE CHANGES USING SERVER RESPONSE//
        }
            
        //TODO: ADD ERROR HANDLING AND STATE CHANGES USING SERVER RESPONSE//
        
		/* transition to closing state */
		this.gameState.setState(State.GAMEPLAY);
		
	}
	/**
	 * GameListState method enables the user to select which game they 
	 * would like to play or to exit, disconnect and close the client application.
	 * @throws IOException
	 */
	private void GameListState() throws IOException 
	{
		
		/* set variables */
		int command = 0;
		
        /* get server response */
        ServerResponse sr = this.receiveMessage();
		
        /* verify type as LIST */
        if (this.messageParser.GetTypeIndicator(sr.getMessage(), sr.getSize()).isEqual(TypeIndicator.LIST)) 
        {
        	MessageParser.ServerGetGameMessage msg = this.messageParser.GetServerGetGameMessage(sr.getMessage(), sr.getSize());

    		/* Log and Publish */
    		logAndPublish(msg.toString(), false, false);          	
        }
        else
        {
        	//TODO: ADD ERROR HANDLING AND STATE CHANGES USING SERVER RESPONSE//
        }
        
        //TODO: ADD ERROR HANDLING AND STATE CHANGES USING SERVER RESPONSE//

        //TODO: ADD DYNAMIC MENU USING SERVER RESPONSE//
        /* Hard Coded Menu */
		System.out.println("1: Play Texas Hold'em");
		System.out.println("2: Close Connection");		
		
		/* loop to handle user command line input */
		reset:
		while(true) {
			try {
				command = Integer.parseInt(br.readLine());
			} catch (NumberFormatException e) {
			
	    		/* Log and Publish */
	    		logAndPublish(e, true, false);
	    		logAndPublish("Invalid Selection.  Try again.", false, true);
				continue reset;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			/* command contains a valid number */
			if ((command ==1) || (command == 2)) 
				break;
		}
		
		/* handle user selection */
		if (command == 1){
		
			/* inform server that user has selected Texas Hold'em */
			MessageParser.ClientSetGameMessage msg = this.messageParser.new ClientSetGameMessage(1, TypeIndicator.SET, GameIndicator.SET_GAME, GameTypeCode.TEXAS_HOLDEM);  
	        this.sendMessage(this.messageParser.CreateClientSetGameMessage(msg));
			this.gameState.setState(State.GAMESET);

		} else {
			
			/* transition to closing state */
			this.gameState.setState(State.CLOSING);
		}
		
	} 
	/**
	 * GameAuthenticateState method performs a version authentication with the server
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void GameAuthenticateState() throws IOException, InterruptedException 
	{

		/* Log and Publish */
		logAndPublish("Sending Version Message", true, true);
        
        MessageParser.VersionMessage oMsg1 = this.messageParser.new VersionMessage(1, TypeIndicator.VERSION, VersionIndicator.CLIENT_VERSION, (short)0, (long)0);
        this.sendMessage(this.messageParser.CreateVersionMessage(oMsg1));
        
        /* get server response */
        ServerResponse sr = this.receiveMessage();
        
		/* verify type as VERSION */
        if (this.messageParser.GetTypeIndicator(sr.getMessage(), sr.getSize()).isEqual(TypeIndicator.VERSION)) 
        {
        	MessageParser.VersionMessage iMsg = this.messageParser.GetVersionMessage(sr.getMessage(), sr.getSize());

    		/* Log and Publish */
    		logAndPublish(iMsg.toString(), false, true);        	
        }
        else
        {
        	//TODO: ADD ERROR HANDLING AND STATE CHANGES USING SERVER RESPONSE//
        }

        //TODO: ADD ERROR HANDLING AND STATE CHANGES USING SERVER RESPONSE//
        
        /* request game list */
		MessageParser.ClientGetGameMessage oMsg2 = this.messageParser.new ClientGetGameMessage(1, TypeIndicator.LIST, GameIndicator.GET_GAME);   
        this.sendMessage(this.messageParser.CreateClientGetGameMessage(oMsg2));

        /* set new state */
        this.gameState.setState(State.GAMELIST);

	}	
    
	/**
	 * getSSLSocketFactory method uses the KeyManagers and TrustManagers packaged with
	 * this JAR to instantiate a new SSL Context using the TLS protocol.
	 * @return
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
     * getTrustManagers method loads the key manager packaged with this JAR, 
     * initializes and returns the TrustManager array
     * @return
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
     * getKeyManagers method loads the key manager packaged with this JAR, 
     * initializes and returns the KeyManager array
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
	 * connect method forms a connection to the server defined by the configuration
	 * @param ssf SSL Socket Factory instance
	 * @param hostName Host name of the Server
	 * @param port Port of the AGMP Server
	 * @throws IOException
	 */
    public void connect(SSLSocketFactory ssf, String hostName, int port) throws IOException 
    {
        
		/* Log and Publish */
		logAndPublish("Open secure connection to " + hostName + ".", true, true);    	
    	
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
   		 	
   			/* Log and Publish */
   			logAndPublish("'connect' status: " + connected, false, false);   		 	
   		 	
   		 	/* Spawn thread with 'this' instance.  Initiate reading from server. */
   		 	Thread t = new Thread(this);
   		 	t.start(); 
        }
    }
    
    /**
     * findConnect method will use the Java InetAddress .isReachable method, 
     * which uses ICMP if logged in as root in linux or OSX, or uses a TCP RST,ACK 
     * ping to Port 7 Echo Request.  
     * @param ssf SSL Socket Factory instance
     * @param port Port of the AGMP Server
     * @throws IOException
     */
    public void findConnect(SSLSocketFactory ssf, int port) throws IOException 
    {
        
		/* Log and Publish */
		logAndPublish("Attempting to find server using ICMP and/or TCP.", true, true); 
    	
    	if(!connected)
        { 	
    		System.out.println("PROBLEM2");
    		
    		/* set variables */
        	this.port = port;
        	
        	/* open secure socket */
        	EchoFinder ef = new EchoFinder(ssf, port);
	        socket = ef.findAGMPServer(port);
	        
	        /* get input and output screens */ 
	        if (socket != null) {
		        oInputStream = socket.getInputStream();
		        oOutputStream = socket.getOutputStream();
				outputStream = new ObjectOutputStream(oOutputStream); 
				inputstream = new ObjectInputStream(oInputStream); 
				connected = true;

	  			/* Log and Publish */
	   			logAndPublish("'findConnect' status: " + connected, false, true);   		 	
				
				/* Spawn thread with 'this' instance.  Initiate reading from server. */
		        Thread t = new Thread(this);
		        t.start();	
	        }
        }
    }
    

    /**
     * receiveMessage method reads server response from ObjectInputStream 
     * @return 
     * @throws IOException
     */
    public ServerResponse receiveMessage () throws IOException
    {
		byte iByteCount = inputstream.readByte();
		byte [] inputBuffer = new byte[iByteCount];
		inputstream.readFully(inputBuffer);   
		
		ServerResponse sr = new ServerResponse(inputBuffer, (int)iByteCount);
		return sr;
    }    
    
    /**
     * sendMessage method dispatches messages to the server
     * @param msg MessageParser assembled byte message
     * @throws IOException
     */
    public void sendMessage (byte[] msg) throws IOException
    {
		outputStream.writeByte((byte)msg.length);
        outputStream.write(msg); 
        outputStream.flush();
    }

    /**
     * Disconnect this client from the server
     */
    public void disconnect() {
    	
    	/* Disconnect from server */
		if(socket != null && connected)
        {
          try {
			this.socket.close();
          }catch(IOException ioe) {
        	  
      		/* Log and Publish */
      		logAndPublish(ioe, true, true); 
          }
          finally {
			this.connected = false;
          }
        }
    }      
    
    /**
     * logAndPublish method to write out to console and/or logger.
     * @param msg Message to publish
     * @param log Write to logger boolean
     * @param console Write to console boolean
     */
    public void logAndPublish (String msg, boolean log, boolean console) {
    	if (console)
    		System.out.println(msg);
    	
    	if (log)
    		this.fLogger.info(msg);
    	
    }   
    
    /**
     * logAndPublish method to write out to console and/or logger.
     * @param msg Exception to publish
     * @param log Write to logger boolean
     * @param console Write to console boolean
     */
    public void logAndPublish (Exception msg, boolean log, boolean console) {
    	if (console)
    		msg.printStackTrace();
    	
    	if (log)
    		this.fLogger.info(msg.getMessage());
    }    
    

	public GameState getGameState() {
		return gameState;
	}
	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
	public int getM_iVersion() {
		return m_iVersion;
	}
	public void setM_iVersion(int m_iVersion) {
		this.m_iVersion = m_iVersion;
	}
	public short getM_iMinorVersion() {
		return m_iMinorVersion;
	}
	public void setM_iMinorVersion(short m_iMinorVersion) {
		this.m_iMinorVersion = m_iMinorVersion;
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
	 * setMessage method to set byte array
	 * @param msg
	 */
	public void setMessage (byte[] msg) 
	{
		this.message = msg;
	}

	/**
	 * setSize method to set array size
	 * @param size
	 */
	public void setSize (int size)
	{
		this.size = size;
	}
	
	/**
	 * getSize method returns array size
	 * @return
	 */
	public int getSize ()
	{
		return size;
	}
	
	/**
	 * getMessage method returns byte array
	 * @return
	 */
	public byte[] getMessage ()
	{
		return message;
	}
    

		
}    

	

