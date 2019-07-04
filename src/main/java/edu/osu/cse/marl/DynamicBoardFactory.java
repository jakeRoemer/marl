package edu.osu.cse.marl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.Position;
import model.Unit;

public class DynamicBoardFactory implements BoardFactory {
	// TODO Optionally add obstacles.

	public Board createBoard(int[] boardSize, List<Unit> team1, List<Unit> team2) {
		Board board = new Board(boardSize, team1, team2);
		positionTeam1(team1, board);
		positionTeam2(team2, board);
		return board;
	}

	private void positionTeam1(List<Unit> team, Board board) {
		int[] boardSize = board.getSize();
		Position team1Center = new Position(boardSize[0] / 2, 0);
		int[] team1Bounds = { 0, boardSize[0], 0, boardSize[1] / 2 };
		positionTeam(team1Center, team, team1Bounds);
	}

	private void positionTeam2(List<Unit> team, Board board) {
		int[] boardSize = board.getSize();
		Position team2Center = new Position(boardSize[0] / 2, boardSize[1] - 1);
		int[] team2Bounds = { 0, boardSize[0], (int) Math.ceil((double) boardSize[1] / 2), boardSize[1] };
		positionTeam(team2Center, team, team2Bounds);
	}

	private void positionTeam(Position center, List<Unit> team, int[] bounds) {
		List<Position> occupiedPositions = new ArrayList<>();
		for (Unit unit : team) {
			Position position = null;
			
			while (!isValidPosition(position, bounds, occupiedPositions)) {
				position = createNewPosition(center, bounds);
			}
			
			occupiedPositions.add(position);
			unit.setPosition(position);
		}
	}

	private Position createNewPosition(Position center, int[] bounds) {
		Random r = new Random();

		double xStdDev = ((double) bounds[1] - bounds[0]) / 4;
		int xMean = center.getX();
		int x = (int) (r.nextGaussian() * xStdDev) + xMean;

		double yStdDev = ((double) bounds[3] - bounds[2]) / 4;
		int yMean = center.getY();
		int y = (int) (r.nextGaussian() * yStdDev) + yMean;
		return new Position(x, y);
	}

	private boolean isValidPosition(Position position, int[] bounds, List<Position> positions) {
		if (position == null) {
			return false;
		}
		if (positions.contains(position)) {
			return false;
		}
		return (position.getX() >= bounds[0] && position.getX() < bounds[1] && position.getY() >= bounds[2]
				&& position.getY() < bounds[3]);
	}

}
