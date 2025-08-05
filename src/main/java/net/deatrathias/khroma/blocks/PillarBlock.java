package net.deatrathias.khroma.blocks;

import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PillarBlock extends Block implements SimpleWaterloggedBlock {
	public static final BooleanProperty TOP = BooleanProperty.create("top");
	public static final BooleanProperty BOTTOM = BlockStateProperties.BOTTOM;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	private final Function<BlockState, VoxelShape> shapes;

	public PillarBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(TOP, true).setValue(BOTTOM, true).setValue(WATERLOGGED, false));
		shapes = makeShapes();
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(TOP, BOTTOM, WATERLOGGED);
	}

	private Function<BlockState, VoxelShape> makeShapes() {
		VoxelShape inside = Block.column(10.0, 0.0, 16.0);
		return this.getShapeForEachState(state -> {
			VoxelShape result = inside;
			if (state.getValue(BOTTOM))
				result = Shapes.or(column(16.0, 0, 3.0), result);
			if (state.getValue(TOP))
				result = Shapes.or(column(16.0, 13.0, 16.0), result);

			return result;
		});
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return shapes.apply(state);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState,
			RandomSource random) {
		if (state.getValue(WATERLOGGED))
			scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

		BlockState result = state;
		if (direction == Direction.UP)
			result = result.setValue(TOP, !neighborState.is(this));
		else if (direction == Direction.DOWN)
			result = result.setValue(BOTTOM, !neighborState.is(this));

		return result;

	}

	@Override
	protected boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		BlockState state = defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
		if (context.getLevel().getBlockState(context.getClickedPos().above()).is(this))
			state = state.setValue(TOP, false);
		if (context.getLevel().getBlockState(context.getClickedPos().below()).is(this))
			state = state.setValue(BOTTOM, false);
		return state;
	}
}
