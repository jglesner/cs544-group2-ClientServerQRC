package common;

public class GameState {
	private int state;
	public static int LISTENING = 0;
	public static int AUTHENTICATE = 1;
	public static int GAMELIST = 2;
	public static int GAMESET = 3;
	public static int GAMEPLAY = 4;
	public static int CLOSED = 5;
	public GameState()
	{
		this.state = LISTENING;
	}
	
	public int getState()
	{
		return this.state;
	}
	
	public void setState(int state)
	{
		if (state >= 0 && state < 6)
		{
			this.state = state;
		}
	}

}
