package net.deatrathias.khroma.blocks.imbuedtrees;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SkyImbuedLogBlock extends ImbuedLogBlock {
	public static final MapCodec<SkyImbuedLogBlock> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("strippedBlock").forGetter(SkyImbuedLogBlock::getStrippedBlock),
					propertiesCodec())
					.apply(instance, SkyImbuedLogBlock::new));

	public SkyImbuedLogBlock(Holder<Block> strippedBlock, Properties properties) {
		super(strippedBlock, properties);
	}

	@Override
	public MapCodec<? extends RotatedPillarBlock> codec() {
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
