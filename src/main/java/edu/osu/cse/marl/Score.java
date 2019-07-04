package edu.osu.cse.marl;

public class Score {
	private int team1Score;
	private int team2Score;
	private int turnCount;
	private String winner;

	public Score(int team1Score, int team2Score, int turnCount) {
		this.team1Score = team1Score;
		this.team2Score = team2Score;
		this.turnCount = turnCount;

		if (team1Score > team2Score) {
			winner = "Team 1";
		} else if (team2Score > team1Score) {
			winner = "Team 2";
		} else {
			winner = null;
		}
	}
	
	public String toString() {
		return "Team 1: " + team1Score + "; Team 2: " + team2Score;
	}

	public String getValue() {
		String value = "Team 1: " + team1Score + "; Team 2: " + team2Score;
		if (winner != null) {
			value += ". Winner is " + winner;
		} else {
			value += ". It's a tie.";
		}
		return value;
	}

	public int getTeam1Score() {
		return team1Score;
	}

	public int getTeam2Score() {
		return team2Score;
	}

	public String getWinner() {
		return winner;
	}

	public int getTurnCount() {
		return turnCount;
	}

}
