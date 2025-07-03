package net.deatrathias.khroma.khroma;

import java.util.Collection;
import java.util.Optional;

import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class KhromaProperty extends IntegerProperty {

	protected KhromaProperty(String name) {
		super(name, 0, 31);
	}

	public static KhromaProperty create(String name) {
		return new KhromaProperty(name);
	}

	@Override
	public Collection<Integer> getPossibleValues() {
		return super.getPossibleValues();
	}

	@Override
	public String getName(Integer value) {
		return Khroma.getNameFromValue(value);
	}

	@Override
	public Optional<Integer> getValue(String value) {
		return Optional.of(Khroma.getValueFromName(value));
	}

}
