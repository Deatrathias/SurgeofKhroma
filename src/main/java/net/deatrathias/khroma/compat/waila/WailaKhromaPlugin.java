package net.deatrathias.khroma.compat.waila;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.entities.KhromaNodeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;
import top.theillusivec4.curios.api.CuriosApi;

@WailaPlugin
public class WailaKhromaPlugin implements IWailaPlugin {

	public static class KhromaNodeComponentProvider implements IEntityComponentProvider {
		@Override
		public boolean isRequired() {
			return true;
		}

		@Override
		public void appendTooltip(ITooltip tooltip, EntityAccessor entity, IPluginConfig cofnig) {
			tooltip.add(Component.translatable(((KhromaNodeEntity) entity.getEntity()).getKhroma().getLocalizedName()));
		}

		@Override
		public ResourceLocation getUid() {
			return SurgeofKhroma.resource("khroma_node");
		}

	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerEntityComponent(new KhromaNodeComponentProvider(), KhromaNodeEntity.class);
		registration.addRayTraceCallback((hitResult, accessor, originalAccessor) -> {
			if (accessor instanceof EntityAccessor entityAcc) {
				if (entityAcc.getEntity().getType() == RegistryReference.ENTITY_KHROMA_NODE.get()) {
					Player player = Minecraft.getInstance().player;
					if (player == null)
						return null;
					var inventory = CuriosApi.getCuriosInventory(player);
					if (inventory.isEmpty())
						return null;
					if (inventory.get().isEquipped(RegistryReference.ITEM_CHROMATIC_GLASSES.get()))
						return accessor;
					return null;
				}
			}
			return accessor;
		});
	}
}
