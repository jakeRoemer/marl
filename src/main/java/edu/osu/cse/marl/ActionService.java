package edu.osu.cse.marl;

import java.util.Map;

import model.Action;
import model.Position;
import model.Unit;

public class ActionService {
	private BoardService bs;
	private Board board;

	public void setBoard(Board board) {
		this.board = board;
		this.bs = new BoardService(board);
	}

	public void process(Map<Unit, Action> teamActions) {
		validateActions(teamActions);
		for (Map.Entry<Unit, Action> entry : teamActions.entrySet()) {
			if (isValid(entry.getKey(), entry.getValue())) {
				process(entry.getKey(), entry.getValue());
			}
		}
	}

	private void validateActions(Map<Unit, Action> teamActions) {
		for (Map.Entry<Unit, Action> entry : teamActions.entrySet()) {
			if (!isValid(entry.getKey(), entry.getValue())) {
				throw new IllegalStateException("Illegal action received from " + entry.getKey().getTeam().getTeamName()
						+ ". " + entry.getKey() + " " + entry.getValue() + ". Board state: " + board);
			}
		}
	}

	private void process(Unit unit, Action action) {
		if (unit.isAlive()) {
			action.execute(unit);
		}
	}

	private boolean isValid(Unit unit, Action action) {
		if (!unit.isAlive()) {
			return false;
		}

		Position currentPosition = unit.getPosition();

		if (action.isDoMovementFirst()) {

			Position movementTarget = action.getMovementTarget();
			if (movementTarget != null && !isValidMovement(unit, currentPosition, movementTarget)) {
				return false;
			}

			if (movementTarget != null) {
				currentPosition = movementTarget;
			}

			Position attackTarget = action.getAttackTarget();
			boolean valid = (attackTarget == null || isValidAttack(currentPosition, attackTarget));
			if (!valid) {
				return false;
			}

			return (attackTarget == null || isValidAttack(currentPosition, attackTarget));
		} else {

			Position attackTarget = action.getAttackTarget();
			if (attackTarget != null && !isValidAttack(currentPosition, attackTarget)) {
				return false;
			}

			if (attackTarget != null) {
				currentPosition = attackTarget;
			}

			Position movementTarget = action.getMovementTarget();
			boolean valid = (movementTarget == null || isValidMovement(unit, currentPosition, movementTarget));
			if (!valid) {
				return false;
			}

			return (movementTarget == null || isValidMovement(unit, currentPosition, movementTarget));
		}
	}

	private boolean isValidAttack(Position currentPosition, Position targetPosition) {
		boolean valid = (Math.abs(targetPosition.getX() - currentPosition.getX()) == 1)
				&& (Math.abs(targetPosition.getY() - currentPosition.getY()) == 1) && !bs.isEmpty(targetPosition);
		return valid;
	}

	private boolean isValidMovement(Unit unit, Position currentPosition, Position targetPosition) {
		// boolean b1 = targetPosition.getDistance(currentPosition) <=
		// bs.getMovementSpeed();
		// boolean b2 = bs.isEmpty(targetPosition);
		// if (!b2) {
		// Unit u = bs.getUnitAt(targetPosition);
		// System.out.println("");
		// }
		// boolean b3 = currentPosition.equals(targetPosition);
		// boolean b4 = unit.getPosition().equals(targetPosition);
		// boolean valid = b1 && (b2 || b3 || b4);
		boolean valid = (targetPosition.getDistance(currentPosition) <= bs.getMovementSpeed()
				&& (bs.isEmpty(targetPosition) || currentPosition.equals(targetPosition)
						|| unit.getPosition().equals(targetPosition)));
		return valid;
	}

}
