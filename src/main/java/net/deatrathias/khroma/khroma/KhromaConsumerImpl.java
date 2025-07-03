package net.deatrathias.khroma.khroma;

import java.util.function.BiFunction;

public class KhromaConsumerImpl implements IKhromaConsumer {
	public static final KhromaConsumerImpl disabled = new KhromaConsumerImpl(false, (t, r) -> 0f, false);
	private boolean canConsume;
	private BiFunction<KhromaThroughput, Boolean, Float> consumeFunction;
	private boolean relay;

	public KhromaConsumerImpl(boolean canConsume, BiFunction<KhromaThroughput, Boolean, Float> consumeFunction, boolean relay) {
		this.canConsume = canConsume;
		this.consumeFunction = consumeFunction;
		this.relay = relay;
	}

	@Override
	public float consumes(KhromaThroughput throughput, boolean simulate) {
		return consumeFunction.apply(throughput, simulate);
	}

	@Override
	public boolean canConsume() {
		return canConsume;
	}

	public boolean isRelay() {
		return relay;
	}
}
