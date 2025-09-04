package net.deatrathias.khroma.khroma;

import java.util.Objects;

import net.deatrathias.khroma.recipes.KhromaImbuementRecipe;

public final class KhromaThroughput {
	public static class KhromaThrouputWrapper {
		public KhromaThroughput throughput;

		public KhromaThrouputWrapper(KhromaThroughput throughput) {
			this.throughput = throughput;
		}
	}

	private static final float COMBINED_KHROMA_PENALTY = 0.9f;

	private Khroma khroma;
	private float rate;

	public KhromaThroughput(Khroma khroma, float rate) {
		this.khroma = khroma;
		this.rate = rate;
	}

	public Khroma getKhroma() {
		return khroma;
	}

	public float getRate() {
		return rate;
	}

	public static KhromaThroughput empty = new KhromaThroughput(Khroma.EMPTY, 0);

	@Override
	public int hashCode() {
		return Objects.hash(khroma, rate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KhromaThroughput other = (KhromaThroughput) obj;
		return Objects.equals(khroma, other.khroma) && Float.floatToIntBits(rate) == Float.floatToIntBits(other.rate);
	}

	public static KhromaThroughput merge(KhromaThroughput t1, KhromaThroughput t2) {
		Khroma empty = Khroma.EMPTY;
		Khroma khroma = t1.khroma;
		if (t1.khroma == empty)
			khroma = t2.khroma;

		if (t1.khroma != t2.khroma && t1.khroma != empty && t2.khroma != empty)
			return t1.rate >= t2.rate ? t1 : t2;
		return new KhromaThroughput(khroma, t1.rate + t2.rate);
	}

	public static KhromaThroughput combine(KhromaThroughput t1, KhromaThroughput t2) {

		if (t1.khroma == t2.khroma) {
			return merge(t1, t2);
		} else if (t1.khroma.contains(t2.khroma)) {
			return t1;
		} else if (t2.khroma.contains(t1.khroma)) {
			return t2;
		}

		int count1 = t1.khroma.countColors();
		int count2 = t2.khroma.countColors();

		float ratio1 = t1.getRate() / (float) count1;
		float ratio2 = t2.getRate() / (float) count2;
		Khroma combined = Khroma.combine(t1.khroma, t2.khroma);

		if (ratio1 >= ratio2) {
			return new KhromaThroughput(combined, ratio2 * combined.countColors());
		} else {
			return new KhromaThroughput(combined, ratio1 * combined.countColors());
		}
	}

	public KhromaThroughput multiply(float m) {
		return new KhromaThroughput(khroma, rate * m);
	}

	public float effectiveConsumed(Khroma consumedKhroma) {
		if (khroma == consumedKhroma)
			return rate;

		if (!khroma.contains(consumedKhroma))
			return 0;

		int colors = khroma.countColors();
		int consumedColors = consumedKhroma.countColors();

		return (rate * ((float) Math.pow(COMBINED_KHROMA_PENALTY, (colors - consumedColors))) * consumedColors / colors);
	}

	public float recipeProgress(KhromaImbuementRecipe recipe, float softLimit) {
		float adjustedRate = effectiveConsumed(recipe.getKhroma());
		if (softLimit == -1 || adjustedRate <= softLimit)
			return adjustedRate;

		float half = softLimit / 2f;
		return softLimit + (float) Math.sqrt((adjustedRate - softLimit) * softLimit + half * half) - half;
	}

	@Override
	public String toString() {
		return "KhromaThroughput [khroma=" + khroma.toString() + ", rate=" + Float.toString(rate) + "]";
	}

	public static KhromaThroughput[] separate(KhromaThroughput t) {
		if (t.khroma == Khroma.EMPTY)
			return new KhromaThroughput[] { KhromaThroughput.empty, KhromaThroughput.empty };

		int colors = t.khroma.countColors();

		Khroma[] khromas = t.khroma.separate();

		return new KhromaThroughput[] { new KhromaThroughput(khromas[0], t.rate * khromas[0].countColors() / colors), new KhromaThroughput(khromas[1], t.rate * khromas[1].countColors() / colors) };
	}

}
