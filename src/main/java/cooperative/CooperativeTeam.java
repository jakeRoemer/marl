package cooperative;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ai.common.QValue;
import ai.common.State;
import model.Action;
import model.Team;
import model.Unit;

public class CooperativeTeam implements Team {
	private List<Unit> units;
	private Map<Unit, Action> turnActions = null;
	private String teamName;

	public CooperativeTeam(List<Unit> units, int teamNumber) {
		this.units = units;
		teamName = "Team " + teamNumber;
	}

	/**
	 * Each unit in the team will pick an action for the execution of the team's
	 * joint action
	 */
	@Override
	public Map<Unit, Action> takeTurn() {
		if (turnActions == null) {
			turnActions = new HashMap<Unit, Action>();
		} else {
			updateQValues(turnActions);
		}
		Map<Unit, Action> actions = new HashMap<Unit, Action>();
		for (Unit unit : units) {
			if (unit.isAlive()) {
				actions.put(unit, unit.makeDecision());
			}
		}
		turnActions = actions;
		return actions;
	}
	
	/** Averages QValues */
	public double combineQValues(double currentQValue, double sharedQValue) {
		return (currentQValue + sharedQValue) / 2;
	}

	/** Updating Q Values will help determine future actions to take. */
	private void updateQValues(Map<Unit, Action> jointAction) {
		for (Unit unit : jointAction.keySet()) { //each unit in the jointAction set should be the last updateQValue for a dead unit then dead units will not be considered in the next jointAction 
			unit.updateQValue(jointAction.get(unit), 3); //update Q value and then do sharing
			//share experiences by passing QValues from ally units
			List<Unit> sharingUnits = new LinkedList<Unit>();
			Random rand = new Random(System.currentTimeMillis());
			for (int i = 0; i < unit.getTeam().getLivingUnits().size() % 3; i++) { //only shared with a subset of allies no greater than 2
				Unit sharingUnit = unit.getTeam().getLivingUnits().get(rand.nextInt(unit.getTeam().getLivingUnits().size()));
				while (sharingUnits.contains(sharingUnit)) {
					sharingUnit = unit.getTeam().getLivingUnits().get(rand.nextInt(unit.getTeam().getLivingUnits().size()));
				}
				sharingUnits.add(sharingUnit);
			}
			State state = unit.calculateState();
			Map<Action, QValue> unitActions = unit.getActions(state);
			for (Unit sharingUnit : sharingUnits) {
				if (sharingUnit.qValues.containsKey(state)) {
					for (Action action : unitActions.keySet()) {
						double qValue = sharingUnit.getActions(state).get(action).getValue();
						if (unitActions.get(action).getValue() == -2) {
							unitActions.get(action).setValue(qValue);
						} else {
							unitActions.get(action).setValue(combineQValues(unitActions.get(action).getValue(), qValue));
						}
					}
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
		return teamName;
	}

	@Override
	public void reset() {
		turnActions = null;
		for (Unit unit : units) {
			unit.setAlive(true);
			unit.killedUnit = null;
		}
		
	}
}
