package common;

public class GameState {
	private State state;
	public GameState()
	{
		this.state = State.LISTENING;
	}
	
	public enum State {
		LISTENING(0), AUTHENTICATE(1), WAIT(2), GAME_STATE(3), CLOSING(4), CLOSED(5);
		private int state;
		State(int state)
		{
			this.setState(state);
		}
		public int getState() {
			return state;
		}
		public void setState(int state) {
			this.state = state;
		}
		public boolean isEqual(State rhs)
		{
			return (this.state == rhs.getState());
		}
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
