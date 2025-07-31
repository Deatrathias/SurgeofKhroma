package net.deatrathias.khroma.registries;

import java.util.function.Supplier;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.entities.KhromaNodeEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class EntityReference {

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, SurgeofKhroma.MODID);

	public static final Supplier<EntityType<KhromaNodeEntity>> KHROMA_NODE = ENTITY_TYPES.register("khroma_node",
			() -> EntityType.Builder.of(KhromaNodeEntity::new, MobCategory.MISC).fireImmune().updateInterval(Integer.MAX_VALUE).eyeHeight(0).clientTrackingRange(10).sized(1, 1)
					.build(SurgeofKhroma.resourceKey(Registries.ENTITY_TYPE, "khroma_node")));

}
