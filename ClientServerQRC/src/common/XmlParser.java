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




public class XmlParser {
	private static Node nNodeServer;
	private static Node nNodeClient;

	static class theLock extends Object {
		
	}
	
	static private theLock lockObject = new theLock();


		
	public XmlParser() {
		
		try {
			ClassLoader classLoader = getClass().getClassLoader();
		    InputStream XmlStream = classLoader.getResourceAsStream("common/Config.xml"); // note, not getSYSTEMResourceAsStream 
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(XmlStream);
			doc.getDocumentElement().normalize();

			nNodeServer = doc.getElementsByTagName("ServerConfig").item(0);
			nNodeClient = doc.getElementsByTagName("ClientConfig").item(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public String getClientTagValue(String sTag) {
		synchronized (lockObject) {
			Element eElement = (Element) nNodeClient;
			Node nNode = eElement.getElementsByTagName(sTag).item(0).getChildNodes().item(0);
			return nNode.getNodeValue();					
		}
	}
	
	public String getServerTagValue(String sTag) {
		synchronized (lockObject) {
			Element eElement = (Element) nNodeServer;
			Node nNode = eElement.getElementsByTagName(sTag).item(0).getChildNodes().item(0);
			return nNode.getNodeValue();					
		}
	}

}
