package net.deatrathias.khroma.registries;

import java.util.Optional;

import net.deatrathias.khroma.SurgeofKhroma;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class TreeGrowerReference {

	public static final ResourceKey<ConfiguredFeature<?, ?>> FEATURE_SPARKTREE = SurgeofKhroma.resourceKey(Registries.CONFIGURED_FEATURE, "sparktree");

	public static final TreeGrower SPARKTREE = new TreeGrower(FEATURE_SPARKTREE.location().getPath(), Optional.empty(), Optional.of(FEATURE_SPARKTREE), Optional.empty());

	public static final ResourceKey<ConfiguredFeature<?, ?>> FEATURE_BLOOMTREE = SurgeofKhroma.resourceKey(Registries.CONFIGURED_FEATURE, "bloomtree");

	public static final TreeGrower BLOOMTREE = new TreeGrower(FEATURE_BLOOMTREE.location().getPath(), Optional.of(FEATURE_BLOOMTREE), Optional.empty(), Optional.empty());

	public static final ResourceKey<ConfiguredFeature<?, ?>> FEATURE_FLOWTREE = SurgeofKhroma.resourceKey(Registries.CONFIGURED_FEATURE, "flowtree");

	public static final TreeGrower FLOWTREE = new TreeGrower(FEATURE_FLOWTREE.location().getPath(), Optional.empty(), Optional.of(FEATURE_FLOWTREE), Optional.empty());

	public static final ResourceKey<ConfiguredFeature<?, ?>> FEATURE_SKYTREE = SurgeofKhroma.resourceKey(Registries.CONFIGURED_FEATURE, "skytree");

	public static final TreeGrower SKYTREE = new TreeGrower(FEATURE_SKYTREE.location().getPath(), Optional.empty(), Optional.of(FEATURE_SKYTREE), Optional.empty());

	public static final ResourceKey<ConfiguredFeature<?, ?>> FEATURE_GRIMTREE = SurgeofKhroma.resourceKey(Registries.CONFIGURED_FEATURE, "grimtree");

	public static final TreeGrower GRIMTREE = new TreeGrower(FEATURE_GRIMTREE.location().getPath(), Optional.empty(), Optional.of(FEATURE_GRIMTREE), Optional.empty());
}
