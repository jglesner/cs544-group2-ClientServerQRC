package common;

public class GameState {
	private State state;
	public GameState()
	{
		this.state = State.LISTENING;
	}
	
	public enum State {
		LISTENING(0), AUTHENTICATE(1), GAMELIST(2), GAMESET(3), GAMEPLAY(4), CLOSING(5), CLOSED(6);
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
