package common;

public class GamePlayState {
	private PlayState state;
	public GamePlayState()
	{
		this.state = PlayState.NOT_SET;
	}
	
	public enum PlayState {
      NOT_SET(0), INIT(1), GET_HOLE(2), GET_FLOP(3), GET_TURN(4), GET_RIVER(5), FOLD(6);
		private int state;
		PlayState(int state)
		{
			this.setPlayState(state);
		}
		public int getPlayState() {
			return state;
		}
		public void setPlayState(int state) {
			this.state = state;
		}
		public boolean isEqual(PlayState rhs)
		{
			return (this.state == rhs.getPlayState());
		}
	}

	
	public PlayState getPlayState()
	{
		return this.state;
	}
	
	public void setPlayState(PlayState state)
	{
		this.state = state;
	}

}
