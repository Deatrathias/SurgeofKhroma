package net.deatrathias.khroma.registries;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import io.wispforest.accessories.Accessories;
import io.wispforest.accessories.api.data.AccessoriesBaseData;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.compat.accessories.AccessoriesRegister;
import net.deatrathias.khroma.compat.curios.CuriosRegister;
import net.deatrathias.khroma.items.FeatheredBootsItem;
import net.deatrathias.khroma.items.GuideItem;
import net.deatrathias.khroma.items.KhrometalBlackPickaxeItem;
import net.deatrathias.khroma.items.KhrometalBlackSwordItem;
import net.deatrathias.khroma.items.KhrometalGreenToolItem;
import net.deatrathias.khroma.items.KhrometalWhitePickaxeItem;
import net.deatrathias.khroma.items.SpannerItem;
import net.deatrathias.khroma.items.WarpCanisterItem;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import top.theillusivec4.curios.api.CuriosResources;

public final class ItemReference {

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SurgeofKhroma.MODID);

	public static final ToolMaterial RED_KHROMETAL_TIER = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 250, 6, 5, 14, TagReference.Items.KHROMETAL_INGOT_RED);
	public static final ToolMaterial GREEN_KHROMETAL_TIER = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 2000, 6, 2, 14, TagReference.Items.KHROMETAL_INGOT_GREEN);
	public static final ToolMaterial BLUE_KHROMETAL_TIER = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 12, 2, 24, TagReference.Items.KHROMETAL_INGOT_BLUE);
	public static final ToolMaterial WHITE_KHROMETAL_TIER = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 6, 2, 14, TagReference.Items.KHROMETAL_INGOT_WHITE);
	public static final ToolMaterial BLACK_KHROMETAL_TIER = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 6, 2, 14, TagReference.Items.KHROMETAL_INGOT_BLACK);

	public static final DeferredItem<Item> KHROMANCER_ARCHIVE = ITEMS.register("khromancer_archive", registryName -> new GuideItem(itemProps(registryName).stacksTo(1)));

	public static final DeferredItem<Item> RAW_CHROMIUM = ITEMS.registerSimpleItem("raw_chromium");

	public static final DeferredItem<Item> CHROMIUM_INGOT = ITEMS.registerSimpleItem("chromium_ingot");
	public static final DeferredItem<Item> CHROMIUM_NUGGET = ITEMS.registerSimpleItem("chromium_nugget");
	public static final DeferredItem<Item> KHROMETAL_INGOT_RED = ITEMS.registerSimpleItem("red_khrometal_ingot");
	public static final DeferredItem<Item> KHROMETAL_INGOT_GREEN = ITEMS.registerSimpleItem("green_khrometal_ingot");
	public static final DeferredItem<Item> KHROMETAL_INGOT_BLUE = ITEMS.registerSimpleItem("blue_khrometal_ingot");
	public static final DeferredItem<Item> KHROMETAL_INGOT_WHITE = ITEMS.registerSimpleItem("white_khrometal_ingot");
	public static final DeferredItem<Item> KHROMETAL_INGOT_BLACK = ITEMS.registerSimpleItem("black_khrometal_ingot");
	public static final DeferredItem<Item> CHROMATIC_NUCLEUS = ITEMS.registerSimpleItem("chromatic_nucleus");

	public static final DeferredItem<Item> CHROMATIC_GLASSES = ITEMS.register("chromatic_glasses", registryName -> new Item(accessoryAttributes(registryName,
			List.of(Pair.of(RegistryReference.ATTRIBUTE_CAN_SEE_NODES, new AttributeModifier(registryName, 1, Operation.ADD_VALUE))), "eyes", AccessoriesBaseData.FACE_SLOT, EquipmentSlot.HEAD)));

	public static final DeferredItem<Item> KHROMETAL_SPANNER = ITEMS.registerItem("khrometal_spanner",
			props -> new SpannerItem(
					SpannerItem.spanner(props).stacksTo(1).component(RegistryReference.DATA_COMPONENT_SPANNER_COLORS, new SpannerItem.SpannerColors(0xFF808080, 0xFF808080, 0xFF808080, 0xFF808080))));
	public static final DeferredItem<Item> KHROMETAL_RED_SWORD = ITEMS.registerItem("red_khrometal_sword", props -> new Item(props.sword(RED_KHROMETAL_TIER, 3, -2.4f)));
	public static final DeferredItem<Item> KHROMETAL_RED_PICKAXE = ITEMS.registerItem("red_khrometal_pickaxe", props -> new Item(props.pickaxe(RED_KHROMETAL_TIER, 1, -2)));
	public static final DeferredItem<Item> KHROMETAL_GREEN_SWORD = ITEMS.registerItem("green_khrometal_sword", props -> new KhrometalGreenToolItem(props.sword(GREEN_KHROMETAL_TIER, 3, -2.4f)));
	public static final DeferredItem<Item> KHROMETAL_GREEN_PICKAXE = ITEMS.registerItem("green_khrometal_pickaxe",
			props -> new KhrometalGreenToolItem(props.pickaxe(GREEN_KHROMETAL_TIER, 1, -2f)));
	public static final DeferredItem<Item> KHROMETAL_BLUE_SWORD = ITEMS.registerItem("blue_khrometal_sword", props -> new Item(props.sword(BLUE_KHROMETAL_TIER, 3, -0.4f)));
	public static final DeferredItem<Item> KHROMETAL_BLUE_PICKAXE = ITEMS.registerItem("blue_khrometal_pickaxe", props -> new Item(props.pickaxe(BLUE_KHROMETAL_TIER, 1, 0)));
	public static final DeferredItem<Item> KHROMETAL_WHITE_SWORD = ITEMS.registerItem("white_khrometal_sword", props -> new Item(addAttributeModifer(props.sword(WHITE_KHROMETAL_TIER, 3, -2.4f),
			Attributes.ATTACK_KNOCKBACK, new AttributeModifier(SurgeofKhroma.resource("white_khrometal_bonus"), 4, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)));
	public static final DeferredItem<Item> KHROMETAL_WHITE_PICKAXE = ITEMS.registerItem("white_khrometal_pickaxe",
			props -> new KhrometalWhitePickaxeItem(addAttributeModifer(props.pickaxe(WHITE_KHROMETAL_TIER, 1, -2f), Attributes.ATTACK_KNOCKBACK,
					new AttributeModifier(SurgeofKhroma.resource("white_khrometal_bonus"), 4, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)));
	public static final DeferredItem<Item> KHROMETAL_BLACK_SWORD = ITEMS.registerItem("black_khrometal_sword",
			props -> new KhrometalBlackSwordItem(addAttributeModifer(props.sword(BLACK_KHROMETAL_TIER, 3, -2.4f), RegistryReference.ATTRIBUTE_TELEPORT_DROPS,
					new AttributeModifier(SurgeofKhroma.resource("black_khrometal_bonus"), 1, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)));
	public static final DeferredItem<Item> KHROMETAL_BLACK_PICKAXE = ITEMS.registerItem("black_khrometal_pickaxe",
			props -> new KhrometalBlackPickaxeItem(addAttributeModifer(props.pickaxe(BLACK_KHROMETAL_TIER, 1, -2f), RegistryReference.ATTRIBUTE_TELEPORT_DROPS,
					new AttributeModifier(SurgeofKhroma.resource("black_khrometal_bonus"), 1, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)));

	public static final DeferredItem<Item> WARP_CANISTER = ITEMS.register("warp_canister", registryName -> new WarpCanisterItem(itemProps(registryName).stacksTo(1)));

	public static final DeferredItem<Item> FEATHERED_BOOTS = ITEMS.register("feathered_boots",
			registryName -> new FeatheredBootsItem(accessoryAttributes(registryName, List.of(
					Pair.of(Attributes.JUMP_STRENGTH, new AttributeModifier(registryName, 0.2, Operation.ADD_VALUE)),
					Pair.of(Attributes.SAFE_FALL_DISTANCE, new AttributeModifier(registryName, 3, Operation.ADD_VALUE)),
					Pair.of(Attributes.STEP_HEIGHT, new AttributeModifier(registryName, 1, Operation.ADD_VALUE))),
					"feet", AccessoriesBaseData.SHOES_SLOT, EquipmentSlot.FEET)));

	public static final DeferredItem<Item> ANKLETS_OF_MOTION = ITEMS.register("anklets_of_motion",
			registryName -> new Item(accessoryAttributes(registryName, List.of(
					Pair.of(Attributes.MOVEMENT_SPEED, new AttributeModifier(registryName, 0.04, Operation.ADD_VALUE)),
					Pair.of(NeoForgeMod.SWIM_SPEED, new AttributeModifier(registryName, 1, Operation.ADD_VALUE))),
					"legs", AccessoriesBaseData.ANKLET_SLOT, EquipmentSlot.LEGS)));

	public static final DeferredItem<Item> STRIX_SPAWN_EGG = ITEMS.registerItem("strix_spawn_egg", SpawnEggItem::new, props -> props.spawnEgg(EntityReference.STRIX.get()));

	public static Item.Properties itemProps(ResourceLocation registryName) {
		return new Properties().setId(ResourceKey.create(Registries.ITEM, registryName));
	}

	public static Item.Properties addAttributeModifer(Item.Properties props, Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slot) {
		ItemAttributeModifiers attributes = (ItemAttributeModifiers) props.components.map.get(DataComponents.ATTRIBUTE_MODIFIERS);
		return props.attributes(attributes.withModifierAdded(attribute, modifier, slot));
	}

	public static Item.Properties accessoryAttributes(ResourceLocation registryName, List<Pair<Holder<Attribute>, AttributeModifier>> modifiers, String curioSlot,
			String accessorySlot,
			EquipmentSlot equipmentSlot) {
		var properties = itemProps(registryName).stacksTo(1);
		if (ModList.get().isLoaded(Accessories.MODID)) {
			return AccessoriesRegister.addModifiers(properties, modifiers, accessorySlot);
		} else if (ModList.get().isLoaded(CuriosResources.MOD_ID)) {
			return CuriosRegister.addModifiers(properties, modifiers, curioSlot);
		} else {
			var builder = ItemAttributeModifiers.builder();
			for (var modifier : modifiers)
				builder.add(modifier.getFirst(), modifier.getSecond(), EquipmentSlotGroup.ARMOR);

			return properties.component(DataComponents.EQUIPPABLE, Equippable.builder(equipmentSlot).setAsset(ResourceKey.create(EquipmentAssets.ROOT_ID, registryName)).build())
					.attributes(builder.build());

		}
	}
}
