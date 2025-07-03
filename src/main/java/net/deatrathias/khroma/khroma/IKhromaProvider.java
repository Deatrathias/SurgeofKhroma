package net.deatrathias.khroma.khroma;

public interface IKhromaProvider {
	KhromaThroughput provides();

	boolean canProvide();

	boolean isRelay();
}
