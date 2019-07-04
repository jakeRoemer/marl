package ai.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.osu.cse.marl.BoardService;
import model.Position;
import model.Surroundings;
import model.Team;
import model.Unit;

@RunWith(MockitoJUnitRunner.class)
public class StateTest {
	@Mock
	private Surroundings surroundings;
	@Mock
	private Team team1, team2;
	@Mock private BoardService bs;

	@Test
	public void visibleUnitPositionsAreRelative() {
		Position currentPosition = new Position(4, 4);
		when(surroundings.getCurrentPosition()).thenReturn(currentPosition);
		List<Unit> visibleUnits = createVisibleUnits();
		when(surroundings.getVisibleUnits()).thenReturn(visibleUnits);
		State state = new State(surroundings, team1);

		Map<Unit, Position> visibleUnitPositions = state.getVisibleUnitPositions();

		assertEquals(2, visibleUnitPositions.size());
		assertTrue(visibleUnitPositions.containsKey(visibleUnits.get(0)));
		assertEquals(new Position(0, 1), visibleUnitPositions.get(visibleUnits.get(0)));
		assertTrue(visibleUnitPositions.containsKey(visibleUnits.get(1)));
		assertEquals(new Position(-1, -1), visibleUnitPositions.get(visibleUnits.get(1)));
		verify(surroundings).getCurrentPosition();
		verify(surroundings).getEmptyPositions();
		verify(surroundings).getVisibleUnits();
	}

	@Test
	public void emptyPositionsAreRelative() {
		Position currentPosition = new Position(4, 4);
		when(surroundings.getCurrentPosition()).thenReturn(currentPosition);
		List<Position> emptyPositions = createEmptyPositions();
		when(surroundings.getEmptyPositions()).thenReturn(emptyPositions);
		State state = new State(surroundings, team1);

		List<Position> relativeEmptyPositions = state.getEmptyPositions();

		assertEquals(6, relativeEmptyPositions.size());
		assertTrue(relativeEmptyPositions.contains(new Position(-1, 0)));
		assertTrue(relativeEmptyPositions.contains(new Position(-1, 1)));
		assertTrue(relativeEmptyPositions.contains(new Position(0, -1)));
		assertTrue(relativeEmptyPositions.contains(new Position(1, -1)));
		assertTrue(relativeEmptyPositions.contains(new Position(1, 0)));
		assertTrue(relativeEmptyPositions.contains(new Position(1, 1)));
		verify(surroundings).getCurrentPosition();
		verify(surroundings).getEmptyPositions();
		verify(surroundings).getVisibleUnits();
	}

	@Test
	public void movementPositionsAreSubsetOfEmptyPositions() {
		Position currentPosition = new Position(4, 4);
		when(surroundings.getCurrentPosition()).thenReturn(currentPosition);
		List<Position> emptyPositions = createEmptyPositions();
		when(surroundings.getEmptyPositions()).thenReturn(emptyPositions);
		State state = new State(surroundings, team1);

		List<Position> movementPositions = state.getMovementPositions(1);

		assertTrue(state.getEmptyPositions().containsAll(movementPositions));
		assertFalse(movementPositions.contains(new Position(-1, 1)));
		assertFalse(movementPositions.contains(new Position(1, 1)));
		assertTrue(movementPositions.contains(new Position(-1, 0)));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void equalsIsTrueForEquivalentStatesOnly() {
		Position p1 = new Position(4, 4);
		Position p2 = new Position(5, 5);
		Position p3 = new Position(4, 5);
		List<Position> empty1 = new ArrayList<Position>();
		empty1.add(new Position(3, 3));
		List<Position> empty2 = new ArrayList<Position>();
		empty2.add(new Position(4, 4));
		List<Unit> units1 = new ArrayList<Unit>();
		units1.add(new Unit(new Position(4, 5)));
		List<Unit> units2 = new ArrayList<Unit>();
		units2.add(new Unit(new Position(5, 6)));
		when(surroundings.getCurrentPosition()).thenReturn(p1, p2, p3);
		when(surroundings.getEmptyPositions()).thenReturn(empty1, empty2, empty1);
		when(surroundings.getVisibleUnits()).thenReturn(units1, units2, units1);

		State state1 = new State(surroundings, team1);
		State state2 = new State(surroundings, team1);
		State state3 = new State(surroundings, team1);

		assertEquals(state1, state2);
		assertNotEquals(state1, state3);
		assertNotEquals(state2, state3);
	}

	@Test
	public void getMovementPositionsAfterAttack() {
		Position currentPosition = new Position(4, 4);
		when(surroundings.getCurrentPosition()).thenReturn(currentPosition);
		List<Unit> visibleUnits = createVisibleUnits();
		when(surroundings.getVisibleUnits()).thenReturn(visibleUnits);
		List<Position> emptyPositions = createEmptyPositions();
		when(surroundings.getEmptyPositions()).thenReturn(emptyPositions);
		State state = new State(surroundings, team1);

		List<Position> movementPositions = state.getMovementPositionsAfterAttack(new Position(-1, -1), 1);
		
		assertEquals(2, movementPositions.size());
		assertTrue(movementPositions.contains(new Position(-1, 0)));
		assertTrue(movementPositions.contains(new Position(0, -1)));
	}
	
	@Test
	public void statesAreDifferentDependingOnUnitTeams() {
		Position p1 = new Position(1, 1);
		when(surroundings.getCurrentPosition()).thenReturn(p1);
		Unit unit1 = new Unit(team1, bs, 1);
		unit1.setPosition(new Position(2,2));
		List<Unit> units1 = new ArrayList<Unit>();
		units1.add(unit1);
		when(surroundings.getVisibleUnits()).thenReturn(units1);
		
		State state1 = new State(surroundings, team1);
		State state2 = new State(surroundings, team2);
		
		assertNotEquals(state1, state2);
	}

	private List<Position> createEmptyPositions() {
		List<Position> emptyPositions = new ArrayList<Position>();
		emptyPositions.add(new Position(3, 4));
		emptyPositions.add(new Position(3, 5));
		emptyPositions.add(new Position(4, 3));
		emptyPositions.add(new Position(5, 3));
		emptyPositions.add(new Position(5, 4));
		emptyPositions.add(new Position(5, 5));
		return emptyPositions;
	}

	private List<Unit> createVisibleUnits() {
		List<Unit> visibleUnits = new ArrayList<Unit>();
		visibleUnits.add(new Unit(new Position(4, 5)));
		visibleUnits.add(new Unit(new Position(3, 3)));
		return visibleUnits;
	}

}
