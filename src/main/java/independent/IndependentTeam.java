package independent;

import java.util.*;

import model.Action;
import model.Team;
import model.Unit;

public class IndependentTeam implements Team {
	private List<Unit> units;
	Map<Unit, Action> previousActions;
	String teamName;

	public IndependentTeam(List<Unit> units, int teamNumber) {
		this.units = units;
		previousActions = new HashMap<>();
		teamName = "Team " + teamNumber;
	}

	@Override
	public Map<Unit, Action> takeTurn() {
		updateQValues(previousActions);

		Map<Unit, Action> newActions = new HashMap<Unit, Action>();
		for (Unit unit : units) {
			if (unit.isAlive()) {
				newActions.put(unit, unit.makeDecision());
			}
		}
		previousActions = newActions;

		return newActions;
	}

	private void updateQValues(Map<Unit, Action> jointAction) {
		for (Unit unit : jointAction.keySet()) {
			unit.updateQValue(jointAction.get(unit));
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
		previousActions = new HashMap<>();
		for (Unit unit : units) {
			unit.setAlive(true);
			unit.killedUnit = null;
		}
		
	}

}
