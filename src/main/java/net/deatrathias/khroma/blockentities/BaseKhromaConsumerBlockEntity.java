package net.deatrathias.khroma.blockentities;

import net.deatrathias.khroma.khroma.IKhromaConsumer;
import net.deatrathias.khroma.khroma.IKhromaConsumerBlock;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.khroma.KhromaConsumerImpl;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.khroma.KhromaThroughput;
import net.deatrathias.khroma.util.BlockDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseKhromaConsumerBlockEntity extends BaseKhromaUserBlockEntity implements IKhromaConsumerBlock {

	protected KhromaConsumerImpl consumer = new KhromaConsumerImpl(true, (t, s) -> consumes(t, s), false);

	public BaseKhromaConsumerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
		super(type, pos, blockState);
	}

	@Override
	public IKhromaConsumer getConsumer(Level level, BlockPos pos, BlockState state, Direction face) {
		if (khromaConnection(face) == ConnectionType.CONSUMER)
			return consumer;
		return KhromaConsumerImpl.disabled;
	}

	public float consumes(KhromaThroughput throughput, boolean simulate) {
		return 1f;
	}

	public KhromaThroughput requestOnSide(Direction side) {
		KhromaNetwork network = KhromaNetwork.findNetwork(level, new BlockDirection(worldPosition, side));
		if (network != null) {
			network.request();
			return network.getKhromaThroughput();
		}

		return KhromaThroughput.empty;
	}

	public Khroma getKhromaOnSide(Direction side) {
		KhromaNetwork network = KhromaNetwork.findNetwork(level, new BlockDirection(worldPosition, side));
		if (network != null)
			return network.getKhroma();

		return Khroma.empty();
	}

	public abstract float getSoftLimit();

	protected abstract void tick();

	public static void serverTick(Level level, BlockPos pos, BlockState state, BaseKhromaConsumerBlockEntity blockEntity) {
		blockEntity.tick();
	}
}
