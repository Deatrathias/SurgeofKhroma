package net.deatrathias.khroma.blocks.imbuedtrees;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class ImbuedLogBlock extends RotatedPillarBlock {
	public static final MapCodec<ImbuedLogBlock> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("strippedBlock").forGetter(ImbuedLogBlock::getStrippedBlock),
					propertiesCodec())
					.apply(instance, ImbuedLogBlock::new));

	private Holder<Block> strippedBlock;

	public ImbuedLogBlock(Holder<Block> strippedBlock, Properties properties) {
		super(properties);
		this.strippedBlock = strippedBlock;
	}

	public Holder<Block> getStrippedBlock() {
		return strippedBlock;
	}

	@Override
	public MapCodec<? extends RotatedPillarBlock> codec() {
		return CODEC;
	}

	@Override
	public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		ItemStack itemStack = context.getItemInHand();
		if (!itemStack.canPerformAction(itemAbility))
			return null;

		if (strippedBlock != null && itemAbility == ItemAbilities.AXE_STRIP) {
			return strippedBlock.value().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
		}

		return super.getToolModifiedState(state, context, itemAbility, simulate);
	}
}
