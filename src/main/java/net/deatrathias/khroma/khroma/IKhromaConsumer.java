package net.deatrathias.khroma.khroma;

public interface IKhromaConsumer {
	float consumes(KhromaThroughput throughput, boolean simulate);

	boolean canConsume();

	boolean isRelay();
}
