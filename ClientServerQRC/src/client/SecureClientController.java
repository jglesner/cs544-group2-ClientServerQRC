/**
 * 
 */
package client;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.*;
import java.util.Vector;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import common.MessageParser;
import common.MessageParser.TypeIndicator;
import common.MessageParser.VersionIndicator;
import common.findServer.EchoFinder;
import common.XmlParser;


/**
 * @author Jeremy Glesner
 *
 */
public class SecureClientController implements Runnable {

	private String trustStore=null;
	private String trustStorePassword=null;	
	private String keyStore=null;
	private String keyStorePassword=null;  
		
	private SSLSocket socket;
	private final XmlParser xmlParser;
	/* logging utility */
	private final Logger fLogger; 
	private boolean connected; //status for listening	
	
	/** For reading input from socket */
    private InputStream oInputStream;

    /** For writing output to socket. */
    private OutputStream oOutputStream;	
	
    /**
     * Port number of server
     */
     private int port = 0; //will be read from the config file

    /**
     * Host Name or IP address in String form
     */
    private String hostName= null;

    public SecureClientController(XmlParser xmlParser, Logger fLogger) {
    	this.xmlParser = xmlParser;
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
	    this.port = Integer.parseInt(this.xmlParser.getClientTagValue("PORT_NUMBER"));
	    this.connected = false;
		this.trustStore=this.xmlParser.getClientTagValue("DEFAULT_TRUSTSTORE");
		this.trustStorePassword=this.xmlParser.getClientTagValue("DEFAULT_TRUSTSTORE_PASSWORD");
		this.keyStore=this.xmlParser.getClientTagValue("DEFAULT_KEYSTORE");
		this.keyStorePassword=this.xmlParser.getClientTagValue("DEFAULT_KEYSTORE_PASSWORD");
		this.fLogger.info("Setting port number to:" + this.port);
		this.fLogger.info("Using TrustStore: " + this.trustStore);
		this.fLogger.info("Setting KeyStore: " + this.keyStore);
    }    
    
    public boolean isConnected() {
		return connected;
    }


    public int getPort(){
            return port;
        }

    public void setPort(int port){
            this.port = port;
        }

    public String getHostName(){
            return hostName;
        }

    public void setHostName(String hostName){
            this.hostName = hostName;
        }   
    
	public void start()
	{
		System.out.println("Starting Client...");	
		SecureClientController c = null;
		SSLSocketFactory ssf = null;
		try {
			c = new SecureClientController(this.xmlParser, this.fLogger);
	        ssf=getSSLSocketFactory();
		}
		catch(Exception e)
		{
			System.out.println("Error " + e);
		}		
        
		//connect to server identified in config settings
		try
		{
	        c.connect(ssf,c.hostName,c.port);
		} catch (IOException e) {
			System.out.println(e);
		}
		
		//find server using various methods
		if (!connected) {
			try {
				c.findConnect(ssf, port);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e);
				e.printStackTrace();
			}
		}
		
	    try {  
	        MessageParser messageParser = new MessageParser();
	        System.out.println("Sending Version Message");
	        byte[] msg = messageParser.CreateVersionMessage(messageParser.new VersionMessage(1, TypeIndicator.VERSION, VersionIndicator.CLIENT_VERSION, (short)0, (long)0));
	        c.sendMessage(msg);
	        Thread.sleep(1000);
	        c.disconnect();			
			System.out.println("Successful\n");	
		}
		catch(Exception e)
		{
			System.out.println("Error " + e);
		}	

	}
	
    public void connect(SSLSocketFactory ssf, String hostName, int port) throws IOException {
        if(!connected)
        {
        	this.hostName = hostName;
            this.port = port;
            socket = (SSLSocket)ssf.createSocket(hostName, port); //new SSLSocket(hostName,port);
           //get I/O from socket
           oInputStream = socket.getInputStream();
           oOutputStream = socket.getOutputStream();

		   connected = true;
           //initiate reading from server...
           Thread t = new Thread(this);
           t.start(); //will call run method of this class
        }
    }
    
    public void findConnect(SSLSocketFactory ssf, int port) throws IOException {
        if(!connected)
        { 	
        	this.port = port;
        	System.out.println("Attempting to find Server via ICMP Echo or TCP Port 7 Echo...");
			EchoFinder ef = new EchoFinder(ssf, port);
	        
	        socket = ef.findAGMPServer(port);
	        
	        //get I/O from socket
	        if (socket != null) {
		        oInputStream = socket.getInputStream();
		        oOutputStream = socket.getOutputStream();
		
				connected = true;
		        //initiate reading from server...
		        Thread t = new Thread(this);
		        t.start(); //will call run method of this class    	
	        }
        }
    }

    public void sendMessage(byte[] msg) throws IOException
    {
		if(connected) {
	        oOutputStream.write(msg);
        } else throw new IOException("Not connected to server");
    }

    public void disconnect() {
		if(socket != null && connected)
        {
          try {
			socket.close();
          }catch(IOException ioe) {
			//unable to close, nothing to do...
          }
          finally {
			this.connected = false;
          }
        }
    }  

    public void run() {
	   byte[] msg = new byte[100];
	   int iByteSize = -1;
       try 
       {
    	   while(connected && (iByteSize = oInputStream.read(msg)) > 0)
    	   {
        	 System.out.println("Server:"+msg);
			 //notify observers//
			 //this.setChanged();
			 
			 //notify+send out received msg to Observers
             //this.notifyObservers(msg);
            }
         }
         catch(IOException ioe) { }
         finally { connected = false; }
    }
    
    /**
     * Provides a SSLSocketFactory which ignores JSSE's choice of truststore,
     * and instead uses either the hard-coded filename and password, or those
     * passed in on the command-line.
     * This method calls out to getTrustManagers() to do most of the
     * grunt-work. It actually just needs to set up a SSLContext and obtain
     * the SSLSocketFactory from there.
     * @return SSLSocketFactory SSLSocketFactory to use
     */
    protected SSLSocketFactory getSSLSocketFactory() throws IOException, GeneralSecurityException
    {
      // Call getTrustManagers to get suitable trust managers
      TrustManager[] tms=getTrustManagers();
      
      // Call getKeyManagers (from CustomKeyStoreClient) to get suitable
      // key managers
      KeyManager[] kms=getKeyManagers();

      // Next construct and initialize a SSLContext with the KeyStore and
      // the TrustStore. We use the default SecureRandom.
      SSLContext context=SSLContext.getInstance("TLS");
      context.init(kms, tms, null);

      // Finally, we get a SocketFactory, and pass it to SimpleSSLClient.
      SSLSocketFactory ssf=context.getSocketFactory();
      return ssf;
    }  
    
    /**
     * Returns an array of TrustManagers, set up to use the required
     * trustStore. This is pulled out separately so that later  
     * examples can call it.
     * This method does the bulk of the work of setting up the custom
     * trust managers.
     * @return an array of TrustManagers set up accordingly.
     */
    protected TrustManager[] getTrustManagers() throws IOException, GeneralSecurityException
    {
      // First, get the default TrustManagerFactory.
      String alg=TrustManagerFactory.getDefaultAlgorithm();
      TrustManagerFactory tmFact=TrustManagerFactory.getInstance(alg);
      
      // Next, set up the TrustStore to use. We need to load the file into
      // a KeyStore instance.
      KeyStore ks=KeyStore.getInstance("jks");
      
      ClassLoader classLoader = getClass().getClassLoader();
      InputStream keystoreStream = classLoader.getResourceAsStream(trustStore); // note, not getSYSTEMResourceAsStream  
      ks.load(keystoreStream, trustStorePassword.toCharArray());

      // Now we initialise the TrustManagerFactory with this KeyStore
      tmFact.init(ks);

      // And now get the TrustManagers
      TrustManager[] tms=tmFact.getTrustManagers();
      return tms;
    }  

    /**
     * Returns an array of KeyManagers, set up to use the required
     * keyStore. This is pulled out separately so that later  
     * examples can call it.
     * This method does the bulk of the work of setting up the custom
     * trust managers.
     * @return an array of KeyManagers set up accordingly.
     */
    protected KeyManager[] getKeyManagers() throws IOException, GeneralSecurityException
    {
      // First, get the default KeyManagerFactory.
      String alg=KeyManagerFactory.getDefaultAlgorithm();
      KeyManagerFactory kmFact=KeyManagerFactory.getInstance(alg);
      
      // Next, set up the KeyStore to use. We need to load the file into
      // a KeyStore instance.
      KeyStore ks=KeyStore.getInstance("jks");

      ClassLoader classLoader = getClass().getClassLoader();
      InputStream keystoreStream = classLoader.getResourceAsStream(keyStore); // note, not getSYSTEMResourceAsStream  
      ks.load(keystoreStream, keyStorePassword.toCharArray()); 

      // Now we initialise the KeyManagerFactory with this KeyStore
      kmFact.init(ks, keyStorePassword.toCharArray());

      // And now get the KeyManagers
      KeyManager[] kms=kmFact.getKeyManagers();
      return kms;
    }  
	
}
