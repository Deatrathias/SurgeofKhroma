package net.deatrathias.khroma.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.data.models.BlockModelGenerators.PlantType;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

@Mixin(PlantType.class)
public class BlockModelMixin {

	@Shadow
	private ModelTemplate blockTemplate;

	@Shadow
	private ModelTemplate flowerPotTemplate;

	@Inject(method = "<init>*", at = @At("RETURN"))
	private void constructor(String name, int index, ModelTemplate blockTemplate, ModelTemplate flowerPotTemplate, boolean isEmissive, CallbackInfo ci) {
		ResourceLocation cutout = ResourceLocation.withDefaultNamespace(ChunkSectionLayer.CUTOUT.label());
		this.blockTemplate = ExtendedModelTemplateBuilder.of(blockTemplate).renderType(cutout).build();
		this.flowerPotTemplate = ExtendedModelTemplateBuilder.of(flowerPotTemplate).renderType(cutout).build();
	}
}
