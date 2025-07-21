package net.deatrathias.khroma.khroma;

import java.util.List;
import java.util.Optional;

import net.minecraft.world.level.block.state.properties.Property;

public class KhromaProperty extends Property<Khroma> {
	public static final KhromaProperty KHROMA = KhromaProperty.create("khroma");

	protected KhromaProperty(String name) {
		super(name, Khroma.class);
	}

	public static KhromaProperty create(String name) {
		return new KhromaProperty(name);
	}

	@Override
	public List<Khroma> getPossibleValues() {
		return Khroma.allKhroma();
	}

	@Override
	public String getName(Khroma value) {
		return value.getName();
	}

	@Override
	public Optional<Khroma> getValue(String value) {
		return Optional.of(Khroma.fromName(value));
	}

	@Override
	public int getInternalIndex(Khroma value) {
		return value.asInt();
	}
}
