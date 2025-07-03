package net.deatrathias.khroma.blocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.khroma.IKhromaConsumer;
import net.deatrathias.khroma.khroma.IKhromaProvider;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.khroma.KhromaProperty;
import net.deatrathias.khroma.util.BlockDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;

public class KhromaLineBlock extends PipeBlock implements SimpleWaterloggedBlock {

	public static final MapCodec<KhromaLineBlock> CODEC = simpleCodec(KhromaLineBlock::new);

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	@Override
	protected MapCodec<KhromaLineBlock> codec() {
		return CODEC;
	}

	public static final KhromaProperty KHROMA = KhromaProperty.create("khroma");

	public KhromaLineBlock(Properties properties) {
		super(0.125f, properties);
		for (int i = 0; i < shapeByIndex.length; i++)
			shapeByIndex[i] = Shapes.or(shapeByIndex[i], Block.box(5, 5, 5, 11, 11, 11));

		registerDefaultState(stateDefinition.any().setValue(KHROMA, 0).setValue(NORTH, false).setValue(SOUTH, false).setValue(EAST, false).setValue(WEST, false).setValue(UP, false)
				.setValue(DOWN, false).setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(KHROMA, NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		Direction direction = context.getClickedFace().getOpposite();
		BlockState state = defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
		if (canSideConnect(context.getLevel(), state, context.getClickedPos(), direction)) {
			return state.trySetValue(PROPERTY_BY_DIRECTION.get(direction), true);
		}
		return state;
	}

	public static boolean canSideConnect(Level level, BlockState lineBlock, BlockPos linePos, Direction side) {
		BlockPos neighborPos = linePos.relative(side);
		Direction reverseSide = side.getOpposite();
		BlockState neighbor = level.getBlockState(neighborPos);
		if (neighbor.is(RegistryReference.BLOCK_KHROMA_LINE))
			return true;
		IKhromaProvider providerCapability = level.getCapability(RegistryReference.KHROMA_PROVIDER_BLOCK, neighborPos, neighbor, level.getBlockEntity(neighborPos), reverseSide);
		if (providerCapability != null && providerCapability.canProvide())
			return true;
		IKhromaConsumer consumerCapability = level.getCapability(RegistryReference.KHROMA_CONSUMER_BLOCK, neighborPos, neighbor, level.getBlockEntity(neighborPos), reverseSide);
		if (consumerCapability != null && consumerCapability.canConsume())
			return true;

		return false;
	}

	private boolean hasDirectionsChanged(BlockState state, BlockState oldState) {
		if (!state.is(this) || !oldState.is(this))
			return true;
		for (var property : PROPERTY_BY_DIRECTION.values()) {
			if (state.getValue(property) != oldState.getValue(property))
				return true;
		}

		return false;
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		BooleanProperty sideProperty = PROPERTY_BY_DIRECTION.get(direction);

		boolean connected = state.getValue(sideProperty);
		if (connected && level instanceof Level && !canSideConnect((Level) level, state, pos, direction))
			return state.setValue(sideProperty, false);
		if (neighborState.is(this) && neighborState.getValue(PROPERTY_BY_DIRECTION.get(direction.getOpposite())))
			return state.setValue(sideProperty, true);
		return state;
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!level.isClientSide && hasDirectionsChanged(state, oldState)) {
			dirtyNetwork(state, level, pos, oldState);
			SurgeofKhroma.LOGGER.info("onplace " + pos.toString());
		}
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (!level.isClientSide) {
			dirtyNetwork(newState, level, pos, state);
		}
	}

	private void dirtyNetwork(BlockState newState, Level level, BlockPos pos, BlockState oldState) {
		// If we only change the Khroma, don't update
		if (oldState.is(this) && newState.is(this) && oldState.setValue(KHROMA, newState.getValue(KHROMA)).equals(newState))
			return;

		KhromaNetwork network = KhromaNetwork.findNetwork(level, new BlockDirection(pos, null));
		if (network != null)
			network.markDirty();
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (!level.isClientSide)
			SurgeofKhroma.LOGGER.info("placedby " + pos.toString());
	}

	public Collection<BlockDirection> getAllConnections(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		if (!state.is(this))
			return null;

		List<BlockDirection> result = new ArrayList<BlockDirection>(6);

		for (var dirPropertyEntry : PROPERTY_BY_DIRECTION.entrySet()) {
			if (state.getValue(dirPropertyEntry.getValue())) {
				result.add(new BlockDirection(pos.relative(dirPropertyEntry.getKey()), dirPropertyEntry.getKey().getOpposite()));
			}
		}

		return result;
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		Khroma khroma = KhromaNetwork.getKhromaAtPos(level, pos);
		if (state.getValue(KHROMA) != khroma.asInt())
			level.setBlockAndUpdate(pos, state.setValue(KHROMA, khroma.asInt()));
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!player.getMainHandItem().isEmpty())
			return InteractionResult.PASS;
		var network = KhromaNetwork.findNetwork(level, new BlockDirection(pos, null));
		if (network != null)
			network.debugNetwork();
		return InteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
		return !state.getValue(WATERLOGGED);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
		return false;
	}
}
