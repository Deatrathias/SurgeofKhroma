package net.deatrathias.khroma.khroma;

public class KhromaConsumerImpl implements IKhromaConsumer {
	public static final KhromaConsumerImpl disabled = new KhromaConsumerImpl(false, 0, false);
	private boolean canConsume;
	private float request;
	private boolean relay;

	public KhromaConsumerImpl(boolean canConsume, float request, boolean relay) {
		this.canConsume = canConsume;
		this.request = request;
		this.relay = relay;
	}

	public KhromaConsumerImpl() {
		this(true, 0, false);
	}

	public void setRequest(float request) {
		this.request = request;
	}

	@Override
	public float request() {
		return request;
	}

	@Override
	public boolean canConsume() {
		return canConsume;
	}

	public boolean isRelay() {
		return relay;
	}
}
