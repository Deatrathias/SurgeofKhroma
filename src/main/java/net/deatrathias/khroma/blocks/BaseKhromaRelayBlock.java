package net.deatrathias.khroma.blocks;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.blockentities.BaseKhromaUserBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
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

public abstract class BaseKhromaRelayBlock<T extends BaseKhromaUserBlockEntity> extends BaseKhromaUserBlock<T> implements EntityBlock, SimpleWaterloggedBlock {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	protected BaseKhromaRelayBlock(Properties properties) {
		super(properties);
		makeShapes();
		registerDefaultState(stateDefinition.any().setValue(getFacingProperty(), Direction.SOUTH).setValue(WATERLOGGED, false));
	}

	protected final VoxelShape[] shapesPerIndex = new VoxelShape[6];

	protected abstract EnumProperty<Direction> getFacingProperty();

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(getFacingProperty()).add(WATERLOGGED);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return shapesPerIndex[state.getValue(getFacingProperty()).get3DDataValue()];
	}

	protected abstract void makeShapes();

	@Override
	protected boolean propagatesSkylightDown(BlockState state) {
		return !state.getValue(WATERLOGGED);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public abstract T getBlockEntity(Level level, BlockPos pos);

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState().setValue(getFacingProperty(), context.getClickedFace()).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		Direction backFace = state.getValue(getFacingProperty()).getOpposite();
		BlockPos neighborPos = pos.relative(backFace);
		BlockState neighborState = level.getBlockState(neighborPos);
		if (neighborState.is(RegistryReference.BLOCK_KHROMA_LINE))
			level.setBlockAndUpdate(neighborPos, neighborState.setValue(KhromaLineBlock.PROPERTY_BY_DIRECTION.get(backFace.getOpposite()), true));
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
