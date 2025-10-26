package net.deatrathias.khroma.items;

import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.klikli_dev.modonomicon.item.ModonomiconItem;
import com.klikli_dev.modonomicon.registry.DataComponentRegistry;

import net.deatrathias.khroma.client.rendering.items.KhromancerArchiveRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class GuideItem extends ModonomiconItem implements GeoItem{
	private static final RawAnimation ANIM_OPEN = RawAnimation.begin().thenPlay("open").thenLoop("opened");
	private static final RawAnimation ANIM_CLOSE = RawAnimation.begin().thenPlay("close").thenLoop("idle");
	public DataTicket<Boolean> openTicket; 

	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	
	public GuideItem(Properties properties) {
		super(properties.component(DataComponentRegistry.BOOK_ID, properties.effectiveModel())
				.component(DataComponentRegistry.BOOK_OPEN, false));
		
		openTicket = DataTicket.create("open", Boolean.class);
		GeoItem.registerSyncedAnimatable(this);
	}
	
	@Override
	public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
		consumer.accept(new GeoRenderProvider() {
			private KhromancerArchiveRenderer renderer;
			
			@Override
			public @Nullable GeoItemRenderer<?> getGeoItemRenderer() {
				if (renderer == null)
					renderer = new KhromancerArchiveRenderer();
				
				return renderer;
			}
		});
	}

	@Override
	public void registerControllers(ControllerRegistrar controllers) {
		controllers.add(new AnimationController<>("Open", 0, animTest -> {
			if (animTest.getData(DataTickets.ITEM_RENDER_PERSPECTIVE) != ItemDisplayContext.GUI) // FIXME: ????
				return PlayState.STOP;
			
			boolean opened = animTest.getData(openTicket);
			if (opened && !animTest.isCurrentAnimation(ANIM_OPEN)) {
				animTest.resetCurrentAnimation();
				return animTest.setAndContinue(ANIM_OPEN);
			}
			else if (!opened && !animTest.isCurrentAnimation(null) && !animTest.isCurrentAnimation(ANIM_CLOSE)) {
				animTest.resetCurrentAnimation();
				return animTest.setAndContinue(ANIM_CLOSE);
			}
			return PlayState.CONTINUE;
		}));
	}
	
	@Override
	public boolean isPerspectiveAware() {
		return true;
	}
	
	@Override
	public void appendHoverText(@NotNull ItemStack itemStack, @NotNull TooltipContext tooltipContext,
			@NotNull TooltipDisplay tooltipDisplay, @NotNull Consumer<Component> consumer,
			@NotNull TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
		Long animId = itemStack.get(GeckoLibConstants.STACK_ANIMATABLE_ID_COMPONENT.get());
		consumer.accept(Component.literal("id:"+ animId));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}
	
	@Override
	public @NotNull InteractionResult use(Level pLevel, Player pPlayer, @NotNull InteractionHand pUsedHand) {
		if (!pLevel.isClientSide())
			GeoItem.getOrAssignId(pPlayer.getItemInHand(pUsedHand), (ServerLevel) pLevel);
		super.use(pLevel, pPlayer, pUsedHand);
		
		return InteractionResult.FAIL;
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return !ItemStack.isSameItem(oldStack, newStack); 
	}
}
