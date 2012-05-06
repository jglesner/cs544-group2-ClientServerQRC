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

/**
 * @author Jeremy Glesner
 *
 */
public class SecureServerController implements Observer {

	private Socket socket;

	/** This vector holds all connected clients.
	 * May be used for broadcasting, etc. */
	private Vector clients;
	private ServerSocket ssocket;  //ServerController Socket
	private StartSecureServerControllerThread sst; //inner class

	
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
	    }

	    public void run() {
	        this.listen = true;
	        try 
	        {

	/**The following constructor provides a default number of
	* connections -- 50, according to Java's documentation.
	* An overloaded constructor is available for providing a 
	* specific number, more or less, about connections. */

	        	SecureServerController.this.ssocket = new ServerSocket(SecureServerController.this.port);


	            while (this.listen) {
				//wait for client to connect//

	            	SecureServerController.this.socket = SecureServerController.this.ssocket.accept();
	                System.out.println("Client connected");
	                try 
	                {
	                	SecureServerController.this.ClientModel = new ClientModel(SecureServerController.this.socket);
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
	}
}


