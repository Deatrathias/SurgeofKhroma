package net.deatrathias.khroma.datagen;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.EntityReference;
import net.deatrathias.khroma.registries.ItemReference;
import net.deatrathias.khroma.registries.RegistryReference;
import net.deatrathias.khroma.registries.TagReference;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class LanguageENDataGen extends LanguageProvider {

	private final static Field dataField;

	private final static List<String> connectors = List.of("the", "a", "an", "of", "to", "with", "in", "for", "at", "by");
	
	private Map<String, String> additional;

	static {
		try {
			dataField = LanguageProvider.class.getDeclaredField("data");
			dataField.setAccessible(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public LanguageENDataGen(PackOutput output, Map<String, String> additional) {
		super(output, SurgeofKhroma.MODID, "en_us");
		this.additional = additional;
	}

	@Override
	protected void addTranslations() {
		khromaNames();
		specialBlockNames();
		specialItemNames();
		specialEntityNames();
		tagNames();
		advancementNames();

		var featherclip = SurgeofKhroma.resourceKey(Registries.ENCHANTMENT, "featherclip");
		addFormatted(featherclip);
		add(featherclip, ".desc", "Deals extra damage to birds and pulls targets to the ground.");
		add("subtitles.surgeofkhroma.block.khrometal_block_black.teleport", "Black Khrometal teleports");
		add("subtitles.surgeofkhroma.item.warp_canister.connect", "Warp Canister connects");
		add("subtitles.surgeofkhroma.item.warp_canister.send", "Warp Canister sends");

		addFormatted(SurgeofKhroma.resourceKey(Registries.RECIPE_TYPE, "khroma_imbuement"));
		addFormatted(SurgeofKhroma.resourceKey(Registries.RECIPE_TYPE, "khroma_combining"));
		addFormatted(SurgeofKhroma.resourceKey(Registries.RECIPE_TYPE, "khroma_separating"));

		add("surgeofkhroma.configuration.title", "Surge of Khroma Configuration");
		add("surgeofkhroma.configuration.section.surgeofkhroma.common.toml", "Surge of Khroma Configuration");
		add("surgeofkhroma.configuration.section.surgeofkhroma.common.toml.title", "Surge of Khroma Configuration");
		add("surgeofkhroma.configuration.chunks_per_node", "Chunks per node");
		add("surgeofkhroma.configuration.chunks_per_node.tooltip", "The average number of chunks between each Khroma Node");

		add("itemGroup.surgeofkhroma", "Surge of Khroma");
		add("container.surgeofkhroma.khroma_aperture", "Khroma Aperture");
		add("container.surgeofkhroma.khroma_imbuer", "Khroma Imbuer");

		add("tooltip.surgeofkhroma.khroma_gauge.color", "Color: %s");
		add("tooltip.surgeofkhroma.khroma_gauge.consumed", "Consumed: %s Kh/t");
		add("tooltip.surgeofkhroma.khroma_gauge.softLimit", "Soft Limit: %s Kh/t");
		add("tooltip.surgeofkhroma.khroma_gauge.effective", "Effective: %s Kh/t");

		add("surgeofkhroma.khroma_cost", "%s Kh");

		add("attribute.surgeofkhroma.teleport_drops", "Teleport drops");
		add("attribute.surgeofkhroma.can_see_nodes", "Can see nodes");

		add("death.attack.surgeofkhroma.red_khrometal_block", "%1$s was destroyed by a red Khrometal block");
		add("death.attack.surgeofkhroma.red_khrometal_block.player", "%1$s was destroyed by a red Khrometal block while fighting %2$s");

		add("jei.surgeofkhroma.error.incompatible_khroma", "Incompatible Khroma");

		BlockReference.BLOCKS.getEntries().forEach(block -> {
			if (!hasKey(block.get().getDescriptionId()))
				addFormatted(block.getKey());
		});

		ItemReference.ITEMS.getEntries().forEach(item -> {
			if (!hasKey(item.get().getDescriptionId()))
				addFormatted(item.getKey());
		});

		EntityReference.ENTITY_TYPES.getEntries().forEach(entityType -> {
			if (!hasKey(entityType.get().getDescriptionId()))
				addFormatted(entityType.getKey());
		});

		RegistryReference.MOB_EFFECTS.getEntries().forEach(effect -> {
			if (!hasKey(effect.get().getDescriptionId()))
				addEffect(effect, formatKey(effect.getKey().location().getPath()));
		});
		
		if (additional != null)
			additional.forEach(this::add);
	}

	private void khromaNames() {
		add(Khroma.EMPTY, "Empty");
		add(Khroma.RED, "Red");
		add(Khroma.GREEN, "Green");
		add(Khroma.BLUE, "Blue");
		add(Khroma.WHITE, "White");
		add(Khroma.BLACK, "Black");
		add(Khroma.get(true, true, false, false, false), "Yellow");
		add(Khroma.get(true, false, true, false, false), "Magenta");
		add(Khroma.get(true, false, false, true, false), "Light Red");
		add(Khroma.get(true, false, false, false, true), "Dark Red");
		add(Khroma.get(false, true, true, false, false), "Cyan");
		add(Khroma.get(false, true, false, true, false), "Light Green");
		add(Khroma.get(false, true, false, false, true), "Dark Green");
		add(Khroma.get(false, false, true, true, false), "Light Blue");
		add(Khroma.get(false, false, true, false, true), "Dark Blue");
		add(Khroma.get(false, false, false, true, true), "Gray");
		add(Khroma.SPECTRUM, "Spectrum");
		add(Khroma.get(true, true, false, true, false), "Light Yellow");
		add(Khroma.get(true, true, false, false, true), "Dark Yellow");
		add(Khroma.get(true, false, true, true, false), "Light Magenta");
		add(Khroma.get(true, false, true, false, true), "Dark Magenta");
		add(Khroma.get(true, false, false, true, true), "Gray Red");
		add(Khroma.get(false, true, true, true, false), "Light Cyan");
		add(Khroma.get(false, true, true, false, true), "Dark Cyan");
		add(Khroma.get(false, true, false, true, true), "Gray Green");
		add(Khroma.LIGHT_SPECTRUM, "Light Spectrum");
		add(Khroma.DARK_SPECTRUM, "Dark Spectrum");
		add(Khroma.get(true, true, false, true, true), "Gray Yellow");
		add(Khroma.get(true, false, true, true, true), "Gray Magenta");
		add(Khroma.get(false, true, true, true, true), "Gray Cyan");
		add(Khroma.KHROMEGA, "Khromega");
	}

	private void specialBlockNames() {
		addBlock(BlockReference.CHROMIUM_BLOCK, "Block of Chromium");
		addBlock(BlockReference.RAW_CHROMIUM_BLOCK, "Block of Raw Chromium");
		addBlock(BlockReference.KHROMETAL_BLOCK_RED, "Block of Red Khrometal");
		addBlock(BlockReference.KHROMETAL_BLOCK_GREEN, "Block of Green Khrometal");
		addBlock(BlockReference.KHROMETAL_BLOCK_BLUE, "Block of Blue Khrometal");
		addBlock(BlockReference.KHROMETAL_BLOCK_WHITE, "Block of White Khrometal");
		addBlock(BlockReference.KHROMETAL_BLOCK_BLACK, "Block of Black Khrometal");
		add(BlockReference.KHROMA_DISSIPATOR.getKey(), ".message", "Dissipating %s Kh/t");
	}

	private void specialItemNames() {
		addItem(ItemReference.KHROMANCER_ARCHIVE, "Khromancer's Archive");

		for (var tree : BlockReference.IMBUED_TREES)
			addItem(tree.getChestBoatItem(), formatKey(tree.getBoatItem().getId().getPath() + "_with_chest"));

		ResourceKey<Item> warpCanister = ItemReference.WARP_CANISTER.getKey();
		add(warpCanister, ".connected", "Connected to %s");
		add(warpCanister, ".unbound", "Unbound");
		add(warpCanister, ".fail", "Connnected container not found. Disconnecting.");
	}

	private void specialEntityNames() {
		for (var tree : BlockReference.IMBUED_TREES)
			addEntityType(tree.getChestBoatEntity(), formatKey(tree.getBoatItem().getId().getPath() + "_with_chest"));
	}

	private void advancementNames() {
		addAdvancement("root", "Surge of Khroma", "Get red, green, blue, white or black dye");
		addAdvancement("smelt_chromium", "Chromed Up", "Get a Chromium Ingot");
		addAdvancement("craft_glasses", "I Can See Colors", "Craft Chromatic Glasses to be able to see nodes");
		addAdvancement("place_collector", "Endless Energy", "Place a Node Collector on a node and start harvesting Khroma");
		addAdvancement("imbue_khrometal", "Wake the Metal", "Create a Khrometal Ingot through imbuement");
	}

	private void tagNames() {
		addFormatted(TagReference.Blocks.KHROMETAL_BLOCKS);
		addFormatted(TagReference.Blocks.IMBUED_TREE_LOGS);
		addFormatted(TagReference.Blocks.IMBUED_TREE_LEAVES);
		addFormatted(TagReference.Blocks.IMBUED_TREE_SAPLINGS);
		addFormatted(TagReference.Blocks.PILLARS);
		addFormatted(TagReference.Blocks.WOODEN_PILLARS);
		add(TagReference.Blocks.COLLECTOR_STRUCTURE_COMPONENTS, "Components of a Node Collector Structure");
	}

	private void addAdvancement(String id, String title, String description) {
		var key = "advancements." + SurgeofKhroma.MODID + "." + id;

		add(key + ".title", title);
		add(key + ".description", description);
	}

	public void add(Khroma key, String name) {
		add(key.getLocalizedName(), name);
	}

	public void add(ResourceKey<?> key, String name) {
		add(key, "", name);
	}

	public void add(ResourceKey<?> key, String suffix, String name) {
		add(key.location().toLanguageKey(key.registry().getPath()) + suffix, name);
	}

	public void addFormatted(ResourceKey<?> key) {
		add(key, formatKey(key.location().getPath()));
	}

	public void addFormatted(TagKey<?> tag) {
		add(tag, formatKey(tag.location().getPath()));
	}

	@SuppressWarnings("unchecked")
	protected boolean hasKey(String key) {
		try {
			Map<String, String> valueData = (Map<String, String>) dataField.get(this);

			return valueData.containsKey(key);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static String formatKey(String key) {
		String[] split = key.split("_");
		if (split.length == 1)
			return capitalize(split[0]);
		StringBuilder builder = new StringBuilder();

		boolean first = true;
		for (String element : split) {
			if (!first)
				builder.append(" ");

			if (first || !connectors.contains(element))
				builder.append(capitalize(element));
			else
				builder.append(element);

			first = false;
		}

		return builder.toString();
	}

	public static String capitalize(String string) {
		return Character.toUpperCase(string.charAt(0)) + string.substring(1);
	}
}
