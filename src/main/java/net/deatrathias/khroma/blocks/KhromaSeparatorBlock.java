package net.deatrathias.khroma.blocks;

import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.blockentities.KhromaSeparatorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;

public class KhromaSeparatorBlock extends BaseKhromaRelayBlock<KhromaSeparatorBlockEntity> implements EntityBlock {

	public KhromaSeparatorBlock(Properties properties) {
		super(properties);
	}

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public static final MapCodec<KhromaSeparatorBlock> CODEC = simpleCodec(KhromaSeparatorBlock::new);

	@Override
	protected MapCodec<KhromaSeparatorBlock> codec() {
		return CODEC;
	}

	@Override
	protected DirectionProperty getFacingProperty() {
		return FACING;
	}

	@Override
	protected void makeShapes() {
		shapesPerIndex[0] = null;
		shapesPerIndex[1] = null;
		shapesPerIndex[2] = Shapes.or(Block.box(0, 6, 6, 16, 10, 10), Block.box(4, 4, 10, 12, 12, 16));
		shapesPerIndex[3] = Shapes.or(Block.box(0, 6, 6, 16, 10, 10), Block.box(4, 4, 0, 12, 12, 6));
		shapesPerIndex[4] = Shapes.or(Block.box(6, 6, 0, 10, 10, 16), Block.box(10, 4, 4, 16, 12, 12));
		shapesPerIndex[5] = Shapes.or(Block.box(6, 6, 0, 10, 10, 16), Block.box(0, 4, 4, 6, 12, 12));
	}

	@Override
	public KhromaSeparatorBlockEntity getBlockEntity(Level level, BlockPos pos) {
		return level.getBlockEntity(pos, RegistryReference.BLOCK_ENTITY_KHROMA_SEPARATOR.get()).get();
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KhromaSeparatorBlockEntity(pos, state);
	}
}
