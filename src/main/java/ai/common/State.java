package ai.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import model.Action;
import model.Position;
import model.Surroundings;
import model.Team;
import model.Unit;

/**
 * State is the relative positioning equivalent of Surroundings
 *
 */

public class State {
	private static final Position CURRENT_RELATIVE_POSITION = new Position(0, 0);

	// All of these are saved as relative positions
	private Map<Unit, Position> visibleAllyPositions;
	private Map<Unit, Position> visibleEnemyPositions;
	private List<Position> emptyPositions;

	public State(Surroundings surroundings, Team team) {
		emptyPositions = new ArrayList<Position>();
		visibleAllyPositions = new HashMap<Unit, Position>();
		visibleEnemyPositions = new HashMap<Unit, Position>();

		Position currentPosition = surroundings.getCurrentPosition();
		for (Position p : surroundings.getEmptyPositions()) {
			emptyPositions.add(Position.getRelativePosition(currentPosition, p));
		}
		for (Unit u : surroundings.getVisibleUnits()) {
			if (team.equals(u.getTeam())) {
				visibleAllyPositions.put(u, Position.getRelativePosition(currentPosition, u.getPosition()));
			} else {
				visibleEnemyPositions.put(u, Position.getRelativePosition(currentPosition, u.getPosition()));
			}
		}
	}

	public static Position getCurrentRelativePosition() {
		return CURRENT_RELATIVE_POSITION;
	}

	public Map<Unit, Position> getVisibleUnitPositions() {
		Map<Unit, Position> allUnits = new HashMap<Unit, Position>();
		allUnits.putAll(visibleEnemyPositions);
		allUnits.putAll(visibleAllyPositions);
		return allUnits;
	}

	public Map<Unit, Position> getVisibleAllyPositions() {
		return visibleAllyPositions;
	}

	public Map<Unit, Position> getVisibleEnemyPositions() {
		return visibleEnemyPositions;
	}

	public List<Position> getEmptyPositions() {
		return emptyPositions;
	}

	public List<Position> getMovementPositions(int range) {
		List<Position> movementPositions = new ArrayList<Position>();
		for (Position p : emptyPositions) {
			if (CURRENT_RELATIVE_POSITION.inRange(p, range)) {
				movementPositions.add(p);
			}
		}
		return movementPositions;
	}

	/**
	 * Of the possible movements, generate a subset that are reachable from the
	 * position ofDeadUnit
	 * 
	 * @param ofDeadUnit
	 *            Relative position of dead unit to current position (0,0)
	 * @param movementSpeed
	 * @return Relative positions of possible movement locations to current
	 *         position (0,0)
	 */
	public List<Position> getMovementPositionsAfterAttack(Position ofDeadUnit, int movementSpeed) {
		List<Position> postAttackMovements = new ArrayList<Position>();
		for (Position p : emptyPositions) {
			if (ofDeadUnit.inRange(p, movementSpeed)) {
				postAttackMovements.add(p);
			}
		}

		if (ofDeadUnit.inRange(CURRENT_RELATIVE_POSITION, movementSpeed)) {
			postAttackMovements.add(CURRENT_RELATIVE_POSITION);
		}
		return postAttackMovements;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof State) {
			State state = (State) obj;
			return visibleAllyPositions.size() == state.getVisibleAllyPositions().size()
					&& visibleAllyPositions.values().containsAll(state.getVisibleAllyPositions().values())
					&& visibleEnemyPositions.size() == state.getVisibleEnemyPositions().size()
					&& visibleEnemyPositions.values().containsAll(state.getVisibleEnemyPositions().values())
					&& emptyPositions.size() == state.getEmptyPositions().size()
					&& emptyPositions.containsAll(state.getEmptyPositions());
		}
		return false;
	}
	
	public State mostEquivalentState(Map<State, Map<Action, QValue>> stateMap) {
		List<State> equivalentStates = new ArrayList<State>();
		int threshold = -1; //can add a bunch to the list that surpass a certain threshold
		//determines the most equivalent states and if multiple hold the equivalence, pick one randomly
		for (State pastStates : stateMap.keySet()) {
			int similarity = relativeEquivalence(pastStates);
			if (similarity > threshold) {
				if (equivalentStates.size() > 1) {
					equivalentStates.clear();
				}
				equivalentStates.add(pastStates);
			} else if (similarity == threshold) {
				equivalentStates.add(pastStates);
			}
		}
		if (equivalentStates.size() > 1) {
			return equivalentStates.get((new Random(System.currentTimeMillis())).nextInt(equivalentStates.size()));
		} else if (equivalentStates.size() == 1) {
			return equivalentStates.get(0);
		}
		return null; //no equivalent states
	}
	
	//+1 for every position that is the same
	public int relativeEquivalence(State testState){
		int similarity = 0;
		for (Unit visibleAllyUnit : visibleAllyPositions.keySet()) {
			//if ally unit is the same and in the same position (conditions can be relaxed)
			if (testState.visibleAllyPositions.containsKey(visibleAllyUnit) && 
					testState.visibleAllyPositions.get(visibleAllyUnit).equals(visibleAllyPositions.get(visibleAllyUnit))) {
				similarity++;
			}
		}
		for (Unit visibleEnemyUnit : visibleEnemyPositions.keySet()) {
			//if enemy unit is the same and in the same position (conditions can be relaxed)
			if (testState.visibleEnemyPositions.containsKey(visibleEnemyUnit) &&
					testState.visibleEnemyPositions.get(visibleEnemyUnit).equals(visibleEnemyPositions.get(visibleEnemyUnit))) {
				similarity++;		
			}
		}
		for (Position emptyPos : emptyPositions) {
			if (testState.emptyPositions.contains(emptyPos)) {
				similarity++;
			}
		}
		return similarity;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(33, 47).append(visibleAllyPositions).append(visibleEnemyPositions)
				.append(emptyPositions).toHashCode();
	}
}
