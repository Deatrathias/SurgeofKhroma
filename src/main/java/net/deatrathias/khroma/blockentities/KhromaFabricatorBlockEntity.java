package net.deatrathias.khroma.blockentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.deatrathias.khroma.khroma.KhromaThroughput;
import net.deatrathias.khroma.recipes.KhromaFabricationInput;
import net.deatrathias.khroma.recipes.KhromaFabricationRecipe;
import net.deatrathias.khroma.registries.BlockReference;
import net.deatrathias.khroma.registries.RecipeReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class KhromaFabricatorBlockEntity extends BaseKhromaConsumerBlockEntity {

	private final RecipeManager.CachedCheck<KhromaFabricationInput, KhromaFabricationRecipe> quickCheck;

	private List<BlockCapabilityCache<ResourceHandler<ItemResource>, Direction>> pedestalCache;

	private Optional<RecipeHolder<KhromaFabricationRecipe>> currentRecipe;

	private float progress;

	public KhromaFabricatorBlockEntity(BlockPos pos, BlockState blockState) {
		super(BlockReference.BE_KHROMA_FABRICATOR.get(), pos, blockState);
		quickCheck = RecipeManager.createCheck(RecipeReference.KHROMA_FABRICATION.get());
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if (level instanceof ServerLevel serverLevel) {
			pedestalCache = new ArrayList<BlockCapabilityCache<ResourceHandler<ItemResource>, Direction>>(9);
			for (int x = -1; x <= 1; x++) {
				for (int z = -1; z <= 1; z++) {
					pedestalCache.add(BlockCapabilityCache.create(Capabilities.Item.BLOCK, serverLevel, worldPosition.offset(x, -1, z), Direction.UP));
				}
			}
		}
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		progress = input.getFloatOr("Progress", 0);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putFloat("Progress", progress);
	}

	private int indexFromOffset(int x, int z) {
		return x + 1 + (z + 1) * 3;
	}

	@Override
	protected void serverTick() {
		ItemOutputModuleBlockEntity outputModule = null;
		List<ResourceHandler<ItemResource>> pedestalHandlers = new ArrayList<ResourceHandler<ItemResource>>(8);

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				if (x == 0 && z == 0) {
					outputModule = level.getBlockEntity(worldPosition.below(), BlockReference.BE_ITEM_OUTPUT_MODULE.get()).orElse(null);
				} else {
					if (!level.getBlockState(worldPosition.offset(x, -1, z)).is(BlockReference.ITEM_PEDESTAL))
						pedestalHandlers.add(null);
					else
						pedestalHandlers.add(pedestalCache.get(indexFromOffset(x, z)).getCapability());
				}
			}
		}

		if (outputModule == null)
			return;

		List<ItemStack> items = new ArrayList<ItemStack>(8);

		for (var handler : pedestalHandlers) {
			if (handler == null)
				items.add(ItemStack.EMPTY);
			else
				items.add(handler.getResource(0).toStack());
		}

		KhromaThroughput throughput = requestOnSide(Direction.UP);

		KhromaFabricationInput input = new KhromaFabricationInput(items, List.of(throughput.getKhroma()));

		var foundRecipe = quickCheck.getRecipeFor(input, (ServerLevel) level);
		if (foundRecipe.isPresent()) {
			ItemStack result = foundRecipe.get().value().getResult();
			if (outputModule.modularInsertItem(result, true).isEmpty()) {
				if (!foundRecipe.equals(currentRecipe)) {
					if (currentRecipe != null) {
						progress = 0;
						setChanged();
					}
					currentRecipe = foundRecipe;
				}
				var recipe = currentRecipe.get().value();
				progress += throughput.recipeProgress(recipe.getKhroma(), getSoftLimit()) / recipe.getKhromaCost();
				setChanged();
				if (progress >= 1) {
					try (Transaction tx = Transaction.openRoot()) {
						for (var handler : pedestalHandlers) {
							if (handler != null)
								handler.extract(handler.getResource(0), 1, tx);
						}
						
						tx.commit();
					}

					outputModule.modularInsertItem(recipe.assemble(input, level.registryAccess()), false);
					progress = 0;
				}
			}
		} else {
			if (progress != 0)
				setChanged();
			progress = 0;
		}
	}
}
