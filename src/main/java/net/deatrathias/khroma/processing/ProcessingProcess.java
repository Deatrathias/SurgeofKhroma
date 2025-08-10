package net.deatrathias.khroma.processing;

public abstract class ProcessingProcess<T, R> extends Process {

	public ProcessingProcess(ProcessType<?> type) {
		super(type);
	}

	public abstract boolean processProcessing(float potency, T input);
}
