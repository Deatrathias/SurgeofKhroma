package net.deatrathias.khroma.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.SegmentedAnglePrecision;
import net.minecraft.util.StringRepresentable;

public enum EightDirection implements StringRepresentable {
	NORTH("north", Direction.NORTH, null, null),
	NORTH_EAST("north_east", Direction.NORTH, Direction.EAST, Direction.NORTH),
	EAST("east", Direction.EAST, null, null),
	SOUTH_EAST("south_east", Direction.SOUTH, Direction.EAST, Direction.EAST),
	SOUTH("south", Direction.SOUTH, null, null),
	SOUTH_WEST("south_west", Direction.SOUTH, Direction.WEST, Direction.SOUTH),
	WEST("west", Direction.WEST, null, null),
	NORTH_WEST("north_west", Direction.NORTH, Direction.WEST, Direction.WEST);

	private static final SegmentedAnglePrecision SEGMENTED_ANGLE8 = new SegmentedAnglePrecision(3);

	private final String name;

	private final Direction firstDirection;

	private final Direction secondDirection;

	private final Direction adjustedDirection;

	private EightDirection(String name, Direction firstDirection, Direction secondDirection, Direction adjustedDirection) {
		this.name = name;
		this.firstDirection = firstDirection;
		this.secondDirection = secondDirection;
		this.adjustedDirection = adjustedDirection;
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public Direction getFirstDirection() {
		return firstDirection;
	}

	public Direction getSecondDirection() {
		return secondDirection;
	}

	public Direction getAdjustedDirection() {
		return adjustedDirection;
	}

	public BlockPos apply(BlockPos pos) {
		BlockPos result = pos.relative(firstDirection);
		if (secondDirection != null)
			return result.relative(secondDirection);
		return result;
	}

	public BlockPos applyReverse(BlockPos pos) {
		BlockPos result = pos.relative(firstDirection.getOpposite());
		if (secondDirection != null)
			return result.relative(secondDirection.getOpposite());
		return result;
	}

	public float convertToDegrees() {
		return SEGMENTED_ANGLE8.toDegrees(ordinal());
	}

	public static EightDirection fromDegrees(float degrees) {
		int segment = SEGMENTED_ANGLE8.fromDegrees(degrees);
		return EightDirection.values()[segment];
	}
}
