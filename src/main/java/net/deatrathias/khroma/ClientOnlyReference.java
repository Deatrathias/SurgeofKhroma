package net.deatrathias.khroma;

import java.util.function.Function;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientOnlyReference {
	public static final RenderPipeline PIPELINE_KHROMA_NODE = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
			.withLocation(SurgeofKhroma.resource("pipelines/khroma_node"))
			.withVertexShader("core/entity")
			.withFragmentShader(SurgeofKhroma.resource("core/khroma_node"))
			.withShaderDefine("EMISSIVE")
			.withShaderDefine("NO_OVERLAY")
			.withShaderDefine("NO_CARDINAL_LIGHTING")
			.withSampler("Sampler0")
			.withBlend(BlendFunction.TRANSLUCENT)
			.withDepthWrite(true)
			.build();

	public static final Function<ResourceLocation, RenderType> RENDER_KHROMA_NODE = Util.memoize(
			p_404026_ -> {
				RenderStateShard.TextureStateShard renderstateshard$texturestateshard = new RenderStateShard.TextureStateShard(p_404026_, TriState.FALSE, false);
				return RenderType.create(
						"khroma_node",
						1536,
						false,
						true,
						PIPELINE_KHROMA_NODE,
						RenderType.CompositeState.builder().setTextureState(renderstateshard$texturestateshard).createCompositeState(false));
			});
}
