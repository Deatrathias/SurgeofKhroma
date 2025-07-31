package net.deatrathias.khroma.compat.waila;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.entities.KhromaNodeEntity;
import net.deatrathias.khroma.entities.renderer.KhromaNodeEntityRenderer;
import net.deatrathias.khroma.registries.EntityReference;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

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

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerEntityComponent(new KhromaNodeComponentProvider(), KhromaNodeEntity.class);
		registration.addRayTraceCallback((hitResult, accessor, originalAccessor) -> {
			if (accessor instanceof EntityAccessor entityAcc) {
				if (entityAcc.getEntity().getType() == EntityReference.KHROMA_NODE.get()) {
					if (KhromaNodeEntityRenderer.canSeeNodes())
						return accessor;
					return null;
				}
			}
			return accessor;
		});
	}
}
