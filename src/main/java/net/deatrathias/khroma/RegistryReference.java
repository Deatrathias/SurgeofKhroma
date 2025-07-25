package net.deatrathias.khroma;

import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;

import net.deatrathias.khroma.blockentities.KhromaApertureBlockEntity;
import net.deatrathias.khroma.blockentities.KhromaImbuerBlockEntity;
import net.deatrathias.khroma.blocks.KhromaApertureBlock;
import net.deatrathias.khroma.blocks.KhromaCombinerBlock;
import net.deatrathias.khroma.blocks.KhromaDissipatorBlock;
import net.deatrathias.khroma.blocks.KhromaImbuerBlock;
import net.deatrathias.khroma.blocks.KhromaLineBlock;
import net.deatrathias.khroma.blocks.KhromaMachineBlock;
import net.deatrathias.khroma.blocks.KhromaProviderBlock;
import net.deatrathias.khroma.blocks.KhromaSeparatorBlock;
import net.deatrathias.khroma.blocks.KhrometalBlackBlock;
import net.deatrathias.khroma.blocks.KhrometalBlueBlock;
import net.deatrathias.khroma.blocks.KhrometalGreenBlock;
import net.deatrathias.khroma.blocks.KhrometalRedBlock;
import net.deatrathias.khroma.blocks.KhrometalWhiteBlock;
import net.deatrathias.khroma.blocks.NodeCollectorBlock;
import net.deatrathias.khroma.blocks.items.KromaLineBlockItem;
import net.deatrathias.khroma.blocks.items.NodeCollectorBlockItem;
import net.deatrathias.khroma.effects.PullDownMobEffect;
import net.deatrathias.khroma.entities.KhromaNodeEntity;
import net.deatrathias.khroma.gui.KhromaApertureMenu;
import net.deatrathias.khroma.gui.KhromaImbuerMenu;
import net.deatrathias.khroma.items.KhrometalBlackPickaxeItem;
import net.deatrathias.khroma.items.KhrometalBlackSwordItem;
import net.deatrathias.khroma.items.KhrometalGreenToolItem;
import net.deatrathias.khroma.items.KhrometalWhitePickaxeItem;
import net.deatrathias.khroma.items.SpannerItem;
import net.deatrathias.khroma.khroma.KhromaBiomeData;
import net.deatrathias.khroma.particles.KhromaParticleOption;
import net.deatrathias.khroma.recipes.CraftingSpannerRecipe;
import net.deatrathias.khroma.recipes.KhromaImbuementRecipe;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.BooleanAttribute;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class RegistryReference {

	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SurgeofKhroma.MODID);
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SurgeofKhroma.MODID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SurgeofKhroma.MODID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, SurgeofKhroma.MODID);
	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, SurgeofKhroma.MODID);
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SurgeofKhroma.MODID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, SurgeofKhroma.MODID);
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, SurgeofKhroma.MODID);
	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, SurgeofKhroma.MODID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SurgeofKhroma.MODID);
	public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, SurgeofKhroma.MODID);
	public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SurgeofKhroma.MODID);
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, SurgeofKhroma.MODID);

	public static final Holder<Attribute> ATTRIBUTE_TELEPORT_DROPS = ATTRIBUTES.register("teleport_drops",
			() -> new BooleanAttribute("attribute." + SurgeofKhroma.MODID + ".teleport_drops", false).setSyncable(true));
	public static final Holder<Attribute> ATTRIBUTE_CAN_SEE_NODES = ATTRIBUTES.register("can_see_nodes",
			() -> new BooleanAttribute("attribute." + SurgeofKhroma.MODID + ".can_see_nodes", false).setSyncable(true));

	/**
	 * 
	 * TIERS
	 * 
	 */
	public static final ToolMaterial RED_KHROMETAL_TIER = new ToolMaterial(BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 250, 6, 5, 14, TagReference.ITEM_KHROMETAL_INGOT_RED);
	public static final ToolMaterial GREEN_KHROMETAL_TIER = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 2000, 6, 2, 14, TagReference.ITEM_KHROMETAL_INGOT_GREEN);
	public static final ToolMaterial BLUE_KHROMETAL_TIER = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 12, 2, 24, TagReference.ITEM_KHROMETAL_INGOT_BLUE);
	public static final ToolMaterial WHITE_KHROMETAL_TIER = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 6, 2, 14, TagReference.ITEM_KHROMETAL_INGOT_WHITE);
	public static final ToolMaterial BLACK_KHROMETAL_TIER = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL, 250, 6, 2, 14, TagReference.ITEM_KHROMETAL_INGOT_BLACK);

	/**
	 * 
	 * DATA COMPONENTS
	 * 
	 */
	public static final Supplier<DataComponentType<SpannerItem.SpannerColors>> DATA_COMPONENT_SPANNER_COLORS = DATA_COMPONENT_TYPES.registerComponentType("spanner_colors",
			builder -> builder.persistent(SpannerItem.SpannerColors.CODEC).networkSynchronized(SpannerItem.SpannerColors.STREAM_CODEC));

	/**
	 * 
	 * BLOCKS
	 * 
	 */
	public static final DeferredBlock<Block> BLOCK_CHROMIUM_ORE = BLOCKS.register("chromium_ore", registryName -> new DropExperienceBlock(ConstantInt.of(0),
			blockProps(registryName).mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F, 3.0F)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_CHROMIUM_ORE = ITEMS.registerSimpleBlockItem(BLOCK_CHROMIUM_ORE);

	public static final DeferredBlock<Block> BLOCK_DEEPSLATE_CHROMIUM_ORE = BLOCKS.register("deepslate_chromium_ore", registryName -> new DropExperienceBlock(ConstantInt.of(0),
			blockProps(registryName).mapColor(MapColor.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(4.5F, 3.0F).sound(SoundType.DEEPSLATE)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_DEEPSLATE_CHROMIUM_ORE = ITEMS.registerSimpleBlockItem(BLOCK_DEEPSLATE_CHROMIUM_ORE);

	public static final DeferredBlock<Block> BLOCK_CHROMIUM_BLOCK = BLOCKS.register("chromium_block", registryName -> new Block(
			blockProps(registryName).mapColor(MapColor.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_CHROMIUM_BLOCK = ITEMS.registerSimpleBlockItem(BLOCK_CHROMIUM_BLOCK);

	public static final DeferredBlock<Block> BLOCK_RAW_CHROMIUM_BLOCK = BLOCKS.register("raw_chromium_block",
			registryName -> new Block(blockProps(registryName).mapColor(MapColor.RAW_IRON).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(5.0F, 6.0F)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_RAW_CHROMIUM_BLOCK = ITEMS.registerSimpleBlockItem(BLOCK_RAW_CHROMIUM_BLOCK);

	public static final DeferredBlock<Block> BLOCK_KHROMETAL_BLOCK_RED = BLOCKS.register("khrometal_block_red", registryName -> new KhrometalRedBlock(
			blockProps(registryName).mapColor(MapColor.COLOR_RED).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_KHROMETAL_BLOCK_RED = ITEMS.registerSimpleBlockItem(BLOCK_KHROMETAL_BLOCK_RED);
	public static final DeferredBlock<Block> BLOCK_KHROMETAL_BLOCK_GREEN = BLOCKS.register("khrometal_block_green", registryName -> new KhrometalGreenBlock(
			blockProps(registryName).mapColor(MapColor.COLOR_GREEN).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_KHROMETAL_BLOCK_GREEN = ITEMS.registerSimpleBlockItem(BLOCK_KHROMETAL_BLOCK_GREEN);
	public static final DeferredBlock<Block> BLOCK_KHROMETAL_BLOCK_BLUE = BLOCKS.register("khrometal_block_blue", registryName -> new KhrometalBlueBlock(
			blockProps(registryName).mapColor(MapColor.COLOR_BLUE).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_KHROMETAL_BLOCK_BLUE = ITEMS.registerSimpleBlockItem(BLOCK_KHROMETAL_BLOCK_BLUE);
	public static final DeferredBlock<Block> BLOCK_KHROMETAL_BLOCK_WHITE = BLOCKS.register("khrometal_block_white", registryName -> new KhrometalWhiteBlock(
			blockProps(registryName).mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_KHROMETAL_BLOCK_WHITE = ITEMS.registerSimpleBlockItem(BLOCK_KHROMETAL_BLOCK_WHITE);
	public static final DeferredBlock<Block> BLOCK_KHROMETAL_BLOCK_BLACK = BLOCKS.register("khrometal_block_black", registryName -> new KhrometalBlackBlock(
			blockProps(registryName).mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SoundType.METAL)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_KHROMETAL_BLOCK_BLACK = ITEMS.registerSimpleBlockItem(BLOCK_KHROMETAL_BLOCK_BLACK);

	public static final DeferredBlock<Block> BLOCK_KHROMA_LINE = BLOCKS.register("khroma_line",
			registryName -> new KhromaLineBlock(blockProps(registryName).mapColor(MapColor.METAL).strength(0.1F, 1.0F).sound(SoundType.METAL).pushReaction(PushReaction.DESTROY)));
	public static final DeferredItem<Item> ITEM_BLOCK_KHROMA_LINE = ITEMS.register("khroma_line",
			registryName -> new KromaLineBlockItem(BLOCK_KHROMA_LINE.get(), itemProps(registryName).useBlockDescriptionPrefix()));

	public static final DeferredBlock<Block> BLOCK_NODE_COLLECTOR = BLOCKS.register("node_collector",
			registryName -> new NodeCollectorBlock(blockProps(registryName).mapColor(MapColor.METAL).strength(10f, 6f).sound(SoundType.METAL).pushReaction(PushReaction.BLOCK).noOcclusion()));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_NODE_COLLECTOR = ITEMS.register("node_collector",
			registryName -> new NodeCollectorBlockItem(BLOCK_NODE_COLLECTOR.get(), itemProps(registryName).useBlockDescriptionPrefix()));

	public static final DeferredBlock<Block> BLOCK_KHROMA_PROVIDER = BLOCKS.register("khroma_provider", registryName -> new KhromaProviderBlock(blockProps(registryName)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_KHROMA_PROVIDER = ITEMS.registerSimpleBlockItem(BLOCK_KHROMA_PROVIDER);

	public static final DeferredBlock<Block> BLOCK_KHROMA_MACHINE = BLOCKS.register("khroma_machine", registryName -> new KhromaMachineBlock(blockProps(registryName)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_KHROMA_MACHINE = ITEMS.registerSimpleBlockItem(BLOCK_KHROMA_MACHINE);

	public static final DeferredBlock<Block> BLOCK_KHROMA_APERTURE = BLOCKS.register("khroma_aperture",
			registryName -> new KhromaApertureBlock(blockProps(registryName).mapColor(MapColor.METAL).strength(1.0f, 2.0f).sound(SoundType.METAL).pushReaction(PushReaction.BLOCK)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_KHROMA_APERTURE = ITEMS.registerSimpleBlockItem(BLOCK_KHROMA_APERTURE);

	public static final DeferredBlock<Block> BLOCK_KHROMA_COMBINER = BLOCKS.register("khroma_combiner",
			registryName -> new KhromaCombinerBlock(blockProps(registryName).mapColor(MapColor.METAL).strength(1.0f, 2.0f).sound(SoundType.METAL).pushReaction(PushReaction.BLOCK)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_KHROMA_COMBINER = ITEMS.registerSimpleBlockItem(BLOCK_KHROMA_COMBINER);

	public static final DeferredBlock<Block> BLOCK_KHROMA_SEPARATOR = BLOCKS.register("khroma_separator",
			registryName -> new KhromaSeparatorBlock(blockProps(registryName).mapColor(MapColor.METAL).strength(1.0f, 2.0f).sound(SoundType.METAL).pushReaction(PushReaction.BLOCK)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_KHROMA_SEPARATOR = ITEMS.registerSimpleBlockItem(BLOCK_KHROMA_SEPARATOR);

	public static final DeferredBlock<Block> BLOCK_KHROMA_DISSIPATOR = BLOCKS.register("khroma_dissipator",
			registryName -> new KhromaDissipatorBlock(blockProps(registryName).mapColor(MapColor.METAL).strength(3.0f, 3.5f).sound(SoundType.METAL).pushReaction(PushReaction.BLOCK)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_KHROMA_DISSIPATOR = ITEMS.registerSimpleBlockItem(BLOCK_KHROMA_DISSIPATOR);

	public static final DeferredBlock<Block> BLOCK_KHROMA_IMBUER = BLOCKS.register("khroma_imbuer",
			registryName -> new KhromaImbuerBlock(blockProps(registryName).mapColor(MapColor.METAL).strength(3.0f, 3.5f).sound(SoundType.METAL).pushReaction(PushReaction.BLOCK)));
	public static final DeferredItem<BlockItem> ITEM_BLOCK_KHROMA_IMBUER = ITEMS.registerSimpleBlockItem(BLOCK_KHROMA_IMBUER);

	/**
	 * 
	 * BLOCK ENTITIES
	 * 
	 */
	public static final Supplier<BlockEntityType<KhromaApertureBlockEntity>> BLOCK_ENTITY_KHROMA_APERTURE = BLOCK_ENTITY_TYPES.register("khroma_aperture_entity",
			() -> (new BlockEntityType<>(KhromaApertureBlockEntity::new, BLOCK_KHROMA_APERTURE.get())));

	public static final Supplier<BlockEntityType<KhromaImbuerBlockEntity>> BLOCK_ENTITY_KHROMA_IMBUER = BLOCK_ENTITY_TYPES.register("khroma_imbuer_entity",
			() -> (new BlockEntityType<>(KhromaImbuerBlockEntity::new, BLOCK_KHROMA_IMBUER.get())));

	/**
	 * 
	 * ITEMS
	 * 
	 */
	public static final DeferredItem<Item> ITEM_RAW_CHROMIUM = ITEMS.registerSimpleItem("raw_chromium");
	public static final DeferredItem<Item> ITEM_CHROMIUM_INGOT = ITEMS.registerSimpleItem("chromium_ingot");
	public static final DeferredItem<Item> ITEM_CHROMIUM_NUGGET = ITEMS.registerSimpleItem("chromium_nugget");
	public static final DeferredItem<Item> ITEM_KHROMETAL_INGOT_RED = ITEMS.registerSimpleItem("khrometal_ingot_red");
	public static final DeferredItem<Item> ITEM_KHROMETAL_INGOT_GREEN = ITEMS.registerSimpleItem("khrometal_ingot_green");
	public static final DeferredItem<Item> ITEM_KHROMETAL_INGOT_BLUE = ITEMS.registerSimpleItem("khrometal_ingot_blue");
	public static final DeferredItem<Item> ITEM_KHROMETAL_INGOT_WHITE = ITEMS.registerSimpleItem("khrometal_ingot_white");
	public static final DeferredItem<Item> ITEM_KHROMETAL_INGOT_BLACK = ITEMS.registerSimpleItem("khrometal_ingot_black");
	public static final DeferredItem<Item> ITEM_CHROMATIC_NUCLEUS = ITEMS.registerSimpleItem("chromatic_nucleus");
	public static final DeferredItem<Item> ITEM_CHROMATIC_GLASSES = ITEMS.registerSimpleItem("chromatic_glasses", new Item.Properties().stacksTo(1)
			.component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD).setAsset(SurgeofKhroma.resourceKey(EquipmentAssets.ROOT_ID, "chromatic_glasses")).build()).attributes(
					ItemAttributeModifiers.builder().add(ATTRIBUTE_CAN_SEE_NODES, new AttributeModifier(SurgeofKhroma.resource("chromatic_glasses"), 1, Operation.ADD_VALUE), EquipmentSlotGroup.HEAD)
							.build()));
	public static final DeferredItem<Item> ITEM_KHROMETAL_SPANNER = ITEMS.registerItem("khrometal_spanner",
			props -> new SpannerItem(SpannerItem.spanner(props).stacksTo(1).component(DATA_COMPONENT_SPANNER_COLORS, new SpannerItem.SpannerColors(0xFF808080, 0xFF808080, 0xFF808080, 0xFF808080))));

	public static final DeferredItem<Item> ITEM_KHROMETAL_RED_SWORD = ITEMS.registerItem("khrometal_red_sword", props -> new Item(props.sword(RED_KHROMETAL_TIER, 3, -2.4f)));
	public static final DeferredItem<Item> ITEM_KHROMETAL_RED_PICKAXE = ITEMS.registerItem("khrometal_red_pickaxe", props -> new Item(props.pickaxe(RED_KHROMETAL_TIER, 1, -2)));

	public static final DeferredItem<Item> ITEM_KHROMETAL_GREEN_SWORD = ITEMS.registerItem("khrometal_green_sword", props -> new KhrometalGreenToolItem(props.sword(GREEN_KHROMETAL_TIER, 3, -2.4f)));
	public static final DeferredItem<Item> ITEM_KHROMETAL_GREEN_PICKAXE = ITEMS.registerItem("khrometal_green_pickaxe",
			props -> new KhrometalGreenToolItem(props.pickaxe(GREEN_KHROMETAL_TIER, 1, -2f)));

	public static final DeferredItem<Item> ITEM_KHROMETAL_BLUE_SWORD = ITEMS.registerItem("khrometal_blue_sword", props -> new Item(props.sword(BLUE_KHROMETAL_TIER, 3, -0.4f)));
	public static final DeferredItem<Item> ITEM_KHROMETAL_BLUE_PICKAXE = ITEMS.registerItem("khrometal_blue_pickaxe", props -> new Item(props.pickaxe(BLUE_KHROMETAL_TIER, 1, 0)));

	public static final DeferredItem<Item> ITEM_KHROMETAL_WHITE_SWORD = ITEMS.registerItem("khrometal_white_sword", props -> new Item(addAttributeModifer(props.sword(WHITE_KHROMETAL_TIER, 3, -2.4f),
			Attributes.ATTACK_KNOCKBACK, new AttributeModifier(SurgeofKhroma.resource("white_khrometal_bonus"), 4, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)));
	public static final DeferredItem<Item> ITEM_KHROMETAL_WHITE_PICKAXE = ITEMS.registerItem("khrometal_white_pickaxe",
			props -> new KhrometalWhitePickaxeItem(addAttributeModifer(props.pickaxe(WHITE_KHROMETAL_TIER, 1, -2f), Attributes.ATTACK_KNOCKBACK,
					new AttributeModifier(SurgeofKhroma.resource("white_khrometal_bonus"), 4, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)));

	public static final DeferredItem<Item> ITEM_KHROMETAL_BLACK_SWORD = ITEMS.registerItem("khrometal_black_sword",
			props -> new KhrometalBlackSwordItem(addAttributeModifer(props.sword(BLACK_KHROMETAL_TIER, 3, -2.4f), ATTRIBUTE_TELEPORT_DROPS,
					new AttributeModifier(SurgeofKhroma.resource("black_khrometal_bonus"), 1, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)));
	public static final DeferredItem<Item> ITEM_KHROMETAL_BLACK_PICKAXE = ITEMS.registerItem("khrometal_black_pickaxe",
			props -> new KhrometalBlackPickaxeItem(addAttributeModifer(props.pickaxe(BLACK_KHROMETAL_TIER, 1, -2f), ATTRIBUTE_TELEPORT_DROPS,
					new AttributeModifier(SurgeofKhroma.resource("black_khrometal_bonus"), 1, Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)));

	/**
	 * 
	 * ENTITIES
	 * 
	 */
	public static final Supplier<EntityType<KhromaNodeEntity>> ENTITY_KHROMA_NODE = ENTITY_TYPES.register("khroma_node",
			() -> EntityType.Builder.of(KhromaNodeEntity::new, MobCategory.MISC).fireImmune().updateInterval(Integer.MAX_VALUE).eyeHeight(0).clientTrackingRange(10).sized(1, 1)
					.build(ResourceKey.create(Registries.ENTITY_TYPE, SurgeofKhroma.resource("khroma_node"))));

	/**
	 * 
	 * MENUS
	 * 
	 */
	public static final Supplier<MenuType<KhromaApertureMenu>> MENU_KHROMA_APERTURE = MENUS.register("khroma_aperture_menu",
			() -> new MenuType<KhromaApertureMenu>(KhromaApertureMenu::new, FeatureFlags.DEFAULT_FLAGS));

	public static final Supplier<MenuType<KhromaImbuerMenu>> MENU_KHROMA_IMBUER = MENUS.register("khroma_imbuer_menu",
			() -> new MenuType<KhromaImbuerMenu>(KhromaImbuerMenu::new, FeatureFlags.DEFAULT_FLAGS));

	// public static final BlockCapability<IKhromaProvider, Direction>
	// KHROMA_PROVIDER_BLOCK =
	// BlockCapability.createSided(SurgeofKhroma.resource("khroma_provider"),
	// IKhromaProvider.class);
	// public static final BlockCapability<IKhromaConsumer, Direction>
	// KHROMA_CONSUMER_BLOCK =
	// BlockCapability.createSided(SurgeofKhroma.resource("khroma_consumer"),
	// IKhromaConsumer.class);

	/**
	 * 
	 * ATTACHMENTS
	 * 
	 */
	public static final Supplier<AttachmentType<KhromaBiomeData>> KHROMA_BIOME_DATA = ATTACHMENT_TYPES.register("khroma_biome_data",
			() -> AttachmentType.serializable(() -> new KhromaBiomeData()).build());

	/**
	 * 
	 * RECIPES
	 * 
	 */
	public static final Supplier<RecipeType<KhromaImbuementRecipe>> RECIPE_KHROMA_IMBUEMENT = RECIPE_TYPES.register("khroma_imbuement",
			() -> RecipeType.simple(SurgeofKhroma.resource("khroma_imbuement")));
	public static final Supplier<RecipeSerializer<KhromaImbuementRecipe>> RECIPE_SERIALIZER_KHROMA_IMBUEMENT = RECIPE_SERIALIZERS.register("khroma_imbuement",
			KhromaImbuementRecipe.KhromaImbuementSerializer::new);
	public static final Supplier<RecipeType<CraftingSpannerRecipe>> RECIPE_CRAFTING_SPANNER = RECIPE_TYPES.register("crafting_spanner",
			() -> RecipeType.simple(SurgeofKhroma.resource("crafting_spanner")));
	public static final Supplier<RecipeSerializer<CraftingSpannerRecipe>> RECIPE_SERIALIZER_CRAFTING_SPANNER = RECIPE_SERIALIZERS.register("crafting_spanner",
			CraftingSpannerRecipe.Serializer::new);

	/**
	 * 
	 * DAMAGE TYPES
	 * 
	 */

	public static final ResourceKey<DamageType> DAMAGE_RED_KHROMETAL_BLOCK = ResourceKey.create(Registries.DAMAGE_TYPE, SurgeofKhroma.resource("red_khrometal_block"));

	/**
	 * 
	 * MOB EFFECTS
	 * 
	 */
	public static final Holder<MobEffect> EFFECT_TELEPORT_SICKNESS = MOB_EFFECTS.register("teleport_sickness",
			() -> new MobEffect(MobEffectCategory.HARMFUL, 0x00000000, ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, ARGB.color(0, 0))) {
			});
	public static final Holder<MobEffect> EFFECT_PULL_DOWN = MOB_EFFECTS.register("pull_down", () -> new PullDownMobEffect(MobEffectCategory.HARMFUL, 0xFF8d6700));

	/**
	 * 
	 * PARTICLES
	 * 
	 */
	public static final Supplier<ParticleType<KhromaParticleOption>> PARTICLE_KHROMA = PARTICLE_TYPES.register("particle_khroma",
			registryName -> RegistryReference.registerParticle(false, type -> KhromaParticleOption.CODEC, type -> KhromaParticleOption.STREAM_CODEC));

	/**
	 * 
	 * CREATIVE TAB
	 * 
	 */
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SURGEOFKHROMA_TAB = CREATIVE_MODE_TABS.register("surgeofkhroma_tab",
			() -> CreativeModeTab.builder().title(Component.translatable("itemGroup.surgeofkhroma")).withTabsBefore(CreativeModeTabs.COMBAT)
					.icon(() -> ITEM_CHROMATIC_NUCLEUS.get().getDefaultInstance()).displayItems(RegistryReference::tabItemsToDisplay).build());

	public static void tabItemsToDisplay(ItemDisplayParameters parameters, Output output) {
		output.accept(ITEM_CHROMIUM_INGOT);
		output.accept(ITEM_BLOCK_CHROMIUM_ORE);
		output.accept(ITEM_BLOCK_DEEPSLATE_CHROMIUM_ORE);
		output.accept(ITEM_BLOCK_CHROMIUM_BLOCK);
		output.accept(ITEM_BLOCK_RAW_CHROMIUM_BLOCK);
		output.accept(ITEM_RAW_CHROMIUM);
		output.accept(ITEM_CHROMIUM_NUGGET);
		output.accept(ITEM_KHROMETAL_INGOT_RED);
		output.accept(ITEM_KHROMETAL_INGOT_GREEN);
		output.accept(ITEM_KHROMETAL_INGOT_BLUE);
		output.accept(ITEM_KHROMETAL_INGOT_WHITE);
		output.accept(ITEM_KHROMETAL_INGOT_BLACK);
		output.accept(ITEM_CHROMATIC_NUCLEUS);
		output.accept(ITEM_CHROMATIC_GLASSES);
		output.accept(ITEM_KHROMETAL_RED_SWORD);
		output.accept(ITEM_KHROMETAL_RED_PICKAXE);
		output.accept(ITEM_KHROMETAL_GREEN_SWORD);
		output.accept(ITEM_KHROMETAL_GREEN_PICKAXE);
		output.accept(ITEM_KHROMETAL_BLUE_SWORD);
		output.accept(ITEM_KHROMETAL_BLUE_PICKAXE);
		output.accept(ITEM_KHROMETAL_WHITE_SWORD);
		output.accept(ITEM_KHROMETAL_WHITE_PICKAXE);
		output.accept(ITEM_KHROMETAL_BLACK_SWORD);
		output.accept(ITEM_KHROMETAL_BLACK_PICKAXE);
		output.accept(ITEM_KHROMETAL_SPANNER);
		output.accept(ITEM_BLOCK_KHROMETAL_BLOCK_RED);
		output.accept(ITEM_BLOCK_KHROMETAL_BLOCK_GREEN);
		output.accept(ITEM_BLOCK_KHROMETAL_BLOCK_BLUE);
		output.accept(ITEM_BLOCK_KHROMETAL_BLOCK_WHITE);
		output.accept(ITEM_BLOCK_KHROMETAL_BLOCK_BLACK);
		output.accept(ITEM_BLOCK_KHROMA_LINE);
		output.accept(ITEM_BLOCK_KHROMA_APERTURE);
		output.accept(ITEM_BLOCK_KHROMA_COMBINER);
		output.accept(ITEM_BLOCK_KHROMA_SEPARATOR);
		output.accept(ITEM_BLOCK_KHROMA_DISSIPATOR);
		output.accept(ITEM_BLOCK_NODE_COLLECTOR);
		output.accept(ITEM_BLOCK_KHROMA_IMBUER);
	}

	private static BlockBehaviour.Properties blockProps(ResourceLocation registryName) {
		return BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, registryName));
	}

	private static Item.Properties itemProps(ResourceLocation registryName) {
		return new Properties().setId(ResourceKey.create(Registries.ITEM, registryName));
	}

	public static Item.Properties addAttributeModifer(Item.Properties props, Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlotGroup slot) {
		ItemAttributeModifiers attributes = (ItemAttributeModifiers) props.components.map.get(DataComponents.ATTRIBUTE_MODIFIERS);
		return props.attributes(attributes.withModifierAdded(attribute, modifier, slot));
	}

	private static <T extends ParticleOptions> ParticleType<T> registerParticle(boolean overrideLimitter, final Function<ParticleType<T>, MapCodec<T>> codecGetter,
			final Function<ParticleType<T>, StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodecGetter) {
		return new ParticleType<T>(overrideLimitter) {
			@Override
			public MapCodec<T> codec() {
				return codecGetter.apply(this);
			}

			@Override
			public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
				return streamCodecGetter.apply(this);
			}
		};
	}
}
