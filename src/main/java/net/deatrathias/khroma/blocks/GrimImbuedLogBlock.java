package net.deatrathias.khroma.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class GrimImbuedLogBlock extends ImbuedLogBlock {

	public GrimImbuedLogBlock(Holder<Block> strippedBlock, Properties properties) {
		super(strippedBlock, properties);
	}

	@Override
	protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		return 0;
	}
}
