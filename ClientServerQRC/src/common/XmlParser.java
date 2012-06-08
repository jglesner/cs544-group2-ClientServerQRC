/*
 * This class defines an xml parser to use for the code instead of hardcoding the values in the class itself.
 */

package common;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.InputStream;



/**
 *  The XmlParser Class
 *  
 *  This class is used to obtain configuration parameters used
 *  to run both the client and the server applications.
 *
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  Members: Jeremy Glesner, Dustin Overmiller, Yiqi Ju, Lei Yuan
 *  Project: Advanced Game Message Protocol Implementation
 *  
 */
public class XmlParser {
	private static Node nNodeServer;
	private static Node nNodeClient;

   /* used to create a mutex to avoid multiple access */
	static class theLock extends Object {
		
	}
	
   /* used to create the mutex to avoid multiple access */
	static private theLock lockObject = new theLock();


	/**
   * Constructor - Opens the config file and prepares the parser for both
   * client and server parsing.
   *
   */
	public XmlParser() {
		
		try {
         /* get the config file and setup the xml parsing */
			ClassLoader classLoader = getClass().getClassLoader();
		    InputStream XmlStream = classLoader.getResourceAsStream("common/Config.xml"); // note, not getSYSTEMResourceAsStream 
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(XmlStream);
			doc.getDocumentElement().normalize();
         /* find the server root of the xml structure */
			nNodeServer = doc.getElementsByTagName("ServerConfig").item(0);
         /* find the client root of the xml structure */
			nNodeClient = doc.getElementsByTagName("ClientConfig").item(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
   /**
   * getClientTagValue - get the value of the client tag that is passed in
   * @param String sTag
   * @return String
   */
	public String getClientTagValue(String sTag) {
      /* obtain the mutex and grab the value of the tag */
		synchronized (lockObject) {
			Element eElement = (Element) nNodeClient;
			Node nNode = eElement.getElementsByTagName(sTag).item(0).getChildNodes().item(0);
			return nNode.getNodeValue();					
		}
	}
	/**
   * getServerTagValue - get the value of the server tag that is passed in
   * @param String sTag
   * @return String
   */
	public String getServerTagValue(String sTag) {
      /* obtain the mutex and grab the value of the tag */
		synchronized (lockObject) {
			Element eElement = (Element) nNodeServer;
			Node nNode = eElement.getElementsByTagName(sTag).item(0).getChildNodes().item(0);
			return nNode.getNodeValue();					
		}
	}

}
