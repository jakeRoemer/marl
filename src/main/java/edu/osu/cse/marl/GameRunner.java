package edu.osu.cse.marl;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import view.Display;

/**
 * Hello world!
 *
 */
public class GameRunner {
	/**
	 * Args consist of player 1 type and player 2 type.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Please include the types for teams 1 and 2.");
			System.out.println("Valid options are cooperative, independent, and oracle.");
			return;
		} else {
			int numberOfGames = 10;
			if (args.length > 2) {
				numberOfGames = Integer.valueOf(args[2]);
			}
			Player team1Type = null;
			Player team2Type = null;
			try {
				team1Type = Player.valueOf(StringUtils.upperCase(args[0]));
				team2Type = Player.valueOf(StringUtils.upperCase(args[1]));

			} catch (IllegalArgumentException e) {
				System.out.println("Invalid team type.");
				System.out.println("Valid options are cooperative, independent, and oracle.");
				System.out.println("");
				return;
			}
			System.out.println("Team 1 is: " + team1Type);
			System.out.println("Team 2 is: " + team2Type);
			GameOptions gameOptions = new GameOptions(team1Type, team2Type);
			Display display = new Display(gameOptions);
//			Display display = null;

			GameMaster gm = new GameMaster(gameOptions, display, new DynamicBoardFactory());
			List<List<Score>> scores = gm.play(numberOfGames);

			printStatistics(scores);
		}
	}

	private static void printStatistics(List<List<Score>> windowedScores) {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("windowedLearning.txt")));
			for (List<Score> scores : windowedScores) {
				DescriptiveStatistics team1ScoreStats = new DescriptiveStatistics();
				DescriptiveStatistics team2ScoreStats = new DescriptiveStatistics();
				DescriptiveStatistics team1Wins = new DescriptiveStatistics(); //Measured in 0 and 1, can use binomal distributions instead [will not work if one team gets all the wins without using binomial distributions]
				DescriptiveStatistics team2Wins = new DescriptiveStatistics(); //Measured in 0 and 1, can use binomal distributions instead
				DescriptiveStatistics team1AverageTurnsToWin = new DescriptiveStatistics();
				DescriptiveStatistics team2AverageTurnsToWin = new DescriptiveStatistics();
				for (Score score : scores) {
					team1ScoreStats.addValue(score.getTeam1Score());
					team2ScoreStats.addValue(score.getTeam2Score());
					if ("Team 1".equals(score.getWinner())) {
						team1Wins.addValue(1);
						team2Wins.addValue(0);
						team1AverageTurnsToWin.addValue(score.getTurnCount());
					} else if ("Team 2".equals(score.getWinner())) {
						team1Wins.addValue(0);
						team2Wins.addValue(1);
						team2AverageTurnsToWin.addValue(score.getTurnCount());
					}
				}
				double team1ScoreStdev = team1ScoreStats.getStandardDeviation();
				double team2ScoreStdev = team2ScoreStats.getStandardDeviation();
				double team1ScoreStderror = team1ScoreStdev / Math.sqrt(team1ScoreStats.getN());
				double team2ScoreStderror = team2ScoreStdev / Math.sqrt(team2ScoreStats.getN());
				double team1ScoreConfidence = 1.95996*team1ScoreStderror; //1.95996 is confidence level
				double team2ScoreConfidence = 1.95996*team2ScoreStderror;
				
				double team1WinsStdev = team1Wins.getStandardDeviation();
				double team2WinsStdev = team2Wins.getStandardDeviation();
				double team1WinsStderror = team1WinsStdev / Math.sqrt(team1Wins.getN());
				double team2WinsStderror = team2WinsStdev / Math.sqrt(team2Wins.getN());
				double team1WinsConfidence = 1.95996*team1WinsStderror;
				double team2WinsConfidence = 1.95996*team2WinsStderror;
	
				double team1AvgTurnToWinConfidence;
				double team2AvgTurnToWinConfidence;
				if (team1Wins.getSum() <= 0) {
					team1AverageTurnsToWin.addValue(0);
					team1AvgTurnToWinConfidence = 0;
				} else {
					double team1AvgTurnToWinStdev = team1AverageTurnsToWin.getStandardDeviation();
					double team1AvgTurnToWinStderror = team1AvgTurnToWinStdev / Math.sqrt(team1AverageTurnsToWin.getN());
					team1AvgTurnToWinConfidence = 1.95996*team1AvgTurnToWinStderror;
				}
				if (team2Wins.getSum() <= 0) {
					team2AverageTurnsToWin.addValue(0);
					team2AvgTurnToWinConfidence = 0;
				} else {
					double team2AvgTurnToWinStdev = team2AverageTurnsToWin.getStandardDeviation();
					double team2AvgTurnToWinStderror = team2AvgTurnToWinStdev / Math.sqrt(team2AverageTurnsToWin.getN());
					team2AvgTurnToWinConfidence = 1.95996*team2AvgTurnToWinStderror;
				}
				System.out.println("\nWindow of Game Play");
				System.out.print("Team 1 Average Score: " + team1ScoreStats.getMean());
				System.out.println(" (+/- " + team1ScoreConfidence + ")");
				System.out.println("Team 1 Number of Wins: " + team1Wins.getSum() + " (of " + team1ScoreStats.getN() + " turns)");
				System.out.print("Team 1 Percentage of Wins: " + team1Wins.getMean());
				System.out.println(" (+/- " + team1WinsConfidence + ")");
				System.out.print("Team 1 Average Turns To Win: " + team1AverageTurnsToWin.getMean());
				System.out.println(" (+/- " + team1AvgTurnToWinConfidence + ")");
				System.out.println("");
				System.out.print("Team 2 Average Score: " + team2ScoreStats.getMean());
				System.out.println(" (+/- "+ team2ScoreConfidence + ")");
				System.out.println("Team 2 Number of Wins: " + team2Wins.getSum() + " (of " + team2ScoreStats.getN() + " turns)");
				System.out.print("Team 2 Percentage of Wins: " + team2Wins.getMean());
				System.out.println(" (+/- " + team2WinsConfidence + ")");
				System.out.print("Team 2 Average Turns To Win: " + team2AverageTurnsToWin.getMean());
				System.out.println(" (+/- " + team2AvgTurnToWinConfidence + ")");
				out.write(team1ScoreStats.getMean() + " " + team1ScoreConfidence + " " + team2ScoreStats.getMean() + " " + team2ScoreConfidence);
				out.write(" " + team1Wins.getSum() + " " + team1WinsConfidence + " " + team2Wins.getSum() + " " + team2WinsConfidence);
				out.write(" " + team1AverageTurnsToWin.getMean() + " " + team1AvgTurnToWinConfidence + " " + team2AverageTurnsToWin.getMean() + " " + team2AvgTurnToWinConfidence + "\n");
			}
			out.close();
		} catch (IOException e) {}
	}

}
