package edu.osu.cse.marl;

import java.util.List;

import model.Unit;

public interface BoardFactory {

	Board createBoard(int[] boardSize, List<Unit> team1, List<Unit> team2);
}