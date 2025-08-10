package net.deatrathias.khroma.processing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mojang.datafixers.util.Pair;

import net.deatrathias.khroma.khroma.Khroma;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueInput.ValueInputList;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueOutput.ValueOutputList;

public class BreakBlockProcess extends BlockProcess {

	private Map<BlockPos, Pair<BlockState, Float>> progressMap;

	private int breakId;

	public BreakBlockProcess(ProcessType<?> type) {
		super(type);
		progressMap = new HashMap<BlockPos, Pair<BlockState, Float>>();

	}

	public void setBreakId(int breakId) {
		this.breakId = breakId;
	}

	@Override
	public boolean processBlock(float potency, Level level, List<BlockPos> posList) {
		Set<BlockPos> remainingPos = new HashSet<BlockPos>(progressMap.keySet());

		for (var pos : posList) {
			BlockState state = level.getBlockState(pos);
			float progress = 0;

			if (progressMap.containsKey(pos)) {
				var pair = progressMap.get(pos);
				if (pair.getFirst() == state)
					progress = pair.getSecond();
			}

			if (canBreak(level, pos, state)) {
				progress += potency / 20f;

				if (progress >= state.getDestroySpeed(level, pos)) {
					level.destroyBlock(pos, true);
					progress = 0;
				} else {
					level.destroyBlockProgress(breakId, pos, (int) (progress * 10f));
				}
			}

			progressMap.put(pos, Pair.of(state, progress));
			remainingPos.remove(pos);
		}

		if (!remainingPos.isEmpty()) {
			for (var pos : remainingPos) {
				progressMap.remove(pos);
				level.destroyBlockProgress(breakId, pos, 0);
			}
		}

		return true;
	}

	private boolean canBreak(Level level, BlockPos pos, BlockState state) {
		if (state.is(Blocks.AIR))
			return false;

		float breakTime = state.getDestroySpeed(level, pos);
		if (breakTime < 0 || breakTime >= 50)
			return false;

		return true;
	}

	@Override
	protected Khroma requiredKhroma() {
		return Khroma.KHROMA_RED;
	}

	@Override
	public void load(ValueInput input) {
		breakId = input.getIntOr("BreakId", 0);
		progressMap.clear();

		ValueInputList progressList = input.childrenListOrEmpty("Progress");
		for (var progress : progressList) {
			BlockPos pos = progress.read("Pos", BlockPos.CODEC).orElse(BlockPos.ZERO);
			BlockState state = progress.read("State", BlockState.CODEC).orElse(Blocks.AIR.defaultBlockState());
			float progressValue = progress.getFloatOr("Value", 0);

			progressMap.put(pos, Pair.of(state, progressValue));
		}
	}

	@Override
	public void save(ValueOutput output) {
		output.putInt("BreakId", breakId);

		ValueOutputList progressList = output.childrenList("Progress");
		for (var entry : progressMap.entrySet()) {
			ValueOutput progress = progressList.addChild();
			progress.store("Pos", BlockPos.CODEC, entry.getKey());
			progress.store("State", BlockState.CODEC, entry.getValue().getFirst());
			progress.putFloat("Value", entry.getValue().getSecond());
		}
	}

}
