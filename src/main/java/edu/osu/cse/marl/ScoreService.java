package edu.osu.cse.marl;

public class ScoreService {
	private static final int TURN_PENALTY = 1;
	private static final int TEAM_POINTS = 100;
	private int startingSize;
	private double pointsPerUnit;
	//TODO Adjust turn_penalty based on board size and team size

	public ScoreService(int startingSize) {
		this.startingSize = startingSize;
		pointsPerUnit = (double)TEAM_POINTS / startingSize;
	}

	public Score calculateScore(int team1Count, int team2Count, int turnCount) {
		int turnPenalty = TURN_PENALTY * turnCount;
		double team1Score = pointsPerUnit * (startingSize - team2Count) - turnPenalty;
		double team2Score = pointsPerUnit * (startingSize - team1Count) - turnPenalty;
		
		return new Score((int)team1Score, (int)team2Score, turnCount);
	}

}
