package net.deatrathias.khroma.blocks;

import com.mojang.serialization.MapCodec;

import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SkySlabBlock extends SlabBlock {
	public static final MapCodec<SkySlabBlock> CODEC = simpleCodec(SkySlabBlock::new);

	public SkySlabBlock(Properties properties) {
		super(properties);
	}

	@Override
	public MapCodec<? extends SlabBlock> codec() {
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
