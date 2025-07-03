package net.deatrathias.khroma.util;

import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class BlockDirection {
	private BlockPos pos;
	
	private Direction direction;

	public BlockPos getPos() {
		return pos;
	}

	public void setPos(BlockPos pos) {
		this.pos = pos;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public BlockDirection(BlockPos pos, Direction direction) {
		this.pos = pos;
		this.direction = direction;
	}

	@Override
	public int hashCode() {
		return Objects.hash(direction, pos);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockDirection other = (BlockDirection) obj;
		return direction == other.direction && Objects.equals(pos, other.pos);
	}
}
