package io.anuke.koru.traits;

public class DirectionTrait{

	public static enum Direction{
		right, front, left, back;

		public static Direction forOrdinal(int ordinal){
			Direction[] values = Direction.values();
			return values[(ordinal + values.length) % values.length];
		}
	}
}
