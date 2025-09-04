package net.deatrathias.khroma.processing;

import java.util.Optional;
import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.registries.ProcessRegistry;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.ValueInput;

public class ProcessType<T extends BaseProcess> {
	public static final Codec<ProcessType<?>> CODEC = ProcessRegistry.PROCESS_TYPE_REGISTRY.byNameCodec();

	private Function<ProcessType<T>, T> factory;

	public ProcessType(Function<ProcessType<T>, T> factory) {
		this.factory = factory;
	}

	public static ResourceLocation getKey(ProcessType<?> processType) {
		return ProcessRegistry.PROCESS_TYPE_REGISTRY.getKey(processType);
	}

	public T create() {
		return factory.apply(this);
	}

	public static Optional<BaseProcess> load(ValueInput input) {
		return Util.ifElse(by(input).map(type -> type.create()), process -> process.load(input),
				() -> SurgeofKhroma.LOGGER.warn("Invalid process type with id {}", input.getStringOr("id", "[invalid]")));
	}

	public static Optional<ProcessType<?>> by(ValueInput input) {
		return input.read("id", CODEC);
	}
}
