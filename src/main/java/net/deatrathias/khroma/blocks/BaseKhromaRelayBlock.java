package net.deatrathias.khroma.blocks;

import java.util.function.Function;

import net.deatrathias.khroma.khroma.IKhromaConsumingBlock;
import net.deatrathias.khroma.khroma.IKhromaProvidingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseKhromaRelayBlock extends BaseKhromaUserBlock implements SimpleWaterloggedBlock, IKhromaConsumingBlock, IKhromaProvidingBlock {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	private final Function<BlockState, VoxelShape> shapes;

	protected BaseKhromaRelayBlock(Properties properties) {
		super(properties);
		shapes = makeShapes();
		registerDefaultState(stateDefinition.any().setValue(getFacingProperty(), Direction.SOUTH).setValue(WATERLOGGED, false));
	}

	protected abstract EnumProperty<Direction> getFacingProperty();

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(getFacingProperty()).add(WATERLOGGED);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return shapes.apply(state);
	}

	protected abstract Function<BlockState, VoxelShape> makeShapes();

	@Override
	protected boolean propagatesSkylightDown(BlockState state) {
		return !state.getValue(WATERLOGGED);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState().setValue(getFacingProperty(), context.getClickedFace()).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
	}

	@Override
	public BlockState rotate(BlockState state, LevelAccessor level, BlockPos pos, Rotation direction) {
		return state.setValue(getFacingProperty(), direction.rotate(state.getValue(getFacingProperty())));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(getFacingProperty())));
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
		return false;
	}
}
