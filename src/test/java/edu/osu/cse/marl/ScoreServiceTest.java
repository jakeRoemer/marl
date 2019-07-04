package edu.osu.cse.marl;

import static org.junit.Assert.*;

import org.junit.Test;

public class ScoreServiceTest {

	@Test
	public void initialScoreIs0_0() {
		int startingSize = 5;
		ScoreService ss = new ScoreService(startingSize);
		
		Score score = ss.calculateScore(startingSize, startingSize, 0);
		
		assertEquals(0, score.getTeam1Score());
		assertEquals(0, score.getTeam2Score());
	}
	
	@Test
	public void maximalScoreIs100_0() {
		int startingSize = 5;
		ScoreService ss = new ScoreService(startingSize);

		Score score = ss.calculateScore(startingSize, 0, 0);
		
		assertEquals(100, score.getTeam1Score());
		assertEquals(0, score.getTeam2Score());
	}
	
	@Test
	public void scoreIsNegativeAfterLongGame() {
		int startingSize = 5;
		ScoreService ss = new ScoreService(startingSize);

		Score score = ss.calculateScore(startingSize, startingSize, 400);

		assertTrue(score.getTeam1Score() < 0);
		assertTrue(score.getTeam2Score() < 0);
	}

}
