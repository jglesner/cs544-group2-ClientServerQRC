package common;

/**
 *  The GameState Class
 *
 *  Used by the client and the server to maintain the state of the DFA
 *  These states are IAW the DFA defined in the documentation. There are also functions to 
 *  get and set the GameState.
 *  
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  Members: Jeremy Glesner, Dustin Overmiller, Yiqi Ju, Lei Yuan
 *  Project: Advanced Game Message Protocol Implementation
 *  
 */
public class GameState {
	private int state;
	public static int LISTENING = 0;
	public static int AUTHENTICATE = 1;
	public static int GAMELIST = 2;
	public static int GAMESET = 3;
	public static int GAMEPLAY = 4;
	public static int CLOSED = 5;
   
   /**
	 * GameState - Contructor used to create the class
	 * @param none
	 */
	public GameState()
	{
		this.state = LISTENING;
	}
	
   /**
	 * getState - simply return the current game state
	 * @return
	 */
	public int getState()
	{
		return this.state;
	}
	
   /**
	 * setPlayState - make sure the state is valid first
	 * @param state
	 */
	public void setState(int state)
	{
      /* Check to make sure the state change is valid */
		if (state >= 0 && state < 6)
		{
			this.state = state;
		}
	}

}
