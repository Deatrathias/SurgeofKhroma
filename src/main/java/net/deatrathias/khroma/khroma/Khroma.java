package net.deatrathias.khroma.khroma;

import java.io.Serializable;
import java.util.List;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.deatrathias.khroma.SurgeofKhroma;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;

public final class Khroma implements Comparable<Khroma>, Serializable, StringRepresentable {
	private static final long serialVersionUID = 1L;

	public static final int FLAG_KHROMA_RED = 1;
	public static final int FLAG_KHROMA_GREEN = 2;
	public static final int FLAG_KHROMA_BLUE = 4;
	public static final int FLAG_KHROMA_WHITE = 8;
	public static final int FLAG_KHROMA_BLACK = 16;

	public static final String[] KhromaNames = new String[] { "red", "green", "blue", "white", "black" };

	private static final int[] KhromaColors = new int[] { 0x00FFFFFF, // empty
			0xFFFF0000, // red
			0xFF00FF00, // green
			0xFFFFFF00, // red green
			0xFF0000FF, // blue
			0xFFFF00FF, // red blue
			0xFF00FFFF, // green blue
			0xFFFFFFFF, // red green blue
			0xFFFFFFFF, // white
			0xFFFF8080, // red white
			0xFF80FF80, // green white
			0xFFFFFF80, // red green white
			0xFF8080FF, // blue white
			0xFFFF80FF, // red blue white
			0xFF80FFFF, // green blue white
			0xFFFFFFFF, // red green blue white
			0xFF202020, // black
			0xFF800000, // red black
			0xFF008000, // green black
			0xFF808000, // red green black
			0xFF000080, // blue black
			0xFF800080, // red blue black
			0xFF008080, // green blue black
			0xFF808080, // red green blue black
			0xFF808080, // white black
			0xFF804040, // red white black
			0xFF408040, // green white black
			0xFF808040, // red green white black
			0xFF404080, // blue white black
			0xFF804080, // red blue white black
			0xFF408080, // green blue white black
			0xFFFFFFFF, // red green blue white black
	};

	private static final Khroma[] khromaInstances = new Khroma[32];

	private int khromaValue = 0;

	private String name;

	static {
		for (int i = 0; i < khromaInstances.length; i++)
			khromaInstances[i] = new Khroma(i);
	}

	public static final Khroma KHROMA_EMPTY = khromaInstances[0];
	public static final Khroma KHROMA_RED = khromaInstances[FLAG_KHROMA_RED];
	public static final Khroma KHROMA_GREEN = khromaInstances[FLAG_KHROMA_GREEN];
	public static final Khroma KHROMA_BLUE = khromaInstances[FLAG_KHROMA_BLUE];
	public static final Khroma KHROMA_WHITE = khromaInstances[FLAG_KHROMA_WHITE];
	public static final Khroma KHROMA_BLACK = khromaInstances[FLAG_KHROMA_BLACK];

	public static final Khroma KHROMA_SPECTRUM = khromaInstances[FLAG_KHROMA_RED | FLAG_KHROMA_GREEN | FLAG_KHROMA_BLUE];
	public static final Khroma KHROMA_LIGHT_SPECTRUM = khromaInstances[FLAG_KHROMA_RED | FLAG_KHROMA_GREEN | FLAG_KHROMA_BLUE | FLAG_KHROMA_WHITE];
	public static final Khroma KHROMA_DARK_SPECTRUM = khromaInstances[FLAG_KHROMA_RED | FLAG_KHROMA_GREEN | FLAG_KHROMA_BLUE | FLAG_KHROMA_BLACK];
	public static final Khroma KHROMA_KHROMEGA = khromaInstances[FLAG_KHROMA_RED | FLAG_KHROMA_GREEN | FLAG_KHROMA_BLUE | FLAG_KHROMA_WHITE | FLAG_KHROMA_BLACK];

	public static final Codec<Khroma> CODEC = ExtraCodecs.orCompressed(Codec.stringResolver(StringRepresentable::getSerializedName, Khroma::fromName),
			ExtraCodecs.idResolverCodec(Khroma::asInt, intValue -> intValue >= 0 && intValue < 32 ? khromaInstances[intValue] : null, -1));

	public static final StreamCodec<ByteBuf, Khroma> STREAM_CODEC = ByteBufCodecs.BYTE.map(Khroma::fromByte, Khroma::toByte);

	public static Khroma get(boolean red, boolean green, boolean blue, boolean white, boolean black) {
		int khromaValue = 0;
		if (red)
			khromaValue |= FLAG_KHROMA_RED;
		if (green)
			khromaValue |= FLAG_KHROMA_GREEN;
		if (blue)
			khromaValue |= FLAG_KHROMA_BLUE;
		if (white)
			khromaValue |= FLAG_KHROMA_WHITE;
		if (black)
			khromaValue |= FLAG_KHROMA_BLACK;

		return khromaInstances[khromaValue];
	}

	public static List<Khroma> allKhroma() {
		return List.of(khromaInstances);
	}

	private Khroma(int khromaValue) {
		this.khromaValue = khromaValue;
		name = getNameFromValue(khromaValue);
	}

	public static Khroma fromInt(int value) {
		if (value < 0 || value >= khromaInstances.length)
			return null;
		return khromaInstances[value];
	}

	public static Khroma fromByte(byte value) {
		return fromInt(value);
	}

	public int asInt() {
		return khromaValue;
	}

	public static byte toByte(Khroma khroma) {
		return (byte) khroma.khromaValue;
	}

	public String getName() {
		return name;
	}

	public static Khroma fromName(String name) {
		return khromaInstances[getValueFromName(name)];
	}

	public static String getNameFromValue(int value) {
		if (value == 0)
			return "empty";

		int val = value;
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < KhromaNames.length; i++) {
			if ((val & 1) == 1)
				result.append(KhromaNames[i]);
			val = val >> 1;
		}

		return result.toString();
	}

	public static int getValueFromName(String name) {
		if ("empty".equals(name) || name == null)
			return 0;

		int val = 0;

		int i = 0;
		while (i < name.length()) {
			boolean found = false;
			for (int j = 0; j < KhromaNames.length; j++) {
				if (i + KhromaNames[j].length() > name.length())
					continue;

				if (name.substring(i, i + KhromaNames[j].length()).equals(KhromaNames[j])) {
					found = true;
					val |= 1 << j;
					i += KhromaNames[j].length();
					break;
				}
			}
			if (!found) {
				SurgeofKhroma.LOGGER.error("Parsing khroma value error: " + name);
				return val;
			}
		}

		return val;
	}

	public static Khroma combine(Khroma k1, Khroma k2) {
		return khromaInstances[k1.khromaValue | k2.khromaValue];
	}

	public int countColors() {
		int val = khromaValue;
		int count = 0;
		while (val != 0) {
			if ((val & 1) == 1)
				count++;
			val = val >> 1;
		}

		return count;
	}

	public Khroma[] separateold() {
		int value = khromaValue;
		int i = 0;

		while (value != 0) {
			if ((value & 1) == 1) {
				return new Khroma[] { khromaInstances[1 << i], khromaInstances[khromaValue & ~(1 << i)] };
			}
			value = value >> 1;
			i++;
		}

		return new Khroma[] { KHROMA_EMPTY, KHROMA_EMPTY };
	}

	public Khroma[] separate() {
		int count = countColors();
		if (count <= 1)
			return new Khroma[] { this, KHROMA_EMPTY };

		count = Math.ceilDiv(count, 2);

		int value = khromaValue;
		int result = 0;
		int i = 0;

		while (value != 0) {
			if ((value & 1) == 1) {
				result |= 1 << i;
				count--;
				if (count <= 0)
					break;
			}
			value = value >> 1;
			i++;
		}

		return new Khroma[] { khromaInstances[result], khromaInstances[khromaValue & ~result] };
	}

	public boolean contains(Khroma o) {
		return (khromaValue | o.khromaValue) == khromaValue;
	}

	public int getTint() {
		return KhromaColors[khromaValue];
	}

	@Override
	public int compareTo(Khroma o) {
		if (o == null)
			return -1;
		return Integer.compare(khromaValue, o.khromaValue);
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(khromaValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Khroma))
			return false;

		return khromaValue == ((Khroma) obj).khromaValue;
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getLocalizedName() {
		return "surgeofkhroma.khroma." + getName();
	}

	@Override
	public String getSerializedName() {
		return getNameFromValue(khromaValue);
	}
}
