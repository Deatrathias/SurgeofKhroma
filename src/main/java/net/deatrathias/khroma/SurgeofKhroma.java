package net.deatrathias.khroma;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.EntityReference;
import net.deatrathias.khroma.registries.ItemReference;
import net.deatrathias.khroma.registries.ProcessRegistry;
import net.deatrathias.khroma.registries.RecipeReference;
import net.deatrathias.khroma.registries.RegistryReference;
import net.deatrathias.khroma.registries.SoundReference;
import net.deatrathias.khroma.registries.UIReference;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SurgeofKhroma.MODID)
public class SurgeofKhroma {
	// Define mod id in a common place for everything to reference
	public static final String MODID = "surgeofkhroma";
	// Directly reference a slf4j logger
	public static final Logger LOGGER = LogUtils.getLogger();

	public SurgeofKhroma(IEventBus modEventBus, ModContainer modContainer) {
		RegistryReference.ATTRIBUTES.register(modEventBus);
		BlockReference.BLOCKS.register(modEventBus);
		ItemReference.ITEMS.register(modEventBus);
		BlockReference.BLOCK_ENTITY_TYPES.register(modEventBus);
		EntityReference.ENTITY_TYPES.register(modEventBus);
		UIReference.MENUS.register(modEventBus);
		RegistryReference.ENTITY_DATA_SERIALIZERS.register(modEventBus);
		RegistryReference.TRUNK_PLACER_TYPES.register(modEventBus);
		RegistryReference.TREE_DECORATOR_TYPES.register(modEventBus);
		RegistryReference.ATTACHMENT_TYPES.register(modEventBus);
		RecipeReference.RECIPE_BOOK_CATEGORIES.register(modEventBus);
		RecipeReference.RECIPE_TYPES.register(modEventBus);
		RecipeReference.RECIPE_SERIALIZERS.register(modEventBus);
		RegistryReference.CREATIVE_MODE_TABS.register(modEventBus);
		RegistryReference.MOB_EFFECTS.register(modEventBus);
		RegistryReference.DATA_COMPONENT_TYPES.register(modEventBus);
		RegistryReference.PARTICLE_TYPES.register(modEventBus);
		SoundReference.SOUND_EVENTS.register(modEventBus);
		ProcessRegistry.PROCESS_TYPES.register(modEventBus);

		modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
	}

	public static ResourceLocation resource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	public static <T> ResourceKey<T> resourceKey(ResourceKey<? extends Registry<T>> registry, String path) {
		return ResourceKey.create(registry, ResourceLocation.fromNamespaceAndPath(MODID, path));
	}
}
