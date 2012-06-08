/**
 * LogAndPublish is the ErrorHandler Class for this AGMP implementation
 */
package common;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *  The LogAndPublish Errorhandler Class
 *
 *  Used by both the client and the server for logging and console output
 *  
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  Members: Jeremy Glesner, Dustin Overmiller, Yiqi Ju, Lei Yuan
 *  Project: Advanced Game Message Protocol Implementation
 *  
 */
public class LogAndPublish {

	/* utilities */
	private Logger fLogger = null;; 
	private XmlParser xmlParser = null;
	
	/**
	 * Constructor
	 */
	public LogAndPublish (XmlParser xmlParser, String type)
	{
	
		/* setup parser */
    	this.xmlParser = xmlParser;
    	
		/* setup logger */

    	if (type.contains("server"))
    	{
			this.fLogger = Logger.getLogger(this.xmlParser.getServerTagValue("LOG_FILE"));
			this.fLogger.setUseParentHandlers(false);
			this.fLogger.removeHandler(new ConsoleHandler());
			try {
				FileHandler fh = new FileHandler(this.xmlParser.getServerTagValue("LOG_FILE"), true);
				fh.setFormatter(new SimpleFormatter());
				this.fLogger.addHandler(fh);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}	    		
    	}
    	else if (type.contains("client"))
    	{
			this.fLogger = Logger.getLogger(this.xmlParser.getClientTagValue("LOG_FILE"));
			this.fLogger.setUseParentHandlers(false);
			this.fLogger.removeHandler(new ConsoleHandler());
			try {
				FileHandler fh = new FileHandler(this.xmlParser.getClientTagValue("LOG_FILE"), true);
				fh.setFormatter(new SimpleFormatter());
				this.fLogger.addHandler(fh);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}	
    	}
		
	}

    /**
     * logAndPublish method to write out to console and/or logger.
     * @param msg String to publish
     * @param log Write to logger boolean
     * @param console Write to console boolean
     */
    public void write (String msg, boolean log, boolean console) {
    	if (console)
    		System.out.println(msg);
    	
    	if (log)
    		this.fLogger.info(msg);
    }       	
	
    /**
     * logAndPublish method to write out to console and/or logger.
     * @param msg Exception to publish
     * @param log Write to logger boolean
     * @param console Write to console boolean
     */
    public void write (Exception msg, boolean log, boolean console) {
    	if (console)
    		msg.printStackTrace();
    	
    	if (log)
    		this.fLogger.info(msg.getMessage());
    }       
	
}
