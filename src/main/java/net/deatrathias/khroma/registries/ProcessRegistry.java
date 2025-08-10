package net.deatrathias.khroma.registries;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.processing.ProcessType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class ProcessRegistry {
	public static final ResourceKey<Registry<ProcessType<?>>> PROCESS_TYPE_KEY = ResourceKey.createRegistryKey(SurgeofKhroma.resource("process_types"));
	public static final Registry<ProcessType<?>> PROCESS_TYPE_REGISTRY = new RegistryBuilder<>(PROCESS_TYPE_KEY).create();

	public static final DeferredRegister<ProcessType<?>> PROCESS_TYPES = DeferredRegister.create(PROCESS_TYPE_REGISTRY, SurgeofKhroma.MODID);
}
