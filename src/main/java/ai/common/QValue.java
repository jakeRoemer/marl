package ai.common;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import model.Action;
import model.Unit;

public class QValue implements Comparable<QValue> {

	private static final double ALPHA = 0.5; // a value between [0, 1)
	private static final double SCALING_FACTOR = 0.5; // a value between [0, 1)
	
	private State staticState;
	private double value = -2;
	
	public QValue(State staticState) {
		this.staticState = staticState;
	}

	// heuristics: 1 - oracle, 2 - independent, 3 - cooperative [1 and 2 use the
	// same value function for now]
	public void updateQValue(Unit unit, int heuristic) {
		State newState = unit.calculateState();
		double reward;
		if (heuristic == 3) {
			reward = Reward.getCoopValue(unit, staticState, newState);
			value = value + ALPHA * (reward + SCALING_FACTOR * coopValueFunction(unit) - value);
		} else {
			reward = Reward.getValue(unit, staticState, newState);
			value = value + ALPHA * (reward + SCALING_FACTOR * valueFunction(unit) - value);
		}
	}

	/** returns the maximum Q value based on the staticState for the action */
	private double valueFunction(Unit unit) {
		double maxQVal = Double.MIN_VALUE;
		Map<Action, QValue> actions = unit.getActions(unit.calculateState());
		for (Action action : actions.keySet()) {
			if (actions.get(action).getValue() > maxQVal) {
				maxQVal = actions.get(action).getValue();
			}
		}
		return maxQVal;
	}

	/** sharing experience to influence QValue update */
	private double coopValueFunction(Unit unit) {
		double maxQVal = Double.MIN_VALUE;
		List<Unit> sharingUnits = new LinkedList<Unit>();
		Random rand = new Random(System.currentTimeMillis());
		for (int i = 0; i < unit.getTeam().getLivingUnits().size() % 3; i++) { //only shared with a subset of allies no greater than 2
			Unit sharingUnit = unit.getTeam().getLivingUnits().get(rand.nextInt(unit.getTeam().getLivingUnits().size()));
			while (sharingUnits.contains(sharingUnit)) {
				sharingUnit = unit.getTeam().getLivingUnits().get(rand.nextInt(unit.getTeam().getLivingUnits().size()));
			}
			sharingUnits.add(sharingUnit);
		}
		
		Map<Action, QValue> actions = unit.getActions(unit.calculateState());
		for (Action action : actions.keySet()) {
			double actionQValue = actions.get(action).getValue();
			for (Unit sharingUnit : sharingUnits) {
				if (sharingUnit.qValues.containsKey(unit.calculateState())) {
					actionQValue = combineQValue(actionQValue, sharingUnit.getActions(unit.calculateState()).get(action).getValue());
				}
			}
			if (actionQValue > maxQVal) {
				maxQVal = actions.get(action).getValue();
			}
		}
		return maxQVal;
	}
	
	/** Helper function for coopValueFunction to average q values together */
	public double combineQValue(double actionQValue, double sharedQValue) {
		actionQValue = (actionQValue + sharedQValue) / 2;
		return actionQValue;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public int compareTo(QValue o) {
		return Double.compare(value, o.getValue());
	}
}
