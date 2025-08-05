package net.deatrathias.khroma.datagen;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.ImbuedTree.TreeBlock;
import net.deatrathias.khroma.registries.TagReference;
import net.minecraft.client.data.models.EquipmentAssetProvider;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import top.theillusivec4.curios.api.CuriosResources;

@EventBusSubscriber(modid = SurgeofKhroma.MODID)
public class DataGen {

	@SubscribeEvent
	public static void gatherDataClient(GatherDataEvent.Client event) {
		DataGenDefinitions.init();
		event.createProvider(ModelDataGen::new);
		event.createProvider(output -> equipmentProvider(output));
		generateServerData(event);
	}

	@SubscribeEvent
	public static void gatherDataServer(GatherDataEvent.Server event) {
		DataGenDefinitions.init();
		generateServerData(event);
	}

	private static void generateServerData(GatherDataEvent event) {
		event.createDatapackRegistryObjects(RegistrySetDataGen.registrySet(), Set.of(SurgeofKhroma.MODID));

		event.createProvider(TagsDataGen.BlockTag::new);
		event.createProvider(TagsDataGen.ItemTag::new);
		event.createProvider(TagsDataGen.DamageTypeTag::new);
		event.createProvider(TagsDataGen.EntityTypeTag::new);
		event.createProvider(TagsDataGen.EnchantmentTag::new);
		event.createProvider(LootTableDataGen::new);
		event.createProvider(AdvancementDataGen::new);

		event.createProvider(RecipeDaraGen.Runner::new);
		event.createProvider(DataMapDataGen::new);
		event.createProvider(SoundDataGen::new);
		if (ModList.get().isLoaded(CuriosResources.MOD_ID))
			event.createProvider(CuriosDataGen::new);
	}

	private static DataProvider equipmentProvider(PackOutput output) {
		return new EquipmentAssetProvider(output) {
			@Override
			protected void registerModels(BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> output) {
				output.accept(SurgeofKhroma.resourceKey(EquipmentAssets.ROOT_ID, "chromatic_glasses"), EquipmentClientInfo.builder()
						.addMainHumanoidLayer(SurgeofKhroma.resource("chromatic_glasses"), false)
						.build());
			}
		};
	}

	private static class DataMapDataGen extends DataMapProvider {

		protected DataMapDataGen(PackOutput packOutput, CompletableFuture<Provider> lookupProvider) {
			super(packOutput, lookupProvider);
		}

		@Override
		protected void gather(Provider provider) {
			builder(NeoForgeDataMaps.COMPOSTABLES)
					.add(TagReference.ITEM_IMBUED_TREE_LEAVES, new Compostable(0.3f), false)
					.add(TagReference.ITEM_IMBUED_TREE_SAPLINGS, new Compostable(0.3f), false)
					.add(BlockReference.BLOOMTREE.getHolder(TreeBlock.SAPLING).unwrapKey().get().location(), new Compostable(1f), false)
					.add(BlockReference.BLOOMTREE.getHolder(TreeBlock.LEAVES).unwrapKey().get().location(), new Compostable(1f), false);
		}
	}
}
