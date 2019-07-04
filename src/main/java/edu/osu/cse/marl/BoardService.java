package edu.osu.cse.marl;

import java.util.ArrayList;
import java.util.List;

import model.Position;
import model.Surroundings;
import model.Unit;

public class BoardService {
	private static int sightRadius;
	private Board board;

	public void setBoard(Board board) {
		this.board = board;
		sightRadius = board.getMovementSpeed() + 1;
	}

	public BoardService() {

	}

	public BoardService(Board board) {
		this.board = board;
		sightRadius = board.getMovementSpeed() + 1;
	}

	public Surroundings getSurroundings(Position position) {
		List<Position> visiblePositions = getVisiblePositions(position);
		List<Unit> units = getAllVisibleUnits(visiblePositions);
		List<Position> emptyPositions = getEmptyPositions(visiblePositions, units);
		return new Surroundings(position, units, emptyPositions);
	}

	private List<Position> getVisiblePositions(Position position) {
		List<Position> visiblePositions = new ArrayList<Position>();
		int[] rangeOfSight = getRangeOfSight(position);
		for (int i = rangeOfSight[0]; i <= rangeOfSight[1]; i++) {
			for (int j = rangeOfSight[2]; j <= rangeOfSight[3]; j++) {
				visiblePositions.add(new Position(i, j));
			}
		}
		return visiblePositions;
	}

	private int[] getRangeOfSight(Position position) {
		int[] rangeOfSight = new int[4];
		rangeOfSight[0] = Math.max(position.getX() - sightRadius, 0);
		rangeOfSight[1] = Math.min(position.getX() + sightRadius, board.getSize()[0] - 1);
		rangeOfSight[2] = Math.max(position.getY() - sightRadius, 0);
		rangeOfSight[3] = Math.min(position.getY() + sightRadius, board.getSize()[1] - 1);
		return rangeOfSight;
	}

	private List<Unit> getAllVisibleUnits(List<Position> visiblePositions) {
		List<Unit> visibleUnits = new ArrayList<Unit>();

		for (Unit unit : getAllUnits()) {
			if (visiblePositions.contains(unit.getPosition())) {
				visibleUnits.add(unit);
			}
		}
		return visibleUnits;
	}

	private List<Unit> getAllUnits() {
		List<Unit> allUnits = new ArrayList<Unit>();
		for (Unit u : board.getTeam1Units()) {
			if (u.isAlive()) {
				allUnits.add(u);
			}
		}
		for (Unit u : board.getTeam2Units()) {
			if (u.isAlive()) {
				allUnits.add(u);
			}
		}
		return allUnits;
	}

	private List<Position> getEmptyPositions(List<Position> visiblePositions, List<Unit> units) {
		for (Unit unit : units) {
			visiblePositions.remove(unit.getPosition());
		}
		return visiblePositions;
	}

	public boolean isEmpty(Position p) {
		if (!isValid(p)) {
			throw new IllegalArgumentException(
					"Position " + p + " is not valid for board dimensions " + board.getSize());
		}
		List<Unit> allUnits = getAllUnits();
		for (Unit unit : allUnits) {
			if (p.equals(unit.getPosition())) {
				return false;
			}
		}
		return true;
	}

	public boolean isValid(Position p) {
		return board.isOnBoard(p);
	}

	public Unit getUnitAt(Position p) {
		if (!isValid(p)) {
			throw new IllegalArgumentException(
					"Position " + p + " is not valid for board dimensions " + board.getSize());
		}
		List<Unit> allUnits = getAllUnits();
		for (Unit unit : allUnits) {
			if (p.equals(unit.getPosition())) {
				return unit;
			}
		}
		return null;
	}

	public int getMovementSpeed() {
		return board.getMovementSpeed();
	}

}
