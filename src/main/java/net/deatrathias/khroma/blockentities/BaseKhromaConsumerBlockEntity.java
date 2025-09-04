package net.deatrathias.khroma.blockentities;

import net.deatrathias.khroma.blocks.BaseKhromaUserBlock;
import net.deatrathias.khroma.blocks.IKhromaDevice;
import net.deatrathias.khroma.khroma.IKhromaConsumer;
import net.deatrathias.khroma.khroma.IKhromaUsingBlock.ConnectionType;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.khroma.KhromaConsumerImpl;
import net.deatrathias.khroma.khroma.KhromaDeviceTier;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.khroma.KhromaThroughput;
import net.deatrathias.khroma.khroma.VariableConsumer;
import net.deatrathias.khroma.util.BlockDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseKhromaConsumerBlockEntity extends BlockEntity {

	protected VariableConsumer consumer = new VariableConsumer();

	public BaseKhromaConsumerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	public IKhromaConsumer getConsumer(Level level, BlockPos pos, BlockState state, Direction face, KhromaNetwork network) {
		if (((BaseKhromaUserBlock) state.getBlock()).khromaConnection(state, face) == ConnectionType.CONSUMER) {
			consumer.setNetwork(network);
			return consumer;
		}
		return KhromaConsumerImpl.disabled;
	}

	public KhromaThroughput requestOnSide(Direction side) {
		return consumer.requestKhroma();
	}

	public Khroma getKhromaOnSide(Direction side) {
		KhromaNetwork network = KhromaNetwork.findNetwork(level, new BlockDirection(worldPosition, side));
		if (network != null)
			return network.getKhroma();

		return Khroma.EMPTY;
	}

	public float getSoftLimit() {
		Block block = level.getBlockState(worldPosition).getBlock();
		if (block instanceof IKhromaDevice device)
			return device.getTier().getSoftLimit();
		return KhromaDeviceTier.BASIC.getSoftLimit();
	}

	protected abstract void tick();

	public static void serverTick(Level level, BlockPos pos, BlockState state, BaseKhromaConsumerBlockEntity blockEntity) {
		blockEntity.tick();
	}
}
