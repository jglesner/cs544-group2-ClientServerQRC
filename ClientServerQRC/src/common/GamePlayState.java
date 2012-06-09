package common;

/**
 *  The GamePlayState Class
 *
 *  Used by the client to maintain the current phase of texas holdem
 *  It contains the various states that the client could transition to, a simple contructor, and
 *  getter and setter functions 
 *  
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  Members: Jeremy Glesner, Dustin Overmiller, Yiqi Ju, Lei Yuan
 *  Project: Advanced Game Message Protocol Implementation
 *  
 */
public class GamePlayState {
	public static int NOT_SET = 0;
	public static int INIT = 1;
	public static int GET_HOLE = 2;
	public static int GET_FLOP = 3;
	public static int GET_TURN = 4;
	public static int GET_RIVER = 5;
	public static int FOLD = 6;
	private int state;
	/**
	 * GamePlayState - Constructor used to create the class
	 * @param none
	 */
	public GamePlayState()
	{
		this.state = NOT_SET;
	}
	
	/**
	 * getPlayState - simply return the current game state
	 * @return int
	 */
	public int getPlayState()
	{
		return this.state;
	}
	
	/**
	 * setPlayState - make sure the state is valid first
	 * @param state
	 */
	public void setPlayState(int state)
	{
		/* Check to make sure the state change is valid */
		if (state >= 0 && state < 7)
		{
			this.state = state;
		}
	}

}
