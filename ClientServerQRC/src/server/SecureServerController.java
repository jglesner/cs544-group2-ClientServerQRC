/**
 * 
 */
package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Vector;
import java.util.Observer;
import java.io.*;
import java.util.UUID;
import javax.net.ssl.*;

import server.SimpleSSLServer.SimpleHandshakeListener;

import java.security.*;
import java.security.cert.*;
import java.io.*;

/**
 * @author Jeremy Glesner
 *
 */
public class SecureServerController implements Observer {

	private final String DEFAULT_TRUSTSTORE="server/truststore-server.jks";
	private final String DEFAULT_TRUSTSTORE_PASSWORD="password";
	private String trustStore=DEFAULT_TRUSTSTORE;
	private String trustStorePassword=DEFAULT_TRUSTSTORE_PASSWORD;	
  
	private final String DEFAULT_KEYSTORE="server/keystore-server.jks";
	private final String DEFAULT_KEYSTORE_PASSWORD="password";
	private String keyStore=DEFAULT_KEYSTORE;
	private String keyStorePassword=DEFAULT_KEYSTORE_PASSWORD;  
	
	/** This vector holds all connected clients.
	 * May be used for broadcasting, etc. */
	private Vector clients;
	
	private SSLSocket socket;
	private SSLServerSocket ssocket;  //ServerController Socket
	
	private StartSecureServerControllerThread sst; //inner class
	private SSLServerSocketFactory ssf;
	
	/**
	 * Represents each currently connected client.
	 * @label initiates
	 * @clientCardinality 1
	 * @supplierCardinality 0..*
	 */
	private ClientModel ClientModel;

	/** Port number of ServerController. */
	private int port;
	private boolean listening; //status for listening	
	
	
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
	
	public SecureServerController() {
		this.clients = new Vector();
	    this.port = 5555; //default port
	    this.listening = false;
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

	        java.util.Enumeration e = this.clients.elements();
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

//	        	SecureServerController.this.ssocket = new ServerSocket(SecureServerController.this.port);
	        	SecureServerController.this.ssocket = (SSLServerSocket)SecureServerController.this.ssf.createServerSocket(SecureServerController.this.port);


	        	
	            while (this.listen) {
				//wait for client to connect//

	            	SecureServerController.this.socket = (SSLSocket)SecureServerController.this.ssocket.accept();
	                System.out.println("Client connected");
	                try 
	                {
   	
	                	SecureServerController.this.ClientModel = new ClientModel(SecureServerController.this.socket);
	                    Thread t = new Thread(SecureServerController.this.ClientModel);
	                    SecureServerController.this.ClientModel.addObserver(SecureServerController.this);
	                    SecureServerController.this.clients.addElement(SecureServerController.this.ClientModel);
	                    
	    	            // We add in a HandshakeCompletedListener, which allows us to
	    	            // peek at the certificate provided by the client.
	    	            HandshakeCompletedListener hcl=new SimpleHandshakeListener(SecureServerController.this.ClientModel.uniqueID);
	    	            socket.addHandshakeCompletedListener(hcl); 
	    	            
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
		    SSLContext context=SSLContext.getInstance("SSL");
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
	   * @param trustStore the TrustStore to use. This should be in JKS format.
	   * @param password the password for this TrustStore.
	   * @return an array of TrustManagers set up accordingly.
	   */
	  protected TrustManager[] getTrustManagers()
	    throws IOException, GeneralSecurityException
	  {
	    // First, get the default TrustManagerFactory.
	    String alg=TrustManagerFactory.getDefaultAlgorithm();
	    TrustManagerFactory tmFact=TrustManagerFactory.getInstance(alg);
	    
	    // Next, set up the TrustStore to use. We need to load the file into
	    // a KeyStore instance.
	    //FileInputStream fis=new FileInputStream(trustStore);
	    KeyStore ks=KeyStore.getInstance("jks");
	    
	    ClassLoader classLoader = getClass().getClassLoader();
	    InputStream keystoreStream = classLoader.getResourceAsStream(trustStore); // note, not getSYSTEMResourceAsStream  
	    ks.load(keystoreStream, trustStorePassword.toCharArray());
	    
	    //ks.load(fis, trustStorePassword.toCharArray());
	    //fis.close();

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
	   * @param trustStore the KeyStore to use. This should be in JKS format.
	   * @param password the password for this KeyStore.
	   * @return an array of KeyManagers set up accordingly.
	   */
	  protected KeyManager[] getKeyManagers()
	    throws IOException, GeneralSecurityException
	  {
	    // First, get the default KeyManagerFactory.
	    String alg=KeyManagerFactory.getDefaultAlgorithm();
	    KeyManagerFactory kmFact=KeyManagerFactory.getInstance(alg);
	    
	    // Next, set up the KeyStore to use. We need to load the file into
	    // a KeyStore instance.
	     
	    //FileInputStream fis=new FileInputStream(keyStore);
	    KeyStore ks=KeyStore.getInstance("jks");

	    ClassLoader classLoader = getClass().getClassLoader();
	    InputStream keystoreStream = classLoader.getResourceAsStream(keyStore); // note, not getSYSTEMResourceAsStream  
	    ks.load(keystoreStream, keyStorePassword.toCharArray()); 
	    
	    //ks.load(fis, keyStorePassword.toCharArray());
	    //fis.close();

	    // Now we initialise the KeyManagerFactory with this KeyStore
	    kmFact.init(ks, keyStorePassword.toCharArray());

	    // And now get the KeyManagers
	    KeyManager[] kms=kmFact.getKeyManagers();
	    return kms;
	  }
	}
	
	  /**
	   * Utility HandshakeCompletedListener which simply displays the
	   * certificate presented by the connecting peer.
	   */
	  class SimpleHandshakeListener implements HandshakeCompletedListener
	  {
	    UUID ident;

	    /**
	     * Constructs a SimpleHandshakeListener with the given
	     * identifier.
	     * @param ident Used to identify output from this Listener.
	     */
	    public SimpleHandshakeListener(UUID ident)
	    {
	      this.ident=ident;
	    }

	    /** Invoked upon SSL handshake completion. */
	    public void handshakeCompleted(HandshakeCompletedEvent event)
	    {
	      // Display the peer specified in the certificate.
	      try {
	        X509Certificate cert=(X509Certificate)event.getPeerCertificates()[0];
	        String peer=cert.getSubjectDN().getName();
	        System.out.println(ident+": Request from "+peer);
	      }
	      catch (SSLPeerUnverifiedException pue) {
	        System.out.println(ident+": Peer unverified");
	      }
	    }
	  }
}


