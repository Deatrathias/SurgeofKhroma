package net.deatrathias.khroma.blocks;

import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.Config;
import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.entities.KhromaNodeEntity;
import net.deatrathias.khroma.khroma.IKhromaProvider;
import net.deatrathias.khroma.khroma.IKhromaProvidingBlock;
import net.deatrathias.khroma.khroma.KhromaBiomeData;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.khroma.KhromaProviderImpl;
import net.deatrathias.khroma.khroma.KhromaThroughput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NodeCollectorBlock extends BaseKhromaUserBlock implements IKhromaProvidingBlock {

	public static final MapCodec<NodeCollectorBlock> CODEC = simpleCodec(NodeCollectorBlock::new);

	@Override
	protected MapCodec<NodeCollectorBlock> codec() {
		return CODEC;
	}

	public NodeCollectorBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		if (!level.isClientSide)
			level.getEntities(EntityTypeTest.forClass(KhromaNodeEntity.class), new AABB(pos), EntitySelector.NO_SPECTATORS).forEach((entity) -> ((KhromaNodeEntity) entity).setForceVisible(true));

	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
		super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
		if (!level.isClientSide)
			level.getEntities(EntityTypeTest.forClass(KhromaNodeEntity.class), new AABB(pos), EntitySelector.NO_SPECTATORS).forEach((entity) -> ((KhromaNodeEntity) entity).setForceVisible(false));
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		ChunkAccess chunk = level.getChunk(pos);
		KhromaBiomeData data = chunk.getData(RegistryReference.KHROMA_BIOME_DATA);
		return data != null && data.isGenerated() && data.getNode().isPresent() && pos.equals(data.getNode().get().getPosition());
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
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
	protected boolean propagatesSkylightDown(BlockState state) {
		return true;
	}

	@Override
	public ConnectionType khromaConnection(BlockState state, Direction direction) {
		return direction == Direction.DOWN ? ConnectionType.PROVIDER : ConnectionType.NONE;
	}

	@Override
	public IKhromaProvider getProvider(Level level, BlockPos pos, BlockState state, Direction face, KhromaNetwork network) {
		if (face == Direction.DOWN) {
			ChunkAccess chunk = level.getChunk(pos);
			KhromaBiomeData data = chunk.getData(RegistryReference.KHROMA_BIOME_DATA);
			if (data.getNode().isEmpty()) {
				SurgeofKhroma.LOGGER.error("No node in chunk " + chunk.getPos().toString() + " for collector at " + pos.toString());
				return KhromaProviderImpl.disabled;
			}
			var node = data.getNode().get();
			return new KhromaProviderImpl(true, new KhromaThroughput(node.getKhroma(), Config.KHROMA_RATE_PER_LEVEL.get().get(node.getLevel() - 1).floatValue()), false);
		}
		return KhromaProviderImpl.disabled;
	}
}
