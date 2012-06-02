package common.findServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class EchoFinder {

    /**
     * Secure Socket connection
     */	
	private SSLSocket socket;
	
    /**
     * Bind Server
     */		
	private BindServer bs = null;
	
    /**
     * Port number of server
     */
    private int port = 0; //will be read from the config file		
	
    SSLSocketFactory ssf = null;
    
    public void echoFinder(SSLSocketFactory ssf, int port) {
    	bs = new BindServer();
    }
    
	public SSLSocket findAGMPServer(int port) {
		try {
			String localAddr = getIpAddress();
			String[] strArray = localAddr.split("\\.");
			int first = Integer.parseInt(strArray[0]);
			int second = Integer.parseInt(strArray[1]);
			int third = Integer.parseInt(strArray[2]);
			
			//loop 1 through 254
			for(int i=1; i<254; i++) {
				String addr = first + "." + second + "." + third + "." + i;
				System.out.println("IP: " + addr);
				byte[] remoteAddress = new byte[]{(byte)first, (byte)second, (byte)third, (byte)i};
				if (InetAddress.getByAddress(remoteAddress).isReachable(2000)) {
					try{
						bs.connect(ssf, remoteAddress, port);
						System.out.println("Connected");
					} catch (IOException e) {
						System.out.println("Connection Refused");	
					}
				}
			}	
		
		} catch (UnknownHostException unk) {
			//No host
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return socket;
	}

	private String getIpAddress() throws UnknownHostException {

		String hostName = InetAddress.getLocalHost().getHostName();
		InetAddress addrs[] = InetAddress.getAllByName(hostName);
		
		String localIP = "";
		
		for (InetAddress addr: addrs) {
			System.out.println ("addr.getHostAddress() = " + addr.getHostAddress());
			System.out.println ("addr.getHostName() = " + addr.getHostName());
			System.out.println ("addr.isAnyLocalAddress() = " + addr.isAnyLocalAddress());
			System.out.println ("addr.isLinkLocalAddress() = " + addr.isLinkLocalAddress());
			System.out.println ("addr.isLoopbackAddress() = " + addr.isLoopbackAddress());
			System.out.println ("addr.isMulticastAddress() = " + addr.isMulticastAddress());
			System.out.println ("addr.isSiteLocalAddress() = " + addr.isSiteLocalAddress());
			System.out.println ("");
			if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
				localIP = addr.getHostAddress();
			}
		}
		
		System.out.println ("\nIP = " + localIP);
		
		return localIP;
	}
	
	
	
}
