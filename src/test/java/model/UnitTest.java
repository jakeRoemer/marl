package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import edu.osu.cse.marl.BoardService;

@RunWith(MockitoJUnitRunner.class)
public class UnitTest {
	@Mock private Team team;
	@Mock private BoardService bs;
	@Mock private Position p1, p2;
	@Mock private Unit u;
	
	@Test
	public void attack() {
		when(bs.getUnitAt(p2)).thenReturn(u);
		Unit unit = new Unit(team, bs, 1);
		unit.setPosition(p1);
		
		assertEquals(u,unit.attack(p2));
		
		verify(u).setAlive(false);
		assertEquals(p2, unit.getPosition());
	}
	
	@Test
	public void move() {
		when(bs.isEmpty(p2)).thenReturn(true);
		Unit unit = new Unit(team, bs, 1);
		unit.setPosition(p1);
		
		assertTrue(unit.move(p2));

		assertEquals(p2, unit.getPosition());
	}
	
}
