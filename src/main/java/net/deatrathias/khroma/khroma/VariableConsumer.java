package net.deatrathias.khroma.khroma;

public class VariableConsumer implements IKhromaConsumer {

	private float previousRequest = 0;

	private float request;

	private KhromaNetwork network;

	public void setNetwork(KhromaNetwork network) {
		this.network = network;
	}

	@Override
	public float request() {
		float result = request;
		previousRequest = result;
		request = 0;
		return result;
	}

	public KhromaThroughput requestKhroma(float requestIntensity) {
		request = requestIntensity;
		if (network != null)
			return network.getKhromaThroughput().multiply(previousRequest);
		previousRequest = 0;
		return KhromaThroughput.empty;
	}

	public KhromaThroughput requestKhroma() {
		return requestKhroma(1f);
	}

	@Override
	public boolean canConsume() {
		return true;
	}

	@Override
	public boolean isRelay() {
		return false;
	}

}
