package net.deatrathias.khroma.blocks.machine;

import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.blocks.BaseKhromaUserBlock;
import net.deatrathias.khroma.khroma.IKhromaConsumer;
import net.deatrathias.khroma.khroma.IKhromaConsumingBlock;
import net.deatrathias.khroma.khroma.KhromaConsumerImpl;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.util.BlockDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

public class KhromaMachineBlock extends BaseKhromaUserBlock implements IKhromaConsumingBlock {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

	public static final MapCodec<KhromaMachineBlock> CODEC = simpleCodec(KhromaMachineBlock::new);

	@Override
	protected MapCodec<KhromaMachineBlock> codec() {
		return CODEC;
	}

	public KhromaMachineBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide()) {
			KhromaNetwork network = KhromaNetwork.findNetwork(level, new BlockDirection(pos, state.getValue(FACING)));
			Component component = Component.translatable(network.getKhroma().getLocalizedName());
			SurgeofKhroma.LOGGER.debug("consumed: " + network.getKhromaRatio() + " " + component.getString());
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public IKhromaConsumer getConsumer(Level level, BlockPos pos, BlockState state, Direction face, KhromaNetwork network) {
		return state.getValue(FACING) == face ? new KhromaConsumerImpl(true, 1, false) : KhromaConsumerImpl.disabled;
	}

	@Override
	public ConnectionType khromaConnection(BlockState state, Direction direction) {
		if (direction == state.getValue(FACING))
			return ConnectionType.CONSUMER;
		return ConnectionType.NONE;
	}

}
