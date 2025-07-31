package net.deatrathias.khroma.registries;

import java.util.Optional;

import net.deatrathias.khroma.SurgeofKhroma;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class TreeGrowerReference {

	public static final ResourceKey<ConfiguredFeature<?, ?>> FEATURE_SPARKTREE = SurgeofKhroma.resourceKey(Registries.CONFIGURED_FEATURE, "sparktree");

	public static final TreeGrower SPARKTREE = new TreeGrower("sparktree", Optional.empty(), Optional.of(FEATURE_SPARKTREE), Optional.empty());
}
