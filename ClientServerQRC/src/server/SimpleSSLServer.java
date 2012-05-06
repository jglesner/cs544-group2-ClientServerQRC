package server;

import javax.net.ssl.*;
import java.security.cert.*;
import java.io.*;
import java.util.Properties;

/**
 * A very simple server which accepts SSL connections, and displays
 * text sent through the SSL socket on stdout. The server requires
 * client authentication.
 * Listens on port 49152 by default, configurable with "-port" on the
 * command-line.
 * The server needs to be stopped with Ctrl-C.
 */
public class SimpleSSLServer extends Thread
{
  private static final int DEFAULT_PORT=49152;

  private SSLServerSocketFactory serverSocketFactory;
  private int port;

  /**
   * main() method, called when run from the command-line. Deals with
   * command-line parameters, then starts listening for connections
   */
  
  public SimpleSSLServer()
  {}
  
  
  public void start()
  {
    int port=DEFAULT_PORT;

    try 
    {

    	Properties systemProps = System.getProperties();
    	systemProps.put("javax.net.ssl.trustStore", "./truststore-server.jks");
    	systemProps.put("javax.net.ssl.trustStorePassword", "password");
    	systemProps.put("javax.net.ssl.keyStore", "./keystore-server.jks");
    	systemProps.put("javax.net.ssl.keyStorePassword", "password");    	
    	System.setProperties(systemProps);   
    	
      SSLServerSocketFactory ssf=
        (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
      SimpleSSLServer server=new SimpleSSLServer(ssf, port);
      server.start();
    }
    catch(Exception e)
    {
    	System.err.println("Error " + e);
    }
  }

  /** Displays the command-line usage for SimpleSSLServer */
  private static void displayUsage()
  {
    System.out.println("Options:");
    System.out.println("\t-port\tport of server (default "+DEFAULT_PORT+")");
  }


  /**
   * Constructs a new SimpleSSLServer on the given port, using
   * the given SSLServerSocketFactory
   * @param ssf the SSLServerSocketFactory to use
   * @param port the port to listen on
   */
  public SimpleSSLServer(SSLServerSocketFactory ssf, int port)
  {
    serverSocketFactory=ssf;
    this.port=port;
  }

  /**
   * SimpleSSLServer is run as a separate Thread. The run() method
   * provides the main loop for the server. It runs as an infinite
   * loop; stop with Ctrl-C.
   */
  public void run()
  {
    System.out.println("SimpleSSLServer running on port "+port);

    try {
      // First, create the server socket on which we'll accept
      // connection requests. We require client authentication.
      SSLServerSocket serverSocket=
        (SSLServerSocket)serverSocketFactory.createServerSocket(port);

      serverSocket.setNeedClientAuth(true);

      // Each connection is given a numeric identifier, starting at 1.
      int id=1;

      // Listen for connection requests. For each request fire off a new
      // thread (the InputDisplayer) which echoes incoming text from the
      // stream to stdout.
      while(true) {
        String ident=String.valueOf(id++);

        // Wait for a connection request.
        SSLSocket socket=(SSLSocket)serverSocket.accept();

        // We add in a HandshakeCompletedListener, which allows us to
        // peek at the certificate provided by the client.
        HandshakeCompletedListener hcl=new SimpleHandshakeListener(ident);
        socket.addHandshakeCompletedListener(hcl);

        InputStream in=socket.getInputStream();
        new InputDisplayer(ident, in);
      }
    }
    catch(IOException ioe) {
      System.out.println("SimpleSSLServer failed with following exception:");
      System.out.println(ioe);
      ioe.printStackTrace();
    }
  }

  /**
   * Utility HandshakeCompletedListener which simply displays the
   * certificate presented by the connecting peer.
   */
  class SimpleHandshakeListener implements HandshakeCompletedListener
  {
    String ident;

    /**
     * Constructs a SimpleHandshakeListener with the given
     * identifier.
     * @param ident Used to identify output from this Listener.
     */
    public SimpleHandshakeListener(String ident)
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
    

  /**
   * Utility thread class which simply forwards any text passed through
   * the supplied InputStream to stdout. An identifier is specified, which
   * preceeds forwarded text in stdout. InputDisplayer also logs its
   * progress to stdout.
   */
  class InputDisplayer extends Thread {
    BufferedReader reader;
    String ident;

    /**
     * Constructs an InputDisplayer with the given identifier, around
     * the given InputStream.
     * @param ident Used to identify all output from this InputDisplayer.
     * @param InputStream Stream of bytes, in UTF-8, to echo to stdout.
     */
    InputDisplayer(String ident, InputStream is)
    {
      this.ident=ident;
      log("New connection request");

      // Set up a reader to the supplied InputStream. The InputStreamReader
      // converts the raw bytes (from the InputStream) into characters, and
      // the BufferedReader splits the stream into lines for us.
      // We use UTF-8 (matching SimpleSSLClient), which should be supported
      // by the JVM. If it isn't, we'll use the JVM's default codepage which
      // is likely to be good enough.
      try {
        reader=new BufferedReader(new InputStreamReader(is, "UTF-8"));
      }
      catch (UnsupportedEncodingException uee) {
        log("Warning: JVM cannot support UTF-8. Using default instead");
        reader=new BufferedReader(new InputStreamReader(is));
      }

      // Mark the thread as a Daemon, and start it.
      setDaemon(true);
      start();
    }

    /**
     * Sits in a loop on the reader, echoing each line to the screen.
     */
    public void run()
    {
      boolean done=false;

      try {
        while (!done) {
          String line=reader.readLine();
          if (line!=null) display(line);
          else done=true;
        }
        log("Client disconnected");
      }
      catch(IOException ioe) {
        // Something went wrong. Log the exception and close.
        log(ioe.toString());
        log("Closing connection.");
      }

      try {
        reader.close();
      }
      catch(IOException ioe) {}
    }

    /**
     * Used to log progress.
     * @param text Text to display to stdout, preceeded by the identifier
     */
    private void log(String text)
    {
      System.out.println(ident+": "+text);
    }

    /**
     * Used to echo text from the InputStream.
     * @param text Text to display to stdout, preceeded by the identifier
     */
    private void display(String text)
    {
      System.out.println(ident+"> "+text);
    }
  }
}