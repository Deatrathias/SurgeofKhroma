package net.deatrathias.khroma.items;

import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.deatrathias.khroma.client.ClientOnlyReference;
import net.deatrathias.khroma.compat.accessories.FeatheredBootsRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.resources.model.EquipmentClientInfo.LayerType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class FeatheredBootsItem extends Item implements GeoItem {
	private static final RawAnimation FLAP_ANIM = RawAnimation.begin().thenLoop("Flap");
	private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

	public FeatheredBootsItem(Properties properties) {
		super(properties);
	}

	@Override
	public void registerControllers(ControllerRegistrar controllers) {
		controllers.add(
				new AnimationController<FeatheredBootsItem>("Flap", 0,
						animTest -> animTest.getDataOrDefault(ClientOnlyReference.IS_ON_GROUND, false) ? PlayState.STOP : animTest.setAndContinue(FLAP_ANIM)));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return geoCache;
	}

	@Override
	public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
		consumer.accept(new GeoRenderProvider() {
			private FeatheredBootsRenderer<?> renderer;

			@Override
			public <S extends HumanoidRenderState> @Nullable GeoArmorRenderer<?, ?> getGeoArmorRenderer(@Nullable S renderState, ItemStack itemStack, EquipmentSlot equipmentSlot, LayerType type,
					@Nullable HumanoidModel<S> original) {
				if (renderer == null)
					renderer = new FeatheredBootsRenderer<>();

				return renderer;
			}
		});
	}
}
