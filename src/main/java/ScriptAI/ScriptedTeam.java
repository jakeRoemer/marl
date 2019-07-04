package ScriptAI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Action;
import model.Position;
import model.Team;
import model.Unit;

public class ScriptedTeam implements Team {
	private List<Unit> units;
	private Map<Unit, Action> turnActions = null;
	private String teamName;
	private List<List<Action>> cycleScript;
	private Action stillPosition = new Action(null, null, true);
	private int[] GMC;
	
	public ScriptedTeam(List<Unit> units, int teamNumber) {
		this.units = units;
		teamName = "Team " + teamNumber;
		GMC = new int[3];
		initiate();
	}
	
	private void initiate() {
		cycleScript = new ArrayList<List<Action>>();
		//add scripted movement cycle of size 4
		cycleScript.add(new ArrayList<Action>());
		cycleScript.get(0).add(new Action(new Position(1, 2), null, true));
		cycleScript.get(0).add(new Action(new Position(1, 4), null, true));
		cycleScript.get(0).add(new Action(new Position(0, 3), null, true));
		cycleScript.get(0).add(new Action(new Position(0, 1), null, true));
		//add scripted movement cycle of size 5
		cycleScript.add(new ArrayList<Action>());
		cycleScript.get(1).add(new Action(new Position(2, 2), null, true));
		cycleScript.get(1).add(new Action(new Position(3, 2), null, true));
		cycleScript.get(1).add(new Action(new Position(3, 4), null, true));
		cycleScript.get(1).add(new Action(new Position(2, 3), null, true));
		cycleScript.get(1).add(new Action(new Position(2, 1), null, true));
		//add scripted movement cycle of size 6
		cycleScript.add(new ArrayList<Action>());
		cycleScript.get(2).add(new Action(new Position(5, 1), null, true));
		cycleScript.get(2).add(new Action(new Position(4, 2), null, true));
		cycleScript.get(2).add(new Action(new Position(5, 2), null, true));
		cycleScript.get(2).add(new Action(new Position(5, 3), null, true));
		cycleScript.get(2).add(new Action(new Position(4, 2), null, true));
		cycleScript.get(2).add(new Action(new Position(4, 1), null, true));
	}
	
	//Create scripted movements. Give the movements to the units as actions
	//The script will be for 3 units now. Can make movements random for n units.
	@Override
	public Map<Unit, Action> takeTurn() {
		Map<Unit, Action> actions = new HashMap<Unit, Action>();
		for (int i = 0; i < units.size(); i++) {
			if (units.get(i).isAlive()) {
				if (noCollision(units.get(i), cycleScript.get(i).get(GMC[i]%(i+4)).getMovementTarget())) {
					actions.put(units.get(i), cycleScript.get(i).get(GMC[i]%(i+4)));
					GMC[i]++;
				} else {
					actions.put(units.get(i), stillPosition);
				}
			}
		}
		turnActions = actions;
		return actions;
	}
	
	//checks for a collision with an enemy unit, but must be relative
	private boolean noCollision(Unit unit, Position wantToMove) {
		return unit.calculateState().getEmptyPositions().contains(Position.getRelativePosition(unit.getPosition(), wantToMove));
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
		for (int i = 0; i < GMC.length; i++) {
			GMC[i] = 0;
		}
	}

}
