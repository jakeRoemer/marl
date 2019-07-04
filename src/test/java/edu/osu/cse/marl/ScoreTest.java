package edu.osu.cse.marl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ScoreTest {

	@Test
	public void getValue() {
		Score score = new Score(1, 0, 0);

		assertEquals("Team 1: 1; Team 2: 0. Winner is Team 1", score.getValue());
	}
	
	@Test
	public void tieHasNoWinner() {
		Score score = new Score(1,1,0);
		
		assertEquals(null, score.getWinner());
	}

}
