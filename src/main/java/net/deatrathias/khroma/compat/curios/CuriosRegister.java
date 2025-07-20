package net.deatrathias.khroma.compat.curios;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.items.renderer.ChromaticGlassesRenderer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class CuriosRegister {
	@OnlyIn(Dist.CLIENT)
	public static void registerCurioRenderer() {
		ICurioRenderer.register(RegistryReference.ITEM_CHROMATIC_GLASSES.get(), () -> new ChromaticGlassesRenderer());
	}

	public static void registerCurioCapabilities(RegisterCapabilitiesEvent event) {
		event.registerItem(CuriosCapability.ITEM, (stack, context) -> new ICurio() {
			@Override
			public ItemStack getStack() {
				return stack;
			}

			@Override
			public boolean canEquipFromUse(SlotContext slotContext) {
				return true;
			}
		}, RegistryReference.ITEM_CHROMATIC_GLASSES);
	}
}
