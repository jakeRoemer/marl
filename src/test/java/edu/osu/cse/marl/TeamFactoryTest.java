package edu.osu.cse.marl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import cooperative.CooperativeTeam;
import independent.IndependentTeam;
import model.Team;
import model.Unit;
import oracle.OracleTeam;

@RunWith(MockitoJUnitRunner.class)
public class TeamFactoryTest {
	@Mock
	private BoardService bs;
	private int size = 3;

	@Test
	public void testCreateCooperativeTeam() {
		TeamFactory tf = new TeamFactory();
		Team team = tf.createTeam(Player.COOPERATIVE, bs, 1, size);

		assertTrue(team instanceof CooperativeTeam);
		assertEquals(size, team.getLivingUnits().size());
		assertEquals("Team 1", team.getTeamName());
	}

	@Test
	public void testCreateIndependentTeam() {
		TeamFactory tf = new TeamFactory();
		Team team = tf.createTeam(Player.INDEPENDENT, bs, 2, size);

		assertTrue(team instanceof IndependentTeam);
		assertEquals(size, team.getLivingUnits().size());
		assertEquals("Team 2", team.getTeamName());
		for (Unit unit : team.getLivingUnits()) {
			assertNotNull(unit);
		}
	}

	@Test
	public void testCreateOracleTeam() {
		TeamFactory tf = new TeamFactory();
		Team team = tf.createTeam(Player.ORACLE, bs, 1, size);

		assertTrue(team instanceof OracleTeam);
		assertEquals(size, team.getLivingUnits().size());
		assertEquals("Team 1", team.getTeamName());
	}

}
