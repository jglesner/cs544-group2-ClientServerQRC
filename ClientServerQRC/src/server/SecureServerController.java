/**
 * 
 * 
 * 
 * 
 * 
 * 
 *  Two Resources: 
 *  
 *  http://docs.oracle.com/javase/7/docs/technotes/guides/security/jsse/JSSERefGuide.html
 *  http://stackoverflow.com/questions/941754/how-to-get-a-path-to-a-resource-in-a-java-jar-file
 *  http://www.ibm.com/developerworks/java/library/j-customssl/
 *  http://www.developer.com/java/ent/article.php/10933_1356891_2/A-PatternFramework-for-ClientServer-Programming-in-Java.htm
 *  http://publib.boulder.ibm.com/infocenter/dzichelp/v2r2/index.jsp?topic=%2Fcom.ibm.ims.soap.doc%2Fsgw_configmutualsslbasicauth.htm
 *  
 */


package server;

import java.util.Enumeration;
import java.util.Observable;
import java.util.Vector;
import java.util.Observer;
import java.io.*;
import javax.net.ssl.*;

import common.XmlParser;

import java.security.*;
import java.security.cert.*;
import java.util.logging.*;


/**
 * @author Jeremy Glesner
 *
 */
public class SecureServerController implements Observer {
	private String trustStore=null;
	private String trustStorePassword=null;	
	private String keyStore=null;
	private String keyStorePassword=null;  
	
	/** This vector holds all connected clients. */
	private Vector<ClientModel> clients;
	
	private SSLSocket socket;
	private SSLServerSocket ssocket;  //ServerController Socket
	
	private StartSecureServerControllerThread sst; //inner class
	private SSLServerSocketFactory ssf;
	private ClientModel ClientModel;
	private final XmlParser xmlParser;
	/* logging utility */
	private final Logger fLogger;
	

	/** Port number of ServerController. */
	private int port = 0;
	
	/** status for listening */
	private boolean listening;	
	
	
	public void start()
	{
		System.out.println("Starting Server...");	
		try
		{
			startServerController();
			System.out.println("Successful\n");	
		}
		catch(Exception e)
		{
			System.out.println("Error " + e);			
		}	
	}	
	
	public SecureServerController(XmlParser xmlParser) {
		this.xmlParser = xmlParser;
		this.fLogger = Logger.getLogger(this.xmlParser.getServerTagValue("LOG_FILE"));
		this.fLogger.setUseParentHandlers(false);
		this.fLogger.removeHandler(new ConsoleHandler());
		try {
			FileHandler fh = new FileHandler(this.xmlParser.getServerTagValue("LOG_FILE"), true);
			fh.setFormatter(new SimpleFormatter());
			this.fLogger.addHandler(fh);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		this.clients = new Vector<ClientModel>();
	    this.port = Integer.parseInt(this.xmlParser.getServerTagValue("PORT_NUMBER"));
	    this.listening = false;
		this.trustStore=this.xmlParser.getServerTagValue("DEFAULT_TRUSTSTORE");
		this.trustStorePassword=this.xmlParser.getServerTagValue("DEFAULT_TRUSTSTORE_PASSWORD");
		this.keyStore=this.xmlParser.getServerTagValue("DEFAULT_KEYSTORE");
		this.keyStorePassword=this.xmlParser.getServerTagValue("DEFAULT_KEYSTORE_PASSWORD");
		this.fLogger.info("Setting port number to:" + this.port);
		this.fLogger.info("Using TrustStore: " + this.trustStore);
		this.fLogger.info("Setting KeyStore: " + this.keyStore);
	}

	public void startServerController() {
		if (!listening) {
			this.sst = new StartSecureServerControllerThread();
	        this.sst.start();
	        this.listening = true;
	    }
	}
	
	public void stopServerController() {
	    if (this.listening) {
	        this.sst.stopServerControllerThread();
	        //close all connected clients//

	        Enumeration<ClientModel> e = this.clients.elements();
	        while(e.hasMoreElements())
	        {
			  ClientModel ct = (ClientModel)e.nextElement();
	          ct.stopClient();
	        }
	        this.listening = false;
	    }
	}

	//Observable interface//
	public void update(Observable observable, Object object) {
	    //notified by observables, do cleanup here//
	    this.clients.removeElement(observable);
	}

	public int getPort() {
	    return port;
	}

	public void setPort(int port) {
	    this.port = port;
	}

	 
	/**
	 * Utility HandshakeCompletedListener which simply displays the
	 * certificate presented by the connecting peer.
	 */
	class SimpleHandshakeListener implements HandshakeCompletedListener
	{
		String ident;
		private final Logger fLogger;

	    /**
	     * Constructs a SimpleHandshakeListener with the given
	     * identifier.
	     * @param ident Used to identify output from this Listener.
	     */
	    public SimpleHandshakeListener(String ident, Logger fLogger)
	    {
	      this.ident=ident;
	      this.fLogger = fLogger;
	    }

	    /** Invoked upon SSL handshake completion. */
	    public void handshakeCompleted(HandshakeCompletedEvent event)
	    {
	      // Display the peer specified in the certificate.
	      try {
	        X509Certificate cert=(X509Certificate)event.getPeerCertificates()[0];
	        String peer=cert.getSubjectDN().getName();
	        this.fLogger.info(ident+": Request from "+peer+"\n");
	      }
	      catch (SSLPeerUnverifiedException pue) {
	    	  this.fLogger.warning(ident+": Peer unverified\n");
	      }
	    }	
	  }

	/** This inner class will keep listening to incoming connections,
	 *  and initiating a ClientModel object for each connection. */
	
	private class StartSecureServerControllerThread extends Thread {
		private boolean listen;
		
	    public StartSecureServerControllerThread() {
	        this.listen = false;
	    	
	        try 
	        {	        
	        	SecureServerController.this.ssf= getSSLServerSocketFactory();
		    }
		    catch(Exception e)
		    {
		    	System.err.println("Error " + e);
		    }
	    }

	    public void run() {
	        this.listen = true;
	        try 
	        {

	/**The following constructor provides a default number of
	* connections -- 50, according to Java's documentation.
	* An overloaded constructor is available for providing a 
	* specific number, more or less, about connections. */

	        	SecureServerController.this.ssocket = (SSLServerSocket)SecureServerController.this.ssf.createServerSocket(SecureServerController.this.port);
	        	ssocket.setNeedClientAuth(true);
	        	
	            while (this.listen) {
				//wait for client to connect//

	            	SecureServerController.this.socket = (SSLSocket)SecureServerController.this.ssocket.accept();
	            	String uniqueID = socket.getInetAddress() + ":" + socket.getPort();
	          
    	            HandshakeCompletedListener hcl=new SimpleHandshakeListener(uniqueID, SecureServerController.this.fLogger);
    	            SecureServerController.this.socket.addHandshakeCompletedListener(hcl);      
	            	
    	            System.out.print("Client " + uniqueID + " connected\n");
	            	SecureServerController.this.fLogger.info("Client " + uniqueID + " connected using ");
	                try 
	                {
	                	SSLSession clientSession = socket.getSession();
	                	SecureServerController.this.fLogger.info("protocol: " + clientSession.getProtocol() + ", ");
	                	SecureServerController.this.fLogger.info("cipher: " + clientSession.getCipherSuite() + "\n");
	                	
	                	SecureServerController.this.ClientModel = new ClientModel(SecureServerController.this.socket, SecureServerController.this.xmlParser, SecureServerController.this.fLogger);
	                    Thread t = new Thread(SecureServerController.this.ClientModel);
	                    SecureServerController.this.ClientModel.addObserver(SecureServerController.this);
	                    SecureServerController.this.clients.addElement(SecureServerController.this.ClientModel);
	    	            
	                    t.start();
	                } catch (IOException ioe) {
	                    System.err.println("Error " + ioe);
	                }
	            }
	        } catch (IOException ioe) {
	            //I/O error in ServerControllerSocket//
	            this.stopServerControllerThread();
	        }
	    }

    public void stopServerControllerThread() {
    	try {
    		SecureServerController.this.ssocket.close();
    	}
    	catch (IOException ioe) {
    		//unable to close ServerControllerSocket
    	}
    	this.listen = false;
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
		  protected SSLServerSocketFactory getSSLServerSocketFactory() throws IOException, GeneralSecurityException
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
		    SSLServerSocketFactory ssf=context.getServerSocketFactory(); //.getSocketFactory();
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

	    // Now we initialize the TrustManagerFactory with this KeyStore
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

	    // Now we initialize the KeyManagerFactory with this KeyStore
	    kmFact.init(ks, keyStorePassword.toCharArray());

	    // And now get the KeyManagers
	    KeyManager[] kms=kmFact.getKeyManagers();
	    return kms;
	  }
	}
}


