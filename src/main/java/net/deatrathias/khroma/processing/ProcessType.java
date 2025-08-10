package net.deatrathias.khroma.processing;

import java.util.Optional;
import java.util.function.Supplier;

import com.mojang.serialization.Codec;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.registries.ProcessRegistry;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.ValueInput;

public class ProcessType<T extends Process> {
	public static final Codec<ProcessType<?>> CODEC = ProcessRegistry.PROCESS_TYPE_REGISTRY.byNameCodec();

	private Supplier<T> factory;

	private ProcessType(Supplier<T> factory) {
		this.factory = factory;
	}

	public static ResourceLocation getKey(ProcessType<?> processType) {
		return ProcessRegistry.PROCESS_TYPE_REGISTRY.getKey(processType);
	}

	public static <T extends Process> ProcessType<T> create(Supplier<T> factory) {
		return new ProcessType<T>(factory);
	}

	public T create() {
		return factory.get();
	}

	public Optional<Process> load(ValueInput input) {
		return Util.ifElse(by(input).map(type -> type.create()), process -> process.load(input),
				() -> SurgeofKhroma.LOGGER.warn("Invalid process type with id {}", input.getStringOr("id", "[invalid]")));
	}

	public static Optional<ProcessType<?>> by(ValueInput input) {
		return input.read("id", CODEC);
	}
}
