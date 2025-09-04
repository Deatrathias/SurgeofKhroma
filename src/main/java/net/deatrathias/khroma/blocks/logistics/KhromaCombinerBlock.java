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

public class KhromaCombinerBlock extends BaseKhromaRelayBlock {

	public KhromaCombinerBlock(Properties properties) {
		super(properties);
	}

	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

	public static final MapCodec<KhromaCombinerBlock> CODEC = simpleCodec(KhromaCombinerBlock::new);

	@Override
	protected MapCodec<KhromaCombinerBlock> codec() {
		return CODEC;
	}

	private static class CombinerProvider implements IKhromaProvider {

		private Level level;

		private BlockPos blockPos;

		public CombinerProvider(Level level, BlockPos blockPos) {
			this.level = level;
			this.blockPos = blockPos;
		}

		@Override
		public KhromaThroughput provides() {
			Direction facing = level.getBlockState(blockPos).getValue(KhromaCombinerBlock.FACING);
			BlockDirection consumer0Pos = new BlockDirection(blockPos, facing.getClockWise());
			BlockDirection consumer1Pos = new BlockDirection(blockPos, facing.getCounterClockWise());
			KhromaNetwork networkFromConsumer0 = KhromaNetwork.findNetwork(level, consumer0Pos);
			KhromaNetwork networkFromConsumer1 = KhromaNetwork.findNetwork(level, consumer1Pos);

			if (networkFromConsumer0 != null && !networkFromConsumer0.isUpdatedThisTick()) {
				KhromaNetwork.setToUpdateNext(networkFromConsumer0);
				return null;
			}

			if (networkFromConsumer1 != null && !networkFromConsumer1.isUpdatedThisTick()) {
				KhromaNetwork.setToUpdateNext(networkFromConsumer1);
				return null;
			}

			float request0 = 0;
			if (networkFromConsumer0 != null) {
				var consumer = networkFromConsumer0.consumerAt(consumer0Pos, true);
				if (consumer.isPresent())
					request0 = ((CombinerConsumer) consumer.get()).request;
			}

			float request1 = 0;
			if (networkFromConsumer1 != null) {
				var consumer = networkFromConsumer1.consumerAt(consumer1Pos, true);
				if (consumer.isPresent())
					request1 = ((CombinerConsumer) consumer.get()).request;
			}

			KhromaThroughput throughput0 = networkFromConsumer0 != null ? networkFromConsumer0.getKhromaThroughput().multiply(request0) : KhromaThroughput.empty;
			KhromaThroughput throughput1 = networkFromConsumer1 != null ? networkFromConsumer1.getKhromaThroughput().multiply(request1) : KhromaThroughput.empty;

			KhromaNetwork networkFromProvider = KhromaNetwork.findNetwork(level, new BlockDirection(blockPos, facing));

			float request = networkFromProvider.getRequest();
			return KhromaThroughput.combine(throughput0, throughput1).multiply(request);
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

	private static class CombinerConsumer implements IKhromaConsumer {
		private float request;

		private int index;

		private Level level;

		private BlockPos blockPos;

		public CombinerConsumer(int index, Level level, BlockPos blockPos) {
			this.index = index;
			this.level = level;
			this.blockPos = blockPos;
		}

		@Override
		public float request() {
			Direction facing = level.getBlockState(blockPos).getValue(KhromaCombinerBlock.FACING);
			KhromaNetwork networkFromProvider = KhromaNetwork.findNetwork(level, new BlockDirection(blockPos, facing));

			if (networkFromProvider == null)
				return 0;

			if (!networkFromProvider.isRequestCalculatedThisTick()) {
				KhromaNetwork.setToUpdateNext(networkFromProvider);
				return -1;
			}

			KhromaNetwork networkFromConsumer0 = KhromaNetwork.findNetwork(level, new BlockDirection(blockPos, facing.getClockWise()));
			KhromaNetwork networkFromConsumer1 = KhromaNetwork.findNetwork(level, new BlockDirection(blockPos, facing.getCounterClockWise()));

			Khroma khroma0 = networkFromConsumer0 == null ? Khroma.EMPTY : networkFromConsumer0.getKhroma();
			Khroma khroma1 = networkFromConsumer1 == null ? Khroma.EMPTY : networkFromConsumer1.getKhroma();

			if (khroma0 == Khroma.EMPTY) {
				if (index == 0) {
					request = 0;
					return 0;
				} else {
					return networkFromProvider.getRequest();
				}
			} else if (khroma1 == Khroma.EMPTY) {
				if (index == 1) {
					request = 0;
					return 0;
				} else {
					return networkFromProvider.getRequest();
				}
			}

			Khroma combined = Khroma.combine(khroma0, khroma1);
			int count = index == 0 ? khroma0.countColors() : khroma1.countColors();
			int colors = Math.max(2, combined.countColors());
			float result = count * networkFromProvider.getRequest() / colors;
			request = (float) count / colors;

			return result;
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
		var map = Shapes.rotateHorizontal(Shapes.or(Block.box(0, 4, 4, 16, 12, 12), Block.box(6, 6, 0, 10, 10, 4)));
		return getShapeForEachState(state -> map.get(state.getValue(FACING)));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
	}

	// FIXME
	@Override
	public IKhromaConsumer getConsumer(Level level, BlockPos pos, BlockState state, Direction face, KhromaNetwork network) {
		Direction facing = state.getValue(KhromaCombinerBlock.FACING);
		if (face == facing.getClockWise()) {
			return new CombinerConsumer(0, level, pos);
		} else if (face == facing.getCounterClockWise()) {
			return new CombinerConsumer(1, level, pos);
		}

		return KhromaConsumerImpl.disabled;
	}

	@Override
	public IKhromaProvider getProvider(Level level, BlockPos pos, BlockState state, Direction face, KhromaNetwork network) {
		Direction facing = state.getValue(KhromaCombinerBlock.FACING);
		if (face == facing) {
			return new CombinerProvider(level, pos);
		}

		return KhromaProviderImpl.disabled;
	}

	@Override
	public ConnectionType khromaConnection(BlockState state, Direction direction) {
		Direction facing = state.getValue(KhromaCombinerBlock.FACING);
		if (facing == direction)
			return ConnectionType.PROVIDER;
		else if (facing.getClockWise() == direction || facing.getCounterClockWise() == direction)
			return ConnectionType.CONSUMER;

		return ConnectionType.NONE;
	}
}
