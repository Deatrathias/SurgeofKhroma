package net.deatrathias.khroma.blocks;

import org.jetbrains.annotations.Nullable;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.blockentities.BaseKhromaUserBlockEntity;
import net.deatrathias.khroma.blockentities.BaseKhromaUserBlockEntity.ConnectionType;
import net.deatrathias.khroma.items.SpannerItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbility;

public abstract class BaseKhromaUserBlock<T extends BaseKhromaUserBlockEntity> extends BaseEntityBlock {

	protected BaseKhromaUserBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	public abstract T getBlockEntity(Level level, BlockPos pos);

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!level.isClientSide)
			getBlockEntity(level, pos).dirtyNetwork();
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState,
			RandomSource random) {
		Level actualLevel = (Level) level;
		if (!actualLevel.isClientSide)
			getBlockEntity(actualLevel, pos).dirtyNetwork(direction);

		return state;
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		var connection = getBlockEntity(level, pos).khromaConnection(hitResult.getDirection());
		if (connection != ConnectionType.NONE && stack.is(RegistryReference.ITEM_BLOCK_KHROMA_LINE.get()))
			return InteractionResult.PASS;
		if (stack.canPerformAction(SpannerItem.SPANNER_ADJUST))
			return InteractionResult.PASS;

		return InteractionResult.TRY_WITH_EMPTY_HAND;
	}

	@Override
	public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		ItemStack itemStack = context.getItemInHand();
		if (!itemStack.canPerformAction(itemAbility))
			return null;

		if (itemAbility == SpannerItem.SPANNER_ADJUST) {
			return rotate(state, context.getLevel(), context.getClickedPos(), Rotation.CLOCKWISE_90);
		}
		return null;
	}
}
