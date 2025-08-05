package net.deatrathias.khroma.blocks.imbuedtrees;

import com.mojang.serialization.MapCodec;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SkySimpleBlock extends Block {
	public static final MapCodec<SkySimpleBlock> CODEC = simpleCodec(SkySimpleBlock::new);

	public SkySimpleBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected MapCodec<? extends Block> codec() {
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
