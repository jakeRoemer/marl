package model;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Action {
	private Position attackTarget;
	private Position movementTarget;
	private boolean doMovementFirst = true;

	// might be a better way to hold old state data since all actions of a
	// single state will have the same state
	public Action(Position movementTarget, Position attackTarget, boolean doMovementFirst) {
		this.movementTarget = movementTarget;
		this.attackTarget = attackTarget;
		this.doMovementFirst = doMovementFirst;
	}

	public void execute(Unit unit) {
		Unit killedUnit = null;
		if (doMovementFirst && movementTarget != null) {
			boolean successful = unit.move(movementTarget);
			if (successful && attackTarget != null) {
				killedUnit = unit.attack(attackTarget);
			}
		} else if (attackTarget != null) { // this should always be true,
											// because doMovementFirst should
											// not be set to false unless a unit
											// plans on attacking
			killedUnit = unit.attack(attackTarget);
			if (killedUnit != null && movementTarget != null) {
				unit.move(movementTarget);
			}
		}
	}

	public Position getMovementTarget() {
		return movementTarget;
	}

	public Position getAttackTarget() {
		return attackTarget;
	}

	public boolean isDoMovementFirst() {
		return doMovementFirst;
	}

	@Override
	public String toString() {
		return "Attack: " + attackTarget + ". Movement: " + movementTarget + ". Do movement first: " + doMovementFirst;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Action)) {
			return false;
		}

		Action action = (Action) obj;

		if (attackTarget == null) {
			if (action.getAttackTarget() != null) {
				return false;
			}
		} else if (!attackTarget.equals(action.getAttackTarget())) {
			return false;
		}

		if (movementTarget == null) {
			if (action.getMovementTarget() != null) {
				return false;
			}
		} else if (!movementTarget.equals(action.getMovementTarget())) {
			return false;
		}

		if (doMovementFirst != action.isDoMovementFirst()) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(29, 7).append(attackTarget).append(movementTarget).append(doMovementFirst)
				.toHashCode();
	}

}
