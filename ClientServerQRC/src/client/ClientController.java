/**
 * 
 */
package client;

import java.net.*;
import java.io.*;

/**
 * @author Jeremy Glesner
 *
 */
public class ClientController {

	private boolean connected; //status for listening		
	private Socket serverSocketConn;
	/** For reading input from socket */
    private BufferedReader br;

    /** For writing output to socket. */
    private PrintWriter pw;	
	
	public void start()
	{
		System.out.println("Starting Client...");	
		try
		{
			connectToServer();
			System.out.println("Successful\n");	
		}
		catch(Exception e)
		{
			System.out.println("Error " + e);			
		}	

	}
		
	public ClientController() 
	{
	    this.connected = false;
	}

	public void connectToServer() {
		if (!connected) {
			try 
			{
			  InetAddress server = InetAddress.getLocalHost();
			  serverSocketConn = new Socket(server , 5555);
	          openChannel(serverSocketConn);
	          
			  System.out.println(br.readLine());
			  pw.print("test");
	          System.out.println(br.readLine());

	          
			}
			catch (UnknownHostException ex) {
			  System.err.println(ex);
			}
			catch (IOException ex) {
			  System.err.println(ex);
			}
			connected = true;
	    }
	}	
	
    public void openChannel(Socket socket) throws IOException {
        this.serverSocketConn = socket;
        //get I/O from socket
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream(), true);
        }
        catch (IOException ioe) {
            throw ioe;
        }
    }	
	
	
	public void closeToServer() {
		if (connected) {	
			try 
			{
				serverSocketConn.close();
			}
			catch (UnknownHostException ex) {
				System.err.println(ex);
			}
			catch (IOException ex) {
			  System.err.println(ex);
			}
			connected = false;				
		}
	}
	
}
