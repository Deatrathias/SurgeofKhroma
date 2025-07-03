package net.deatrathias.khroma;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SurgeofKhroma.MODID)
public class SurgeofKhroma {
	// Define mod id in a common place for everything to reference
	public static final String MODID = "surgeofkhroma";
	// Directly reference a slf4j logger
	public static final Logger LOGGER = LogUtils.getLogger();

	// Create a Deferred Register to hold CreativeModeTabs which will all be
	// registered under the "surgeofkhroma" namespace
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

	// The constructor for the mod class is the first code that is run when your mod
	// is loaded.
	// FML will recognize some parameter types like IEventBus or ModContainer and
	// pass them in automatically.
	public SurgeofKhroma(IEventBus modEventBus, ModContainer modContainer) {
		// Register the commonSetup method for modloading
		modEventBus.addListener(this::commonSetup);

		RegistryReference.BLOCKS.register(modEventBus);
		RegistryReference.ITEMS.register(modEventBus);
		RegistryReference.BLOCK_ENTITY_TYPES.register(modEventBus);
		RegistryReference.MENUS.register(modEventBus);

		CREATIVE_MODE_TABS.register(modEventBus);

		// Register ourselves for server and other game events we are interested in.
		// Note that this is necessary if and only if we want *this* class
		// (SurgeofKhroma) to respond directly to events.
		// Do not add this line if there are no @SubscribeEvent-annotated functions in
		// this class, like onServerStarting() below.
		NeoForge.EVENT_BUS.register(this);

		// Register our mod's ModConfigSpec so that FML can create and load the config
		// file for us
		modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		LOGGER.info("Setting up...");
		// Some common setup code
		/*
		 * LOGGER.info("HELLO FROM COMMON SETUP");
		 * 
		 * if (Config.LOG_DIRT_BLOCK.getAsBoolean()) { LOGGER.info("DIRT BLOCK >> {}",
		 * BuiltInRegistries.BLOCK.getKey(Blocks.DIRT)); }
		 * 
		 * LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(),
		 * Config.MAGIC_NUMBER.getAsInt());
		 * 
		 * Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
		 */
	}

	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {

	}
}
