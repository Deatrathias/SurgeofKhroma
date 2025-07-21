package net.deatrathias.khroma.khroma;

public interface IKhromaConsumer {
	float request();

	boolean canConsume();

	boolean isRelay();
}
