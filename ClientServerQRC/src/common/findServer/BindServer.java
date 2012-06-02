package common.findServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class BindServer {
	
    /**
     * Secure Socket connection
     */	
	private SSLSocket socket = null;
	
	public BindServer() {
		
	}
	
	public SSLSocket connect(SSLSocketFactory ssf, byte[] remoteAddress, int port) throws IOException {
		
	    InetAddress rAddress = InetAddress.getByAddress(remoteAddress);          
	    SocketAddress sAddress = new InetSocketAddress(rAddress, port);
	    socket = (SSLSocket)ssf.createSocket();
	    socket.connect(sAddress, 100);
	    return socket;
	}
    
	
}
