package net.deatrathias.khroma;

import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = SurgeofKhroma.MODID, bus = EventBusSubscriber.Bus.GAME)
public class GameServerEventSubscriber {

	@SubscribeEvent
	private static void levelTickPost(LevelTickEvent.Post event) {
		if (event.getLevel().isClientSide)
			return;
		KhromaNetwork.updateNetworksForLevel(event.getLevel());
	}
}
