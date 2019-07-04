package edu.osu.cse.marl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import model.Position;
import model.Unit;

@RunWith(MockitoJUnitRunner.class)
public class BoardFactoryTest {
	@Mock
	Unit u1, u2;

	@Test
	public void createBoard() {
		int[] dimensions = { 5, 7 };
		List<Unit> team1 = new ArrayList<Unit>();
		team1.add(u1);
		List<Unit> team2 = new ArrayList<Unit>();
		team2.add(u2);
		DynamicBoardFactory bf = new DynamicBoardFactory();

		Board board = bf.createBoard(dimensions, team1, team2);

		assertEquals(team1, board.getTeam1Units());
		assertEquals(team2, board.getTeam2Units());
		assertEquals(dimensions, board.getSize());
		verify(u1).setPosition(any(Position.class));
		verify(u2).setPosition(any(Position.class));
	}

}
