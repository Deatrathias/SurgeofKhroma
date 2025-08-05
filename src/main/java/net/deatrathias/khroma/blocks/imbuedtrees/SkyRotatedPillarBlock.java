package net.deatrathias.khroma.blocks.imbuedtrees;

import com.mojang.serialization.MapCodec;

import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SkyRotatedPillarBlock extends RotatedPillarBlock {
	public static final MapCodec<SkyRotatedPillarBlock> CODEC = simpleCodec(SkyRotatedPillarBlock::new);

	public SkyRotatedPillarBlock(Properties properties) {
		super(properties);
	}

	@Override
	public MapCodec<SkyRotatedPillarBlock> codec() {
		return CODEC;
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
