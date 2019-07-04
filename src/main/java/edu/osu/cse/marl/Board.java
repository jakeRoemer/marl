package edu.osu.cse.marl;

import java.util.List;

import model.Position;
import model.Unit;

public class Board {
	private static int movementSpeed;
	
	int[] boardSize;
	private List<Unit> team1Units;
	private List<Unit> team2Units;

	public Board(int[] boardSize, List<Unit> team1, List<Unit> team2) {
		this.boardSize = boardSize;
		movementSpeed = (int) Math.ceil((double) (boardSize[0] + boardSize[1]) / 10);
		this.team1Units = team1;
		this.team2Units = team2;
	}

	public List<Unit> getTeam1Units() {
		return team1Units;
	}

	public List<Unit> getTeam2Units() {
		return team2Units;
	}

	public void setTeam1Units(List<Unit> team1Units) {
		this.team1Units = team1Units;
	}

	public void setTeam2Units(List<Unit> team2Units) {
		this.team2Units = team2Units;
	}

	public int[] getSize() {
		return boardSize;
	}

	public int getMovementSpeed() {
		return movementSpeed;
	}
	
	public String toString() {
		return "Team 1: " + team1Units + "; Team 2: " + team2Units;
	}
	
	public boolean isOnBoard(Position p) {
		return (p.getX() >= 0 && p.getX() < boardSize[0] && p.getY() >= 0
				&& p.getY() < boardSize[1]);
	}

}
