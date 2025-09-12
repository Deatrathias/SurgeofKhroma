package net.deatrathias.khroma.client;

import java.util.function.Function;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.registries.BlockReference;
import net.minecraft.Util;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.constant.dataticket.DataTicket;

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
				RenderStateShard.TextureStateShard renderstateshard$texturestateshard = new RenderStateShard.TextureStateShard(p_404026_, false);
				return RenderType.create(
						"khroma_node",
						1536,
						false,
						true,
						PIPELINE_KHROMA_NODE,
						RenderType.CompositeState.builder().setTextureState(renderstateshard$texturestateshard).createCompositeState(false));
			});

	public static final ModelLayerLocation SPARKTREE_BOAT = new ModelLayerLocation(SurgeofKhroma.resource("boat/" + BlockReference.SPARKTREE.getName()), "main");
	public static final ModelLayerLocation SPARKTREE_CHEST_BOAT = new ModelLayerLocation(SurgeofKhroma.resource("chest_boat/" + BlockReference.SPARKTREE.getName()), "main");
	public static final ModelLayerLocation BLOOMTREE_BOAT = new ModelLayerLocation(SurgeofKhroma.resource("boat/" + BlockReference.BLOOMTREE.getName()), "main");
	public static final ModelLayerLocation BLOOMTREE_CHEST_BOAT = new ModelLayerLocation(SurgeofKhroma.resource("chest_boat/" + BlockReference.BLOOMTREE.getName()), "main");
	public static final ModelLayerLocation FLOWTREE_BOAT = new ModelLayerLocation(SurgeofKhroma.resource("boat/" + BlockReference.FLOWTREE.getName()), "main");
	public static final ModelLayerLocation FLOWTREE_CHEST_BOAT = new ModelLayerLocation(SurgeofKhroma.resource("chest_boat/" + BlockReference.FLOWTREE.getName()), "main");
	public static final ModelLayerLocation SKYTREE_BOAT = new ModelLayerLocation(SurgeofKhroma.resource("boat/" + BlockReference.SKYTREE.getName()), "main");
	public static final ModelLayerLocation SKYTREE_CHEST_BOAT = new ModelLayerLocation(SurgeofKhroma.resource("chest_boat/" + BlockReference.SKYTREE.getName()), "main");
	public static final ModelLayerLocation GRIMTREE_BOAT = new ModelLayerLocation(SurgeofKhroma.resource("boat/" + BlockReference.GRIMTREE.getName()), "main");
	public static final ModelLayerLocation GRIMTREE_CHEST_BOAT = new ModelLayerLocation(SurgeofKhroma.resource("chest_boat/" + BlockReference.GRIMTREE.getName()), "main");

	public static final ModelLayerLocation STRIX = new ModelLayerLocation(SurgeofKhroma.resource("strix"), "main");

	public static final DataTicket<Boolean> IS_ON_GROUND = DataTicket.create("is_on_ground", Boolean.class);
}
