package edu.osu.cse.marl;

public class GameOptions {

	private Player team1Type;
	private Player team2Type;
	private int teamSize = 3;
	private int[] boardSize = { 5, 8 };
	private int maxTurns = 50;
	private int turnSpeed = 100; //number set is milliseconds of delay [a value of 1000 will make turns take 1 second]
	private boolean replay = false;
	private boolean actionText = false;
	public static boolean autoHomogeneousState = false;
	
	public GameOptions(Player team1Type, Player team2Type) {
		this.team1Type = team1Type;
		this.team2Type = team2Type;
	}

	public Player getTeam1Type() {
		return team1Type;
	}

	public void setTeam1Type(Player team1Type) {
		this.team1Type = team1Type;
	}

	public Player getTeam2Type() {
		return team2Type;
	}

	public void setTeam2Type(Player team2Type) {
		this.team2Type = team2Type;
	}

	public int[] getBoardSize() {
		return boardSize;
	}

	public void setBoardSize(int[] boardSize) {
		this.boardSize = boardSize;
	}

	public int getTeamSize() {
		return teamSize;
	}

	public void setTeamSize(int teamSize) {
		this.teamSize = teamSize;
	}

	public int getMaxTurns() {
		return maxTurns;
	}

	public void setMaxTurns(int maxTurns) {
		this.maxTurns = maxTurns;
	}
	
	public boolean doReplay() {
		return replay;
	}
	
	public int getTurnSpeed() {
		return turnSpeed;
	}
	
	public boolean getActionText() {
		return actionText;
	}

}
