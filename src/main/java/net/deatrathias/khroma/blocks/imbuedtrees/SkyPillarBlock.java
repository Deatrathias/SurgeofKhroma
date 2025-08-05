package net.deatrathias.khroma.blocks.imbuedtrees;

import net.deatrathias.khroma.blocks.PillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SkyPillarBlock extends PillarBlock {

	public SkyPillarBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state) {
		return true;
	}

	@Override
	protected int getLightBlock(BlockState state) {
		return 0;
	}

	@Override
	protected boolean useShapeForLightOcclusion(BlockState state) {
		return false;
	}
}
