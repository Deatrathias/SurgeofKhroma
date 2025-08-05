package net.deatrathias.khroma.blocks.imbuedtrees;

import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SkyStairBlock extends StairBlock {

	public SkyStairBlock(BlockState baseState, Properties properties) {
		super(baseState, properties);
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state) {
		return true;
	}

	@Override
	protected int getLightBlock(BlockState state) {
		return 0;
	}
}
