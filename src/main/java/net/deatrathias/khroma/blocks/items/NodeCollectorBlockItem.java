package net.deatrathias.khroma.blocks.items;

import net.deatrathias.khroma.RegistryReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class NodeCollectorBlockItem extends BlockItem {

	public NodeCollectorBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		Vec3 playerPosition = player.getEyePosition();
		Vec3 playerView = player.getViewVector(1.0F);
		Vec3 playerForward = playerPosition.add(playerView.x * 100.0, playerView.y * 100.0, playerView.z * 100.0);
		EntityHitResult entityhitresult = ProjectileUtil.getEntityHitResult(level, player, playerPosition, playerForward, new AABB(playerPosition, playerForward).inflate(1.0),
				entity -> entity.getType() == RegistryReference.ENTITY_KHROMA_NODE.get(), 0.0F);

		if (entityhitresult != null && entityhitresult.getEntity() != null) {
			return new InteractionResultHolder<ItemStack>(
					useOn(new UseOnContext(player, usedHand, new BlockHitResult(entityhitresult.getLocation(), Direction.DOWN, new BlockPos(entityhitresult.getEntity().blockPosition()), false))),
					stack);
		}

		return super.use(level, player, usedHand);
	}
}
