package net.deatrathias.khroma.blocks;

import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.blockentities.KhromaImbuerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public class KhromaImbuerBlock extends BaseKhromaUserBlock<KhromaImbuerBlockEntity> {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public static final MapCodec<KhromaImbuerBlock> CODEC = simpleCodec(KhromaImbuerBlock::new);

	@Override
	protected MapCodec<KhromaImbuerBlock> codec() {
		return CODEC;
	}

	public KhromaImbuerBlock(Properties properties) {
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
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KhromaImbuerBlockEntity(pos, state);
	}

	@Override
	public KhromaImbuerBlockEntity getBlockEntity(Level level, BlockPos pos) {
		return level.getBlockEntity(pos, RegistryReference.BLOCK_ENTITY_KHROMA_IMBUER.get()).get();
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, RegistryReference.BLOCK_ENTITY_KHROMA_IMBUER.get(), KhromaImbuerBlockEntity::serverTick);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide) {
			player.openMenu(state.getMenuProvider(level, pos));
			return InteractionResult.CONSUME;
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	protected boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			var blockentity = getBlockEntity(level, pos);
			if (blockentity != null) {
				if (level instanceof ServerLevel) {
					Containers.dropContents(level, pos, blockentity);
				}

				super.onRemove(state, level, pos, newState, isMoving);
				level.updateNeighbourForOutputSignal(pos, this);
			} else {
				super.onRemove(state, level, pos, newState, isMoving);
			}
		}
	}
}
