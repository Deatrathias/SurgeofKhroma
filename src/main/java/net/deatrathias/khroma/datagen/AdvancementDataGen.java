package net.deatrathias.khroma.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.ItemReference;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements.Strategy;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemUsedOnLocationTrigger;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public class AdvancementDataGen extends AdvancementProvider {

	public AdvancementDataGen(PackOutput output, CompletableFuture<Provider> registries) {
		super(output, registries, List.of(new SurgeofKhromaAdvancements()));
	}

	public static class SurgeofKhromaAdvancements implements AdvancementSubProvider {
		@Override
		public void generate(Provider registries, Consumer<AdvancementHolder> writer) {
			var root = Advancement.Builder.advancement()
					.display(ItemReference.CHROMATIC_NUCLEUS, Component.translatable("advancements.surgeofkhroma.root.title"),
							Component.translatable("advancements.surgeofkhroma.root.description"), SurgeofKhroma.resource("gui/advancements/backgrounds/surgeofkhroma"), AdvancementType.TASK, false,
							false, false)
					.addCriterion("red_dye", InventoryChangeTrigger.TriggerInstance.hasItems(Items.RED_DYE))
					.addCriterion("green_dye", InventoryChangeTrigger.TriggerInstance.hasItems(Items.GREEN_DYE))
					.addCriterion("blue_dye", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BLUE_DYE))
					.addCriterion("white_dye", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHITE_DYE))
					.addCriterion("black_dye", InventoryChangeTrigger.TriggerInstance.hasItems(Items.BLACK_DYE))
					.requirements(Strategy.OR)
					.save(writer, SurgeofKhroma.resource("root"));
			var smelt_chromium = Advancement.Builder.advancement().parent(root)
					.display(ItemReference.CHROMIUM_INGOT, Component.translatable("advancements.surgeofkhroma.smelt_chromium.title"),
							Component.translatable("advancements.surgeofkhroma.smelt_chromium.description"), null, AdvancementType.TASK, true, false, false)
					.addCriterion("chromium", InventoryChangeTrigger.TriggerInstance.hasItems(ItemReference.CHROMIUM_INGOT))
					.save(writer, SurgeofKhroma.resource("smelt_chromium"));
			var craft_glasses = Advancement.Builder.advancement().parent(smelt_chromium)
					.display(ItemReference.CHROMATIC_GLASSES, Component.translatable("advancements.surgeofkhroma.craft_glasses.title"),
							Component.translatable("advancements.surgeofkhroma.craft_glasses.description"), null, AdvancementType.TASK, true, false, false)
					.addCriterion("chromatic_glasses", InventoryChangeTrigger.TriggerInstance.hasItems(ItemReference.CHROMATIC_GLASSES))
					.save(writer, SurgeofKhroma.resource("craft_glasses"));
			var place_collector = Advancement.Builder.advancement().parent(craft_glasses)
					.display(BlockReference.NODE_COLLECTOR, Component.translatable("advancements.surgeofkhroma.place_collector.title"),
							Component.translatable("advancements.surgeofkhroma.place_collector.description"), null, AdvancementType.TASK, true, true, false)
					.addCriterion("node_collector", ItemUsedOnLocationTrigger.TriggerInstance.placedBlock(BlockReference.NODE_COLLECTOR.get()))
					.save(writer, SurgeofKhroma.resource("place_collector"));
			Advancement.Builder.advancement().parent(place_collector)
					.display(BlockReference.KHROMA_IMBUER, Component.translatable("advancements.surgeofkhroma.imbue_khrometal.title"),
							Component.translatable("advancements.surgeofkhroma.imbue_khrometal.description"), null, AdvancementType.TASK, true, false, false)
					.addCriterion("khrometal_red", InventoryChangeTrigger.TriggerInstance.hasItems(ItemReference.KHROMETAL_INGOT_RED))
					.addCriterion("khrometal_green", InventoryChangeTrigger.TriggerInstance.hasItems(ItemReference.KHROMETAL_INGOT_GREEN))
					.addCriterion("khrometal_blue", InventoryChangeTrigger.TriggerInstance.hasItems(ItemReference.KHROMETAL_INGOT_BLUE))
					.addCriterion("khrometal_white", InventoryChangeTrigger.TriggerInstance.hasItems(ItemReference.KHROMETAL_INGOT_WHITE))
					.addCriterion("khrometal_black", InventoryChangeTrigger.TriggerInstance.hasItems(ItemReference.KHROMETAL_INGOT_BLACK))
					.requirements(Strategy.OR)
					.save(writer, SurgeofKhroma.resource("imbue_khrometal"));
		}
	}
}
