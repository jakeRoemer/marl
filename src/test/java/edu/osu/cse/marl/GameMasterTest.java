package edu.osu.cse.marl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import model.Action;
import model.Position;
import model.Team;
import model.Unit;
import view.Display;

@RunWith(MockitoJUnitRunner.class)
public class GameMasterTest {
	@Mock
	private GameOptions gameOptions;
	@Mock
	private TeamFactory tf;
	@Mock
	private DynamicBoardFactory bf;
	@Mock
	private Team team1, team2;
	@Mock
	private Board board;
	@Mock
	private Unit unit;
	@Mock
	private Position p;
	@Mock
	private List<Unit> units;
	@Mock
	private List<Unit> team2Units;
	@Mock
	private Display display;
	@Mock
	private ScoreService ss;
	@Mock
	private Score score;
	@Mock
	private ActionService as;

	@Test
	public void teamsAreCreatedBasedOnGameOptions() {
		when(gameOptions.getTeamSize()).thenReturn(2);
		when(gameOptions.getTeam1Type()).thenReturn(Player.ORACLE);
		when(gameOptions.getTeam2Type()).thenReturn(Player.COOPERATIVE);
		when(tf.createTeam(eq(Player.ORACLE), any(BoardService.class), eq(1), eq(2))).thenReturn(team1);
		when(tf.createTeam(eq(Player.COOPERATIVE), any(BoardService.class), eq(2), eq(2))).thenReturn(team2);

		@SuppressWarnings("unused")
		GameMaster gm = new GameMaster(gameOptions, tf, bf, display, ss, as);

		verify(gameOptions, times(2)).getTeamSize();
		verify(tf).createTeam(eq(Player.ORACLE), any(BoardService.class), eq(1), eq(2));
		verify(tf).createTeam(eq(Player.COOPERATIVE), any(BoardService.class), eq(2), eq(2));
	}

	@Test
	public void eachTeamCalledInOrderDuringTurn() {
		when(gameOptions.getTeamSize()).thenReturn(2);
		when(tf.createTeam(any(Player.class), any(BoardService.class), eq(1), eq(2))).thenReturn(team1);
		when(team1.getLivingUnits()).thenReturn(units);
		when(tf.createTeam(any(Player.class), any(BoardService.class), eq(2), eq(2))).thenReturn(team2);
		when(team2.getLivingUnits()).thenReturn(team2Units);
		int[] dimensions = { 5, 5 };
		when(gameOptions.getBoardSize()).thenReturn(dimensions);
		when(bf.createBoard(dimensions, units, team2Units)).thenReturn(board);
		when(board.getMovementSpeed()).thenReturn(1);
		when(gameOptions.getMaxTurns()).thenReturn(5);
		when(units.isEmpty()).thenReturn(false);
		GameMaster gm = new GameMaster(gameOptions, tf, bf, display, ss, as);

		gm.play(1);

		verify(gameOptions).getBoardSize();
		verify(team1, atLeastOnce()).takeTurn();
		verify(team2, atLeastOnce()).takeTurn();
	}

	@Test
	public void teamDecisionsAreProcessed() {
		when(tf.createTeam(any(Player.class), any(BoardService.class), eq(1), eq(1))).thenReturn(team1);
		when(tf.createTeam(any(Player.class), any(BoardService.class), eq(2), eq(1))).thenReturn(team2);
		when(gameOptions.getMaxTurns()).thenReturn(1);
		when(gameOptions.getTeamSize()).thenReturn(1);
		int[] dimensions = { 5, 5 };
		when(gameOptions.getBoardSize()).thenReturn(dimensions);
		when(bf.createBoard(dimensions, units, team2Units)).thenReturn(board);
		GameMaster gm = new GameMaster(gameOptions, tf, bf, display, ss, as);
		Map<Unit, Action> decisions = new HashMap<Unit, Action>();
		when(team1.takeTurn()).thenReturn(decisions);
		when(team2.takeTurn()).thenReturn(decisions);
		when(team1.getLivingUnits()).thenReturn(units);
		when(team2.getLivingUnits()).thenReturn(team2Units);
		when(units.isEmpty()).thenReturn(false);

		gm.play(1);

		verify(team1).takeTurn();
		verify(team2).takeTurn();
		verify(as, times(2)).process(decisions);
	}

	@Test
	public void scoreIsUpdatedAfterEachTeamsTurn() {
		int[] dimensions = { 5, 5 };
		when(gameOptions.getBoardSize()).thenReturn(dimensions);
		when(gameOptions.getMaxTurns()).thenReturn(5);
		when(bf.createBoard(dimensions, units, team2Units)).thenReturn(board);
		when(gameOptions.getTeamSize()).thenReturn(1);
		when(tf.createTeam(any(Player.class), any(BoardService.class), eq(1), eq(1))).thenReturn(team1);
		when(tf.createTeam(any(Player.class), any(BoardService.class), eq(2), eq(1))).thenReturn(team2);
		when(team1.getLivingUnits()).thenReturn(units);
		when(team2.getLivingUnits()).thenReturn(team2Units);
		Map<Unit, Action> decisions = new HashMap<Unit, Action>();
		when(team1.takeTurn()).thenReturn(decisions);
		when(units.isEmpty()).thenReturn(false, false);
		when(team2Units.isEmpty()).thenReturn(false, true);
		when(units.size()).thenReturn(1);
		when(team2Units.size()).thenReturn(0);
		GameMaster gm = new GameMaster(gameOptions, tf, bf, display, ss, as);
		when(ss.calculateScore(1, 0, 1)).thenReturn(score);

		List<List<Score>> scores = gm.play(1);

		assertEquals(score, scores.get(0));
		verify(ss, times(2)).calculateScore(1, 0, 1);
	}

	@Test
	public void twoGamesArePlayed() {
		when(gameOptions.getTeamSize()).thenReturn(2);
		when(tf.createTeam(any(Player.class), any(BoardService.class), eq(1), eq(2))).thenReturn(team1);
		when(team1.getLivingUnits()).thenReturn(units);
		when(tf.createTeam(any(Player.class), any(BoardService.class), eq(2), eq(2))).thenReturn(team2);
		when(team2.getLivingUnits()).thenReturn(team2Units);
		int[] dimensions = { 5, 5 };
		when(gameOptions.getBoardSize()).thenReturn(dimensions);
		when(bf.createBoard(dimensions, units, team2Units)).thenReturn(board);
		when(board.getMovementSpeed()).thenReturn(1);
		when(gameOptions.getMaxTurns()).thenReturn(5);
		when(units.isEmpty()).thenReturn(false);
		GameMaster gm = new GameMaster(gameOptions, tf, bf, display, ss, as);

		List<List<Score>> scores = gm.play(2);

		assertEquals(2, scores.size());
		verify(tf).createTeam(any(Player.class), any(BoardService.class), eq(1), eq(2));
		verify(tf).createTeam(any(Player.class), any(BoardService.class), eq(2), eq(2));
		verify(bf, times(2)).createBoard(dimensions, units, team2Units);
		verify(team1, times(2)).reset();
		verify(team2, times(2)).reset();
	}

}
