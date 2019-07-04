package edu.osu.cse.marl;

import java.util.ArrayList;
import java.util.List;

import model.Team;
import view.Display;

public class GameMaster {

	private Display display;
	private BoardFactory bf;
	private TeamFactory tf;
	private ScoreService ss;
	private ActionService as;
	private BoardService bs;
	private GameOptions go;

	private Team team1;
	private Team team2;

	private Board board;
	private int turnCount;
	private Score score;

	public GameMaster(GameOptions gameOptions, Display display) {
		this.display = display;
		this.bf = new DynamicBoardFactory();
		this.ss = new ScoreService(gameOptions.getTeamSize());
		this.tf = new TeamFactory();
		initialize(gameOptions);
		this.as = new ActionService();
	}
	
	public GameMaster(GameOptions gameOptions, Display display, BoardFactory bf) {
		this.display = display;
		this.bf = bf;
		this.ss = new ScoreService(gameOptions.getTeamSize());
		this.tf = new TeamFactory();
		initialize(gameOptions);
		this.as = new ActionService();
	}

	public GameMaster(GameOptions gameOptions, TeamFactory tf, BoardFactory bf, Display display, ScoreService ss, ActionService as) {
		this.display = display;
		this.tf = tf;
		this.bf = bf;
		this.ss = ss;
		this.as = as;
		initialize(gameOptions);
	}

	private void initialize(GameOptions gameOptions) {
		this.go = gameOptions;
		this.bs = new BoardService();
		team1 = tf.createTeam(gameOptions.getTeam1Type(), bs, 1, gameOptions.getTeamSize());
		team2 = tf.createTeam(gameOptions.getTeam2Type(), bs, 2, gameOptions.getTeamSize());

	}

	public List<List<Score>> play(int numberOfGames) {
		List<List<Score>> windowedScores = new ArrayList<List<Score>>();
		List<Score> scores = new ArrayList<Score>();
		for (int i = 0; i < numberOfGames; i++) {
			setUpGame();
			scores.add(play());
			if ((i+1) % Math.ceil((numberOfGames/(double)10)) == 0) { //every n games one of 10 points are generated, (i+1) since 0%anything is 0
				windowedScores.add(scores);
				scores = new ArrayList<Score>();
			}
		}
		return windowedScores;
	}

	private void setUpGame() {
		turnCount = 1;
		team1.reset();
		team2.reset();
		board = bf.createBoard(go.getBoardSize(), team1.getLivingUnits(), team2.getLivingUnits());
		bs.setBoard(board);
		as.setBoard(board);
		updateScore();
	}

	private Score play() {
		while (!gameOver()) {
			processTurn(team1);

			if (gameOver()) {
				break;
			}

			processTurn(team2);

			turnCount++;
		}
//		System.out.println(score.getValue());
		return score;
	}

	private void processTurn(Team team) {
		as.process(team.takeTurn());
		updateScore();
	}

	private boolean gameOver() {
		return turnCount > go.getMaxTurns() || team1.getLivingUnits().isEmpty() || team2.getLivingUnits().isEmpty();
	}

	private void updateScore() {
		score = ss.calculateScore(team1.getLivingUnits().size(), team2.getLivingUnits().size(), turnCount);
		if (display != null) {
			display.notify(score, board.getTeam1Units(), board.getTeam2Units());
		}
	}

}
