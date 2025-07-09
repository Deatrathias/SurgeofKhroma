package net.deatrathias.khroma;

import java.util.Arrays;
import java.util.List;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Range;

public class Config {
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

	public static final ModConfigSpec.IntValue CHUNKS_PER_NODE = BUILDER.comment("The average number of chunks between each Khroma Node").defineInRange("chunks_per_node", 16, 1, Integer.MAX_VALUE);

	public static final ModConfigSpec.ConfigValue<List<? extends Double>> KHROMA_RATE_PER_LEVEL = BUILDER.comment("Khroma rate extracted from a node based on the node level").defineList(
			Arrays.asList("khroma_rate_per_level"), () -> Arrays.asList(100.0, 1000.0, 5000.0), () -> 0.0, (value) -> value instanceof Number && ((Number) value).doubleValue() >= 0, Range.of(2, 2));

	static final ModConfigSpec SPEC = BUILDER.build();
}
