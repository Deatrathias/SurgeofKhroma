package net.deatrathias.khroma.khroma;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.blocks.logistics.KhromaLineBlock;
import net.deatrathias.khroma.khroma.IKhromaUsingBlock.ConnectionType;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.util.BlockDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class KhromaNetwork {
	private static final Map<Level, List<KhromaNetwork>> networksPerLevel = new HashMap<Level, List<KhromaNetwork>>();

	private Set<BlockPos> lines;

	private Map<BlockDirection, IKhromaProvider> providers;

	private Map<BlockDirection, IKhromaConsumer> consumers;

	private Map<BlockDirection, IKhromaConsumer> relays;

	private Level level;

	private Khroma khroma;

	private float khromaRatio;

	private boolean dirty;

	private boolean updatedThisTick;

	private float request;

	private float lastRequest;

	private boolean requestCalculatedThisTick;

	private boolean updating;

	private static KhromaNetwork toUpdateNext = null;

	public static void setToUpdateNext(KhromaNetwork toUpdateNext) {
		KhromaNetwork.toUpdateNext = toUpdateNext;
	}

	private KhromaNetwork(Level level) {
		lines = new HashSet<>();
		providers = new HashMap<BlockDirection, IKhromaProvider>();
		consumers = new HashMap<BlockDirection, IKhromaConsumer>();
		relays = new HashMap<BlockDirection, IKhromaConsumer>();
		khroma = Khroma.EMPTY;
		this.level = level;
	}

	private void addToLevel() {
		if (networksPerLevel.containsKey(level)) {
			networksPerLevel.get(level).add(this);
		} else {
			var list = new LinkedList<KhromaNetwork>();
			list.add(this);
			networksPerLevel.put(level, list);
		}
	}

	public static KhromaNetwork create(Level level, BlockDirection provider) {
		KhromaNetwork network = new KhromaNetwork(level);
		IKhromaProvider prov = network.getProvider(level, provider);
		if (prov == null || !prov.canProvide())
			return null;
		network.addToLevel();
		network.providers.put(provider, prov);
		network.markDirty();
		return network;
	}

	public void markDirty() {
		dirty = true;
	}

	public boolean isRequestCalculatedThisTick() {
		return requestCalculatedThisTick;
	}

	public boolean rebuildNetwork(Set<BlockDirection> leftoverProviders, Set<BlockDirection> allProviders) {
		Set<BlockDirection> remainingProviders = new HashSet<BlockDirection>(providers.keySet());
		updateLineStates();

		IKhromaProvider provider = null;
		BlockDirection providerBlockDirection = null;

		for (BlockDirection provBlock : providers.keySet()) {
			IKhromaProvider prov = getProvider(level, provBlock);
			if (prov != null && prov.canProvide()) {
				provider = prov;
				providerBlockDirection = provBlock;
				break;
			} else
				remainingProviders.remove(provBlock);
		}

		if (provider == null || allProviders.contains(providerBlockDirection))
			return false;

		allProviders.add(providerBlockDirection);
		remainingProviders.remove(providerBlockDirection);
		leftoverProviders.remove(providerBlockDirection);

		Queue<BlockDirection> lineQueue = new LinkedList<BlockDirection>();
		lineQueue.add(new BlockDirection(providerBlockDirection.pos().relative(providerBlockDirection.direction()), providerBlockDirection.direction().getOpposite()));
		KhromaLineBlock khromaLineBlock = (KhromaLineBlock) BlockReference.KHROMA_LINE.get();

		Set<BlockDirection> visited = new HashSet<BlockDirection>();
		lines.clear();
		providers.clear();
		providers.put(providerBlockDirection, provider);
		visited.add(providerBlockDirection);
		consumers.clear();
		relays.clear();
		while (!lineQueue.isEmpty()) {
			BlockDirection element = lineQueue.poll();
			if (visited.contains(element))
				continue;
			visited.add(element);
			BlockState state = level.getBlockState(element.pos());
			if (state.is(khromaLineBlock) && state.getValue(KhromaLineBlock.PROPERTY_BY_DIRECTION.get(element.direction()))) {
				lines.add(element.pos());
				for (Direction dir : Direction.values())
					visited.add(new BlockDirection(element.pos(), dir));
				lineQueue.addAll(khromaLineBlock.getAllConnections(level, element.pos()));
				continue;
			}
			IKhromaProvider foundProvider = getProvider(level, element);
			if (foundProvider != null && foundProvider.canProvide()) {
				allProviders.add(element);
				remainingProviders.remove(element);
				leftoverProviders.remove(element);
				providers.put(element, foundProvider);
				continue;
			}
			IKhromaConsumer foundConsumer = getConsumer(level, element);
			if (foundConsumer != null && foundConsumer.canConsume()) {
				if (foundConsumer.isRelay()) {
					relays.put(element, foundConsumer);
				} else
					consumers.put(element, foundConsumer);

			}

		}

		dirty = false;
		updateLineStates();
		leftoverProviders.addAll(remainingProviders);
		return true;
	}

	public boolean calculateRequests() {
		if (updating) {
			requestCalculatedThisTick = true;
			request = 0;
			return true;
		}
		request = 0;
		updating = true;
		float relayRequest = 0;
		for (var relay : relays.values()) {
			float requested = relay.request();
			while (requested < 0) {
				toUpdateNext.calculateRequests();
				requested = relay.request();
			}

			relayRequest += requested;
		}

		for (var consumer : consumers.values()) {
			request += consumer.request();
		}

		request += relayRequest;
		requestCalculatedThisTick = true;
		updating = false;
		return true;
	}

	public boolean updateProviders() {
		if (updating) {
			updatedThisTick = true;
			khromaRatio = 0;
			return true;
		}
		updating = true;
		KhromaThroughput providing = KhromaThroughput.empty;
		for (IKhromaProvider provider : providers.values()) {

			if (provider.isRelay()) {
				KhromaThroughput provided = provider.provides();
				while (provided == null) {
					toUpdateNext.updateProviders();
					provided = provider.provides();
				}
				providing = KhromaThroughput.merge(providing, provided);
			} else {
				providing = KhromaThroughput.merge(providing, provider.provides());
			}
		}

		if (request > 0)
			khromaRatio = providing.getRate() / request;
		else
			khromaRatio = 0;

		updatedThisTick = true;

		if (khroma != providing.getKhroma()) {
			khroma = providing.getKhroma();
			updateLineStates();
		}
		lastRequest = request;
		request = 0;

		return true;
	}

	public void updateLineStates() {
		Block khromaLineBlock = BlockReference.KHROMA_LINE.get();
		for (var linePos : lines) {
			if (!level.getBlockTicks().hasScheduledTick(linePos, khromaLineBlock))
				level.scheduleTick(linePos, BlockReference.KHROMA_LINE.get(), 0);
		}

		for (var consumer : consumers.keySet()) {
			BlockState consumerState = level.getBlockState(consumer.pos());
			if (!level.getBlockTicks().hasScheduledTick(consumer.pos(), consumerState.getBlock()))
				level.scheduleTick(consumer.pos(), consumerState.getBlock(), 0);
		}
	}

	public static @Nullable KhromaNetwork findNetwork(Level level, BlockDirection blockDirection) {
		var networks = networksPerLevel.get(level);
		if (networks == null)
			return null;

		for (var network : networks) {
			if (network.lines.contains(blockDirection.pos()) || network.providers.containsKey(blockDirection) || network.consumers.containsKey(blockDirection)
					|| network.relays.containsKey(blockDirection))
				return network;
		}

		return null;
	}

	public static Khroma getKhromaAtPos(Level level, BlockPos pos) {
		var networks = networksPerLevel.get(level);
		if (networks == null)
			return Khroma.EMPTY;

		Khroma result = Khroma.EMPTY;
		boolean found = false;
		for (var network : networks) {
			if (network.lines.contains(pos)) {
				if (found) {
					SurgeofKhroma.LOGGER.error("found twice " + pos.toString(), new Exception("Khroma line found in multiple networks"));
				}
				found = true;
				result = network.khroma;
			}
		}

		return result;
	}

	public KhromaThroughput getKhromaThroughput() {
		return new KhromaThroughput(khroma, khromaRatio);
	}

	public boolean isUpdatedThisTick() {
		return updatedThisTick;
	}

	public float getRequest() {
		return request;
	}

	public void request() {
		request++;
	}

	public float getKhromaRatio() {
		return khromaRatio;
	}

	public Khroma getKhroma() {
		return khroma;
	}

	public Optional<IKhromaProvider> providerAt(BlockDirection blockDir) {
		return Optional.ofNullable(providers.get(blockDir));
	}

	public Optional<IKhromaConsumer> consumerAt(BlockDirection blockDir, boolean relay) {
		return relay ? Optional.ofNullable(relays.get(blockDir)) : Optional.ofNullable(consumers.get(blockDir));
	}

	public void debugNetwork() {
		Logger logger = SurgeofKhroma.LOGGER;
		logger.debug("network " + this);
		logger.debug("providers:");
		for (var provider : providers.keySet()) {
			logger.debug(provider.pos().toString() + " " + provider.direction().toString());
		}
		logger.debug("consumers:");
		for (var consumer : consumers.keySet()) {
			logger.debug(consumer.pos().toString() + " " + consumer.direction().toString());
		}
		logger.debug("relays:");
		for (var consumer : relays.keySet()) {
			logger.debug(consumer.pos().toString() + " " + consumer.direction().toString());
		}
		logger.debug("request: " + lastRequest);
		logger.debug("ratio: " + khromaRatio);
	}

	public static class NetworkSavedData extends SavedData {
		public static final Codec<NetworkSavedData> CODEC = RecordCodecBuilder
				.create(instance -> instance.group(Codec.list(BlockDirection.CODEC).fieldOf("providers").forGetter(s -> s.providers)).apply(instance, NetworkSavedData::new));

		public static final SavedDataType<NetworkSavedData> ID = new SavedDataType<KhromaNetwork.NetworkSavedData>("khroma_network",
				NetworkSavedData::new, CODEC);

		private List<BlockDirection> providers;

		public List<BlockDirection> getProviders() {
			return providers;
		}

		public NetworkSavedData() {
			providers = List.of();
		}

		public NetworkSavedData(List<BlockDirection> providers) {
			this.providers = providers;
		}

		public void compareWtihSet(Set<BlockDirection> providerSet) {
			if (providers.size() != providerSet.size() || !providerSet.containsAll(providers)) {
				providers = List.copyOf(providerSet);
				setDirty();
			}
		}

	}

	public static boolean isLevelLoaded(Level level) {
		return networksPerLevel.containsKey(level);
	}

	public static void updateNetworksForLevel(Level level) {
		boolean dirty = false;

		var networks = networksPerLevel.get(level);
		if (networks == null)
			return;

		Set<BlockDirection> leftoverProviders = new HashSet<BlockDirection>();
		Set<BlockDirection> allProviders = new HashSet<BlockDirection>();
		Iterator<KhromaNetwork> networkIter = networks.iterator();
		while (networkIter.hasNext()) {
			KhromaNetwork network = networkIter.next();
			if (network.dirty) {
				dirty = true;
				SurgeofKhroma.LOGGER.debug("rebuilding network");
				if (!network.rebuildNetwork(leftoverProviders, allProviders)) {
					SurgeofKhroma.LOGGER.debug("Network removed");
					networkIter.remove();
				}
			}

		}

		while (!leftoverProviders.isEmpty()) {
			dirty = true;
			SurgeofKhroma.LOGGER.debug("creating new network");
			BlockDirection first = leftoverProviders.iterator().next();
			leftoverProviders.remove(first);
			KhromaNetwork addedNetwork = KhromaNetwork.create(level, first);
			if (addedNetwork != null)
				addedNetwork.rebuildNetwork(leftoverProviders, allProviders);
		}

		for (KhromaNetwork network : networks) {
			network.updatedThisTick = false;
			network.requestCalculatedThisTick = false;
			network.updating = false;
		}

		toUpdateNext = null;
		Queue<KhromaNetwork> networkQueue = new LinkedList<KhromaNetwork>(networks);
		while (!networkQueue.isEmpty()) {
			KhromaNetwork network = networkQueue.poll();
			if (!network.requestCalculatedThisTick)
				if (!network.calculateRequests())
					networkQueue.add(network);
		}

		toUpdateNext = null;
		networkQueue.addAll(networks);
		while (!networkQueue.isEmpty()) {
			KhromaNetwork network = networkQueue.poll();
			if (!network.updatedThisTick)
				if (!network.updateProviders())
					networkQueue.add(network);
		}

		if (dirty) {
			updateeSaveData(level);
		}
	}

	private static void updateeSaveData(Level level) {
		Set<BlockDirection> providers = new HashSet<BlockDirection>();
		networksPerLevel.get(level).forEach((network) -> providers.addAll(network.providers.keySet()));
		var saveData = ((ServerLevel) level).getDataStorage().computeIfAbsent(NetworkSavedData.ID);
		saveData.compareWtihSet(providers);
	}

	public static ConnectionType getConnectionType(Level level, BlockPos pos, Direction face) {
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() instanceof IKhromaUsingBlock khromaUser)
			return khromaUser.khromaConnection(state, face);
		return ConnectionType.NONE;
	}

	public IKhromaProvider getProvider(Level level, BlockDirection blockDirection) {
		BlockState state = level.getBlockState(blockDirection.pos());
		if (state.getBlock() instanceof IKhromaProvidingBlock providing)
			return providing.getProvider(level, blockDirection.pos(), state, blockDirection.direction(), this);
		return null;
	}

	public IKhromaConsumer getConsumer(Level level, BlockDirection blockDirection) {
		BlockState state = level.getBlockState(blockDirection.pos());
		if (state.getBlock() instanceof IKhromaConsumingBlock consuming)
			return consuming.getConsumer(level, blockDirection.pos(), state, blockDirection.direction(), this);
		return null;
	}
}
