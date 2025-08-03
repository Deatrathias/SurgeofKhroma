package net.deatrathias.khroma.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

public class GrimStairBlock extends StairBlock {

	public GrimStairBlock(BlockState baseState, Properties properties) {
		super(baseState, properties);
	}

	@Override
	protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return 0;
	}

	@Override
	protected int getLightBlock(BlockState state) {
		return 15;
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state) {
		return false;
	}
}
