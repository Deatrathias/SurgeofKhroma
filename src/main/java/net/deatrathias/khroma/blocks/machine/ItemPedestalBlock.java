package net.deatrathias.khroma.blocks.machine;

import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.blockentities.ItemPedestalBlockEntity;
import net.deatrathias.khroma.items.SpannerItem;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.util.EightDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbility;

public class ItemPedestalBlock extends BaseEntityBlock {
	public static final MapCodec<ItemPedestalBlock> CODEC = simpleCodec(ItemPedestalBlock::new);

	public static final Property<EightDirection> FACING = EnumProperty.create("facing", EightDirection.class);

	private final Function<BlockState, VoxelShape> shapes;

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	public ItemPedestalBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, EightDirection.NORTH));
		shapes = makeShapes();
	}

	private Function<BlockState, VoxelShape> makeShapes() {
		VoxelShape regular = Shapes.or(Block.box(4, 0, 11, 12, 14, 15), Block.box(3, 10, 0, 13, 11, 10));
		VoxelShape diagonal = Shapes.or(Block.box(1.5, 0, 8.5, 7.5, 14, 14.5), Block.box(5, 10, 1, 15, 11, 11));
		var rotateMap = Shapes.rotateHorizontal(regular);
		var rotateDiagonalMap = Shapes.rotateHorizontal(diagonal);
		return this.getShapeForEachState(state -> {
			EightDirection direction = state.getValue(FACING);
			if (direction.getAdjustedDirection() == null)
				return rotateMap.get(direction.getFirstDirection());
			else
				return rotateDiagonalMap.get(direction.getAdjustedDirection());
		});
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return shapes.apply(state);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ItemPedestalBlockEntity(pos, state);
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
		return false;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(FACING, EightDirection.fromDegrees(context.getRotation()));
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		level.getBlockEntity(pos, BlockReference.BE_ITEM_PEDESTAL.get()).ifPresent(be -> be.onStateChanged());
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (hand == InteractionHand.OFF_HAND)
			return InteractionResult.PASS;
		ItemStack handItem = player.getItemInHand(hand);
		if (handItem.canPerformAction(SpannerItem.SPANNER_ADJUST))
			return InteractionResult.PASS;
		ItemPedestalBlockEntity be = level.getBlockEntity(pos, BlockReference.BE_ITEM_PEDESTAL.get()).get();
		ItemStack result = be.placeItem(handItem, false);
		if (ItemStack.matches(handItem, result))
			return InteractionResult.TRY_WITH_EMPTY_HAND;
		else {
			player.setItemInHand(hand, result);
			level.playSound(player, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS);
			return InteractionResult.SUCCESS;
		}
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		ItemPedestalBlockEntity be = level.getBlockEntity(pos, BlockReference.BE_ITEM_PEDESTAL.get()).get();
		ItemStack result = be.takeItem(99, false);
		if (result.isEmpty())
			return InteractionResult.PASS;
		else {
			if (!level.isClientSide()) {
				be.createItemEntityForPlayerPickup(result, player);
			}

			level.playSound(player, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS);
			return InteractionResult.SUCCESS;
		}
	}

	@Override
	public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		ItemStack itemStack = context.getItemInHand();
		if (!itemStack.canPerformAction(itemAbility))
			return null;

		if (itemAbility == SpannerItem.SPANNER_ADJUST) {

			BlockState rotated = state.setValue(FACING, EightDirection.values()[(state.getValue(FACING).ordinal() + 1) % EightDirection.values().length]);
			if (rotated != state)
				return rotated;
		}
		return null;
	}
}
