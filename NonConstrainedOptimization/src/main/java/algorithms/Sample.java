package algorithms;

import java.util.List;

public class Sample implements Comparable<Sample> {
	public final List<Double> point;
	public final double value;
	
	public Sample(List<Double> point, double value) {
		this.point = point;
		this.value = value;
	}

	@Override
	public int compareTo(Sample s) {
		double diff = value - s.value;
		return diff < 0 ? -1 : (diff > 0 ? 1 : 0);
	}
}
