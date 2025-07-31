package net.deatrathias.khroma.blocks;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

public class ImbuedLogBlock extends RotatedPillarBlock {

	private Holder<Block> strippedBlock;

	public ImbuedLogBlock(Properties properties, Holder<Block> strippedBlock) {
		super(properties);
		this.strippedBlock = strippedBlock;
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
