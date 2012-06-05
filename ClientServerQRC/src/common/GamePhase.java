package common;

import common.GameState.State;

public class GamePhase {
	private Phase phase;

	
	public Phase getPhase() {
		return phase;
	}


	public void setPhase(Phase phase) {
		this.phase = phase;
	}


	public enum Phase {
		INIT(0), HOLE(1), FLOP(2), TURN(3), RIVER(4), FOLD(5), QUIT(6);
		private int phase;
		Phase(int phase)
		{
			this.setPhase(phase);
		}
		public int getPhase() {
			return phase;
		}
		public void setPhase(int phase) {
			this.phase = phase;
		}
		public boolean isEqual(Phase rhs)
		{
			return (this.phase == rhs.getPhase());
		}
	}
}
