package net.deatrathias.khroma.blocks.machine.modular;

import java.util.function.Supplier;

import net.deatrathias.khroma.processing.BaseProcess;
import net.deatrathias.khroma.processing.ProcessType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ProcessControllerBlock extends Block {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

	protected final Supplier<ProcessType<?>> processTypeHolder;

	public ProcessControllerBlock(Supplier<ProcessType<?>> processTypeHolder, Properties properties) {
		super(properties);
		this.processTypeHolder = processTypeHolder;
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
	}

	public ProcessType<?> getProcessType() {
		return processTypeHolder.get();
	}

	public BaseProcess instantiateProcess() {
		return processTypeHolder.get().create();
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@SuppressWarnings("deprecation")
	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}
