package net.deatrathias.khroma.blocks;

import java.util.function.Function;

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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KhromaApertureBlock extends BaseKhromaRelayBlock<KhromaApertureBlockEntity> implements EntityBlock {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;

	public static final MapCodec<KhromaApertureBlock> CODEC = simpleCodec(KhromaApertureBlock::new);

	@Override
	protected MapCodec<KhromaApertureBlock> codec() {
		return CODEC;
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
