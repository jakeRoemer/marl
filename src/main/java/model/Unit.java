package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ai.common.QValue;
import ai.common.State;
import edu.osu.cse.marl.BoardService;
import edu.osu.cse.marl.GameOptions;

public class Unit {
	private static final int DEFAULT_HEURISTIC = 1;

	private Position position;
	private BoardService boardService;
	private Team team;
	public Map<State, Map<Action, QValue>> qValues;
	private boolean isAlive;
	public Unit killedUnit;
	private State lastState;
	private Action lastRelativeAction;
	private QValue lastChosenQValue;
	private String unitName;

	public QValue getLastChosenQValue() {
		return lastChosenQValue;
	}

	public Unit(Team team, BoardService boardService, int unitIndex) {
		unitName = "U" + unitIndex;
		this.team = team;
		this.boardService = boardService;
		isAlive = true;
		qValues = new HashMap<State, Map<Action, QValue>>();
	}

	// For testing only
	public Unit(Position p) {
		this.position = p;
		isAlive = true;
	}

	/**
	 * returns the list of actions associated with the state. If the state has
	 * never been seen by the unit then the full list of actions are generated
	 * and the state is mapped to the list of actions.
	 */
	public Map<Action, QValue> getActions(State state) {
		if (!qValues.containsKey(state)) {
			Map<Action, QValue> newActions = generateActions(state);
			qValues.put(state, newActions);
			return newActions;
		} else {
			return qValues.get(state);
		}
	}

	public void learn(State state, Action action, QValue value) {
		if (!qValues.containsKey(state)) {
			Map<Action, QValue> newActions = generateActions(state);
			qValues.put(state, newActions);
		}
		if (-2 == qValues.get(state).get(action).getValue()) {
			qValues.get(state).get(action).setValue(value.getValue());
		}

	}
	
	/** Creates the full list of possible RELATIVE actions from the state */
	private Map<Action, QValue> generateActions(State state) {
		Map<Action, QValue> actions = new HashMap<Action, QValue>();

		// stay in place and don't attack
		actions.put(new Action(null, null, true), new QValue(state));

		// move first then attack or just move
		for (Position p : state.getMovementPositions(boardService.getMovementSpeed())) {
			// only move
			actions.put(new Action(p, null, true), new QValue(state));

			Map<Unit, Position> visibleUnitPositions = state.getVisibleUnitPositions();
			for (Unit unit : visibleUnitPositions.keySet()) {
				if (p.isDiagonal(visibleUnitPositions.get(unit))) {
					// move and attack if possible
					actions.put(new Action(p, visibleUnitPositions.get(unit), true), new QValue(state));
				}
			}
		}

		// attack first then move
		Map<Unit, Position> visibleUnitPositions = state.getVisibleUnitPositions();
		for (Unit unit : visibleUnitPositions.keySet()) {
			Position relativeUnitPosition = visibleUnitPositions.get(unit);
			if (State.getCurrentRelativePosition().isDiagonal(relativeUnitPosition)) {
				// attack and don't move
				actions.put(new Action(null, relativeUnitPosition, false), new QValue(state));

				for (Position p : state.getMovementPositionsAfterAttack(relativeUnitPosition,
						boardService.getMovementSpeed())) {
					// attack and move if possible
					actions.put(new Action(p, relativeUnitPosition, false), new QValue(state));
				}
			}
		}
		return actions;
	}

	public void updateQValue(Action action, int heuristic) {
		lastChosenQValue.updateQValue(this, heuristic);
	}

	public void updateQValue(Action action) {
		lastChosenQValue.updateQValue(this, DEFAULT_HEURISTIC);
	}

	/**
	 * An action from the possible list of actions of the state is chosen based
	 * on max Q value if the current phase is not an exploration phase. If it is
	 * the exploration phase then an action that has not been taken will be
	 * chosen. Ties are settled by picking randomly.
	 */
	public Action makeDecision() {
		if (!isAlive) {
			throw new IllegalStateException("Dead unit is planning decisions");
		}
		State state = calculateState();
		lastState = state;
		Map<Action, QValue> possibleActions = getActions(state);
		return chooseAction(possibleActions);
	}

	/**
	 * 
	 * @param forbiddenActions
	 *            List of ABSOLUTE actions
	 * @return Absolute decision
	 */
	public Action makeDecision(List<Action> forbiddenActions) {
		if (!isAlive) {
			throw new IllegalStateException("Dead unit is planning decisions");
		}
		State state = calculateState();
		lastState = state;
		List<Action> relativeForbiddenActions = new ArrayList<Action>();
		for (Action action : forbiddenActions) {
			relativeForbiddenActions.add(getRelativeAction(action));
		}
		Map<Action, QValue> allowedActions = filterForbiddenActions(forbiddenActions, getActions(state));
		return chooseAction(allowedActions);

	}

	private Action chooseAction(Map<Action, QValue> actions) {
		Action decision = null;
		if (shouldExplore(actions)) {
			//Unprofessional, but I wanted all the options to be in one place
			//So autoHomogeneousState determines if a unit uses similar states to determine action from GameOptions
			if (GameOptions.autoHomogeneousState) { //since we are exploring, lets pick based on different states that have explored the action
				decision = findBestSimilarAction(actions);
				if (decision == null) { //if we couldn't find a possible similar action
					decision = findFirstUnexploredAction(actions);
				}
			} else {
				decision = findFirstUnexploredAction(actions);
			}
		} else {
			decision = findActionWithMaximalQValue(actions);
		}
		lastChosenQValue = actions.get(decision);
		lastRelativeAction = decision;
		return getAbsoluteAction(decision);
	}
	
	private Action findBestSimilarAction(Map<Action, QValue> actions) {
		State homogeneousState = lastState.mostEquivalentState(qValues);
		if (homogeneousState == null) {
			return findFirstUnexploredAction(actions);
		}
		return findSimilarActionWithMaximalQValue(qValues.get(homogeneousState));
	}

	private Action getAbsoluteAction(Action relativeAction) {
		Position movementPosition = null;
		if (relativeAction.getMovementTarget() != null) {
			movementPosition = new Position(position, relativeAction.getMovementTarget());
		}
		Position attackPosition = null;
		if (relativeAction.getAttackTarget() != null) {
			attackPosition = new Position(position, relativeAction.getAttackTarget());
		}
		return new Action(movementPosition, attackPosition, relativeAction.isDoMovementFirst());
	}

	private Action getRelativeAction(Action absoluteAction) {
		Position movementPosition = null;
		if (absoluteAction.getMovementTarget() != null) {
			movementPosition = Position.getRelativePosition(position, absoluteAction.getMovementTarget());
		}
		Position attackPosition = null;
		if (absoluteAction.getAttackTarget() != null) {
			attackPosition = Position.getRelativePosition(position, absoluteAction.getAttackTarget());
		}
		return new Action(movementPosition, attackPosition, absoluteAction.isDoMovementFirst());
	}
	
	private Action findSimilarActionWithMaximalQValue(Map<Action, QValue> actions) {
		Action decision = null;
		for (Action action : actions.keySet()) {
			QValue qValue = actions.get(action);
			if (decision == null || qValue.compareTo(actions.get(decision)) > 0) {
				if (possibleAction(action)) {
					decision = action;
				}
			} else if (qValue.compareTo(actions.get(decision)) == 0) {
				// If two actions have same qValue, pick one randomly
				if (new Random(System.currentTimeMillis()).nextBoolean()) {
					if (possibleAction(action)) {
						decision = action;
					}
				}
			}
		}
		return decision;
	}
	
	private boolean possibleAction(Action action) {
		return	noCollision(action.getMovementTarget()) &&
				enemyExists(action.getAttackTarget());
	}
	
	private boolean noCollision(Position wantToMove) {
		return lastState.getEmptyPositions().contains(wantToMove);
	}
	
	private boolean enemyExists(Position enemyPos) {
		if (enemyPos == null) {
			return true;
		}
		if (lastState.getVisibleEnemyPositions().isEmpty()) {
			return false;
		}
		return lastState.getVisibleEnemyPositions().containsValue(enemyPos);
	}

	private Action findActionWithMaximalQValue(Map<Action, QValue> actions) {
		Action decision = null;
		for (Action action : actions.keySet()) {
			QValue qValue = actions.get(action);
			if (decision == null || qValue.compareTo(actions.get(decision)) > 0) {
				decision = action;
			} else if (qValue.compareTo(actions.get(decision)) == 0) {
				// if two actions have same qValue, pick one randomly
				if (new Random(System.currentTimeMillis()).nextBoolean()) {
					decision = action;
				}
			}
		}
		return decision;
	}

	private Action findFirstUnexploredAction(Map<Action, QValue> actions) {
		for (Action action : actions.keySet()) {
			QValue qValue = actions.get(action);
			if (qValue.getValue() == -2) {
				return action;
			}
		}
		return null;
	}

	private Map<Action, QValue> filterForbiddenActions(List<Action> forbiddenActions,
			Map<Action, QValue> possibleActions) {
		Map<Action, QValue> allowedActions = new HashMap<Action, QValue>();

		for (Action action : possibleActions.keySet()) {
			if (!isForbidden(action, forbiddenActions)) {
				allowedActions.put(action, possibleActions.get(action));
			}
		}

		return allowedActions;
	}

	private boolean isForbidden(Action action, List<Action> forbiddenActions) {
		for (Action forbiddenAction : forbiddenActions) {
			if (action.getMovementTarget() != null && forbiddenAction.getMovementTarget() != null
					&& action.getMovementTarget().equals(forbiddenAction.getMovementTarget())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if a unit should attempt to explore new actions or not. This
	 * takes care of the case in which all actions have been explored.
	 */
	private boolean shouldExplore(Map<Action, QValue> actions) {
		int actionNotTaken = 0;
		for (Action action : actions.keySet()) {
			if (actions.get(action).getValue() == -2) {
				actionNotTaken++;
			}
		}

		if (actionNotTaken == 0) {
			return false;
		}
		return new Random(System.currentTimeMillis()).nextInt(actionNotTaken) > actionNotTaken / 2 ? true : false;
	}

	public State calculateState() {
		return new State(boardService.getSurroundings(position), team);
	}

	public boolean move(Position p) {
		if (!isAlive) {
			throw new IllegalStateException("Dead unit is moving");
		}
		if (boardService.isEmpty(p) || this.equals(boardService.getUnitAt(p))) {
			position = p;
			return true;
		}
		return false;
	}

	public Unit attack(Position p) {
		if (!isAlive) {
			throw new IllegalStateException("Dead unit is attacking");
		}
		Unit killedUnit = boardService.getUnitAt(p);
		if (killedUnit != null) {
			position = p;
			killedUnit.setAlive(false);
			this.killedUnit = killedUnit;
		}
		return killedUnit;
	}

	public Team getTeam() {
		return team;
	}

	public State getLastState() {
		return lastState;
	}

	public Action getLastRelativeAction() {
		return lastRelativeAction;
	}
	
	//For hard copying units in display
	public void setLastRelativeAction(Action lastRelativeAction) {
		this.lastRelativeAction = lastRelativeAction;
	}

	public Position getPosition() {
		return position;
	}
	
	public BoardService getBoardService() {
		return boardService;
	}

	// Does no position validity checking and should only be used for initial
	// board setup
	public void setPosition(Position position) {
		this.position = position;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean alive) {
		isAlive = alive;
	}

	@Override
	public String toString() {
		return "Position: " + position + ". Alive: " + isAlive;
	}
	
	public String getUnitName() {
		return unitName;
	}
	
	public String getDisplayName() {
		return "T" + team.getTeamName().charAt(team.getTeamName().length()-1) + ": " + getUnitName();
	}
	
	public String getDisplayAction() {
		if (getLastRelativeAction() == null) {
			return getDisplayName() + ": No Action Taken";
		}
		return getDisplayName() + ": " + getLastRelativeAction();
	}

}
