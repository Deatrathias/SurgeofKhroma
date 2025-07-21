package net.deatrathias.khroma.blocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.items.SpannerItem;
import net.deatrathias.khroma.khroma.IKhromaUsingBlock.ConnectionType;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.khroma.KhromaProperty;
import net.deatrathias.khroma.util.BlockDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.extensions.IBlockExtension;

public class KhromaLineBlock extends PipeBlock implements SimpleWaterloggedBlock, IBlockExtension {

	public static final MapCodec<KhromaLineBlock> CODEC = simpleCodec(KhromaLineBlock::new);

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public static float PIPE_SIZE = 4f;

	public static float CENTER_SIZE = 6f;

	public static float MIN_SIDE = (16f - CENTER_SIZE) / 32f;

	public static float MAX_SIDE = 1f - MIN_SIDE;

	@Override
	protected MapCodec<KhromaLineBlock> codec() {
		return CODEC;
	}

	public static final KhromaProperty KHROMA = KhromaProperty.create("khroma");

	public KhromaLineBlock(Properties properties) {
		super(PIPE_SIZE, properties);

		registerDefaultState(stateDefinition
				.any()
				.setValue(KHROMA, Khroma.KHROMA_EMPTY)
				.setValue(NORTH, false)
				.setValue(SOUTH, false)
				.setValue(EAST, false)
				.setValue(WEST, false)
				.setValue(UP, false)
				.setValue(DOWN, false)
				.setValue(WATERLOGGED, false));
		shapes = getShapeForEachState(shapes.andThen(shape -> Shapes.or(shape, cube(CENTER_SIZE))));
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
		if (KhromaNetwork.getConnectionType(level, neighborPos, reverseSide) != ConnectionType.NONE)
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
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState,
			RandomSource random) {
		BooleanProperty sideProperty = PROPERTY_BY_DIRECTION.get(direction);

		boolean connected = state.getValue(sideProperty);
		if (connected && level instanceof Level && !canSideConnect((Level) level, state, pos, direction))
			return state.setValue(sideProperty, false);
		if (neighborState.is(this)) {
			if (neighborState.getValue(PROPERTY_BY_DIRECTION.get(direction.getOpposite())))
				return state.setValue(sideProperty, true);
			else
				return state.setValue(sideProperty, false);
		}
		return state;
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!level.isClientSide && hasDirectionsChanged(state, oldState)) {
			dirtyNetwork(state, level, pos, oldState);
		}
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
		if (!level.isClientSide) {
			dirtyNetwork(Blocks.AIR.defaultBlockState(), level, pos, state);
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
		if (state.getValue(KHROMA) != khroma)
			level.setBlock(pos, state.setValue(KHROMA, khroma), 18);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!player.getMainHandItem().isEmpty())
			return InteractionResult.PASS;
		var network = KhromaNetwork.findNetwork(level, new BlockDirection(pos, null));
		if (network != null)
			network.debugNetwork();
		return InteractionResult.PASS;
	}

	@Override
	public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation rotation) {
		BlockState newState = state;
		for (Direction dir : Direction.values()) {
			newState.setValue(PROPERTY_BY_DIRECTION.get(rotation.rotate(dir)), state.getValue(PROPERTY_BY_DIRECTION.get(dir)));
		}
		return newState;
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		switch (mirror) {
		case FRONT_BACK:
			return state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH));
		case LEFT_RIGHT:
			return state.setValue(WEST, state.getValue(EAST)).setValue(EAST, state.getValue(WEST));
		default:
			return state;
		}
	}

	@Override
	public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
		ItemStack itemStack = context.getItemInHand();
		if (!itemStack.canPerformAction(itemAbility))
			return null;

		if (itemAbility == SpannerItem.SPANNER_ADJUST) {
			BlockPos pos = context.getClickedPos();
			Vec3 hit = context.getClickLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
			Direction hitDirection;
			if (hit.x <= MIN_SIDE)
				hitDirection = Direction.WEST;
			else if (hit.x >= MAX_SIDE)
				hitDirection = Direction.EAST;
			else if (hit.y <= MIN_SIDE)
				hitDirection = Direction.DOWN;
			else if (hit.y >= MAX_SIDE)
				hitDirection = Direction.UP;
			else if (hit.z <= MIN_SIDE)
				hitDirection = Direction.NORTH;
			else if (hit.z >= MAX_SIDE)
				hitDirection = Direction.SOUTH;
			else
				return null;

			return connect(context.getLevel(), state, pos, hitDirection);
		}

		return null;
	}

	public static @Nullable BlockState connect(Level level, BlockState state, BlockPos pos, Direction direction) {
		var property = PROPERTY_BY_DIRECTION.get(direction);

		if (state.getValue(property))
			return state.setValue(property, false);

		if (canSideConnect(level, state, pos, direction))
			return state.setValue(property, true);

		return null;
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state) {
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
