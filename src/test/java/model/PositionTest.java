package model;

import static org.junit.Assert.*;

import org.junit.Test;

public class PositionTest {

	@Test
	public void testIsDiagonal() {
		Position p = new Position(0, 0);

		assertTrue(p.isDiagonal(new Position(1, 1)));
		assertTrue(p.isDiagonal(new Position(-1, 1)));
		assertTrue(p.isDiagonal(new Position(1, -1)));
		assertTrue(p.isDiagonal(new Position(-1, -1)));

		assertFalse(p.isDiagonal(new Position(0, 1)));
		assertFalse(p.isDiagonal(new Position(1, 0)));
		assertFalse(p.isDiagonal(new Position(0, -1)));
		assertFalse(p.isDiagonal(new Position(0, 2)));
	}

	@Test
	public void testInRange() {
		Position p1 = new Position(2, 3);
		Position p2 = new Position(3, 4);
		assertTrue(p1.inRange(p2, 2));
		assertFalse(p1.inRange(p2, 1));
	}

	@Test
	public void distanceIsTransitive() {
		Position p1 = new Position(3, 5);
		Position p2 = new Position(-3, 0);
		assertEquals(p1.getDistance(p2), p2.getDistance(p1));
		assertEquals(11, p1.getDistance(p2));
	}

}
