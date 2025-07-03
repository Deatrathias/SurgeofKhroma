package net.deatrathias.khroma;

import net.deatrathias.khroma.khroma.IKhromaConsumerBlock;
import net.deatrathias.khroma.khroma.IKhromaProviderBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = SurgeofKhroma.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class CommonEventSubscriber {

	@SubscribeEvent
	private static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerBlock(RegistryReference.KHROMA_PROVIDER_BLOCK, (level, pos, state, be, side) -> (((IKhromaProviderBlock) level.getBlockEntity(pos)).getProvider(level, pos, state, side)),
				RegistryReference.BLOCK_KHROMA_PROVIDER.get(), RegistryReference.BLOCK_KHROMA_APERTURE.get(), RegistryReference.BLOCK_KHROMA_COMBINER.get(),
				RegistryReference.BLOCK_KHROMA_SEPARATOR.get());
		event.registerBlock(RegistryReference.KHROMA_CONSUMER_BLOCK, (level, pos, state, be, side) -> (((IKhromaConsumerBlock) level.getBlockEntity(pos)).getConsumer(level, pos, state, side)),
				RegistryReference.BLOCK_KHROMA_APERTURE.get(), RegistryReference.BLOCK_KHROMA_COMBINER.get(), RegistryReference.BLOCK_KHROMA_MACHINE.get(),
				RegistryReference.BLOCK_KHROMA_SEPARATOR.get());
	}
}
