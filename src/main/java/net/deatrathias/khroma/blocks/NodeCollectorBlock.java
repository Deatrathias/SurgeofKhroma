package net.deatrathias.khroma.blocks;

import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.blockentities.NodeCollectorBlockEntity;
import net.deatrathias.khroma.entities.KhromaNodeEntity;
import net.deatrathias.khroma.khroma.KhromaBiomeData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NodeCollectorBlock extends BaseKhromaUserBlock<NodeCollectorBlockEntity> implements EntityBlock {

	public static final MapCodec<NodeCollectorBlock> CODEC = simpleCodec(NodeCollectorBlock::new);

	@Override
	protected MapCodec<NodeCollectorBlock> codec() {
		return CODEC;
	}

	public NodeCollectorBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new NodeCollectorBlockEntity(pos, state);
	}

	@Override
	public NodeCollectorBlockEntity getBlockEntity(Level level, BlockPos pos) {
		return level.getBlockEntity(pos, RegistryReference.BLOCK_ENTITY_NODE_COLLECTOR.get()).get();
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		if (!level.isClientSide)
			level.getEntities(EntityTypeTest.forClass(KhromaNodeEntity.class), new AABB(pos), EntitySelector.NO_SPECTATORS).forEach((entity) -> ((KhromaNodeEntity) entity).setForceVisible(true));

	}

	@Override
	protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		super.onRemove(state, level, pos, newState, movedByPiston);
		if (!level.isClientSide)
			level.getEntities(EntityTypeTest.forClass(KhromaNodeEntity.class), new AABB(pos), EntitySelector.NO_SPECTATORS).forEach((entity) -> ((KhromaNodeEntity) entity).setForceVisible(false));
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		ChunkAccess chunk = level.getChunk(pos);
		KhromaBiomeData data = chunk.getData(RegistryReference.KHROMA_BIOME_DATA);
		return data != null && data.isGenerated() && data.getNode() != null && pos.equals(data.getNode().getPosition());
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, RegistryReference.BLOCK_ENTITY_NODE_COLLECTOR.get(), NodeCollectorBlockEntity::serverTick);
	}

	@Override
	protected boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		return adjacentBlockState.is(this) ? true : super.skipRendering(state, adjacentBlockState, side);
	}

	@Override
	protected VoxelShape getVisualShape(BlockState p_309057_, BlockGetter p_308936_, BlockPos p_308956_, CollisionContext p_309006_) {
		return Shapes.empty();
	}

	@Override
	protected float getShadeBrightness(BlockState p_308911_, BlockGetter p_308952_, BlockPos p_308918_) {
		return 1.0F;
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState p_309084_, BlockGetter p_309133_, BlockPos p_309097_) {
		return true;
	}
}
