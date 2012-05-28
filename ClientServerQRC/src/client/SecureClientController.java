/**
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
import java.net.*;

/**
 * @author Jeremy Glesner
 *
 */
public class SecureClientController implements Runnable {

	
	private final String DEFAULT_TRUSTSTORE="client/truststore-client.jks";
	private final String DEFAULT_TRUSTSTORE_PASSWORD="password";
	private String trustStore=DEFAULT_TRUSTSTORE;
	private String trustStorePassword=DEFAULT_TRUSTSTORE_PASSWORD;	
	  
	private final String DEFAULT_KEYSTORE="client/keystore-client.jks";
	private final String DEFAULT_KEYSTORE_PASSWORD="password";
    private String keyStore=DEFAULT_KEYSTORE;
	private String keyStorePassword=DEFAULT_KEYSTORE_PASSWORD;  
	  
	private boolean connected; //status for listening		
	private SSLSocket socket;
	
	/** For reading input from socket */
    private BufferedReader br;

    /** For writing output to socket. */
    private PrintWriter pw;	
	
    /**
     * Port number of server
     */
     private int port=5555; //default port

    /**
     * Host Name or IP address in String form
     */
    private String hostName="localhost";//default host name

    public SecureClientController() {
		connected = false;
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
		try
		{
			//connectToServer();
	        SecureClientController c = new SecureClientController();
	        SSLSocketFactory ssf=getSSLSocketFactory();	        
	        c.connect(ssf,"localhost",5555);
	        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        String msg = "";
	        while(!msg.equalsIgnoreCase("quit"))
	        {
	           msg = br.readLine();
	           c.sendMessage(msg);
	        }
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
           //socket = (SSLSocket)ssf.createSocket(hostName, port); //new SSLSocket(hostName,port);
           socket = (SSLSocket)ssf.createSocket();
           
           InetAddress rAddress = InetAddress.getByAddress(new byte[]{(byte)192, (byte)168, 1, (byte)214});
           SocketAddress sAddress = new InetSocketAddress(rAddress, 5555);
           
           socket.connect(sAddress);
           
           //get I/O from socket
           br = new BufferedReader(new         InputStreamReader(socket.getInputStream()));
           pw = new PrintWriter(socket.getOutputStream(),true);

		   connected = true;
           //initiate reading from server...
           Thread t = new Thread(this);
           t.start(); //will call run method of this class
        }
    }

    public void sendMessage(String msg) throws IOException
    {
		if(connected) {
	        pw.println(msg);
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
	   String msg = ""; //holds the msg received from server
       try 
       {
          while(connected && (msg = br.readLine())!= null)
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
