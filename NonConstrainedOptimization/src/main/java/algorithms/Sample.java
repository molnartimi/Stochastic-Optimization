package algorithms;

public class Sample<ParamType> implements Comparable<Sample<ParamType>> {
	public final ParamType point;
	public final double value;
	
	public Sample(ParamType point, double value) {
		this.point = point;
		this.value = value;
	}

	@Override
	public int compareTo(Sample<ParamType> s) {
		double diff = value - s.value;
		return diff < 0 ? -1 : (diff > 0 ? 1 : 0);
	}
}
