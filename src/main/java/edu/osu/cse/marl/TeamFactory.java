package edu.osu.cse.marl;

import java.util.ArrayList;
import java.util.List;

import ScriptAI.ScriptedTeam;
import cooperative.CooperativeTeam;
import independent.IndependentTeam;
import model.Team;
import model.Unit;
import oracle.OracleTeam;

public class TeamFactory {

	public Team createTeam(Player type, BoardService bs, int teamNumber, int teamSize) {
		List<Unit> units = new ArrayList<Unit>();

		Team team = null;
		switch (type) {
		case COOPERATIVE:
			team = new CooperativeTeam(units, teamNumber);
			break;
		case INDEPENDENT:
			team = new IndependentTeam(units, teamNumber);
			break;
		case ORACLE:
			team = new OracleTeam(units, teamNumber);
			break;
		case SCRIPTED:
			team = new ScriptedTeam(units, teamNumber);
			break;
		}

		for (int i = 0; i < teamSize; i++) {
			units.add(new Unit(team, bs, i));
		}

		return team;
	}

}
