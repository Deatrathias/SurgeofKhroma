package net.deatrathias.khroma.blockentities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.deatrathias.khroma.Config;
import net.deatrathias.khroma.client.particles.KhromaParticleOption;
import net.deatrathias.khroma.khroma.IKhromaProvider;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.khroma.KhromaBiomeData;
import net.deatrathias.khroma.khroma.KhromaNode;
import net.deatrathias.khroma.khroma.KhromaThroughput;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.RegistryReference;
import net.deatrathias.khroma.registries.TagReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class NodeCollectorBlockEntity extends BaseKhromaUserBlockEntity {

	public static final Map<TagKey<Block>, Khroma> structureBlockTags = Map.of(
			TagReference.Blocks.COLLECTOR_STRUCTURE_COMPONENTS_RED, Khroma.RED,
			TagReference.Blocks.COLLECTOR_STRUCTURE_COMPONENTS_GREEN, Khroma.GREEN,
			TagReference.Blocks.COLLECTOR_STRUCTURE_COMPONENTS_BLUE, Khroma.BLUE,
			TagReference.Blocks.COLLECTOR_STRUCTURE_COMPONENTS_WHITE, Khroma.WHITE,
			TagReference.Blocks.COLLECTOR_STRUCTURE_COMPONENTS_BLACK, Khroma.BLACK);

	private int pillars;

	private List<BlockPos> pillarPositions = new ArrayList<BlockPos>();

	private Khroma khroma = Khroma.EMPTY;

	private static class CollectorProvider implements IKhromaProvider {

		private int pillars;

		private int nodeLevel;

		private Khroma khroma;

		public void setPillars(int pillars) {
			this.pillars = pillars;
		}

		public void setNodeLevel(int nodeLevel) {
			this.nodeLevel = nodeLevel;
		}

		public void setKhroma(Khroma khroma) {
			this.khroma = khroma;
		}

		@Override
		public KhromaThroughput provides() {
			return new KhromaThroughput(khroma, Config.KHROMA_RATE_PER_LEVEL.get().get(nodeLevel - 1).floatValue() * (1f + pillars / 10f));
		}

		@Override
		public boolean canProvide() {
			return true;
		}

		@Override
		public boolean isRelay() {
			return false;
		}

	}

	private CollectorProvider provider;

	public NodeCollectorBlockEntity(BlockPos pos, BlockState blockState) {
		super(BlockReference.BE_NODE_COLLECTOR.get(), pos, blockState);
	}

	@Override
	public void setLevel(Level level) {
		super.setLevel(level);

	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (!level.isClientSide) {
			if (provider == null)
				provider = new CollectorProvider();
			KhromaNode node = getNode();
			khroma = node.getKhroma();
			provider.setKhroma(khroma);
			provider.setNodeLevel(node.getLevel());

			checkStructure();
		}
	}

	public IKhromaProvider getProvider() {
		return provider;
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		khroma = input.read("Khroma", Khroma.CODEC).orElse(Khroma.EMPTY);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		if (khroma != null)
			output.store("Khroma", Khroma.CODEC, khroma);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, NodeCollectorBlockEntity blockEntity) {
		blockEntity.tick();
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, NodeCollectorBlockEntity blockEntity) {
		blockEntity.tick();
		var random = level.getRandom();

		for (var pillarPos : blockEntity.pillarPositions) {
			BlockPos pillarTopPos = pillarPos.above(2);
			BlockState pillarBlock = level.getBlockState(pillarTopPos);
			Khroma pillarKhroma = structureBlockTags.entrySet().stream().filter(entry -> pillarBlock.is(entry.getKey())).map(Map.Entry::getValue).findFirst().orElse(Khroma.EMPTY);
			if (pillarKhroma != Khroma.EMPTY) {
				double x = pillarTopPos.getX() + random.nextDouble();
				double y = pillarTopPos.getY() + random.nextDouble();
				double z = pillarTopPos.getZ() + random.nextDouble();

				Vec3 towards = (pos.getCenter().subtract(new Vec3(x, y, z))).normalize().scale(3 / 17.5);

				level.addParticle(new KhromaParticleOption(pillarKhroma), x, y, z, towards.x, towards.y, towards.z);
			}
		}
	}

	private void tick() {
		if (level.getGameTime() % 100 == 0) {
			checkStructure();
		}
	}

	public void checkStructure() {
		pillarPositions.clear();
		pillars = 0;
		if (khroma == Khroma.EMPTY)
			return;
		Map<Khroma, Integer> pillarCount = new HashMap<Khroma, Integer>();
		BlockPos basePos = worldPosition.below(3);

		for (var direction : Direction.Plane.HORIZONTAL) {
			BlockPos pillarPos = basePos.mutable().move(direction, 2).move(direction.getClockWise(), 2);
			var foundTag = checkPillar(level, pillarPos);
			if (foundTag == null)
				continue;

			Khroma pillarKhroma = structureBlockTags.get(foundTag);
			if (khroma.contains(pillarKhroma)) {
				int currentCount = pillarCount.getOrDefault(pillarKhroma, 0);
				if (currentCount < 2) {
					currentCount++;
					pillars++;
					pillarPositions.add(pillarPos.immutable());
					pillarCount.put(pillarKhroma, currentCount);
				}
			}
		}

		if (provider != null)
			provider.setPillars(pillars);
	}

	public static TagKey<Block> checkPillar(Level level, BlockPos from) {
		MutableBlockPos iterPos = from.mutable();
		BlockState state = level.getBlockState(iterPos);
		if (!state.is(TagReference.Blocks.WOODEN_PILLARS))
			return null;
		var colorTag = state.getTags().filter(structureBlockTags.keySet()::contains).findFirst().orElse(null);
		if (colorTag == null)
			return null;

		iterPos.move(Direction.UP);
		state = level.getBlockState(iterPos);
		if (!state.is(TagReference.Blocks.WOODEN_PILLARS))
			return null;
		if (!state.is(colorTag))
			return null;

		iterPos.move(Direction.UP);
		state = level.getBlockState(iterPos);
		if (!state.is(TagReference.Blocks.KHROMETAL_BLOCKS))
			return null;
		if (!state.is(colorTag))
			return null;

		return colorTag;
	}

	public KhromaNode getNode() {
		ChunkAccess chunk = level.getChunk(worldPosition);
		KhromaBiomeData data = chunk.getData(RegistryReference.ATTACHMENT_KHROMA_BIOME_DATA);
		if (data.getNode().isPresent()) {
			var node = data.getNode().get();
			if (node.getPosition().equals(worldPosition))
				return node;
		}

		return null;
	}

	@Override
	public CompoundTag getUpdateTag(Provider registries) {
		return saveCustomOnly(registries);
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}
