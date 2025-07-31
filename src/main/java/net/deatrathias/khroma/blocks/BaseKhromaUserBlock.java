package net.deatrathias.khroma.blocks;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.deatrathias.khroma.blocks.logistics.KhromaLineBlock;
import net.deatrathias.khroma.items.SpannerItem;
import net.deatrathias.khroma.khroma.IKhromaUsingBlock;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.util.BlockDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbility;

public abstract class BaseKhromaUserBlock extends Block implements IKhromaUsingBlock, IKhromaDevice {

	protected BaseKhromaUserBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	public void dirtyNetwork(Level level, BlockPos pos) {
		for (Direction direction : Direction.values()) {
			dirtyNetwork(level, pos, direction);
		}
	}

	public void dirtyNetwork(Level level, BlockPos pos, Direction direction) {
		BlockState state = level.getBlockState(pos);
		var blockDir = new BlockDirection(pos, direction);
		Optional.ofNullable(KhromaNetwork.findNetwork(level, blockDir)).ifPresentOrElse(network -> network.markDirty(), () -> {
			if (state.is(this) && khromaConnection(state, direction) == ConnectionType.PROVIDER)
				KhromaNetwork.create(level, blockDir);
		});
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!level.isClientSide)
			dirtyNetwork(level, pos);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		for (Direction direction : Direction.values()) {
			if (khromaConnection(state, direction) == ConnectionType.NONE)
				continue;
			BlockPos neighborPos = pos.relative(direction);
			BlockState neighborState = level.getBlockState(neighborPos);
			if (neighborState.is(BlockReference.KHROMA_LINE))
				level.setBlockAndUpdate(neighborPos, neighborState.setValue(KhromaLineBlock.PROPERTY_BY_DIRECTION.get(direction.getOpposite()), true));
		}
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
		dirtyNetwork(level, pos);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState,
			RandomSource random) {
		Level actualLevel = (Level) level;
		if (!actualLevel.isClientSide)
			dirtyNetwork(actualLevel, pos, direction);

		return state;
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		var connection = khromaConnection(state, hitResult.getDirection());
		if (connection != ConnectionType.NONE && stack.is(BlockReference.KHROMA_LINE.asItem()))
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
			BlockState rotated = rotate(state, context.getLevel(), context.getClickedPos(), Rotation.CLOCKWISE_90);
			if (rotated != state)
				return rotated;
		}
		return null;
	}
}
