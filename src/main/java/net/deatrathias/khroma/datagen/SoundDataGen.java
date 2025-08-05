package net.deatrathias.khroma.datagen;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.registries.SoundReference;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class SoundDataGen extends SoundDefinitionsProvider {

	protected SoundDataGen(PackOutput output) {
		super(output, SurgeofKhroma.MODID);
	}

	@Override
	public void registerSounds() {
		add(SoundReference.BLACK_KHROMETAL_TELEPORT, SoundDefinition.definition().with(
				sound(SurgeofKhroma.resource("block/khrometal_block_black/teleport")))
				.subtitle("sound." + SurgeofKhroma.MODID + ".block.khrometal_block_black.teleport"));
	}
}
