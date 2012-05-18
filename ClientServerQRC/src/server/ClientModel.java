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
import common.MessageParser.TypeIndicator;
import common.MessageParser.VersionIndicator;

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
		if (this.messageParser.GetTypeIndicator(inputBuffer, iByteCount).isEqual(TypeIndicator.VERSION))
		{
			MessageParser.VersionMessage msg = this.messageParser.GetVersionMessage(inputBuffer, iByteCount);
			if ((msg.getVersion() == this.m_iVersion) && (msg.getVersionType().isEqual(VersionIndicator.CLIENT_VERSION)))
			{
				msg.setBankAmount(this.m_lClientBankAmount);
				msg.setVersionType(VersionIndicator.VERSION_ACK);
				try	{
					oOutputStream.write(this.messageParser.CreateVersionMessage(msg));
					this.gameState.setState(State.WAIT);
				} catch (Exception e) {
					e.printStackTrace();
					this.gameState.setState(State.CLOSED);
				}
			}
			else if (msg.getVersionType().isEqual(VersionIndicator.CLIENT_VERSION))
			{
				// client needs to upgrade
				// The server cannot communicate so send the message and close the connection
				msg.setBankAmount((long)0);
				msg.setVersion(this.m_iVersion);
				msg.setMinorVersion(this.m_iMinorVersion);
				msg.setTypeCode(TypeIndicator.VERSION);
				msg.setVersionType(VersionIndicator.VERSION_UPGRADE);
				try {
					oOutputStream.write(this.messageParser.CreateVersionMessage(msg));
					Thread.sleep(500);
					this.gameState.setState(State.CLOSED);
				} catch (Exception e) {
					e.printStackTrace();
					this.gameState.setState(State.CLOSED);
				}
			}
			else
			{
				msg.setBankAmount((long)0);
				msg.setMinorVersion(this.m_iMinorVersion);
				msg.setTypeCode(TypeIndicator.VERSION);
				msg.setVersion(this.m_iVersion);
				msg.setVersionType(VersionIndicator.VERSION_REQUIREMENT);
				try {
					oOutputStream.write(this.messageParser.CreateVersionMessage(msg));
				} catch (Exception e) {
					e.printStackTrace();
					this.gameState.setState(State.CLOSED);
				}
			}
		}
		else
		{
			// client did not send the right message, server needs to force a version message
			MessageParser.VersionMessage msg = this.messageParser.new VersionMessage(this.m_iVersion, TypeIndicator.VERSION, VersionIndicator.VERSION_REQUIREMENT, this.m_iMinorVersion,(long)0);
			try {
				oOutputStream.write(this.messageParser.CreateVersionMessage(msg));
			} catch (Exception e) {
				e.printStackTrace();
				this.gameState.setState(State.CLOSED);
			}
		}
		
	}	
}
