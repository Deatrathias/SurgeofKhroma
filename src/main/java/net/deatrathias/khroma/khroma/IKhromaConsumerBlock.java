package net.deatrathias.khroma.khroma;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IKhromaConsumerBlock {
	IKhromaConsumer getConsumer(Level level, BlockPos pos, BlockState state, Direction face);
}
