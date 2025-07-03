package net.deatrathias.khroma.khroma;

import java.util.function.Supplier;

public class KhromaProviderImpl implements IKhromaProvider {
	public static final KhromaProviderImpl disabled = new KhromaProviderImpl(false, () -> KhromaThroughput.empty, false);
	private boolean canProvide;
	private Supplier<KhromaThroughput> provideSupplier;
	private boolean relay;

	public KhromaProviderImpl(boolean canProvide, Supplier<KhromaThroughput> provideSupplier, boolean relay) {
		this.canProvide = canProvide;
		this.provideSupplier = provideSupplier;
		this.relay = relay;
	}

	@Override
	public KhromaThroughput provides() {
		return provideSupplier.get();
	}

	@Override
	public boolean canProvide() {
		return canProvide;
	}

	@Override
	public boolean isRelay() {
		return relay;
	}

	public void setRelay(boolean relay) {
		this.relay = relay;
	}
}
