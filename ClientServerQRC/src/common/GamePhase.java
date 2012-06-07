package common;


public class GamePhase {
	public static int INIT = 0;
	public static int HOLE = 1;
	public static int FLOP = 2;
	public static int TURN = 3;
	public static int RIVER = 4;
	public static int FOLD = 5;
	public static int QUIT = 6;
	
	private int phase;
	
	public GamePhase(int phase)
	{
		this.phase = 0;
		if (phase >= 0 && phase < 7)
		{
			this.phase = phase;
		}
	}

	
	public int getPhase() {
		return phase;
	}


	public void setPhase(int phase) {
		if (phase >= 0 && phase < 7)
		{
			this.phase = phase;
		}
	}
}
