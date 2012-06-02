package common.findServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class EchoFinder {

    /**
     * Secure Socket connection
     */	
	private SSLSocket socket = null;
	
    /**
     * Bind Server
     */		
	private BindServer bs = null;
	
    /**
     * Port number of server
     */
    private int port = 0; //will be read from the config file		
	
    SSLSocketFactory ssf = null;

	public EchoFinder(SSLSocketFactory ssf, int port) {
    	this.bs = new BindServer();
    	this.ssf = ssf;
    	this.port = port;
    }
    
	public SSLSocket findAGMPServer(int port) {
		try {
			InetAddress localAddr = null;
			try {
		        	localAddr = getIpAddress();
		    }
			catch (SocketException e)
			{
					e.printStackTrace();
			}
			
			String[] strArray = localAddr.toString().split("\\.");
			int first = Integer.parseInt(strArray[0].substring(1));
			int second = Integer.parseInt(strArray[1]);
			int third = Integer.parseInt(strArray[2]);
			
			//loop 1 through 254
			for(int i=1; i<254; i++) {
				String addr = first + "." + second + "." + third + "." + i;
				System.out.println("IP: " + addr);
				byte[] remoteAddress = new byte[]{(byte)first, (byte)second, (byte)third, (byte)i};
				if (InetAddress.getByAddress(remoteAddress).isReachable(100)) {
					try{
						socket = bs.connect(ssf, remoteAddress, port);
						System.out.println("Connected");
						break;
					} catch (IOException e) {
						System.out.println("Connection Refused");	
					}
				} else {
					System.out.println("Connection Refused");	
				}
			}	
		
		} catch (UnknownHostException unk) {
			//No host
			System.out.println(unk);
			unk.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return socket;
	}
	
    public InetAddress getIpAddress() throws SocketException {
    	
    	InetAddress localaddr = null;
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces(); //.getNetworkInterfaces();

        while (nets.hasMoreElements()) {
        	
        	NetworkInterface netint = nets.nextElement();
        	
	    	//http://docs.oracle.com/javase/tutorial/networking/nifs/listing.html
	        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
	        while (inetAddresses.hasMoreElements()){ 
	            //System.out.printf("InetAddress: %s\n", inetAddresses.nextElement());
	            InetAddress i = (InetAddress) inetAddresses.nextElement();
	            //System.out.println("string: " + i.toString());
	            if((! i.toString().startsWith("/127")) && (! i.toString().startsWith("/0"))) {
	            	System.out.println("My IP = " + i.getHostAddress());
	            	localaddr = i;
	            }
	        }
        }
        return localaddr;
     }	
	
    
	
}
