package common;

public class GamePlayState {
	public static int NOT_SET = 0;
	public static int INIT = 1;
	public static int GET_HOLE = 2;
	public static int GET_FLOP = 3;
	public static int GET_TURN = 4;
	public static int GET_RIVER = 5;
	public static int FOLD = 6;
	private int state;
	public GamePlayState()
	{
		this.state = NOT_SET;
	}
	
	public int getPlayState()
	{
		return this.state;
	}
	
	public void setPlayState(int state)
	{
		if (state >= 0 && state < 7)
		{
			this.state = state;
		}
	}

}
