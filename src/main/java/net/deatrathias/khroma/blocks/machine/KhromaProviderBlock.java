package net.deatrathias.khroma.blocks.machine;

import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.blocks.BaseKhromaUserBlock;
import net.deatrathias.khroma.khroma.IKhromaProvider;
import net.deatrathias.khroma.khroma.IKhromaProvidingBlock;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.khroma.KhromaProviderImpl;
import net.deatrathias.khroma.khroma.KhromaThroughput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class KhromaProviderBlock extends BaseKhromaUserBlock implements IKhromaProvidingBlock {
	private static final Khroma[] CYCLE = new Khroma[] { Khroma.get(true, true, false, false, false), Khroma.get(true, false, true, false, false), Khroma.get(true, false, false, true, false),
			Khroma.get(true, false, false, false, true), Khroma.get(false, true, true, false, false), Khroma.get(false, true, false, true, false), Khroma.get(false, true, false, false, true),
			Khroma.get(false, false, true, true, false), Khroma.get(false, false, true, false, true), Khroma.get(false, false, false, true, true) };

	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

	public static final IntegerProperty COLOR = IntegerProperty.create("color", 0, CYCLE.length - 1);

	public static final MapCodec<KhromaProviderBlock> CODEC = simpleCodec(KhromaProviderBlock::new);

	@Override
	protected MapCodec<KhromaProviderBlock> codec() {
		return CODEC;
	}

	public KhromaProviderBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(COLOR, 0));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING).add(COLOR);
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (state.getBlock() != oldState.getBlock() || state.getValue(FACING) != oldState.getValue(FACING))
			super.onPlace(state, level, pos, oldState, movedByPiston);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide())
			level.setBlock(pos, state.cycle(COLOR), UPDATE_CLIENTS | UPDATE_KNOWN_SHAPE);
		return InteractionResult.SUCCESS;
	}

	@Override
	public ConnectionType khromaConnection(BlockState state, Direction direction) {
		return state.getValue(FACING) == direction ? ConnectionType.PROVIDER : ConnectionType.NONE;
	}

	@Override
	public IKhromaProvider getProvider(Level level, BlockPos pos, BlockState state, Direction face, KhromaNetwork network) {
		if (face == state.getValue(FACING))
			return new IKhromaProvider() {
				@Override
				public KhromaThroughput provides() {
					return new KhromaThroughput(CYCLE[level.getBlockState(pos).getValue(COLOR)], 100f);
				}

				@Override
				public boolean isRelay() {
					return false;
				}

				@Override
				public boolean canProvide() {
					return true;
				}
			};
		return KhromaProviderImpl.disabled;
	}
}
