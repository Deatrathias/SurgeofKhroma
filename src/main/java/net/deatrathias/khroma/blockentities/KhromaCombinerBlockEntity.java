package net.deatrathias.khroma.blockentities;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.blocks.KhromaCombinerBlock;
import net.deatrathias.khroma.khroma.IKhromaConsumer;
import net.deatrathias.khroma.khroma.IKhromaConsumerBlock;
import net.deatrathias.khroma.khroma.IKhromaProvider;
import net.deatrathias.khroma.khroma.IKhromaProviderBlock;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.khroma.KhromaConsumerImpl;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.khroma.KhromaProviderImpl;
import net.deatrathias.khroma.khroma.KhromaThroughput;
import net.deatrathias.khroma.util.BlockDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class KhromaCombinerBlockEntity extends BaseKhromaUserBlockEntity implements IKhromaProviderBlock, IKhromaConsumerBlock {

	private final KhromaProviderImpl output = new KhromaProviderImpl(true, () -> provides(), true);

	private final KhromaConsumerImpl input0 = new KhromaConsumerImpl(true, (throughput, simulate) -> consumes(throughput, simulate, 0), true);

	private final KhromaConsumerImpl input1 = new KhromaConsumerImpl(true, (throughput, simulate) -> consumes(throughput, simulate, 1), true);

	private float request0;

	private float request1;

	public KhromaCombinerBlockEntity(BlockPos pos, BlockState blockState) {
		super(RegistryReference.BLOCK_ENTITY_KHROMA_COMBINER.get(), pos, blockState);
	}

	@Override
	public IKhromaConsumer getConsumer(Level level, BlockPos pos, BlockState state, Direction face) {
		Direction facing = state.getValue(KhromaCombinerBlock.FACING);

		if (face == facing.getClockWise()) {
			return input0;
		} else if (face == facing.getCounterClockWise()) {
			return input1;
		}

		return KhromaConsumerImpl.disabled;
	}

	@Override
	public IKhromaProvider getProvider(Level level, BlockPos pos, BlockState state, Direction face) {
		Direction facing = state.getValue(KhromaCombinerBlock.FACING);
		if (face == facing) {
			return output;
		}

		return KhromaProviderImpl.disabled;
	}

	private float consumes(KhromaThroughput throughput, Boolean simulate, int inputIndex) {
		Direction facing = getBlockState().getValue(KhromaCombinerBlock.FACING);
		KhromaNetwork networkFromProvider = KhromaNetwork.findNetwork(level, new BlockDirection(worldPosition, facing));

		if (networkFromProvider == null)
			return 0;

		if (!networkFromProvider.isRequestCalculatedThisTick()) {
			KhromaNetwork.setToUpdateNext(networkFromProvider);
			return -1;
		}

		KhromaNetwork networkFromConsumer0 = KhromaNetwork.findNetwork(level, new BlockDirection(worldPosition, facing.getClockWise()));
		KhromaNetwork networkFromConsumer1 = KhromaNetwork.findNetwork(level, new BlockDirection(worldPosition, facing.getCounterClockWise()));

		Khroma khroma0 = networkFromConsumer0 == null ? Khroma.KHROMA_EMPTY : networkFromConsumer0.getKhroma();
		Khroma khroma1 = networkFromConsumer1 == null ? Khroma.KHROMA_EMPTY : networkFromConsumer1.getKhroma();

		if (khroma0 == Khroma.KHROMA_EMPTY) {
			if (inputIndex == 0) {
				request0 = 0;
				return 0;
			} else {
				request1 = 1;
				return networkFromProvider.getRequest();
			}
		} else if (khroma1 == Khroma.KHROMA_EMPTY) {
			if (inputIndex == 1) {
				request1 = 0;
				return 0;
			} else {
				request0 = 1;
				return networkFromProvider.getRequest();
			}
		}

		Khroma combined = Khroma.combine(khroma0, khroma1);
		int count = inputIndex == 0 ? khroma0.countColors() : khroma1.countColors();
		int colors = Math.max(2, combined.countColors());
		float result = count * networkFromProvider.getRequest() / colors;
		if (inputIndex == 0)
			request0 = (float) count / colors;
		else
			request1 = (float) count / colors;

		return result;
	}

	private KhromaThroughput provides() {
		Direction facing = getBlockState().getValue(KhromaCombinerBlock.FACING);
		KhromaNetwork networkFromConsumer0 = KhromaNetwork.findNetwork(level, new BlockDirection(worldPosition, facing.getClockWise()));
		KhromaNetwork networkFromConsumer1 = KhromaNetwork.findNetwork(level, new BlockDirection(worldPosition, facing.getCounterClockWise()));

		if (networkFromConsumer0 != null && !networkFromConsumer0.isUpdatedThisTick()) {
			KhromaNetwork.setToUpdateNext(networkFromConsumer0);
			return null;
		}

		if (networkFromConsumer1 != null && !networkFromConsumer1.isUpdatedThisTick()) {
			KhromaNetwork.setToUpdateNext(networkFromConsumer1);
			return null;
		}

		KhromaThroughput throughput0 = networkFromConsumer0 != null ? networkFromConsumer0.getKhromaThroughput().multiply(request0) : KhromaThroughput.empty;
		KhromaThroughput throughput1 = networkFromConsumer1 != null ? networkFromConsumer1.getKhromaThroughput().multiply(request1) : KhromaThroughput.empty;

		KhromaNetwork networkFromProvider = KhromaNetwork.findNetwork(level, new BlockDirection(worldPosition, facing));

		float request = networkFromProvider.getRequest();
		return KhromaThroughput.combine(throughput0, throughput1).multiply(request);
	}

	@Override
	public ConnectionType khromaConnection(Direction direction) {
		Direction facing = getBlockState().getValue(KhromaCombinerBlock.FACING);
		if (facing == direction)
			return ConnectionType.PROVIDER;
		else if (facing.getClockWise() == direction || facing.getCounterClockWise() == direction)
			return ConnectionType.CONSUMER;

		return ConnectionType.NONE;
	}
}
