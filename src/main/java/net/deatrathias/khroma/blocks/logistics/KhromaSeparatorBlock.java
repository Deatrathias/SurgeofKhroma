package net.deatrathias.khroma.blocks.logistics;

import java.util.function.Function;

import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.khroma.IKhromaConsumer;
import net.deatrathias.khroma.khroma.IKhromaProvider;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.khroma.KhromaConsumerImpl;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.khroma.KhromaProviderImpl;
import net.deatrathias.khroma.khroma.KhromaThroughput;
import net.deatrathias.khroma.util.BlockDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KhromaSeparatorBlock extends BaseKhromaRelayBlock {

	public KhromaSeparatorBlock(Properties properties) {
		super(properties);
	}

	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

	public static final MapCodec<KhromaSeparatorBlock> CODEC = simpleCodec(KhromaSeparatorBlock::new);

	@Override
	protected MapCodec<KhromaSeparatorBlock> codec() {
		return CODEC;
	}

	private static class SeparatorProvider implements IKhromaProvider {

		private int index;

		private Level level;

		private BlockPos blockPos;

		public SeparatorProvider(int index, Level level, BlockPos blockPos) {
			this.index = index;
			this.level = level;
			this.blockPos = blockPos;
		}

		@Override
		public KhromaThroughput provides() {
			Direction facing = level.getBlockState(blockPos).getValue(KhromaSeparatorBlock.FACING);
			BlockDirection consumerPos = new BlockDirection(blockPos, facing.getOpposite());
			KhromaNetwork networkFromConsumer = KhromaNetwork.findNetwork(level, consumerPos);

			if (networkFromConsumer != null && !networkFromConsumer.isUpdatedThisTick()) {
				KhromaNetwork.setToUpdateNext(networkFromConsumer);
				return null;
			}

			KhromaThroughput throughput = networkFromConsumer != null ? networkFromConsumer.getKhromaThroughput() : KhromaThroughput.empty;

			KhromaThroughput[] separated = KhromaThroughput.separate(throughput);

			float request = 0;
			if (networkFromConsumer != null) {
				var consumer = networkFromConsumer.consumerAt(consumerPos, true);
				if (consumer.isPresent())
					request = ((SeparatorConsumer) consumer.get()).request;
			}

			return separated[index].multiply(request);
		}

		@Override
		public boolean canProvide() {
			return true;
		}

		@Override
		public boolean isRelay() {
			return true;
		}
	}

	private static class SeparatorConsumer implements IKhromaConsumer {

		private Level level;

		private BlockPos blockPos;

		private KhromaNetwork network;

		private float request;

		public SeparatorConsumer(Level level, BlockPos blockPos, KhromaNetwork network) {
			this.level = level;
			this.blockPos = blockPos;
			this.network = network;
			request = 0;
		}

		@Override
		public float request() {
			request = 0;
			Direction facing = level.getBlockState(blockPos).getValue(KhromaSeparatorBlock.FACING);
			BlockDirection provider0Pos = new BlockDirection(blockPos, facing.getClockWise());
			BlockDirection provider1Pos = new BlockDirection(blockPos, facing.getCounterClockWise());
			KhromaNetwork networkFromProvider0 = KhromaNetwork.findNetwork(level, provider0Pos);
			KhromaNetwork networkFromProvider1 = KhromaNetwork.findNetwork(level, provider1Pos);

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

			if (network == null)
				return 0;
			Khroma khroma = network.getKhroma();
			if (khroma == Khroma.KHROMA_EMPTY)
				return 0;

			int colors = khroma.countColors();

			if (colors <= 1) {
				request = networkFromProvider0.getRequest();
				return request;
			}

			int half = Math.ceilDiv(colors, 2);

			request = Math.max(networkFromProvider0.getRequest() * colors / (float) half, networkFromProvider1.getRequest() * colors / (float) (colors - half));
			return request;
		}

		@Override
		public boolean canConsume() {
			return true;
		}

		@Override
		public boolean isRelay() {
			return true;
		}

	}

	@Override
	protected EnumProperty<Direction> getFacingProperty() {
		return FACING;
	}

	@Override
	protected Function<BlockState, VoxelShape> makeShapes() {
		var map = Shapes.rotateHorizontal(Shapes.or(Block.box(0, 6, 6, 16, 10, 10), Block.box(4, 4, 11, 12, 12, 16)));
		return getShapeForEachState(state -> map.get(state.getValue(FACING)));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
	}

	@Override
	public IKhromaConsumer getConsumer(Level level, BlockPos pos, BlockState state, Direction face, KhromaNetwork network) {
		if (face == state.getValue(KhromaSeparatorBlock.FACING).getOpposite())
			return new SeparatorConsumer(level, pos, network);
		return KhromaConsumerImpl.disabled;
	}

	@Override
	public IKhromaProvider getProvider(Level level, BlockPos pos, BlockState state, Direction face, KhromaNetwork network) {
		Direction facing = state.getValue(KhromaSeparatorBlock.FACING);
		if (face == facing.getClockWise()) {
			return new SeparatorProvider(0, level, pos);
		} else if (face == facing.getCounterClockWise()) {
			return new SeparatorProvider(1, level, pos);
		}
		return KhromaProviderImpl.disabled;
	}

	@Override
	public ConnectionType khromaConnection(BlockState state, Direction direction) {
		Direction facing = state.getValue(KhromaCombinerBlock.FACING);
		if (facing.getOpposite() == direction)
			return ConnectionType.CONSUMER;
		else if (facing.getClockWise() == direction || facing.getCounterClockWise() == direction)
			return ConnectionType.PROVIDER;

		return ConnectionType.NONE;
	}

}
