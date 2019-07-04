package ai.common;

import java.util.Set;

import model.Position;
import model.Unit;

public class Reward {

	private static final double ENEMY_KILLED = 1;
	private static final double ALLY_KILLED = -1;
	private static final double SELF_KILLED = -2;
	private static final double CLOSE_TO_ALLY = 0.25;
	private static final double CLOSE_TO_ENEMY = 0.5;

	/** The reward getValue is called when a Q value for a unit is updated.
	 * So if a unit killed an enemy or ally, a record is kept until the Q value is updated, then the record is cleared.
	 * The distance calculation will determine if a unit was seen before. 
	 * If not, then the unit must have moved closer to the unit for a positive reward.
	 * If a unit was seen before, the distance in the new state must be smaller or equal to the distance seen in the old state to be a positive reward.
	 * If a unit was seen before, but not seen in the new state then the distance must have increased for no reward.
	 * If a unit was killed during the turn it will return false for isAlive which results in a negative reward.
	 * I am unsure if the dead unit updates its Q value before being removed for the active list. */
	public static double getValue(Unit unit, State oldState, State newState) {
		int distAllyCount = 0;
		int distEnemyCount = 0;
		Set<Unit> oldUnits = oldState.getVisibleUnitPositions().keySet();
		for (Unit newUnit : newState.getVisibleUnitPositions().keySet()) {
			if (!oldUnits.contains(newUnit)) { 
				if (teamEquivalent(unit, newUnit)) {
					distAllyCount++;
				} else {
					distEnemyCount++;
				}
			} else {
				double initialDist = getDist(oldState.getVisibleUnitPositions().get(newUnit));
				double finalDist = getDist(newState.getVisibleUnitPositions().get(newUnit));
				if (finalDist <= initialDist) {
					if (teamEquivalent(unit, newUnit)) {
						distAllyCount++;
					} else {
						distEnemyCount++;
					}
				}
			}
		}
		if (unit.killedUnit != null) {
			if (teamEquivalent(unit, unit.killedUnit)) {
				unit.killedUnit = null;
				return ALLY_KILLED + (unit.isAlive() ? 0 : SELF_KILLED) 
						+ distAllyCount*CLOSE_TO_ALLY + distEnemyCount*CLOSE_TO_ENEMY;
			} else {
				unit.killedUnit = null;
				return ENEMY_KILLED + (unit.isAlive() ? 0 : SELF_KILLED) 
						+ distAllyCount*CLOSE_TO_ALLY + distEnemyCount*CLOSE_TO_ENEMY;
			}
		}
		return (unit.isAlive() ? 0 : SELF_KILLED) + distAllyCount*CLOSE_TO_ALLY + distEnemyCount*CLOSE_TO_ENEMY;
	}
	
	
	/** For now this does the same as getValue*/
	public static double getCoopValue(Unit unit, State oldState, State newState) {
		int distAllyCount = 0;
		int distEnemyCount = 0;
		
		//TODO: modify reward to share sight resources of a closet subset of allies
		//Only affects new state sight radius. 
		//Choose early which units to share sight radius with?
		
		Set<Unit> oldUnits = oldState.getVisibleUnitPositions().keySet();
		for (Unit newUnit : newState.getVisibleUnitPositions().keySet()) {
			if (!oldUnits.contains(newUnit)) { 
				if (teamEquivalent(unit, newUnit)) {
					distAllyCount++;
				} else {
					distEnemyCount++;
				}
			} else {
				double initialDist = getDist(oldState.getVisibleUnitPositions().get(newUnit));
				double finalDist = getDist(newState.getVisibleUnitPositions().get(newUnit));
				if (finalDist <= initialDist) {
					if (teamEquivalent(unit, newUnit)) {
						distAllyCount++;
					} else {
						distEnemyCount++;
					}
				}
			}
		}
		if (unit.killedUnit != null) {
			if (teamEquivalent(unit, unit.killedUnit)) {
				unit.killedUnit = null;
				return ALLY_KILLED + (unit.isAlive() ? 0 : SELF_KILLED) 
						+ distAllyCount*CLOSE_TO_ALLY + distEnemyCount*CLOSE_TO_ENEMY;
			} else {
				unit.killedUnit = null;
				return ENEMY_KILLED + (unit.isAlive() ?0 : SELF_KILLED) 
						+ distAllyCount*CLOSE_TO_ALLY + distEnemyCount*CLOSE_TO_ENEMY;
			}
		}
		return (unit.isAlive() ? 0 : SELF_KILLED) + distAllyCount*CLOSE_TO_ALLY + distEnemyCount*CLOSE_TO_ENEMY;
	}
	
	/** The State gives relative position of the otherUnit to the unit the State is based off of.
	 * Therefore, the euclidean distance is calculated from (0,0) */
	public static double getDist(Position otherUnit) {
		double xAxis = 0 - otherUnit.getX();
		double yAxis = 0 - otherUnit.getY();
		return xAxis*xAxis + yAxis*yAxis;
	}
	
	public static boolean teamEquivalent(Unit unit1, Unit unit2) {
		return unit1.getTeam().equals(unit2.getTeam());
	}
}
