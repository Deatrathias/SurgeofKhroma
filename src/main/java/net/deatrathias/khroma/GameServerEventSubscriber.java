package net.deatrathias.khroma;

import net.deatrathias.khroma.entities.KhromaNodeEntity;
import net.deatrathias.khroma.khroma.KhromaNetwork;
import net.deatrathias.khroma.khroma.KhromaNode;
import net.deatrathias.khroma.registries.RegistryReference;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = SurgeofKhroma.MODID)
public class GameServerEventSubscriber {

	@SubscribeEvent
	private static void serverStarted(ServerStartedEvent event) {

	}

	@SubscribeEvent
	private static void levelLoadEvent(LevelEvent.Load event) {
		if (event.getLevel() instanceof ServerLevel serverLevel && !KhromaNetwork.isLevelLoaded(serverLevel)) {
			var data = serverLevel.getDataStorage().computeIfAbsent(KhromaNetwork.NetworkSavedData.ID);
			for (var provider : data.getProviders()) {
				KhromaNetwork.create(serverLevel, provider);
			}
		}
	}

	@SubscribeEvent
	private static void levelTickPost(LevelTickEvent.Post event) {
		if (event.getLevel().isClientSide)
			return;
		KhromaNetwork.updateNetworksForLevel(event.getLevel());
	}

	@SubscribeEvent
	private static void chunkLoadEvent(ChunkEvent.Load event) {
		if (event.getLevel().isClientSide())
			return;

		var data = event.getChunk().getData(RegistryReference.KHROMA_BIOME_DATA);
		if (!data.isGenerated()) {
			data.generateNode(event.getLevel(), event.getChunk());
			event.getChunk().setData(RegistryReference.KHROMA_BIOME_DATA, data);
			if (data.getNode().isPresent()) {
				KhromaNode node = data.getNode().get();
				event.getLevel().addFreshEntity(KhromaNodeEntity.create((Level) event.getLevel(), node.getPosition(), node.getKhroma(), node.getLevel()));
			}
		}
	}

	@SubscribeEvent
	private static void blockDrops(BlockDropsEvent event) {
		if (event.getBreaker() instanceof Player player) {
			if (player.getAttributeValue(RegistryReference.ATTRIBUTE_TELEPORT_DROPS) > 0) {
				var iter = event.getDrops().iterator();
				while (iter.hasNext()) {
					var drop = iter.next();
					drop.setNoPickUpDelay();
					int count = drop.getItem().getCount();
					drop.playerTouch(player);
					if (drop.isRemoved() || drop.getItem().getCount() != count)
						((ServerPlayer) player).connection.send(new ClientboundSoundEntityPacket(Holder.direct(SoundEvents.ITEM_PICKUP), SoundSource.PLAYERS, player, 0.2f,
								(player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 1.4F + 2.0F, 0));
					if (drop.isRemoved())
						iter.remove();
					else
						drop.setDefaultPickUpDelay();
				}
			}
		}
	}

	@SubscribeEvent
	private static void livingDrops(LivingDropsEvent event) {
		if (event.getSource() != null && event.getSource().getEntity() != null && event.getSource().getEntity() instanceof Player player) {
			if (player.getAttributeValue(RegistryReference.ATTRIBUTE_TELEPORT_DROPS) > 0) {
				var iter = event.getDrops().iterator();
				while (iter.hasNext()) {
					var drop = iter.next();
					drop.setNoPickUpDelay();
					int count = drop.getItem().getCount();
					drop.playerTouch(player);
					if (drop.isRemoved() || drop.getItem().getCount() != count)
						((ServerPlayer) player).connection.send(new ClientboundSoundEntityPacket(Holder.direct(SoundEvents.ITEM_PICKUP), SoundSource.PLAYERS, player, 0.2f,
								(player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 1.4F + 2.0F, 0));
					if (drop.isRemoved())
						iter.remove();
					else
						drop.setDefaultPickUpDelay();
				}
			}
		}
	}
}
