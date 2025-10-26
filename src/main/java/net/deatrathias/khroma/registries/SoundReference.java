package net.deatrathias.khroma.registries;

import net.deatrathias.khroma.SurgeofKhroma;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class SoundReference {

	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, SurgeofKhroma.MODID);

	public static final Holder<SoundEvent> BLACK_KHROMETAL_TELEPORT = SOUND_EVENTS.register("black_khrometal_teleport", SoundEvent::createVariableRangeEvent);

	public static final Holder<SoundEvent> WARP_CANISTER_CONNECT = SOUND_EVENTS.register("warp_canister_connect", SoundEvent::createVariableRangeEvent);

	public static final Holder<SoundEvent> WARP_CANISTER_SEND = SOUND_EVENTS.register("warp_canister_send", SoundEvent::createVariableRangeEvent);
}
