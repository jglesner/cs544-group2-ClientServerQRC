/**
 * 
 */
package client;

import java.net.*;
import java.util.Observable;
import java.io.*;


/**
 * @author Jeremy Glesner
 *
 */
public class SecureClientController implements Runnable {

	private boolean connected; //status for listening		
	private Socket serverSocketConn;
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
	        ClientController c = new ClientController();
	        c.connect("localhost",5555);
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
	
    public void connect(String hostName, int port) throws IOException {
        if(!connected)
        {
	     this.hostName = hostName;
           this.port = port;
           serverSocketConn = new Socket(hostName,port);
           //get I/O from socket
           br = new BufferedReader(new         InputStreamReader(serverSocketConn.getInputStream()));
           pw = new PrintWriter(serverSocketConn.getOutputStream(),true);

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
		if(serverSocketConn != null && connected)
        {
          try {
			serverSocketConn.close();
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
    
//	public void connectToServer() {
//	if (!connected) {
//		try 
//		{
//		  InetAddress server = InetAddress.getLocalHost();
//		  serverSocketConn = new Socket(server , 5555);
//          openChannel(serverSocketConn);
//          
//          //Print initial connect message
//		  System.out.println(br.readLine());
//
//		  //Send test message
//		  pw.println("test");
//		  
//		  //Capture response and print
//          System.out.println(br.readLine());
//
//          
//		}
//		catch (UnknownHostException ex) {
//		  System.err.println(ex);
//		}
//		catch (IOException ex) {
//		  System.err.println(ex);
//		}
//		connected = true;
//    }
//}	
//
//public void openChannel(Socket socket) throws IOException {
//    this.serverSocketConn = socket;
//    //get I/O from socket
//    try {
//        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        pw = new PrintWriter(socket.getOutputStream(), true);
//    }
//    catch (IOException ioe) {
//        throw ioe;
//    }
//}	    

//	public void closeToServer() {
//	if (connected) {	
//		try 
//		{
//			serverSocketConn.close();
//		}
//		catch (UnknownHostException ex) {
//			System.err.println(ex);
//		}
//		catch (IOException ex) {
//		  System.err.println(ex);
//		}
//		connected = false;				
//	}
//}
	
}
