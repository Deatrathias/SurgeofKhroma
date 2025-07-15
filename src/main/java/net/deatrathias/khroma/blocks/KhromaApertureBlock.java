package net.deatrathias.khroma.blocks;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.blockentities.KhromaApertureBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;

public class KhromaApertureBlock extends BaseKhromaRelayBlock<KhromaApertureBlockEntity> implements EntityBlock {
	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	public static final MapCodec<KhromaApertureBlock> CODEC = simpleCodec(KhromaApertureBlock::new);

	@Override
	protected MapCodec<KhromaApertureBlock> codec() {
		return CODEC;
	}

	public KhromaApertureBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected DirectionProperty getFacingProperty() {
		return FACING;
	}

	@Override
	protected void makeShapes() {
		shapesPerIndex[0] = Shapes.or(Block.box(5, 7, 5, 11, 16, 11), Block.box(6, 0, 6, 10, 7, 10));
		shapesPerIndex[1] = Shapes.or(Block.box(5, 0, 5, 11, 9, 11), Block.box(6, 9, 6, 10, 16, 10));
		shapesPerIndex[2] = Shapes.or(Block.box(5, 5, 7, 11, 11, 16), Block.box(6, 6, 0, 10, 10, 7));
		shapesPerIndex[3] = Shapes.or(Block.box(5, 5, 0, 11, 11, 9), Block.box(6, 6, 9, 10, 10, 16));
		shapesPerIndex[4] = Shapes.or(Block.box(7, 5, 5, 16, 11, 11), Block.box(0, 6, 6, 7, 10, 10));
		shapesPerIndex[5] = Shapes.or(Block.box(0, 5, 5, 9, 11, 11), Block.box(9, 6, 6, 16, 10, 10));
	}

	@Override
	public KhromaApertureBlockEntity getBlockEntity(Level level, BlockPos pos) {
		return level.getBlockEntity(pos, RegistryReference.BLOCK_ENTITY_KHROMA_APERTURE.get()).get();
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
		return true;
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
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KhromaApertureBlockEntity(pos, state);
	}
}
