package net.deatrathias.khroma.registries;

import java.util.function.Supplier;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.entities.KhromaNodeEntity;
import net.deatrathias.khroma.entities.PlacedItemEntity;
import net.deatrathias.khroma.entities.Strix;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EntityReference {

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, SurgeofKhroma.MODID);

	public static final Supplier<EntityType<KhromaNodeEntity>> KHROMA_NODE = ENTITY_TYPES.register("khroma_node",
			registryName -> EntityType.Builder.of(KhromaNodeEntity::new, MobCategory.MISC).fireImmune().updateInterval(Integer.MAX_VALUE).eyeHeight(0).clientTrackingRange(10).sized(1, 1)
					.build(ResourceKey.create(Registries.ENTITY_TYPE, registryName)));

	public static final Supplier<EntityType<PlacedItemEntity>> PLACED_ITEM = ENTITY_TYPES.register("placed_item",
			registryName -> EntityType.Builder.<PlacedItemEntity>of(PlacedItemEntity::new, MobCategory.MISC)
					.noLootTable()
					.sized(0.25F, 0.25F)
					.eyeHeight(0.2125F)
					.clientTrackingRange(6)
					.updateInterval(20)
					.build(ResourceKey.create(Registries.ENTITY_TYPE, registryName)));

	public static final Supplier<EntityType<Strix>> STRIX = ENTITY_TYPES.register("strix",
			registryName -> EntityType.Builder.of(Strix::new, MobCategory.MONSTER)
					.sized(1.125f, 2.4375f)
					.clientTrackingRange(10)
					.eyeHeight(2.219f)
					.build(ResourceKey.create(Registries.ENTITY_TYPE, registryName)));

}
