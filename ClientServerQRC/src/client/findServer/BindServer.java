package client.findServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 *  The BindServer Class
 *
 *  Used to create the socket connection between the server and the client
 *  
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  Members: Jeremy Glesner, Dustin Overmiller, Yiqi Ju, Lei Yuan
 *  Project: Advanced Game Message Protocol Implementation
 *  
 */
public class BindServer {
	
    /**
     * Secure Socket connection
     */	
	private SSLSocket socket = null;
	
	/**
	 * Default constructor
	 * @param - none
	 * @return - none
	 */
	public BindServer() {
		
	}
	
	/**
	 * connect - create the socket between the server and the client
	 * @param ssf
	 * @param remoteAddress
	 * @param port
	 * @return
	 * @throws IOException
	 */
	public SSLSocket connect(SSLSocketFactory ssf, byte[] remoteAddress, int port) throws IOException {
		
	    InetAddress rAddress = InetAddress.getByAddress(remoteAddress);          
	    SocketAddress sAddress = new InetSocketAddress(rAddress, port);
	    socket = (SSLSocket)ssf.createSocket();
	    socket.connect(sAddress, 100);
	    return socket;
	}
    
	
}
