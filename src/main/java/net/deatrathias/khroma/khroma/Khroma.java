package net.deatrathias.khroma.khroma;

import java.io.Serializable;

import net.deatrathias.khroma.SurgeofKhroma;

public final class Khroma implements Comparable<Khroma>, Serializable {
	private static final long serialVersionUID = 1L;

	public static final int FLAG_KHROMA_RED = 1;
	public static final int FLAG_KHROMA_GREEN = 2;
	public static final int FLAG_KHROMA_BLUE = 4;
	public static final int FLAG_KHROMA_WHITE = 8;
	public static final int FLAG_KHROMA_BLACK = 16;

	public static enum KhromaColor {
		RED, GREEN, BLUE, WHITE, BLACK
	}

	public static final String[] KhromaNames = new String[] { "red", "green", "blue", "white", "black" };

	public static final int[] KhromaColors = new int[] { 0xFFFFFFFF, // empty
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

	public static Khroma empty() {
		return khromaInstances[0];
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

	public int asInt() {
		return khromaValue;
	}

	public String getName() {
		return name;
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

	public Khroma[] separate() {
		int value = khromaValue;
		int i = 0;

		while (value != 0) {
			if ((value & 1) == 1) {
				return new Khroma[] { khromaInstances[1 << i], khromaInstances[khromaValue & ~(1 << i)] };
			}
			value = value >> 1;
			i++;
		}

		return new Khroma[] { empty(), empty() };
	}

	public boolean contains(Khroma o) {
		return (khromaValue | o.khromaValue) == khromaValue;
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
		return getNameFromValue(khromaValue);
	}

	public String getLocalizedName() {
		return "surgeofkhroma.khroma." + getName();
	}
}
