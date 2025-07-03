package net.deatrathias.khroma;

import net.deatrathias.khroma.blocks.KhromaLineBlock;
import net.deatrathias.khroma.gui.KhromaApertureScreen;
import net.deatrathias.khroma.khroma.Khroma;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = SurgeofKhroma.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventSubscriber {
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		SurgeofKhroma.LOGGER.info("client started");
	}

	@SubscribeEvent
	public static void registerBlockColorHandles(RegisterColorHandlersEvent.Block event) {
		event.register(((state, level, pos, tintIndex) -> {
			Integer khroma = state.getValue(KhromaLineBlock.KHROMA);
			return Khroma.KhromaColors[khroma];
		}), RegistryReference.BLOCK_KHROMA_LINE.value());
	}

	@SubscribeEvent
	public static void registerScreens(RegisterMenuScreensEvent event) {
		event.register(RegistryReference.MENU_KHROMA_APERTURE.get(), KhromaApertureScreen::new);
	}
}
