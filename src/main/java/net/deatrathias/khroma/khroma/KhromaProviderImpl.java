package net.deatrathias.khroma.khroma;

public class KhromaProviderImpl implements IKhromaProvider {
	public static final KhromaProviderImpl disabled = new KhromaProviderImpl(false, KhromaThroughput.empty, false);
	private boolean canProvide;
	private KhromaThroughput providing;
	private boolean relay;

	public KhromaProviderImpl(boolean canProvide, KhromaThroughput providing, boolean relay) {
		this.canProvide = canProvide;
		this.providing = providing;
		this.relay = relay;
	}

	@Override
	public KhromaThroughput provides() {
		return providing;
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
