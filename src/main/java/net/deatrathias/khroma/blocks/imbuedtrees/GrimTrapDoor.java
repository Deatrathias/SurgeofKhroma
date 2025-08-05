package net.deatrathias.khroma.blocks.imbuedtrees;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class GrimTrapDoor extends TrapDoorBlock {

	public GrimTrapDoor(BlockSetType type, Properties properties) {
		super(type, properties);
	}

	@Override
	protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getValue(OPEN) ? super.getShadeBrightness(state, level, pos) : 0;
	}

	@Override
	protected int getLightBlock(BlockState state) {
		return state.getValue(OPEN) ? super.getLightBlock(state) : 15;
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state) {
		return state.getValue(OPEN) ? super.propagatesSkylightDown(state) : false;
	}
}
