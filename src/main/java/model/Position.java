package model;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Position {
	private int x, y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Position(Position absolutePosition, Position relativePosition) {
		this.x = absolutePosition.getX() + relativePosition.getX();
		this.y = absolutePosition.getY() + relativePosition.getY();
	}

	public static Position getRelativePosition(Position currentPosition, Position targetPosition) {
		return new Position(targetPosition.getX() - currentPosition.getX(),
				targetPosition.getY() - currentPosition.getY());

	}

	@Override
	public String toString() {
		return "{" + x + ", " + y + "}";
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Position) {
			return x == ((Position) object).getX() && y == ((Position) object).getY();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(67, 31).append(x).append(y).toHashCode();
	}

	public boolean isDiagonal(Position target) {
		int diffX = this.getX() - target.getX();
		int diffY = this.getY() - target.getY();

		if ((diffX == 1 && (diffY == 1 || diffY == -1)) || (diffX == -1 && (diffY == 1 || diffY == -1))) {
			return true;
		}
		return false;
	}

	public boolean inRange(Position target, int distance) {
		return getDistance(target) <= distance;
	}

	public int getDistance(Position p) {
		return Math.abs(x - p.getX()) + Math.abs(y - p.getY());
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
