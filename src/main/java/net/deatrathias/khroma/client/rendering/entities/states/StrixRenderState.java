package net.deatrathias.khroma.client.rendering.entities.states;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;

public class StrixRenderState extends LivingEntityRenderState {
	public final AnimationState standAnimationState = new AnimationState();
	public final AnimationState flyAnimationState = new AnimationState();
	public final AnimationState hoverAnimationState = new AnimationState();
}
