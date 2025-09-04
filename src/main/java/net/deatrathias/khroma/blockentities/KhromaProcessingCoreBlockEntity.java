package net.deatrathias.khroma.blockentities;

import net.deatrathias.khroma.blocks.machine.modular.ProcessControllerBlock;
import net.deatrathias.khroma.processing.BaseProcess;
import net.deatrathias.khroma.processing.ProcessType;
import net.deatrathias.khroma.registries.BlockReference;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class KhromaProcessingCoreBlockEntity extends BlockEntity {

	private ItemStack itemContent = ItemStack.EMPTY;

	private BaseProcess currentProcess;

	public KhromaProcessingCoreBlockEntity(BlockPos pos, BlockState blockState) {
		super(BlockReference.BE_KHROMA_PROCESSING_CORE.get(), pos, blockState);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		itemContent = input.read("itemContent", ItemStack.CODEC).orElse(ItemStack.EMPTY);
		currentProcess = input.child("Process").map(child -> ProcessType.load(child).orElse(null)).orElse(null);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.store("itemContent", ItemStack.CODEC, itemContent);
		if (currentProcess != null)
			currentProcess.saveWithId(output);
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		super.preRemoveSideEffects(pos, state);
		Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemContent);
	}

	public void refreshNeighborChanges() {
		findProcess();
	}

	public void findProcess() {
		for (var direction : Direction.values()) {
			BlockState neighborState = level.getBlockState(worldPosition.relative(direction));
			if (neighborState.getBlock() instanceof ProcessControllerBlock controller && neighborState.getValue(ProcessControllerBlock.FACING) == direction) {
				if (currentProcess == null || currentProcess.getType() != controller.getProcessType()) {
					currentProcess = controller.instantiateProcess();
					setChanged();
				}
				break;
			}
		}
	}

	private void tick() {

	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, KhromaProcessingCoreBlockEntity blockEntity) {
		blockEntity.tick();
	}
}
