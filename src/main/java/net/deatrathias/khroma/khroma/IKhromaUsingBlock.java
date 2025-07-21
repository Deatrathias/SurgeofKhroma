package net.deatrathias.khroma.khroma;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface IKhromaUsingBlock {
	static enum ConnectionType {
		NONE, PROVIDER, CONSUMER
	}

	IKhromaUsingBlock.ConnectionType khromaConnection(BlockState state, Direction direction);
}
