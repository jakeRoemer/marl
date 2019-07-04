package edu.osu.cse.marl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import model.Position;
import model.Surroundings;
import model.Unit;

@RunWith(MockitoJUnitRunner.class)
public class BoardServiceTest {
	@Mock
	Board board;

	@Test
	public void testGetSurroundings() {
		Position p = new Position(0, 0);
		Unit u = new Unit(p);
		Unit ally1 = new Unit(new Position(2, 0));
		Unit ally2 = new Unit(new Position(3, 0));
		List<Unit> team1 = new ArrayList<Unit>();
		team1.add(u);
		team1.add(ally1);
		team1.add(ally2);
		Unit enemy1 = new Unit(new Position(0, 2));
		Unit enemy2 = new Unit(new Position(2, 2));
		Unit enemy3 = new Unit(new Position(0, 3));
		Unit enemy4 = new Unit(new Position(3, 2));
		List<Unit> team2 = new ArrayList<Unit>();
		team2.add(enemy1);
		team2.add(enemy2);
		team2.add(enemy3);
		team2.add(enemy4);
		when(board.getTeam1Units()).thenReturn(team1);
		when(board.getTeam2Units()).thenReturn(team2);
		when(board.getMovementSpeed()).thenReturn(1);
		int[] boardSize = { 5, 5 };
		when(board.getSize()).thenReturn(boardSize);
		BoardService bs = new BoardService(board);

		Surroundings surroundings = bs.getSurroundings(p);

		List<Unit> visibleUnits = surroundings.getVisibleUnits();
		assertEquals(4, visibleUnits.size());
		assertTrue(visibleUnits.contains(u));
		assertTrue(visibleUnits.contains(ally1));
		assertFalse(visibleUnits.contains(ally2));
		assertTrue(visibleUnits.contains(enemy1));
		assertTrue(visibleUnits.contains(enemy2));
		assertFalse(visibleUnits.contains(enemy3));
		assertFalse(visibleUnits.contains(enemy4));
		List<Position> emptyPositions = surroundings.getEmptyPositions();
		assertEquals(5, emptyPositions.size());
		assertFalse(emptyPositions.contains(p));
	}

	@Test
	public void testGetMovementSpeed() {
		when(board.getMovementSpeed()).thenReturn(51);
		BoardService bs = new BoardService(board);

		assertEquals(51, bs.getMovementSpeed());
	}

	@Test
	public void occupiedPositionNotEmpty() {
		int[] boardSize = { 5, 5 };
		when(board.getSize()).thenReturn(boardSize);
		List<Unit> team1 = new ArrayList<Unit>();
		when(board.isOnBoard(new Position(3, 2))).thenReturn(true);
		Unit u = new Unit(new Position(3, 2));
		team1.add(u);
		when(board.getTeam1Units()).thenReturn(team1);
		BoardService bs = new BoardService(board);

		assertFalse(bs.isEmpty(new Position(3, 2)));
	}

	@Test
	public void unoccupiedPositionIsEmpty() {
		int[] boardSize = { 5, 5 };
		when(board.getSize()).thenReturn(boardSize);
		List<Unit> team1 = new ArrayList<Unit>();
		Unit u = new Unit(new Position(3, 2));
		team1.add(u);
		when(board.isOnBoard(new Position(3, 2))).thenReturn(true);
		when(board.isOnBoard(new Position(2, 3))).thenReturn(true);
		when(board.getTeam1Units()).thenReturn(team1);
		BoardService bs = new BoardService(board);

		assertTrue(bs.isEmpty(new Position(2, 3)));

	}

	@Test
	public void unoccupiedPositionHasNullUnit() {
		int[] boardSize = { 5, 5 };
		when(board.getSize()).thenReturn(boardSize);
		List<Unit> team1 = new ArrayList<Unit>();
		Unit u = new Unit(new Position(3, 2));
		team1.add(u);
		when(board.getTeam1Units()).thenReturn(team1);
		when(board.isOnBoard(new Position(3, 2))).thenReturn(true);
		when(board.isOnBoard(new Position(2, 3))).thenReturn(true);
		BoardService bs = new BoardService(board);

		assertEquals(null, bs.getUnitAt(new Position(2, 3)));
	}

	@Test
	public void occupiedPositionReturnsCorrectUnit() {
		int[] boardSize = { 5, 5 };
		when(board.getSize()).thenReturn(boardSize);
		List<Unit> team1 = new ArrayList<Unit>();
		Unit u = new Unit(new Position(3, 2));
		team1.add(u);
		when(board.isOnBoard(new Position(3, 2))).thenReturn(true);
		when(board.getTeam1Units()).thenReturn(team1);
		BoardService bs = new BoardService(board);

		assertEquals(u, bs.getUnitAt(new Position(3, 2)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidPositionThrowsException() {
		int[] boardSize = { 5, 5 };
		when(board.getSize()).thenReturn(boardSize);
		BoardService bs = new BoardService(board);
		Position invalidPosition = new Position(8, 0);

		bs.isEmpty(invalidPosition);
	}

	@Test(expected = IllegalArgumentException.class)
	public void unitAtInvalidPositionThrowsException() {
		int[] boardSize = { 5, 5 };
		when(board.getSize()).thenReturn(boardSize);
		BoardService bs = new BoardService(board);
		Position invalidPosition = new Position(8, 0);

		bs.getUnitAt(invalidPosition);
	}

}
