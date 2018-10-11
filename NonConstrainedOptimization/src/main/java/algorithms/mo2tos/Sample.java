package algorithms.mo2tos;

public class Sample implements Comparable<Sample>{
	public final double[] values;
	public final double lowResult;
	private Double heighResult;
	
	public Sample(double[] values, double lowResult) {
		this.values = values;
		this.lowResult = lowResult;
	}

	@Override
	public int compareTo(Sample o) {
		double diff = this.lowResult - o.lowResult;
		return (diff > 0) ? 1 : (diff < 0) ?  -1 : 0;
	}
	
	public void setHeighResult(double heighResult) {
		this.heighResult = heighResult;
	}
	
	public double getHeighResult() {
		return this.heighResult;
	}
	
	public boolean isComputed() {
		return heighResult != null;
	}
}
