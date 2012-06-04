/**
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  Members: Jeremy Glesner, Dustin Overmiller, Yiqi Ju, Lei Yuan
 *  Project: Advanced Game Message Protocol Implementation
 *  
 *  This is an example of a client side application using the Advanced Game Management Protocol to send messages   
 * 
 *  This application framework originally drew heavily from the following resource:
 *  1. Saleem, Usman. "A Pattern/Framework for Client/Server Programming in Java". Year accessed: 2012, Month accessed: 05, Day accessed: 2.
 *  http://www.developer.com/java/ent/article.php/10933_1356891_2/A-PatternFramework-for-ClientServer-Programming-in-Java.htm
 *  
 *  However, the code has changed significantly since that time. Other contributing resources: 
 *  
 *  2. Oracle Corporation. "Java™ Secure Socket Extension (JSSE) Reference Guide". Java SE Documentation. Year accessed: 2012,
 *  Month accessed: 05, Day accessed: 2. http://docs.oracle.com/javase/7/docs/technotes/guides/security/jsse/JSSERefGuide.html
 *   
 *  3. StackOverflow. "How to get a path to a resource in a Java JAR file". Year accessed: 2012, Month accessed: 05, Day accessed: 2.
 *  http://stackoverflow.com/questions/941754/how-to-get-a-path-to-a-resource-in-a-java-jar-file
 *  
 *  4. IBM. "Custom SSL for advanced JSSE developers". Year accessed: 2012, Month accessed: 05, Day accessed: 2.
 *  http://www.ibm.com/developerworks/java/library/j-customssl/
 *  
 */

package controller;

import common.XmlParser;
import client.*;
import server.*;

/**
 *  Main Class initiates either the SecureClientController or the SecureServerController 
 *  based on command line arguments.
 */
public class Main {

	/**
	 *  main method initiates either the SecureClientController or the SecureServerController 
	 *  based on command line arguments.
	 * @param args
	 */
	public static void main(String[] args) {
		
		try
		{
			final XmlParser xmlParser = new XmlParser();

			if (args[0].equalsIgnoreCase("secureClient"))
			{
				SecureClientController scc = new SecureClientController(xmlParser, null);
				scc.start();
			}
			else if (args[0].equalsIgnoreCase("secureServer"))
			{
				SecureServerController ssc = new SecureServerController(xmlParser);
				ssc.start();
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			System.out.println("Please pass in an argument of \"secureClient\" or \"secureServer\"");
			System.out.println("An example would be: ");
			System.out.println("java -jar ClientServerQRC.jar secureClient");
			System.out.println("or");
			System.out.println("java -jar ClientServerQRC.jar secureServer");
		}
	}

}
