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
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import common.GameState.State;
import common.MessageParser.GameIndicator;
import common.MessageParser.GamePlayRequest;
import common.MessageParser.GameTypeCode;
import common.MessageParser.TypeIndicator;
import common.MessageParser.VersionIndicator;
import common.findServer.EchoFinder;
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
    
    /**
     * Constructor for this class.
     * @param xmlParser Incoming XML Parser
     * @param fLogger Incoming Logger
     */
    public SecureClientController(XmlParser xmlParser, LogAndPublish logAndPublish) 
    {
        
		/* setup parser */
    	this.xmlParser = xmlParser;
    	this.logAndPublish = logAndPublish;
    	
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
		this.bankAmount = 0;
		
		logAndPublish.write("Setting port number to:" + this.port, true, false);
		logAndPublish.write("Using TrustStore: " + this.trustStore, true, false);
		logAndPublish.write("Setting KeyStore: " + this.keyStore, true, false);
	
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
		logAndPublish.write("Client connecting to server...", true, true);

		/* instantiate a new SecureClientController */
		try {
			c = new SecureClientController(this.xmlParser, logAndPublish);
	        ssf=getSSLSocketFactory();
		}
		catch(Exception e)
		{
			/* Log and Publish */
			logAndPublish.write(e, true, true);			
		}		
        
		/* Connect to server identified in configuration settings */
		try
		{
	        c.connect(ssf,c.hostName,c.port);
		} catch (IOException e) 
		{
			/* Log and Publish */
			logAndPublish.write(e, true, true);
		}
		
		/* find server using ICMP/TCP methods */
		if (!c.connected) {
			System.out.println("PROBLEM");
			
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
     * run method executes when a new SecureClientController thread, 
     * complete with socket is started 
     */
    public void run() {
       try 
       {
    	   /* Game loop */
    	   while(connected && this.gameState.getState() != State.CLOSED) 
    	   {
   		    
    		   	if (this.gameState.getState().isEqual(State.AUTHENTICATE))
    		   	{
    				/* Log and Publish */
    				logAndPublish.write("Enter Authentication State.", false, true);
    				
    				GameAuthenticateState();
    		   	}
 
    		   	else if (this.gameState.getState().isEqual(State.GAMELIST))
	       		{
    				/* Log and Publish */
    				logAndPublish.write("Enter List State.", false, true);
    				
	       			GameListState();
	       		}    	        		
    		   	else if (this.gameState.getState().isEqual(State.GAMESET))
        		{
    				/* Log and Publish */
    				logAndPublish.write("Enter Set State.", false, true);
    				
        			GameSetState();
        		}        		
        		
        		else if (this.gameState.getState().isEqual(State.GAMEPLAY))
        		{
    				/* Log and Publish */
    				logAndPublish.write("Enter Play State.", false, true);
    				
        			GamePlayState();
        		}
        		else if (this.gameState.getState().isEqual(State.CLOSING))
        		{
    				/* Log and Publish */
    				logAndPublish.write("Enter Closing State.", false, true);
    				
        			ClosingState();
        			break;
        		}
				/* Log and Publish */
				logAndPublish.write("Connection status: " + connected, false, false);
				
				/* Log and Publish */
				logAndPublish.write("Game state: " + this.gameState.getState(), false, false);				

        	}
    	   connected = false;
		
		}
		catch(Exception e)
		{
      		/* Log and Publish */
      		logAndPublish.write(e, true, true); 
		}	    		   
        finally { connected = false; }
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
		
		DISPLAY:
		while(true)
		{
		
			try
			{
				
				/* minimum ante allowable */
				int min_ante = Integer.parseInt(this.xmlParser.getServerTagValue("MIN_ANTE"));
				
				
				/* sub-state: Verify player has enough money to play */
				{
					
					if (this.bankAmount < min_ante)
					{
						logAndPublish.write("You are too poor to play this game.  Returning to game list...", false, true);
						this.gameState.setState(State.GAMELIST);
						break;
					}
					MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, TypeIndicator.GAME, GameIndicator.PLAY_GAME, GameTypeCode.TEXAS_HOLDEM, GamePlayRequest.NOT_SET, (long)0);  
			        this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));					
				    
			        
				}
				
				
				/* sub-state: Begin Playing A Game Of Texas Holdem? */
				{
					
					logAndPublish.write("Begin Playing A Game Of Texas Holdem?", false, true);
					
					/* loop to handle user command line input */
					command = UserSelection("Enter 1 to begin, and 2 to return to game list.", 1, 2);
					
					/* handle user selection */
					if (command == 1){
					
						/* send init message */
						MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, TypeIndicator.GAME, GameIndicator.PLAY_GAME, GameTypeCode.TEXAS_HOLDEM, GamePlayRequest.INIT, (long)0);  
				        this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));						
						
						/* proceed to the next step */
						this.gameState.setState(State.GAMEPLAY);
	
					} else {
						
						/* transition to closing state */
						this.gameState.setState(State.GAMELIST);
						break;
					}				
				}
				
				
				/* sub-state: Player puts down an ante */
				{

					/* sub-state: place your bet */
					logAndPublish.write("Place your bet.", false, true);
					

					
					/* loop to handle user command line input */
					command = UserSelection("Enter any amount greater than " + min_ante + " to begin, and 2 to return to game list.", min_ante, (int)this.bankAmount);
					
					/* handle user selection */
					
						/* send init message */
						MessageParser.ClientPlayGameMessage msg = this.messageParser.new ClientPlayGameMessage(1, TypeIndicator.GAME, GameIndicator.PLAY_GAME, GameTypeCode.TEXAS_HOLDEM, GamePlayRequest.GET_HOLE, (long)command);  
				        this.sendMessage(this.messageParser.CreateClientPlayGameMessage(msg));						
						
						/* proceed to the next step */
						this.gameState.setState(State.GAMEPLAY);
					
				}
			
				/* Dealer will give the player two cards */
				{
					
			        /* get server response */
			        ServerResponse sr = this.receiveMessage();
					

				}

			
			}
			catch(Exception e)
			{
				logAndPublish.write(e,true,true);
				this.disconnect();
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
    		logAndPublish.write(msg.toString(), false, true);  
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
    		logAndPublish.write(msg.toString(), false, false);          	
        }
        else
        {
        	//TODO: ADD ERROR HANDLING AND STATE CHANGES USING SERVER RESPONSE//
        }
        
        //TODO: ADD ERROR HANDLING AND STATE CHANGES USING SERVER RESPONSE//

        //TODO: ADD DYNAMIC MENU USING SERVER RESPONSE//	
		
		/* loop to handle user command line input */
		command = UserSelection("1: Play Texas Hold'em\n2: Close Connection", 1, 2);
		
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
		logAndPublish.write("Sending Version Message", true, true);
        
        MessageParser.VersionMessage oMsg1 = this.messageParser.new VersionMessage(1, TypeIndicator.VERSION, VersionIndicator.CLIENT_VERSION, (short)0, (long)0);
        this.sendMessage(this.messageParser.CreateVersionMessage(oMsg1));
        
        /* get server response */
        ServerResponse sr = this.receiveMessage();
        
		/* verify type as VERSION */
        if (this.messageParser.GetTypeIndicator(sr.getMessage(), sr.getSize()).isEqual(TypeIndicator.VERSION)) 
        {
        	MessageParser.VersionMessage iMsg = this.messageParser.GetVersionMessage(sr.getMessage(), sr.getSize());

        	this.bankAmount = iMsg.getBankAmount();
        	
    		/* Log and Publish */
    		logAndPublish.write(iMsg.toString(), false, true);        	
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
   		 	
   			/* Log and Publish */
   			logAndPublish.write("'connect' status: " + connected, false, false);   		 	
   		 	
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
		logAndPublish.write("Attempting to find server using ICMP and/or TCP.", true, true); 
    	
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
	   			logAndPublish.write("'findConnect' status: " + connected, false, true);   		 	
				
				/* Spawn thread with 'this' instance.  Initiate reading from server. */
		        Thread t = new Thread(this);
		        t.start();	
	        }
        }
    }
    
	/**
	 * UserSelection method to capture user input from command line to questions
	 * 
	 * @param choice String summarizing the choice to be made
	 * @param greaterThan Integer of a value to be greater than as first number in the choice sequence
	 * @param lessThan Integer of a value to be less than and equal to the last number in the choice sequence
	 * @return
	 */
	public int UserSelection(String choice, int greaterThan, int lessThan)
	{
		int command = -1;
		
		reset:
		while(true) {
			try {
				logAndPublish.write(choice, false, true);
				command = Integer.parseInt(br.readLine());
			} catch (NumberFormatException e) {
			
	    		/* Log and Publish */
	    		logAndPublish.write(e, true, false);
	    		logAndPublish.write("Invalid Selection.  Try again.", false, true);
				continue reset;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.disconnect();
			}
			
			/* command contains a valid number */
			if ((command >= greaterThan) && (command <=lessThan))
				break;
			else 
			{
				logAndPublish.write("Invalid Selection.  Try again.", false, true);
				continue reset;
			}
		}	
		
		return command;
	
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
    public void disconnect() 
    {
    	
    	/* Disconnect from server */
		if(socket != null && connected)
        {
          try {
			this.socket.close();
          }catch(IOException ioe) {
        	  
      		/* Log and Publish */
      		logAndPublish.write(ioe, true, true); 
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

	

