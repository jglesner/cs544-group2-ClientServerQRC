package common;

/**
 * GamePlayState class is used by the client to maintain the current phase of texas holdem
 * It contains the various states that the client could transition to, a simple contructor, and
 * getter and setter functions 
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
	 * GamePhase - Contructor used to create the class
	 * @param none
	 */
	public GamePlayState()
	{
		this.state = NOT_SET;
	}
	
	/**
	 * getPlayState - simply return the current game state
	 * @return
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
