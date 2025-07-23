package net.deatrathias.khroma.blockentities;

import java.util.Optional;

import net.deatrathias.khroma.RegistryReference;
import net.deatrathias.khroma.SurgeofKhroma;
import net.deatrathias.khroma.blocks.KhromaImbuerBlock;
import net.deatrathias.khroma.gui.KhromaImbuerMenu;
import net.deatrathias.khroma.khroma.IKhromaUsingBlock.ConnectionType;
import net.deatrathias.khroma.khroma.Khroma;
import net.deatrathias.khroma.khroma.KhromaThroughput;
import net.deatrathias.khroma.recipes.ItemKhromaRecipeInput;
import net.deatrathias.khroma.recipes.KhromaImbuementRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class KhromaImbuerBlockEntity extends BaseKhromaConsumerBlockEntity implements WorldlyContainer, MenuProvider {

	private static final Component CONTAINER_TITLE = Component.translatable("container." + SurgeofKhroma.MODID + ".khroma_imbuer");

	private static final int SLOT_INPUT = 0;

	private static final int SLOT_OUTPUT = 1;

	private final RecipeManager.CachedCheck<ItemKhromaRecipeInput, KhromaImbuementRecipe> quickCheck;

	protected NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

	private Optional<RecipeHolder<KhromaImbuementRecipe>> currentRecipe;

	private KhromaThroughput lastThroughput;

	private float effectiveRate;

	protected final ContainerData dataAccess = new ContainerData() {

		@Override
		public void set(int index, int value) {
			switch (index) {
			case 0:
				progress = (float) value / Integer.MAX_VALUE;
				break;
			case 1:
			case 2:
			case 3:
			case 4:
				// No need to change throughput
				break;
			}
		}

		@Override
		public int getCount() {
			return KhromaImbuerMenu.DATA_COUNT;
		}

		@Override
		public int get(int index) {
			switch (index) {
			case 0:
				return Mth.floor(progress * Integer.MAX_VALUE);
			case 1:
				return lastThroughput.getKhroma().asInt();
			case 2:
				return Math.round(lastThroughput.getRate() * 100f);
			case 3:
				return Math.round(getSoftLimit() * 100f);
			case 4:
				return Math.round(effectiveRate * 100f);
			default:
				return 0;
			}
		}
	};

	private float progress;

	protected static final int[] SLOT_DOWN = new int[] { SLOT_OUTPUT };

	protected static final int[] SLOT_NOT_DOWN = new int[] { SLOT_INPUT };

	public KhromaImbuerBlockEntity(BlockPos pos, BlockState blockState) {
		super(RegistryReference.BLOCK_ENTITY_KHROMA_IMBUER.get(), pos, blockState);
		quickCheck = RecipeManager.createCheck(RegistryReference.RECIPE_KHROMA_IMBUEMENT.get());
		progress = 0;
		currentRecipe = Optional.empty();
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, KhromaImbuerBlockEntity blockEntity) {
		blockEntity.tick();
	}

	@Override
	protected void tick() {
		Direction side = getBlockState().getValue(KhromaImbuerBlock.FACING).getOpposite();
		Khroma khroma = getKhromaOnSide(side);
		lastThroughput = new KhromaThroughput(khroma, 0);
		effectiveRate = 0;

		if (currentRecipe.isEmpty() && khroma != Khroma.KHROMA_EMPTY && !items.get(SLOT_INPUT).isEmpty()) {
			currentRecipe = quickCheck.getRecipeFor(new ItemKhromaRecipeInput(items.get(SLOT_INPUT), khroma), (ServerLevel) level);
		}
		if (currentRecipe.isPresent()) {
			var recipe = currentRecipe.get();
			if (!canProcess(recipe, khroma)) {
				if (progress != 0)
					setChanged();
				progress = 0;
				currentRecipe = Optional.empty();
			} else {
				KhromaThroughput throughput = requestOnSide(side);
				effectiveRate = throughput.recipeProgress(recipe.value(), getSoftLimit());
				progress += effectiveRate / recipe.value().getKhromaCost();
				if (progress >= 1f) {
					process(recipe, khroma);
				}
				lastThroughput = throughput;
				setChanged();
			}
		} else if (progress != 0) {
			progress = 0;
			setChanged();
		}
	}

	protected boolean canProcess(RecipeHolder<KhromaImbuementRecipe> recipe, Khroma khroma) {
		if (recipe == null)
			return false;
		ItemStack input = items.get(SLOT_INPUT);
		if (input.isEmpty())
			return false;
		ItemKhromaRecipeInput recipeInput = new ItemKhromaRecipeInput(input, khroma);
		if (!recipe.value().matches(recipeInput, level))
			return false;
		ItemStack output = items.get(SLOT_OUTPUT);
		if (output.isEmpty())
			return true;
		ItemStack result = recipe.value().assemble(recipeInput, level.registryAccess());
		if (!ItemStack.isSameItemSameComponents(output, result))
			return false;
		return output.getCount() + result.getCount() <= getMaxStackSize(result);
	}

	protected void process(RecipeHolder<KhromaImbuementRecipe> recipe, Khroma khroma) {
		ItemStack input = items.get(SLOT_INPUT);
		ItemStack output = items.get(SLOT_OUTPUT);
		ItemStack result = recipe.value().assemble(new ItemKhromaRecipeInput(input, khroma), level.registryAccess());
		if (output.isEmpty())
			items.set(SLOT_OUTPUT, result);
		else
			output.grow(result.getCount());
		input.shrink(1);
		progress -= 1;
		if (input.isEmpty())
			progress = 0;
	}

	public ConnectionType khromaConnection(Direction direction) {
		if (getBlockState().getValue(KhromaImbuerBlock.FACING).getOpposite() == direction)
			return ConnectionType.CONSUMER;
		return ConnectionType.NONE;
	}

	@Override
	protected void loadAdditional(CompoundTag tag, Provider registries) {
		super.loadAdditional(tag, registries);
		items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items, registries);
		progress = tag.getFloat("progress").orElse(0f);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, Provider registries) {
		super.saveAdditional(tag, registries);
		ContainerHelper.saveAllItems(tag, items, registries);
		tag.putFloat("progress", progress);
		// tag.putString("recipe", currentRecipe.);
	}

	@Override
	public float getSoftLimit() {
		return 20f;
	}

	@Override
	public int getContainerSize() {
		return items.size();
	}

	@Override
	public boolean isEmpty() {
		return items.stream().allMatch((stack) -> stack.isEmpty());
	}

	@Override
	public ItemStack getItem(int slot) {
		if (slot < 0 || slot >= getContainerSize())
			return null;
		return items.get(slot);
	}

	@Override
	public ItemStack removeItem(int slot, int amount) {
		ItemStack stack = ContainerHelper.removeItem(items, slot, amount);
		if (stack != null && !stack.isEmpty())
			setChanged();
		return stack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		ItemStack result = items.get(slot);
		items.set(slot, result);
		return result;
	}

	@Override
	public void setItem(int slot, ItemStack stack) {
		items.set(slot, stack);
		stack.limitSize(getMaxStackSize());
		setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		return Container.stillValidBlockEntity(this, player);
	}

	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		return slot != SLOT_OUTPUT;
	}

	@Override
	public void clearContent() {
		items.clear();
		setChanged();
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		if (side == Direction.DOWN)
			return SLOT_DOWN;
		return SLOT_NOT_DOWN;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, Direction direction) {
		return canPlaceItem(index, itemStack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return true;
	}

	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
		return new KhromaImbuerMenu(containerId, playerInventory, this, dataAccess);
	}

	@Override
	public Component getDisplayName() {
		return CONTAINER_TITLE;
	}

}
