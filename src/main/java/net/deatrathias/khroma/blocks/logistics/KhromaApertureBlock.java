package net.deatrathias.khroma.blocks.logistics;

import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.blockentities.KhromaApertureBlockEntity;
import net.deatrathias.khroma.khroma.IKhromaConsumer;
import net.deatrathias.khroma.khroma.IKhromaProvider;
import net.deatrathias.khroma.khroma.IKhromaUsingBlock;
import net.deatrathias.khroma.khroma.KhromaConsumerImpl;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.khroma.KhromaProviderImpl;
import net.deatrathias.khroma.khroma.KhromaThroughput;
import net.deatrathias.khroma.util.BlockDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KhromaApertureBlock extends BaseKhromaRelayBlock implements EntityBlock {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

	public static final MapCodec<KhromaApertureBlock> CODEC = simpleCodec(KhromaApertureBlock::new);

	@Override
	protected MapCodec<KhromaApertureBlock> codec() {
		return CODEC;
	}

	private static class ApertureProvider implements IKhromaProvider {

		private KhromaApertureBlockEntity entity;

		public ApertureProvider(KhromaApertureBlockEntity entity) {
			this.entity = entity;
		}

		@Override
		public KhromaThroughput provides() {
			Level level = entity.getLevel();
			BlockPos pos = entity.getBlockPos();
			if (entity.getLimit() == 0 || level.hasNeighborSignal(pos))
				return KhromaThroughput.empty;

			Direction facing = entity.getBlockState().getValue(KhromaApertureBlock.FACING);

			KhromaNetwork networkFromConsumer = KhromaNetwork.findNetwork(level, new BlockDirection(pos, facing.getOpposite()));
			if (networkFromConsumer == null)
				return KhromaThroughput.empty;

			if (!networkFromConsumer.isUpdatedThisTick()) {
				KhromaNetwork.setToUpdateNext(networkFromConsumer);
				return null;
			}

			KhromaNetwork networkFromProvider = KhromaNetwork.findNetwork(level, new BlockDirection(pos, facing));

			float request = networkFromProvider.getRequest();
			return new KhromaThroughput(networkFromConsumer.getKhroma(), networkFromConsumer.getKhromaRatio() * request * entity.getLimit());
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

	private static class ApertureConsumer implements IKhromaConsumer {

		private KhromaApertureBlockEntity entity;

		public ApertureConsumer(KhromaApertureBlockEntity entity) {
			this.entity = entity;
		}

		@Override
		public float request() {
			KhromaNetwork networkFromProvider = KhromaNetwork.findNetwork(entity.getLevel(), new BlockDirection(entity.getBlockPos(), entity.getBlockState().getValue(KhromaApertureBlock.FACING)));

			if (networkFromProvider == null)
				return 0;

			if (!networkFromProvider.isRequestCalculatedThisTick()) {
				KhromaNetwork.setToUpdateNext(networkFromProvider);
				return -1;
			}

			return entity.getLevel().hasNeighborSignal(entity.getBlockPos()) ? 0 : entity.getLimit() * networkFromProvider.getRequest();
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

	public KhromaApertureBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected EnumProperty<Direction> getFacingProperty() {
		return FACING;
	}

	@Override
	protected Function<BlockState, VoxelShape> makeShapes() {
		var map = Shapes.rotateAll(Shapes.or(Block.box(5, 5, 7, 11, 11, 16), Block.box(6, 6, 0, 10, 10, 7)));
		return getShapeForEachState(state -> map.get(state.getValue(FACING)));
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
		return true;
	}

	@Override
	protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
		super.triggerEvent(state, level, pos, id, param);
		BlockEntity blockentity = level.getBlockEntity(pos);
		return blockentity == null ? false : blockentity.triggerEvent(id, param);
	}

	@Nullable
	@Override
	protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
		BlockEntity blockentity = level.getBlockEntity(pos);
		return blockentity instanceof MenuProvider ? (MenuProvider) blockentity : null;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide()) {
			player.openMenu(state.getMenuProvider(level, pos));
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KhromaApertureBlockEntity(pos, state);
	}

	@Override
	public IKhromaConsumer getConsumer(Level level, BlockPos pos, BlockState state, Direction face, KhromaNetwork network) {
		if (state.getValue(FACING).getOpposite() == face)
			return new ApertureConsumer((KhromaApertureBlockEntity) level.getBlockEntity(pos));
		return KhromaConsumerImpl.disabled;
	}

	@Override
	public IKhromaProvider getProvider(Level level, BlockPos pos, BlockState state, Direction face, KhromaNetwork network) {
		if (state.getValue(FACING) == face)
			return new ApertureProvider((KhromaApertureBlockEntity) level.getBlockEntity(pos));
		return KhromaProviderImpl.disabled;
	}

	@Override
	public ConnectionType khromaConnection(BlockState state, Direction direction) {
		Direction facing = state.getValue(KhromaApertureBlock.FACING);
		if (facing == direction)
			return IKhromaUsingBlock.ConnectionType.PROVIDER;
		if (facing == direction.getOpposite())
			return IKhromaUsingBlock.ConnectionType.CONSUMER;
		return IKhromaUsingBlock.ConnectionType.NONE;
	}
}
