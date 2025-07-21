package net.deatrathias.khroma.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.client.internal.NeoForgeClientProxy;

/**
 * Fixing issue
 * <a href="https://github.com/neoforged/NeoForge/issues/2488">#2488</a>
 */
@Mixin(NeoForgeClientProxy.class)
public class NeoForgeFixMixin {
	@Inject(method = "resolveLookup", at = @At(value = "TAIL"), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	public <T> void surgeofkhroma$resolveLookup(ResourceKey<? extends Registry<T>> key, CallbackInfoReturnable<HolderLookup.RegistryLookup<T>> ci, HolderLookup.RegistryLookup<T> lookup) {
		ci.setReturnValue(lookup);
		ci.cancel();
	}
}
