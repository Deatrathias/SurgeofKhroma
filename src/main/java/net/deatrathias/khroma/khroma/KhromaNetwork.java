package net.deatrathias.khroma.khroma;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.slf4j.Logger;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.blocks.KhromaLineBlock;
import net.deatrathias.khroma.util.BlockDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class KhromaNetwork implements Comparable<KhromaNetwork> {
	private static final Map<Level, List<KhromaNetwork>> networksPerLevel = new HashMap<Level, List<KhromaNetwork>>();

	private Set<BlockPos> lines;

	private Set<BlockDirection> providers;

	private Set<BlockDirection> consumers;

	private Set<BlockDirection> relays;

	private Set<BlockDirection> relaysTo;

	private boolean master;

	private Level level;

	private KhromaThroughput khromaContent;

	private Khroma khroma;

	private float khromaRatio;

	private boolean dirty;

	private boolean updatedThisTick;

	private int relaysNumber;

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
		providers = new HashSet<BlockDirection>();
		consumers = new HashSet<BlockDirection>();
		relays = new HashSet<BlockDirection>();
		relaysTo = new HashSet<BlockDirection>();
		khromaContent = new KhromaThroughput(Khroma.KHROMA_EMPTY, 0);
		khroma = Khroma.KHROMA_EMPTY;
		this.level = level;
		if (networksPerLevel.containsKey(level)) {
			networksPerLevel.get(level).add(this);
		} else {
			var list = new LinkedList<KhromaNetwork>();
			list.add(this);
			networksPerLevel.put(level, list);
		}
	}

	public static KhromaNetwork create(Level level, BlockDirection provider) {
		IKhromaProvider prov = level.getCapability(RegistryReference.KHROMA_PROVIDER_BLOCK, provider.getPos(), provider.getDirection());
		if (prov == null || !prov.canProvide())
			return null;
		KhromaNetwork network = new KhromaNetwork(level);
		network.providers.add(provider);
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
		Set<BlockDirection> remainingProviders = new HashSet<BlockDirection>(providers);
		updateLineStates();

		IKhromaProvider provider = null;
		BlockDirection providerBlockDirection = null;

		for (BlockDirection provBlock : providers) {
			IKhromaProvider prov = level.getCapability(RegistryReference.KHROMA_PROVIDER_BLOCK, provBlock.getPos(), provBlock.getDirection());
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
		lineQueue.add(new BlockDirection(providerBlockDirection.getPos().relative(providerBlockDirection.getDirection()), providerBlockDirection.getDirection().getOpposite()));
		KhromaLineBlock khromaLineBlock = (KhromaLineBlock) RegistryReference.BLOCK_KHROMA_LINE.get();

		Set<BlockPos> visited = new HashSet<BlockPos>();
		lines.clear();
		providers.clear();
		providers.add(providerBlockDirection);
		consumers.clear();
		relays.clear();
		relaysTo.clear();
		while (!lineQueue.isEmpty()) {
			BlockDirection element = lineQueue.poll();
			if (visited.contains(element.getPos()))
				continue;
			visited.add(element.getPos());
			BlockState state = level.getBlockState(element.getPos());
			if (state.is(khromaLineBlock) && state.getValue(KhromaLineBlock.PROPERTY_BY_DIRECTION.get(element.getDirection()))) {
				lines.add(element.getPos());
				lineQueue.addAll(khromaLineBlock.getAllConnections(level, element.getPos()));
				continue;
			}
			IKhromaProvider foundProvider = level.getCapability(RegistryReference.KHROMA_PROVIDER_BLOCK, element.getPos(), element.getDirection());
			if (foundProvider != null && foundProvider.canProvide()) {
				allProviders.add(element);
				remainingProviders.remove(element);
				leftoverProviders.remove(element);
				providers.add(element);
				continue;
			}
			IKhromaConsumer foundConsumer = level.getCapability(RegistryReference.KHROMA_CONSUMER_BLOCK, element.getPos(), element.getDirection());
			if (foundConsumer != null && foundConsumer.canConsume()) {
				// BlockDirection[] relayProviders = foundConsumer.relaysTo();
				if (foundConsumer.isRelay()) {
					relays.add(element);
					// for (BlockDirection relay : relayProviders)
					// relaysTo.add(relay);
				} else
					consumers.add(element);

			}

		}

		dirty = false;
		updateLineStates();
		leftoverProviders.addAll(remainingProviders);
		return true;
	}

	public void update() {
		Khroma previousKhroma = khromaContent.getKhroma();

		khromaContent = new KhromaThroughput(Khroma.KHROMA_EMPTY, 0);
		for (BlockDirection providerPos : providers) {
			IKhromaProvider provider = level.getCapability(RegistryReference.KHROMA_PROVIDER_BLOCK, providerPos.getPos(), providerPos.getDirection());

			khromaContent = KhromaThroughput.merge(khromaContent, provider.provides());
		}

		Map<IKhromaConsumer, Float> consumerAndDemand = new HashMap<IKhromaConsumer, Float>();
		float provided = khromaContent.getRate();
		float consumed = 0;
		for (BlockDirection consumerPos : consumers) {
			IKhromaConsumer consumer = level.getCapability(RegistryReference.KHROMA_CONSUMER_BLOCK, consumerPos.getPos(), consumerPos.getDirection());

			float consumerConsume = consumer.consumes(khromaContent, true);
			consumed += consumerConsume;
			consumerAndDemand.put(consumer, consumerConsume);
		}

		float ratio = 1;
		if (provided <= 0)
			ratio = 0;
		else if (consumed > provided)
			ratio = provided / consumed;

		for (var entry : consumerAndDemand.entrySet()) {
			entry.getKey().consumes(new KhromaThroughput(khromaContent.getKhroma(), entry.getValue() * ratio), false);
		}

		if (previousKhroma != khromaContent.getKhroma())
			updateLineStates();

		updatedThisTick = true;

		if (relaysTo != null) {
			for (BlockDirection relay : relaysTo) {
				KhromaNetwork slaveNetwork = findNetwork(level, relay);
				if (slaveNetwork != null && !slaveNetwork.updatedThisTick)
					slaveNetwork.update();
			}
		}
	}

	public boolean calculateRequests() {
		if (updating) {
			requestCalculatedThisTick = true;
			request = 0;
			return true;
		}
		updating = true;
		float relayRequest = 0;
		for (var relay : relays) {
			IKhromaConsumer khromaConsumer = level.getCapability(RegistryReference.KHROMA_CONSUMER_BLOCK, relay.getPos(), relay.getDirection());
			float requested = khromaConsumer.consumes(khromaContent, true);
			while (requested < 0) {
				toUpdateNext.calculateRequests();
				requested = khromaConsumer.consumes(khromaContent, true);
			}

			relayRequest += requested;
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
		KhromaThroughput providing = khromaContent;
		for (BlockDirection providerPos : providers) {
			IKhromaProvider provider = level.getCapability(RegistryReference.KHROMA_PROVIDER_BLOCK, providerPos.getPos(), providerPos.getDirection());

			if (provider.isRelay()) {
				KhromaThroughput provided = provider.provides();
				while (provided == null) {
					toUpdateNext.updateProviders();
					provided = provider.provides();
				}
				providing = KhromaThroughput.merge(providing, provided);
			}
		}

		khromaContent = providing;

		if (request > 0)
			khromaRatio = khromaContent.getRate() / request;
		else
			khromaRatio = 0;

		updatedThisTick = true;

		if (khroma != khromaContent.getKhroma()) {
			khroma = khromaContent.getKhroma();
			updateLineStates();
		}
		lastRequest = request;
		request = 0;
		khromaContent = KhromaThroughput.empty;

		return true;
	}

	public void updateLineStates() {
		Block khromaLineBlock = RegistryReference.BLOCK_KHROMA_LINE.get();
		for (var linePos : lines) {
			if (!level.getBlockTicks().hasScheduledTick(linePos, khromaLineBlock))
				level.scheduleTick(linePos, RegistryReference.BLOCK_KHROMA_LINE.get(), 0);
		}
	}

	public void provide(KhromaThroughput throughput) {
		khromaContent = KhromaThroughput.merge(khromaContent, throughput);
	}

	public static KhromaNetwork findNetwork(Level level, BlockDirection blockDirection) {
		var networks = networksPerLevel.get(level);
		if (networks == null)
			return null;

		for (var network : networks) {
			if (network.lines.contains(blockDirection.getPos()) || network.providers.contains(blockDirection) || network.consumers.contains(blockDirection) || network.relays.contains(blockDirection))
				return network;
		}

		return null;
	}

	public static Khroma getKhromaAtPos(Level level, BlockPos pos) {
		var networks = networksPerLevel.get(level);
		if (networks == null)
			return Khroma.KHROMA_EMPTY;

		Khroma result = Khroma.KHROMA_EMPTY;
		boolean found = false;
		for (var network : networks) {
			if (network.lines.contains(pos)) {
				if (found)
					SurgeofKhroma.LOGGER.error("found twice " + pos.toString());
				found = true;
				result = network.khroma;
			}
		}

		return result;
	}

	public KhromaThroughput getKhromaContent() {
		return khromaContent;
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

	public void countRelays() {
		relaysNumber = 0;
		for (BlockDirection provBlock : providers) {
			IKhromaProvider provider = level.getCapability(RegistryReference.KHROMA_PROVIDER_BLOCK, provBlock.getPos(), provBlock.getDirection());
			if (provider.isRelay())
				relaysNumber++;
		}
	}

	public void debugNetwork() {
		Logger logger = SurgeofKhroma.LOGGER;
		logger.debug("network " + this);
		logger.debug("master: " + master);
		logger.debug("content: " + khromaContent.toString());
		logger.debug("relays: " + relaysNumber);
		logger.debug("providers:");
		for (var provider : providers) {
			logger.debug(provider.getPos().toString() + " " + provider.getDirection().toString());
		}
		logger.debug("consumers:");
		for (var consumer : consumers) {
			logger.debug(consumer.getPos().toString() + " " + consumer.getDirection().toString());
		}
		logger.debug("relays:");
		for (var consumer : relays) {
			logger.debug(consumer.getPos().toString() + " " + consumer.getDirection().toString());
		}
		logger.debug("request: " + lastRequest);
		logger.debug("ratio: " + khromaRatio);
	}

	public static void updateNetworksForLevel(Level level) {

		var networks = networksPerLevel.get(level);
		if (networks == null)
			return;

		Set<BlockDirection> leftoverProviders = new HashSet<BlockDirection>();
		Set<BlockDirection> allProviders = new HashSet<BlockDirection>();
		Iterator<KhromaNetwork> networkIter = networks.iterator();
		while (networkIter.hasNext()) {
			KhromaNetwork network = networkIter.next();
			if (network.dirty) {
				SurgeofKhroma.LOGGER.debug("rebuilding network");
				if (!network.rebuildNetwork(leftoverProviders, allProviders))
					networkIter.remove();
			}

		}

		while (!leftoverProviders.isEmpty()) {
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
		// for (KhromaNetwork network : networks) {
		while (!networkQueue.isEmpty()) {
			KhromaNetwork network = networkQueue.poll();
			if (!network.updatedThisTick)
				if (!network.updateProviders())
					networkQueue.add(network);
		}

	}

	@Override
	public int compareTo(KhromaNetwork o) {
		if (master && !o.master)
			return -1;
		else if (!master && o.master)
			return 1;
		return relaysNumber - o.relaysNumber;
	}
}
