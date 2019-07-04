package oracle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ai.common.QValue;
import ai.common.State;
import model.Action;
import model.Position;
import model.Team;
import model.Unit;

public class OracleTeam implements Team {
	private List<Unit> units;
	private Map<Unit, Action> turnActions = null;
	private String teamName;

	public OracleTeam(List<Unit> units, int teamNumber) {
		this.units = units;
		turnActions = new HashMap<>();
		teamName = "Team " + teamNumber;
	}

	@Override
	public Map<Unit, Action> takeTurn() {
		updateQValues(turnActions);

		Map<Unit, Action> actions = new HashMap<Unit, Action>();

		for (Unit unit : units) {
			if (unit.isAlive()) {
				actions.put(unit, unit.makeDecision());
//				List<Unit> remainingUnits = units.subList(units.indexOf(unit) + 1, units.size());
//
//				Action action = calculateActionBasedOnAllies(unit, remainingUnits);
//				actions.put(unit, action);
			}

		}
		turnActions = actions;
		return actions;
	}

	// TODO this logic still might have issues
	private Action calculateActionBasedOnAllies(Unit unit, List<Unit> remainingUnits) {
		Action action = unit.makeDecision();
		List<Action> forbiddenActions = new ArrayList<Action>();

		for (Unit otherUnit : remainingUnits) {
			if (otherUnit.isAlive()) {
				Action otherAction = otherUnit.makeDecision();

				if (areActionsConflicting(action, otherAction)
						&& unit.getLastChosenQValue().compareTo(otherUnit.getLastChosenQValue()) < 0) {
					forbiddenActions.add(action);
				}
			}
		}

		if (!forbiddenActions.isEmpty()) {
			action = unit.makeDecision(forbiddenActions);
		}
		return action;
	}

	private boolean areActionsConflicting(Action action1, Action action2) {
		return areConflicting(action1.getMovementTarget(), action2.getMovementTarget())
				|| areConflicting(action1.getAttackTarget(), action2.getAttackTarget());
	}

	// Can't chose to move to currently occupied square, so only time movements
	// can conflict is
	// when they both try to move to same place
	// Similar for attacks
	private boolean areConflicting(Position target1, Position target2) {
		return (target1 != null && target2 != null && target1.equals(target2));
	}

	private void updateQValues(Map<Unit, Action> jointAction) {
		for (Unit unit : jointAction.keySet()) {
			unit.updateQValue(jointAction.get(unit));
		}
		shareNewQValues(jointAction.keySet());

	}

	private void shareNewQValues(Set<Unit> units) {
		for (Unit sharingUnit : units) {
			State state = sharingUnit.getLastState();
			Action action = sharingUnit.getLastRelativeAction();
			QValue value = sharingUnit.getLastChosenQValue();
			for (Unit learningUnit : units) {
				if (sharingUnit != learningUnit) {
					learningUnit.learn(state, action, value);
				}
			}
		}
	}

	@Override
	public List<Unit> getLivingUnits() {
		List<Unit> livingUnits = new ArrayList<Unit>();
		for (Unit unit : units) {
			if (unit.isAlive()) {
				livingUnits.add(unit);
			}
		}
		return livingUnits;
	}

	@Override
	public String getTeamName() {
		return this.teamName;
	}

	@Override
	public void reset() {
		turnActions = new HashMap<>();
		for (Unit unit : units) {
			unit.setAlive(true);
			unit.killedUnit = null;
		}

	}

}
