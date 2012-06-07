package client.findServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * EchoFinder class is used to scan through the ip address of the network and use either ICMP or TCP
 * to find the server
 *
 */
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

    /**
     * EchoFinder - constructor for the class
     * @param ssf
     * @param port
     */
	public EchoFinder(SSLSocketFactory ssf, int port) {
    	this.bs = new BindServer();
    	this.ssf = ssf;
    	this.port = port;
    }
    
	/**
	 * findAGMPServer - main routine to scan the different ip address and use either ICMP or TCP to find the server
	 * @param port
	 * @return
	 */
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
			
			/* get the first three numbers of the ip address */
			String[] strArray = localAddr.toString().split("\\.");
			int first = Integer.parseInt(strArray[0].substring(1));
			int second = Integer.parseInt(strArray[1]);
			int third = Integer.parseInt(strArray[2]);
			
			//loop 1 through 254
			for(int i=1; i<255; i++) {
				String addr = first + "." + second + "." + third + "." + i;
				byte[] remoteAddress = new byte[]{(byte)first, (byte)second, (byte)third, (byte)i};
				if (InetAddress.getByAddress(remoteAddress).isReachable(100)) {
					try{
						socket = bs.connect(ssf, remoteAddress, port);
						System.out.println("Connected to IP: " + addr);
						break;
					} catch (IOException e) {
						//System.out.println("Connection Refused");	
					}
				} else {
					//System.out.println("Connection Refused");	
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
	
	/**
	 * getIpAddress - searches through the various network interfaces to find the ip address to use
	 * @return
	 * @throws SocketException
	 */
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
