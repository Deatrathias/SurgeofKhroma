package net.deatrathias.khroma.blocks.machine.modular;

import net.deatrathias.khroma.blocks.BaseKhromaUserBlock;
import net.deatrathias.khroma.khroma.IKhromaConsumer;
import net.deatrathias.khroma.khroma.IKhromaConsumingBlock;
import net.deatrathias.khroma.khroma.KhromaConsumerImpl;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.khroma.VariableConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class KhromaInjectorBlock extends BaseKhromaUserBlock implements IKhromaConsumingBlock {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

	protected KhromaInjectorBlock(Properties properties) {
		super(properties);
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

	@Override
	public ConnectionType khromaConnection(BlockState state, Direction direction) {
		return state.getValue(FACING) == direction ? ConnectionType.CONSUMER : ConnectionType.NONE;
	}

	@Override
	public IKhromaConsumer getConsumer(Level level, BlockPos pos, BlockState state, Direction face, KhromaNetwork network) {
		if (state.getValue(FACING) != face)
			return KhromaConsumerImpl.disabled;
		VariableConsumer consumer = new VariableConsumer();
		consumer.setNetwork(network);
		return consumer;
	}

}
