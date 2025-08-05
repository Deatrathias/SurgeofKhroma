package net.deatrathias.khroma.items;

import guideme.GuidesCommon;
import net.deatrathias.khroma.compat.guideme.SoKGuide;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class GuideItem extends Item {

	public GuideItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide) {
			GuidesCommon.openGuide(player, SoKGuide.KHROMANCER_ARCHIVE.getId());
		}

		return InteractionResult.SUCCESS;
	}
}
