/**
 * 
 */
package server;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Observable;
import javax.net.ssl.SSLSocket;
import java.util.logging.*;
import common.*;
import common.GameState.State;

/**
 * @author Jeremy Glesner
 *
 */
public class ClientModel extends Observable implements Runnable {

	/** For reading input from socket */
    private InputStream oInputStream;
    /** For writing output to socket. */
    private OutputStream oOutputStream;

    /** Socket object representing client connection */

    private SSLSocket socket;
    private boolean running;
    public String uniqueID;
    private final Logger fLogger;
    private GameState gameState;
    private XmlParser xmlParser;
    private MessageParser messageParser = null;
    private int m_iVersion = -1;
    private short m_iMinorVersion = -1;
    private long m_lClientBankAmount = -1;
    
    
    public ClientModel(SSLSocket socket, XmlParser xmlParser, Logger fLogger) throws IOException {
        this.socket = socket;
        this.uniqueID = "" + socket.getInetAddress() + ":" + socket.getPort();
        this.fLogger = fLogger;
        this.xmlParser = xmlParser;
        this.gameState = new GameState();
        this.gameState.setState(State.AUTHENTICATE);
        this.messageParser = new MessageParser();
        this.m_iVersion = Integer.parseInt(this.xmlParser.getServerTagValue("VERSION"));
        this.m_iMinorVersion = (short)Integer.parseInt(this.xmlParser.getServerTagValue("MINOR_VERSION"));
        this.m_lClientBankAmount = Integer.parseInt(this.xmlParser.getServerTagValue("CLIENT_BANK_AMOUNT"));
        running = false;
        //get I/O from socket
        try {
            this.oInputStream = socket.getInputStream();
            this.oOutputStream = socket.getOutputStream();
            running = true; //set status
        }
        catch (IOException ioe) {
            throw ioe;
        }
    }
	
    /** 
     *Stops clients connection
     */

    public void stopClient()
    {
        try {
        this.gameState.setState(State.CLOSED);
		this.socket.close();
        }catch(IOException ioe){ };
    }

    public void run() {
        byte[] inputBuffer = new byte[100];
        int iByteCount = -1;
        
        //start listening message from client//
        try 
        {
        	while (running && (iByteCount = oInputStream.read(inputBuffer)) > 0 && this.gameState.getState() != State.CLOSED)
        	{
        		switch(this.gameState.getState())
        		{
        			case AUTHENTICATE:
        			{
        				AuthenticateState(inputBuffer, iByteCount);
        			}
        			case WAIT:
        			{
        				WaitState(inputBuffer, iByteCount);
        			}
        			case GAME_STATE:
        			{
        				GameState(inputBuffer, iByteCount);
        			}
        			case CLOSING:
        			{
        				ClosingState(inputBuffer, iByteCount);
        			}
        			
        		}
        	}
        	running = false;
        }
        catch (IOException ioe) {
        	running = false;
        }
        
        //it's time to close the socket
        try {
        	this.socket.close();
        	this.fLogger.info("Closing " + uniqueID + " connection.");
        	System.out.println("Closing " + uniqueID + " connection.");
        } catch (IOException ioe) { }
        
        //notify the observers for cleanup etc.
        this.setChanged();              //inherit from Observable
        this.notifyObservers(this);     //inherit from Observable
    }

	private void ClosingState(byte[] inputBuffer, int iByteCount) {
		// TODO Auto-generated method stub
		
	}

	private void GameState(byte[] inputBuffer, int iByteCount) {
		// TODO Auto-generated method stub
		
	}

	private void WaitState(byte[] inputBuffer, int iByteCount) {
		// TODO Auto-generated method stub
		
	}

	private void AuthenticateState(byte[] inputBuffer, int iByteCount) {
		int TypeIndicator = this.messageParser.GetTypeIndicator(inputBuffer, iByteCount);
		System.out.println("Type Indicator is: " + TypeIndicator);
		if (TypeIndicator != MessageParser.TypeIndicator.VERSION.getIndicator())
		{
			// need to send a version message to the client
			byte[] outputBuffer = this.messageParser.CreateVersionMessage(m_iVersion, MessageParser.TypeIndicator.VERSION.getIndicator(), (short)MessageParser.VersionIndicator.VERSION_REQUIREMENT.getIndicator(), m_iMinorVersion, 0);
			try
			{
				oOutputStream.write(outputBuffer);
			}
			catch (Exception e)
			{
				// TODO: log the problem and exit
			}
		}
		else
		{
			short VersionIndicator = this.messageParser.GetVersionType(inputBuffer, iByteCount);
			if (VersionIndicator == (short)MessageParser.VersionIndicator.CLIENT_VERSION.getIndicator())
			{
				// need to check the versioning of the client
				if (this.messageParser.GetVersion(inputBuffer, iByteCount) == this.m_iVersion)
				{
					// The client has a valid version and can communicate properly
					// need to send a version message to the client
					byte[] outputBuffer = this.messageParser.CreateVersionMessage(m_iVersion, MessageParser.TypeIndicator.VERSION.getIndicator(), (short)MessageParser.VersionIndicator.VERSION_ACK.getIndicator(), this.messageParser.GetMinorVersion(inputBuffer, iByteCount), this.m_lClientBankAmount);
					try
					{
						oOutputStream.write(outputBuffer);
					}
					catch (Exception e)
					{
						// TODO: log the problem and exit
					}
					// This finishes the authentication state
					System.out.println("Got a valid version from the client!");
					this.gameState.setState(State.WAIT);
				}
				else
				{
					// client needs to upgrade their version
					byte[] outputBuffer = this.messageParser.CreateVersionMessage(m_iVersion, MessageParser.TypeIndicator.VERSION.getIndicator(), (short)MessageParser.VersionIndicator.VERSION_UPGRADE.getIndicator(), this.messageParser.GetMinorVersion(inputBuffer, iByteCount), 0);
					try
					{
						oOutputStream.write(outputBuffer);
					}
					catch (Exception e)
					{
						// TODO: log the problem and exit
					}
					// Close the connection
					this.gameState.setState(State.CLOSED);
				}
			}
			else
			{
				// need to send a version message to the client
				byte[] outputBuffer = this.messageParser.CreateVersionMessage(m_iVersion, MessageParser.TypeIndicator.VERSION.getIndicator(), (short)MessageParser.VersionIndicator.VERSION_REQUIREMENT.getIndicator(), m_iMinorVersion, 0);
				try
				{
					oOutputStream.write(outputBuffer);
				}
				catch (Exception e)
				{
					// TODO: log the problem and exit
				}
			}
		}
		
	}	
}
