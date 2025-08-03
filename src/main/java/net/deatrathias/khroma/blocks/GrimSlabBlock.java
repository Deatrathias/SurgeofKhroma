package net.deatrathias.khroma.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;

public class GrimSlabBlock extends SlabBlock {

	public GrimSlabBlock(Properties properties) {
		super(properties);
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
