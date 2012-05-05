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
			  // send and receive data...
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
			connected = true;				
		}
	}
	
}
