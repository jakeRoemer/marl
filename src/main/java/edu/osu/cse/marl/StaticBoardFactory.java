package edu.osu.cse.marl;

import java.util.List;

import model.Position;
import model.Unit;

public class StaticBoardFactory implements BoardFactory {

	/**
	 * Expects boardSize of 5x8 Team sizes are 3 each
	 */
	@Override
	public Board createBoard(int[] boardSize, List<Unit> team1, List<Unit> team2) {
		Board board = new Board(boardSize, team1, team2);
		positionTeam1(team1, board);
		positionTeam2(team2, board);
		return board;
	}

	private void positionTeam1(List<Unit> team, Board board) {
		Position p1 = new Position(0, 1);
		Position p2 = new Position(2, 1);
		Position p3 = new Position(4, 1);

		team.get(0).setPosition(p1);
		team.get(1).setPosition(p2);
		team.get(2).setPosition(p3);
	}

	private void positionTeam2(List<Unit> team, Board board) {
		Position p1 = new Position(0, 6);
		Position p2 = new Position(2, 6);
		Position p3 = new Position(4, 6);

		team.get(0).setPosition(p1);
		team.get(1).setPosition(p2);
		team.get(2).setPosition(p3);
	}

}
