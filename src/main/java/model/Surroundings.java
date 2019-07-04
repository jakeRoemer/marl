package model;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Surroundings uses absolute positions
 *
 */
public class Surroundings {
	private Position currentPosition;
	private List<Unit> visibleUnits;
	private List<Position> emptyPositions;

	public Surroundings(Position currentPosition, List<Unit> visibleUnits, List<Position> emptyPositions) {
		this.currentPosition = currentPosition;
		this.visibleUnits = visibleUnits;
		this.emptyPositions = emptyPositions;
	}

	public List<Position> getEmptyPositions() {
		return emptyPositions;
	}

	public List<Unit> getVisibleUnits() {
		return visibleUnits;
	}

	public Position getCurrentPosition() {
		return currentPosition;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}

		if (obj.getClass() != getClass()) {
			return false;
		}
		Surroundings surroundings = (Surroundings) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj))
				.append(currentPosition, surroundings.getCurrentPosition())
				.append(visibleUnits, surroundings.getVisibleUnits())
				.append(emptyPositions, surroundings.getEmptyPositions()).isEquals();
	}
}
