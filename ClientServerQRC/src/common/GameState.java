package common;

public class GameState {
	private State state;
	public GameState()
	{
		this.state = State.LISTENING;
	}
	
	public enum State {
		LISTENING, AUTHENTICATE, WAIT, GAME_STATE, CLOSING, CLOSED
	}
	
	public State getState()
	{
		return this.state;
	}
	
	public void setState(State state)
	{
		this.state = state;
	}

}
