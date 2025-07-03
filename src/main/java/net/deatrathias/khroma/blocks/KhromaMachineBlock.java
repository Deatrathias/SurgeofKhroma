package net.deatrathias.khroma.blocks;

import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.blockentities.KhromaMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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

public class KhromaMachineBlock extends BaseKhromaUserBlock<KhromaMachineBlockEntity> {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

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
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KhromaMachineBlockEntity(pos, state);
	}

	@Override
	public KhromaMachineBlockEntity getBlockEntity(Level level, BlockPos pos) {
		return level.getBlockEntity(pos, RegistryReference.BLOCK_ENTITY_KHROMA_MACHINE.get()).get();
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide())
			getBlockEntity(level, pos).onUse();
		return InteractionResult.sidedSuccess(level.isClientSide());
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, RegistryReference.BLOCK_ENTITY_KHROMA_MACHINE.get(), KhromaMachineBlockEntity::serverTick);
	}
}
