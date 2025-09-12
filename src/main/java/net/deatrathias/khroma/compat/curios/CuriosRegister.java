package net.deatrathias.khroma.compat.curios;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.deatrathias.khroma.client.rendering.items.ChromaticGlassesRenderer;
import net.deatrathias.khroma.registries.ItemReference;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import top.theillusivec4.curios.api.CurioAttributeModifiers;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.CuriosDataComponents;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class CuriosRegister {
	public static void registerCurioRenderer() {
		ICurioRenderer.register(ItemReference.CHROMATIC_GLASSES.get(), () -> new ChromaticGlassesRenderer());
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
		}, ItemReference.CHROMATIC_GLASSES);
	}

	public static Item.Properties addModifiers(Item.Properties properties, List<Pair<Holder<Attribute>, AttributeModifier>> modifiers, String slot) {
		var builder = CurioAttributeModifiers.builder();
		for (var modifier : modifiers)
			builder.addModifier(modifier.getFirst(), modifier.getSecond(), slot);

		return properties.component(CuriosDataComponents.ATTRIBUTE_MODIFIERS, builder.build());
	}
}
