package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import ai.common.QValue;

@RunWith(MockitoJUnitRunner.class)
public class ActionTest {
	@Mock
	private Unit u, killedUnit;

	@Test
	public void thisIsATest() {
		Position p1 = new Position(1, 1);
		Position p2 = new Position(2, 2);
		Action action1 = new Action(p1, p2, true);

		Position p3 = new Position(1, 1);
		Position p4 = new Position(2, 2);
		Action action2 = new Action(p3, p4, true);

		assertEquals(action1, action2);

		Map<Action, QValue> testMap = new HashMap<Action, QValue>();
		testMap.put(action1, new QValue(null));
		assertTrue(testMap.containsKey(action2));
	}

	@Test
	public void doNothingWithNulls() {
		Action a = new Action(null, null, true);
		when(u.move(any(Position.class))).thenReturn(true);

		a.execute(u);

		verify(u, times(0)).move(any(Position.class));
		verify(u, times(0)).attack(any(Position.class));
	}

	@Test
	public void doNothingWithSameMovementPosition() {
		Position p = new Position(1, 2);
		Action a = new Action(p, null, true);

		a.execute(u);

		verify(u).move(p);
		verify(u, times(0)).attack(any(Position.class));
	}

	@Test
	public void attackAndNoMovement() {
		Position p = new Position(1, 2);
		Action a = new Action(null, p, false);
		when(u.attack(p)).thenReturn(killedUnit);

		a.execute(u);

		verify(u).attack(p);
		verify(u, times(0)).move(any(Position.class));
	}

	@Test
	public void moveThenAttack() {
		Position mp = new Position(1, 2);
		Position ap = new Position(4, 5);
		Action a = new Action(mp, ap, true);
		when(u.move(mp)).thenReturn(true);
		when(u.attack(ap)).thenReturn(killedUnit);

		a.execute(u);

		InOrder inOrder = inOrder(u);
		inOrder.verify(u).move(mp);
		inOrder.verify(u).attack(ap);
	}

	@Test
	public void attackThenMove() {
		Position mp = new Position(1, 2);
		Position ap = new Position(4, 5);
		Action a = new Action(mp, ap, false);
		when(u.move(mp)).thenReturn(true);
		when(u.attack(ap)).thenReturn(killedUnit);

		a.execute(u);

		InOrder inOrder = inOrder(u);
		inOrder.verify(u).attack(ap);
		inOrder.verify(u).move(mp);
	}

}
