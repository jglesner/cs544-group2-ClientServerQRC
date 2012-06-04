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
import common.GameState;
import common.MessageParser;
import common.GameState.State;
import common.MessageParser.GameIndicator;
import common.MessageParser.GameTypeCode;
import common.MessageParser.TypeIndicator;
import common.MessageParser.VersionIndicator;
import common.findServer.EchoFinder;
import common.XmlParser;


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
    
    /**
     * Constructor for this class.
     * @param xmlParser Incoming XML Parser
     * @param fLogger Incoming Logger
     */
    public SecureClientController(XmlParser xmlParser, Logger fLogger) {
        
		/* setup parser */
    	this.xmlParser = xmlParser;
    	
		/* setup logger */
    	if (fLogger != null)
    	{
    		this.fLogger = fLogger;
    	}
    	else
    	{
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

		/* instantiate a new SecureClientController */
		try {
			c = new SecureClientController(this.xmlParser, this.fLogger);
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
    public void run() {
       try 
       {
    	   /* Game loop */
    	   while(connected && this.gameState.getState() != State.CLOSED) 
    	   {
   		    
    		   	if (this.gameState.getState().isEqual(State.AUTHENTICATE))
    		   	{
    				/* Log and Publish */
    				logAndPublish("Enter Authentication State.", false, true);
    				
    				GameAuthenticateState();
    		   	}
 
    		   	else if (this.gameState.getState().isEqual(State.GAMELIST))
	       		{
    				/* Log and Publish */
    				logAndPublish("Enter List State.", false, true);
    				
	       			GameListState();
	       		}    	        		
    		   	else if (this.gameState.getState().isEqual(State.GAMESET))
        		{
    				/* Log and Publish */
    				logAndPublish("Enter Set State.", false, true);
    				
        			GameSetState();
        		}        		
        		
        		else if (this.gameState.getState().isEqual(State.GAMEPLAY))
        		{
    				/* Log and Publish */
    				logAndPublish("Enter Play State.", false, true);
    				
        			GamePlayState();
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

	

