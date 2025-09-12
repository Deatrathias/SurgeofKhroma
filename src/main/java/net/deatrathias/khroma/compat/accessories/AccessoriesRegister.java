package net.deatrathias.khroma.compat.accessories;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import io.wispforest.accessories.api.client.AccessoriesRendererRegistry;
import io.wispforest.accessories.api.components.AccessoriesDataComponents;
import io.wispforest.accessories.api.components.AccessoryItemAttributeModifiers;
import net.deatrathias.khroma.registries.ItemReference;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

public class AccessoriesRegister {
	public static void registerAccessoriesRenderer() {
		AccessoriesRendererRegistry.bindItemToRenderer(ItemReference.CHROMATIC_GLASSES.get(), ItemReference.CHROMATIC_GLASSES.getKey().location(), () -> new ChromaticGlassesRenderer());
		AccessoriesRendererRegistry.bindItemToRenderer(ItemReference.FEATHERED_BOOTS.get(), ItemReference.FEATHERED_BOOTS.getKey().location(), () -> new FeatheredBootsRenderer<>());
		AccessoriesRendererRegistry.bindItemToRenderer(ItemReference.ANKLETS_OF_MOTION.get(), ItemReference.ANKLETS_OF_MOTION.getKey().location(), () -> new AnkletsOfMotionRenderer());
	}

	public static Item.Properties addModifiers(Item.Properties properties, List<Pair<Holder<Attribute>, AttributeModifier>> modifiers, String slot) {
		var builder = AccessoryItemAttributeModifiers.builder();
		for (var modifier : modifiers)
			builder.addForSlot(modifier.getFirst(), modifier.getSecond(), slot, false);

		return properties.component(AccessoriesDataComponents.ATTRIBUTES, builder.build());
	}
}
