package model;

import java.util.List;
import java.util.Map;

public interface Team {
	
	Map<Unit,Action> takeTurn();

	List<Unit> getLivingUnits();
	
	String getTeamName();

	void reset();
}
