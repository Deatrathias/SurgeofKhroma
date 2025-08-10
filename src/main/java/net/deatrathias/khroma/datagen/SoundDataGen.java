package net.deatrathias.khroma.datagen;

import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.ItemReference;
import net.deatrathias.khroma.registries.SoundReference;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class SoundDataGen extends SoundDefinitionsProvider {

	protected SoundDataGen(PackOutput output) {
		super(output, SurgeofKhroma.MODID);
	}

	@Override
	public void registerSounds() {
		add(SoundReference.BLACK_KHROMETAL_TELEPORT, SoundDefinition.definition().with(
				sound(SurgeofKhroma.resource("block/khrometal_block_black/teleport")))
				.subtitle(subtitle(BlockReference.KHROMETAL_BLOCK_BLACK, "teleport")));
		add(SoundReference.WARP_CANISTER_CONNECT, SoundDefinition.definition().with(
				sound(SurgeofKhroma.resource("item/warp_canister/connect")))
				.subtitle(subtitle(ItemReference.WARP_CANISTER, "connect")));
		add(SoundReference.WARP_CANISTER_SEND, SoundDefinition.definition().with(
				sound(SurgeofKhroma.resource("item/warp_canister/send")))
				.subtitle(subtitle(ItemReference.WARP_CANISTER, "send")));
	}

	private String subtitle(DeferredItem<? extends Item> itemHolder, String suffix) {
		return "subtitles." + SurgeofKhroma.MODID + ".item." + itemHolder.getId().getPath() + "." + suffix;
	}

	private String subtitle(DeferredBlock<? extends Block> blockHolder, String suffix) {
		return "subtitles." + SurgeofKhroma.MODID + ".block." + blockHolder.getId().getPath() + "." + suffix;
	}
}
