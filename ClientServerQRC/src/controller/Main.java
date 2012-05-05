/**
 * 
 */
package controller;

import client.ClientController;
import server.ServerController;

/**
 * @author Jeremy Glesner
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args[0].equalsIgnoreCase("client"))
		{
			ClientController cc = new ClientController();
			cc.start();
		}
		if (args[0].equalsIgnoreCase("server"))
		{
			ServerController sc = new ServerController();
			sc.start();
		}
	}

}
