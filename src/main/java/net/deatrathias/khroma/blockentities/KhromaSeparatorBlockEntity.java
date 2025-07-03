package net.deatrathias.khroma.blockentities;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.blocks.KhromaCombinerBlock;
import net.deatrathias.khroma.blocks.KhromaSeparatorBlock;
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

public class KhromaSeparatorBlockEntity extends BaseKhromaUserBlockEntity implements IKhromaProviderBlock, IKhromaConsumerBlock {

	private final KhromaProviderImpl output0 = new KhromaProviderImpl(true, () -> provides(0), true);

	private final KhromaProviderImpl output1 = new KhromaProviderImpl(true, () -> provides(1), true);

	private final KhromaConsumerImpl input = new KhromaConsumerImpl(true, (throughput, simulate) -> consumes(throughput, simulate), true);

	private float request;

	public KhromaSeparatorBlockEntity(BlockPos pos, BlockState blockState) {
		super(RegistryReference.BLOCK_ENTITY_KHROMA_SEPARATOR.get(), pos, blockState);
	}

	@Override
	public IKhromaConsumer getConsumer(Level level, BlockPos pos, BlockState state, Direction face) {
		Direction facing = state.getValue(KhromaSeparatorBlock.FACING);
		if (face == facing.getOpposite()) {
			return input;
		}

		return KhromaConsumerImpl.disabled;
	}

	@Override
	public IKhromaProvider getProvider(Level level, BlockPos pos, BlockState state, Direction face) {
		Direction facing = state.getValue(KhromaSeparatorBlock.FACING);
		if (face == facing.getClockWise()) {
			return output0;
		} else if (face == facing.getCounterClockWise()) {
			return output1;
		}
		return KhromaProviderImpl.disabled;
	}

	private float consumes(KhromaThroughput throughput, Boolean simulate) {
		Direction facing = getBlockState().getValue(KhromaSeparatorBlock.FACING);
		KhromaNetwork networkFromProvider0 = KhromaNetwork.findNetwork(level, new BlockDirection(worldPosition, facing.getClockWise()));
		KhromaNetwork networkFromProvider1 = KhromaNetwork.findNetwork(level, new BlockDirection(worldPosition, facing.getCounterClockWise()));

		if (networkFromProvider0 == null || networkFromProvider1 == null)
			return 0;

		if (!networkFromProvider0.isRequestCalculatedThisTick()) {
			KhromaNetwork.setToUpdateNext(networkFromProvider0);
			return -1;
		}

		if (!networkFromProvider1.isRequestCalculatedThisTick()) {
			KhromaNetwork.setToUpdateNext(networkFromProvider1);
			return -1;
		}

		KhromaNetwork networkFromConsumer = KhromaNetwork.findNetwork(level, new BlockDirection(worldPosition, facing.getOpposite()));
		if (networkFromConsumer == null)
			return 0;
		Khroma khroma = networkFromConsumer.getKhroma();
		if (khroma == Khroma.empty())
			return 0;

		int colors = khroma.countColors();

		if (colors == 1) {
			request = networkFromProvider0.getRequest();
			return request;
		}

		request = Math.max(networkFromProvider0.getRequest() * colors, networkFromProvider1.getRequest() * colors / (float) (colors - 1));
		return request;
	}

	private KhromaThroughput provides(int outputIndex) {
		Direction facing = getBlockState().getValue(KhromaSeparatorBlock.FACING);
		KhromaNetwork networkFromConsumer = KhromaNetwork.findNetwork(level, new BlockDirection(worldPosition, facing.getOpposite()));

		if (networkFromConsumer != null && !networkFromConsumer.isUpdatedThisTick()) {
			KhromaNetwork.setToUpdateNext(networkFromConsumer);
			return null;
		}

		KhromaThroughput throughput = networkFromConsumer != null ? networkFromConsumer.getKhromaThroughput() : KhromaThroughput.empty;

		KhromaThroughput[] separated = KhromaThroughput.separate(throughput);

		// KhromaNetwork networkFromProvider = KhromaNetwork.findNetwork(level, new
		// BlockDirection(worldPosition, outputIndex == 0 ? facing.getClockWise() :
		// facing.getCounterClockWise()));

		// float request = networkFromProvider.getRequest();
		return separated[outputIndex].multiply(request);
	}

	@Override
	public ConnectionType khromaConnection(Direction direction) {
		Direction facing = getBlockState().getValue(KhromaCombinerBlock.FACING);
		if (facing.getOpposite() == direction)
			return ConnectionType.CONSUMER;
		else if (facing.getClockWise() == direction || facing.getCounterClockWise() == direction)
			return ConnectionType.PROVIDER;

		return ConnectionType.NONE;
	}
}
