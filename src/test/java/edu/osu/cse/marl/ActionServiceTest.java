package edu.osu.cse.marl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import model.Action;
import model.Position;
import model.Team;
import model.Unit;

@RunWith(MockitoJUnitRunner.class)
public class ActionServiceTest {
	@Mock
	private Team t;
	@Mock
	private Board board;
	@Mock
	private Unit unit;
	private List<Unit> units;
	@Mock
	private Action action;
	@Mock
	private Position p1, p2, p3;

	@Before
	public void setUp() {
		units = new ArrayList<Unit>();
	}

	@Test(expected = IllegalStateException.class)
	public void movementTargetIsOccupied() {
		when(unit.getPosition()).thenReturn(p1);
		when(unit.getTeam()).thenReturn(t);
		when(unit.isAlive()).thenReturn(true);
		Map<Unit, Action> actions = new HashMap<Unit, Action>();
		actions.put(unit, action);
		when(action.isDoMovementFirst()).thenReturn(true);
		when(action.getMovementTarget()).thenReturn(p2);
		when(p2.getDistance(p1)).thenReturn(2);
		units.add(new Unit(p2));
		when(board.getTeam1Units()).thenReturn(units);
		when(board.getMovementSpeed()).thenReturn(10);
		int[] size = { 5, 5 };
		when(board.getSize()).thenReturn(size);
		when(board.isOnBoard(p1)).thenReturn(true);
		when(board.isOnBoard(p2)).thenReturn(true);

		ActionService as = new ActionService();
		as.setBoard(board);
		as.process(actions);
	}

	@Test(expected = IllegalStateException.class)
	public void attackTargetIsUnoccupied() {
		when(unit.getPosition()).thenReturn(p1);
		when(unit.getTeam()).thenReturn(t);
		when(unit.isAlive()).thenReturn(true);
		Map<Unit, Action> actions = new HashMap<Unit, Action>();
		actions.put(unit, action);
		when(action.isDoMovementFirst()).thenReturn(false);
		when(action.getAttackTarget()).thenReturn(p2);
		when(p2.getDistance(p1)).thenReturn(2);
		when(board.getTeam1Units()).thenReturn(units);
		when(board.getMovementSpeed()).thenReturn(10);

		ActionService as = new ActionService();
		as.setBoard(board);
		as.process(actions);
	}

	@Test
	public void attackThenMoveBack() {
		when(unit.getPosition()).thenReturn(p1);
		when(unit.getTeam()).thenReturn(t);
		when(unit.isAlive()).thenReturn(true);
		Map<Unit, Action> actions = new HashMap<Unit, Action>();
		actions.put(unit, action);
		when(action.isDoMovementFirst()).thenReturn(false);
		when(action.getAttackTarget()).thenReturn(p2);
		when(action.getMovementTarget()).thenReturn(p1);
		when(board.getTeam1Units()).thenReturn(units);
		units.add(unit);
		units.add(new Unit(p2));
		when(p1.getX()).thenReturn(0);
		when(p1.getY()).thenReturn(0);
		when(p2.getX()).thenReturn(1);
		when(p2.getY()).thenReturn(1);
		int[] size = { 5, 5 };
		when(board.getSize()).thenReturn(size);
		when(board.isOnBoard(p1)).thenReturn(true);
		when(board.isOnBoard(p2)).thenReturn(true);

		ActionService as = new ActionService();
		as.setBoard(board);
		as.process(actions);

		verify(action).execute(unit);
	}

	@Test
	public void moveThenAttack() {
		when(unit.getPosition()).thenReturn(p1);
		when(unit.getTeam()).thenReturn(t);
		when(unit.isAlive()).thenReturn(true);
		Map<Unit, Action> actions = new HashMap<Unit, Action>();
		actions.put(unit, action);
		when(action.isDoMovementFirst()).thenReturn(true);
		when(action.getMovementTarget()).thenReturn(p2);
		when(p2.getDistance(p1)).thenReturn(2);
		when(board.getMovementSpeed()).thenReturn(2);
		when(action.getAttackTarget()).thenReturn(p3);
		when(board.getTeam1Units()).thenReturn(units);
		units.add(new Unit(p3));
		when(p2.getX()).thenReturn(0);
		when(p2.getY()).thenReturn(0);
		when(p3.getX()).thenReturn(1);
		when(p3.getY()).thenReturn(1);
		int[] size = { 5, 5 };
		when(board.getSize()).thenReturn(size);
		when(board.isOnBoard(p1)).thenReturn(true);
		when(board.isOnBoard(p2)).thenReturn(true);
		when(board.isOnBoard(p3)).thenReturn(true);

		ActionService as = new ActionService();
		as.setBoard(board);
		as.process(actions);

		verify(action).execute(unit);
	}

	@Test
	public void movementTargetIsNull() {
		when(unit.getPosition()).thenReturn(p1);
		when(unit.getTeam()).thenReturn(t);
		when(unit.isAlive()).thenReturn(true);
		Map<Unit, Action> actions = new HashMap<Unit, Action>();
		actions.put(unit, action);
		when(action.isDoMovementFirst()).thenReturn(false);
		when(action.getMovementTarget()).thenReturn(null);
		when(action.getAttackTarget()).thenReturn(p2);
		when(board.getTeam1Units()).thenReturn(units);
		units.add(new Unit(p2));
		when(p1.getX()).thenReturn(0);
		when(p1.getY()).thenReturn(0);
		when(p2.getX()).thenReturn(1);
		when(p2.getY()).thenReturn(1);
		int[] size = { 5, 5 };
		when(board.getSize()).thenReturn(size);
		when(board.isOnBoard(p2)).thenReturn(true);

		ActionService as = new ActionService();
		as.setBoard(board);
		as.process(actions);

		verify(action).execute(unit);
	}

	@Test
	public void attackTargetIsNull() {
		when(unit.getPosition()).thenReturn(p1);
		when(unit.getTeam()).thenReturn(t);
		when(unit.isAlive()).thenReturn(true);
		Map<Unit, Action> actions = new HashMap<Unit, Action>();
		actions.put(unit, action);
		when(action.getAttackTarget()).thenReturn(null);
		when(action.getMovementTarget()).thenReturn(p2);
		when(p2.getDistance(p1)).thenReturn(2);
		when(board.getTeam1Units()).thenReturn(units);
		when(board.getMovementSpeed()).thenReturn(10);
		int[] size = { 5, 5 };
		when(board.getSize()).thenReturn(size);
		when(board.isOnBoard(p1)).thenReturn(true);
		when(board.isOnBoard(p2)).thenReturn(true);

		ActionService as = new ActionService();
		as.setBoard(board);
		as.process(actions);

		verify(action).execute(unit);
	}

}
