package net.deatrathias.khroma.blocks;

import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.blockentities.KhromaCombinerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;

public class KhromaCombinerBlock extends BaseKhromaRelayBlock<KhromaCombinerBlockEntity> implements EntityBlock {

	public KhromaCombinerBlock(Properties properties) {
		super(properties);
	}

	public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

	public static final MapCodec<KhromaCombinerBlock> CODEC = simpleCodec(KhromaCombinerBlock::new);

	@Override
	protected MapCodec<KhromaCombinerBlock> codec() {
		return CODEC;
	}

	@Override
	protected EnumProperty<Direction> getFacingProperty() {
		return FACING;
	}

	@Override
	protected void makeShapes() {
		shapesPerIndex[0] = null;
		shapesPerIndex[1] = null;
		shapesPerIndex[2] = Shapes.or(Block.box(0, 4, 4, 16, 12, 12), Block.box(6, 6, 0, 10, 10, 4));
		shapesPerIndex[3] = Shapes.or(Block.box(0, 4, 4, 16, 12, 12), Block.box(6, 6, 12, 10, 10, 16));
		shapesPerIndex[4] = Shapes.or(Block.box(4, 4, 0, 12, 12, 16), Block.box(0, 6, 6, 4, 10, 10));
		shapesPerIndex[5] = Shapes.or(Block.box(4, 4, 0, 12, 12, 16), Block.box(12, 6, 6, 16, 10, 10));
	}

	@Override
	public KhromaCombinerBlockEntity getBlockEntity(Level level, BlockPos pos) {
		return level.getBlockEntity(pos, RegistryReference.BLOCK_ENTITY_KHROMA_COMBINER.get()).get();
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new KhromaCombinerBlockEntity(pos, state);
	}
}
